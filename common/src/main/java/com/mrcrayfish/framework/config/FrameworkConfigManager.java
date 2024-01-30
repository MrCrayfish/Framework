package com.mrcrayfish.framework.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.config.AbstractProperty;
import com.mrcrayfish.framework.api.config.ConfigProperty;
import com.mrcrayfish.framework.api.config.ConfigType;
import com.mrcrayfish.framework.api.config.FrameworkConfig;
import com.mrcrayfish.framework.api.config.event.FrameworkConfigEvents;
import com.mrcrayfish.framework.api.event.ClientConnectionEvents;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import com.mrcrayfish.framework.network.Network;
import com.mrcrayfish.framework.network.message.configuration.S2CConfigData;
import com.mrcrayfish.framework.network.message.play.S2CSyncConfigData;
import com.mrcrayfish.framework.platform.Services;
import com.mrcrayfish.framework.util.ConfigHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * Author: MrCrayfish
 */
public class FrameworkConfigManager
{
    private static final LevelResource WORLD_CONFIG = createLevelResource("serverconfig");

    private static FrameworkConfigManager instance;

    public static FrameworkConfigManager getInstance()
    {
        if(instance == null)
        {
            instance = new FrameworkConfigManager();
        }
        return instance;
    }

    private final Map<ResourceLocation, FrameworkConfigImpl> configs;

    private FrameworkConfigManager()
    {
        Map<ResourceLocation, FrameworkConfigImpl> configs = new HashMap<>();
        Services.CONFIG.getAllFrameworkConfigs().forEach(pair ->
        {
            ConfigScanData data = ConfigScanData.analyze(pair.getLeft(), pair.getRight());
            FrameworkConfigImpl entry = new FrameworkConfigImpl(data);
            configs.put(entry.getName(), entry);
        });
        this.configs = ImmutableMap.copyOf(configs);

        ServerEvents.STARTING.register(this::onServerStarting);
        ServerEvents.STOPPED.register(this::onServerStopped);
        ClientConnectionEvents.LOGGING_OUT.register(this::onClientDisconnect);
    }

    public List<FrameworkConfigImpl> getConfigs()
    {
        return ImmutableList.copyOf(this.configs.values());
    }

    @Nullable
    public FrameworkConfigImpl getConfig(ResourceLocation id)
    {
        return this.configs.get(id);
    }

    public List<S2CConfigData> getConfigurationMessages()
    {
        return this.configs.values().stream()
            .filter(entry -> entry.getType().isSync())
            .map(entry -> {
                ResourceLocation key = entry.getName();
                byte[] data = ConfigHelper.getBytes(entry.config);
                return new S2CConfigData(key, data);
            }).collect(Collectors.toList());
    }

    public boolean processConfigData(S2CConfigData message)
    {
        Constants.LOG.info("Loading synced config from server: " + message.key());
        FrameworkConfigImpl entry = this.configs.get(message.key());
        if(entry != null && entry.getType().isSync())
        {
            return entry.loadFromData(message.data());
        }
        return false;
    }

    // Unloads all synced configs since they should no longer be accessible
    public void onClientDisconnect(@Nullable Connection connection)
    {
        if(connection != null && !connection.isMemoryConnection()) // Run only if disconnected from remote server
        {
            Constants.LOG.info("Unloading synced configs from server");
            this.configs.values().stream().filter(entry -> entry.getType().isSync()).forEach(entry -> entry.unload(true, true));
        }
    }

    public boolean processSyncData(S2CSyncConfigData message)
    {
        FrameworkConfigImpl frameworkConfig = this.configs.get(message.id());
        if(frameworkConfig == null)
        {
            Constants.LOG.error("Server sent data for a config that doesn't exist: {}", message.id());
            return false;
        }

        if(frameworkConfig.isReadOnly())
        {
            Constants.LOG.error("Server sent data for a read-only config '{}'. This should not happen!", message.id());
            return false;
        }

        if(!frameworkConfig.getType().isSync())
        {
            Constants.LOG.error("Server sent data for non-sync config '{}'. This should not happen!", message.id());
            return false;
        }

        if(!frameworkConfig.isLoaded())
        {
            Constants.LOG.error("Tried to perform sync update on an unloaded config. Something went wrong...");
            return false;
        }

        try
        {
            CommentedConfig config = TomlFormat.instance().createParser().parse(new ByteArrayInputStream(message.data()));
            if(!frameworkConfig.isCorrect(config))
            {
                Constants.LOG.error("Received incorrect config data");
                return false;
            }

            if(frameworkConfig.config instanceof Config c)
            {
                c.putAll(config);
                frameworkConfig.allProperties.forEach(AbstractProperty::invalidateCache);
                FrameworkConfigEvents.RELOAD.post().handle(frameworkConfig.source);
                Constants.LOG.debug("Successfully processed sync update for config: {}", message.id());
                return true;
            }
        }
        catch(ParsingException e)
        {
            Constants.LOG.error("Received malformed config data");
            e.printStackTrace();
        }
        catch(Exception e)
        {
            Constants.LOG.error("An exception was thrown when processing config data: {}", e.toString());
            e.printStackTrace();
        }
        return false;
    }

    private void onServerStarting(MinecraftServer server)
    {
        Constants.LOG.info("Loading server configs...");

        // Create the server config directory
        Path serverConfig = server.getWorldPath(WORLD_CONFIG);
        createDirectory(serverConfig);

        // Handle loading server configs based on type
        this.configs.values().forEach(entry ->
        {
            switch(entry.configType)
            {
                case WORLD, WORLD_SYNC -> entry.load(serverConfig);
                case SERVER, SERVER_SYNC -> entry.load(Services.CONFIG.getConfigPath());
                case DEDICATED_SERVER ->
                {
                    if(EnvironmentHelper.getEnvironment() == Environment.DEDICATED_SERVER)
                    {
                        entry.load(Services.CONFIG.getConfigPath());
                    }
                }
            }
        });
    }

    private void onServerStopped(MinecraftServer server)
    {
        Constants.LOG.info("Unloading server configs...");
        this.configs.values().forEach(entry -> entry.unload(true, false));
    }

    public static final class FrameworkConfigImpl
    {
        private final Object source;
        private final String id;
        private final String name;
        private final boolean readOnly;
        private final char separator;
        private final ConfigType configType;
        private final Set<AbstractProperty<?>> allProperties;
        private final ConfigSpec spec;
        private final ClassLoader classLoader;
        private final CommentedConfig comments;
        @Nullable
        private UnmodifiableConfig config;
        private boolean preventNextChangeCallback;

        private FrameworkConfigImpl(ConfigScanData data)
        {
            Preconditions.checkArgument(!data.getConfig().id().trim().isEmpty(), "The 'id' of the config cannot be empty");
            Preconditions.checkArgument(Services.PLATFORM.isModLoaded(data.getConfig().id()), "The 'id' of the config must match a mod id");
            Preconditions.checkArgument(!data.getConfig().name().trim().isEmpty(), "The 'name' of the config cannot be empty");
            Preconditions.checkArgument(data.getConfig().name().length() <= 64, "The 'name' of the config must be 64 characters or less");
            Preconditions.checkArgument(isValidSeparator(data.getConfig().separator()), "The 'separator' of the config is invalid. It can only be '.' or '-'");

            this.source = data.getSource();
            this.id = data.getConfig().id();
            this.name = data.getConfig().name();
            this.readOnly = data.getConfig().readOnly();
            this.configType = data.getConfig().type();
            this.separator = data.getConfig().separator();
            this.allProperties = ImmutableSet.copyOf(data.getProperties());
            this.spec = createSpec(this.allProperties);
            this.comments = createComments(this.spec, data.getComments());
            this.classLoader = Thread.currentThread().getContextClassLoader();

            // Load non-server configs immediately
            if(!this.configType.isServer())
            {
                if(this.configType == ConfigType.MEMORY)
                {
                    this.load(null);
                }
                else
                {
                    this.load(Services.CONFIG.getConfigPath());
                }
            }
        }

        /**
         * Loads the config from the given path. If the path is null then a memory config will be
         * loaded instead.
         *
         * @param configDir the path of the configuration directory
         */
        private void load(@Nullable Path configDir)
        {
            Optional<Environment> env = this.getType().getEnv();
            if(env.isPresent() && !EnvironmentHelper.getEnvironment().equals(env.get()))
                return;
            Preconditions.checkState(this.config == null, "Config is already loaded. Unload before loading again.");
            UnmodifiableConfig config = this.createConfig(configDir);
            ConfigHelper.loadConfig(config);
            this.correct(config);
            this.allProperties.forEach(p -> p.updateProxy(new ValueProxy(config, p.getPath(), this.readOnly)));
            this.config = config;
            if(!this.readOnly && this.configType != ConfigType.MEMORY)
            {
                ConfigHelper.watchConfig(config, this::changeCallback);
            }
        }

        private boolean loadFromData(byte[] data)
        {
            this.unload(false, true);
            try
            {
                Preconditions.checkState(this.configType.isServer(), "Only server configs can be loaded from data");
                CommentedConfig commentedConfig = TomlFormat.instance().createParser().parse(new ByteArrayInputStream(data));
                if(!this.isCorrect(commentedConfig)) // The server should be sending correct configs
                    return false;
                this.correct(commentedConfig);
                UnmodifiableConfig config = this.isReadOnly() ? commentedConfig.unmodifiable() : commentedConfig;
                this.allProperties.forEach(p -> p.updateProxy(new ValueProxy(config, p.getPath(), this.readOnly)));
                this.config = config;
                FrameworkConfigEvents.LOAD.post().handle(this.source);
                return true;
            }
            catch(ParsingException e)
            {
                Constants.LOG.info("Failed to parse config data: {}", e.toString());
                return false;
            }
            catch(Exception e)
            {
                Constants.LOG.info("An exception occurred when loading config data: {}", e.toString());
                this.unload(false, true);
                return false;
            }
        }

        private UnmodifiableConfig createConfig(@Nullable Path configDir)
        {
            if(this.readOnly)
            {
                Preconditions.checkArgument(configDir != null, "Config dir must not be null for read only configs");
                return createReadOnlyConfig(configDir, this.id, this.separator, this.name, this::correct);
            }
            return createFrameworkConfig(configDir, this.id, this.separator, this.name);
        }

        private void unload(boolean sendEvent, boolean fromServer)
        {
            if(this.config != null)
            {
                this.allProperties.forEach(p -> p.updateProxy(ValueProxy.EMPTY));
                if(!this.readOnly && this.configType != ConfigType.MEMORY && (!this.configType.isSync() || !fromServer))
                {
                    ConfigHelper.unwatchConfig(this.config);
                }
                this.config = null;
                if(sendEvent)
                {
                    Constants.LOG.info("Sending config unload event for {}", this.getFileName());
                    FrameworkConfigEvents.UNLOAD.post().handle(this.source);
                }
            }
        }

        private void changeCallback()
        {
            Thread.currentThread().setContextClassLoader(this.classLoader);
            if(!this.preventNextChangeCallback && this.config != null && !this.isReadOnly())
            {
                ConfigHelper.loadConfig(this.config);
                this.correct(this.config);
                this.allProperties.forEach(AbstractProperty::invalidateCache);
                EnvironmentHelper.submitOn(EnvironmentHelper.getEnvironment(), () -> () -> {
                    FrameworkConfigEvents.RELOAD.post().handle(this.source);
                });
                // Send updates to clients if server exists
                EnvironmentHelper.submitOn(LogicalEnvironment.SERVER, () -> () -> {
                    if(this.getType().isSync()) {
                        Network.getPlayChannel().sendToAll(new S2CSyncConfigData(this.getName(), this.getData()));
                    }
                });
            }
            this.preventNextChangeCallback = false;
        }

        private boolean isCorrect(UnmodifiableConfig config)
        {
            // Check if comments are correct
            if(config instanceof CommentedConfig c)
            {
                for(AbstractProperty<?> prop : this.allProperties)
                {
                    String propComment = prop.getComment();
                    if(!propComment.isBlank())
                    {
                        String configComment = c.getComment(prop.getPath());
                        if(!propComment.equals(configComment))
                        {
                            return false;
                        }
                    }
                }
            }
            if(config instanceof Config)
            {
                return this.spec.isCorrect((Config) config);
            }
            return true;
        }

        private void correct(UnmodifiableConfig config)
        {
            if(config instanceof Config && !this.isCorrect(config))
            {
                ConfigHelper.createBackup(config);
                this.spec.correct((Config) config);
                if(config instanceof CommentedConfig c)
                    c.putAllComments(this.comments);
                ConfigHelper.saveConfig(config);
            }
        }

        public ResourceLocation getName()
        {
            return new ResourceLocation(this.id, this.name);
        }

        public ConfigType getType()
        {
            return this.configType;
        }

        public String getFileName()
        {
            return String.format("%s%s%s.toml", this.id, this.separator, this.name);
        }

        public boolean isReadOnly()
        {
            return this.readOnly;
        }

        public boolean isLoaded()
        {
            return this.config != null;
        }

        @Nullable
        public byte[] getData()
        {
            return this.config != null ? ConfigHelper.getBytes(this.config) : null;
        }

        public Object getSource()
        {
            return this.source;
        }

        @Nullable
        public UnmodifiableConfig getConfig()
        {
            return this.config;
        }

        public Set<AbstractProperty<?>> getAllProperties()
        {
            return this.allProperties;
        }

        public ConfigSpec getSpec()
        {
            return this.spec;
        }
    }

    /**
     * Creates a tunnel from a ConfigProperty to a value in Config. This allows for a ConfigProperty
     * to be linked to any config and easily swappable.
     */
    public static class ValueProxy
    {
        private static final ValueProxy EMPTY = new ValueProxy();

        private final UnmodifiableConfig config;
        private final List<String> path;
        private final boolean readOnly;

        private ValueProxy()
        {
            this.config = null;
            this.path = null;
            this.readOnly = true;
        }

        private ValueProxy(UnmodifiableConfig config, List<String> path, boolean readOnly)
        {
            this.config = config;
            this.path = path;
            this.readOnly = readOnly;
        }

        public boolean isLinked()
        {
            return this != EMPTY;
        }

        public boolean isWritable()
        {
            return !this.readOnly;
        }

        @Nullable
        public <T> T get(BiFunction<UnmodifiableConfig, List<String>, T> function)
        {
            if(this.isLinked() && this.config != null)
            {
                return function.apply(this.config, this.path);
            }
            return null;
        }

        public <T> void set(T value)
        {
            if(this.isLinked() && this.isWritable() && this.config instanceof Config c)
            {
                c.set(this.path, value);
            }
        }
    }

    public static class PropertyData
    {
        private final String name;
        private final List<String> path;
        private final String translationKey;
        private final String comment;
        private final boolean worldRestart;
        private final boolean gameRestart;

        private PropertyData(String name, List<String> path, String translationKey, String comment, boolean worldRestart, boolean gameRestart)
        {
            this.name = name;
            this.path = ImmutableList.copyOf(path);
            this.translationKey = translationKey;
            this.comment = comment;
            this.worldRestart = worldRestart;
            this.gameRestart = gameRestart;
        }

        public String getName()
        {
            return this.name;
        }

        public List<String> getPath()
        {
            return this.path;
        }

        public String getTranslationKey()
        {
            return this.translationKey;
        }

        public String getComment()
        {
            return this.comment;
        }

        public boolean requiresWorldRestart()
        {
            return this.worldRestart;
        }

        public boolean requiresGameRestart()
        {
            return this.gameRestart;
        }
    }

    public interface IMapEntry {}

    private static class ConfigScanData
    {
        private final FrameworkConfig config;
        private final Object source;
        private final Set<AbstractProperty<?>> properties = new HashSet<>();
        private final Map<List<String>, String> comments = new HashMap<>();

        private ConfigScanData(FrameworkConfig config, Object source)
        {
            this.config = config;
            this.source = source;
        }

        public FrameworkConfig getConfig()
        {
            return this.config;
        }

        public Object getSource()
        {
            return this.source;
        }

        public Set<AbstractProperty<?>> getProperties()
        {
            return this.properties;
        }

        public Map<List<String>, String> getComments()
        {
            return this.comments;
        }

        private static ConfigScanData analyze(FrameworkConfig config, Object source)
        {
            Preconditions.checkArgument(!source.getClass().isPrimitive(), "FrameworkConfig annotation can only be applied to objects");
            ConfigScanData data = new ConfigScanData(config, source);
            data.scan(new Stack<>(), source);
            return data;
        }

        private void scan(Stack<String> stack, Object instance)
        {
            Field[] fields = instance.getClass().getDeclaredFields();
            Stream.of(fields).forEach(field -> Optional.ofNullable(field.getDeclaredAnnotation(ConfigProperty.class)).ifPresent(prop ->
            {
                stack.push(prop.name());
                try
                {
                    field.setAccessible(true);

                    // Retrieve the field object
                    Object obj = field.get(instance);
                    String comment = this.pushComment(stack, prop, obj);
                    if(obj instanceof AbstractProperty<?> property)
                    {
                        List<String> path = new ArrayList<>(stack);
                        String key = String.format("framework_config.%s.%s.%s", this.config.id(), this.config.name(), StringUtils.join(path, '.'));
                        property.initProperty(new PropertyData(prop.name(), path, key, comment, prop.worldRestart(), prop.gameRestart()));
                        this.properties.add(property);
                    }
                    else
                    {
                        this.scan(stack, obj);
                    }
                }
                catch(IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                stack.pop();
            }));
        }

        private String pushComment(Stack<String> stack, ConfigProperty prop, Object obj)
        {
            if(!prop.comment().isBlank())
            {
                String comment = prop.comment();
                if(obj instanceof AbstractProperty<?> property)
                {
                    String hint = property.getAllowedValuesString();
                    if(!hint.isBlank())
                    {
                        comment = comment + "\n" + hint;
                    }
                }
                comment = " " + comment.replace("\n", "\n ");
                this.comments.put(new ArrayList<>(stack), comment);
                return comment;
            }
            return "";
        }
    }

    public static class ConfigDataTask implements ConfigurationTask
    {
        public static final Type TYPE = new Type("framework:config_data");

        @Override
        public void start(Consumer<Packet<?>> consumer)
        {

        }

        @Override
        public Type type()
        {
            return TYPE;
        }
    }

    private static boolean isValidSeparator(char c)
    {
        return c == '.' || c == '-';
    }

    private static CommentedConfig createFrameworkConfig(@Nullable Path folder, String id, char separator, String name)
    {
        if(folder != null)
        {
            String fileName = String.format("%s%s%s.toml", id, separator, name);
            File file = new File(folder.toFile(), fileName);
            return CommentedFileConfig.builder(file).autosave().sync().onFileNotFound((file1, configFormat) -> initConfig(file1, configFormat, fileName)).build();
        }
        return CommentedConfig.inMemory();
    }

    private static UnmodifiableCommentedConfig createReadOnlyConfig(Path folder, String id, char separator, String name, Consumer<Config> corrector)
    {
        CommentedFileConfig temp = createTempConfig(folder, id, separator, name);
        ConfigHelper.loadConfig(temp);
        corrector.accept(temp);
        CommentedConfig config = CommentedConfig.inMemory();
        config.putAll(temp);
        temp.close();
        return config.unmodifiable();
    }

    private static CommentedFileConfig createTempConfig(Path folder, String id, char separator, String name)
    {
        String fileName = String.format("%s%s%s.toml", id, separator, name);
        File file = new File(folder.toFile(), fileName);
        return CommentedFileConfig.builder(file).sync().onFileNotFound((file1, configFormat) -> initConfig(file1, configFormat, fileName)).build();
    }

    private static boolean initConfig(final Path file, final ConfigFormat<?> format, final String fileName) throws IOException
    {
        Files.createDirectories(file.getParent());
        Path defaultConfigPath = Services.CONFIG.getGamePath().resolve(Services.CONFIG.getDefaultConfigPath());
        Path defaultConfigFile = defaultConfigPath.resolve(fileName);
        if(Files.exists(defaultConfigFile))
        {
            Files.copy(defaultConfigFile, file);
            return true;
        }
        Files.createFile(file);
        format.initEmptyFile(file);
        return false;
    }

    private static ConfigSpec createSpec(Set<AbstractProperty<?>> properties)
    {
        ConfigSpec spec = new ConfigSpec();
        properties.forEach(p -> p.defineSpec(spec));
        return spec;
    }

    private static CommentedConfig createComments(ConfigSpec spec, Map<List<String>, String> comments)
    {
        CommentedConfig config = CommentedConfig.inMemory();
        spec.correct(config);
        comments.forEach(config::setComment);
        return config;
    }

    private static void createDirectory(Path path)
    {
        try
        {
            PathUtils.createParentDirectories(path);
            if(!Files.isDirectory(path))
            {
                Files.createDirectory(path);
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static LevelResource createLevelResource(String path)
    {
        try
        {
            Constructor<LevelResource> constructor = LevelResource.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(path);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
