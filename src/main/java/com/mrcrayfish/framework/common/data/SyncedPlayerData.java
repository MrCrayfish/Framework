package com.mrcrayfish.framework.common.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.Reference;
import com.mrcrayfish.framework.api.data.SyncedDataKey;
import com.mrcrayfish.framework.network.Network;
import com.mrcrayfish.framework.network.message.handshake.S2CSyncedPlayerData;
import com.mrcrayfish.framework.network.message.play.S2CUpdatePlayerData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Basically a clone of DataParameter system. It's not good to init custom data parameters to
 * other entities that aren't your own. It can cause mismatched ids and crash the game. This synced
 * data system attempts to solve the problem (at least for player entities) and allows data to be
 * easily synced to clients. The data can only be controlled on the logical server. Changing the
 * data on the logical client will have no affect on the server.</p>
 * <p></p>
 * <p>To use this system you first need to create a synced data key instance. This should be a public
 * static final field. You will need to specify an key id (based on your modid), the serializer, and
 * a default value supplier.</p>
 * <code>public static final SyncedDataKey&lt;Double&gt; CURRENT_SPEED = SyncedDataKey.create(new ResourceLocation("examplemod:speed"), Serializers.DOUBLE, () -> 0.0);</code>
 * <p></p>
 * <p>Next the key needs to be registered. This can simply be done in the common setup of your mod.</p>
 * <code>SyncedPlayerData.instance().registerKey(CURRENT_SPEED);</code>
 * <p></p>
 * <p>Then anywhere you want (as long as it's on the main thread), you can set the value by calling</p>
 * <code>SyncedPlayerData.instance().set(player, CURRENT_SPEED, 5.0);</code>
 * <p></p>
 * <p>The value can be retrieved on the server or client by calling</p>
 * <code>SyncedPlayerData.instance().get(player, CURRENT_SPEED);</code>
 * <p></p>
 * <p>Author: MrCrayfish</p>
 */
public class SyncedPlayerData
{
    private static final Marker SYNCED_PLAYER_DATA_MARKER = MarkerManager.getMarker("SYNCED_PLAYER_DATA");
    private static final Capability<DataHolder> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private static SyncedPlayerData instance;

    private final Map<ResourceLocation, SyncedDataKey<?>> registeredDataKeys = new HashMap<>();
    private final BiMap<Integer, SyncedDataKey<?>> idToDataKey = HashBiMap.create();

    private int nextKeyId = 0;
    private boolean dirty = false;
    private boolean loaded = false;

    private SyncedPlayerData() {}

    public static SyncedPlayerData instance()
    {
        if(instance == null)
        {
            instance = new SyncedPlayerData();
        }
        return instance;
    }

    public static void onRegisterCapability(RegisterCapabilitiesEvent event)
    {
        event.register(DataHolder.class);
    }

    /**
     * Registers a synced data key into the system.
     *
     * @param key a synced data key instance
     */
    public synchronized void registerKey(SyncedDataKey<?> key)
    {
        if(this.loaded)
        {
            throw new IllegalStateException(String.format("Tried to register the data key '%s' after initialization phase", key.getKey()));
        }
        if(this.registeredDataKeys.containsKey(key.getKey()))
        {
            throw new IllegalArgumentException(String.format("The data key '%s' is already registered", key.getKey()));
        }
        int nextId = this.nextKeyId++;
        this.registeredDataKeys.put(key.getKey(), key);
        this.idToDataKey.put(nextId, key);
    }

    /**
     * Sets the value of a synced data key to the specified player
     *
     * @param player the player to assign the value to
     * @param key    a registered synced data key
     * @param value  a new value that matches the synced data key type
     */
    public <T> void set(Player player, SyncedDataKey<T> key, T value)
    {
        if(!this.registeredDataKeys.values().contains(key))
        {
            throw new IllegalArgumentException(String.format("The data key '%s' is not registered!", key.getKey()));
        }
        DataHolder holder = this.getDataHolder(player);
        if(holder != null && holder.set(player, key, value))
        {
            if(!player.level.isClientSide())
            {
                this.dirty = true;
            }
        }
    }

    /**
     * Gets the value for the synced data key from the specified player. It is best to check that
     * the player is alive before getting the value.
     *
     * @param player the player to retrieve the data from
     * @param key    a registered synced data key
     */
    public <T> T get(Player player, SyncedDataKey<T> key)
    {
        if(!this.registeredDataKeys.values().contains(key))
        {
            throw new IllegalArgumentException(String.format("The data key '%s' is not registered!", key.getKey()));
        }
        DataHolder holder = this.getDataHolder(player);
        return holder != null ? holder.get(key) : key.getDefaultValueSupplier().get();
    }

    @OnlyIn(Dist.CLIENT)
    public <T> void updateClientEntry(Player player, DataEntry<T> entry)
    {
        SyncedPlayerData.instance().set(player, entry.getKey(), entry.getValue());
    }

    public int getId(SyncedDataKey<?> key)
    {
        return this.idToDataKey.inverse().get(key);
    }

    @Nullable
    private SyncedDataKey<?> getKey(int id)
    {
        return this.idToDataKey.get(id);
    }

    public List<SyncedDataKey<?>> getKeys()
    {
        return ImmutableList.copyOf(this.registeredDataKeys.values());
    }

    @Nullable
    private DataHolder getDataHolder(Player player)
    {
        return player.getCapability(CAPABILITY, null).orElse(null);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player)
        {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "synced_player_data"), new Provider());
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player player && !event.getPlayer().level.isClientSide())
        {
            DataHolder holder = this.getDataHolder(player);
            if(holder != null)
            {
                List<DataEntry<?>> entries = holder.gatherAll();
                entries.removeIf(entry -> !entry.getKey().shouldSyncToAllPlayers());
                if(!entries.isEmpty())
                {
                    Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new S2CUpdatePlayerData(player.getId(), entries));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Player player && !event.getWorld().isClientSide())
        {
            DataHolder holder = this.getDataHolder(player);
            if(holder != null)
            {
                List<DataEntry<?>> entries = holder.gatherAll();
                if(!entries.isEmpty())
                {
                    Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new S2CUpdatePlayerData(player.getId(), entries));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        Player original = event.getOriginal();
        if(original.level.isClientSide())
            return;

        Player player = event.getPlayer();
        DataHolder oldHolder = this.getDataHolder(original);
        if(oldHolder == null)
            return;

        DataHolder newHolder = this.getDataHolder(player);
        if(newHolder == null)
            return;

        Map<SyncedDataKey<?>, DataEntry<?>> dataMap = new HashMap<>(oldHolder.dataMap);
        if(event.isWasDeath())
        {
            dataMap.entrySet().removeIf(entry -> !entry.getKey().isPersistent());
        }
        newHolder.dataMap = dataMap;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        if(!this.dirty)
            return;

        Player player = event.player;
        if(player.level.isClientSide())
            return;

        DataHolder holder = this.getDataHolder(player);
        if(holder == null || !holder.isDirty())
            return;

        List<DataEntry<?>> entries = holder.gatherDirty();
        if(entries.isEmpty())
            return;

        Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new S2CUpdatePlayerData(player.getId(), entries));
        List<DataEntry<?>> syncToAllEntries = entries.stream().filter(entry -> entry.getKey().shouldSyncToAllPlayers()).collect(Collectors.toList());
        if(!syncToAllEntries.isEmpty())
        {
            Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new S2CUpdatePlayerData(player.getId(), syncToAllEntries));
        }
        holder.clean();
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(this.dirty)
            {
                this.dirty = false;
            }
        }
    }

    public boolean updateMappings(S2CSyncedPlayerData message)
    {
        List<ResourceLocation> missingKeys = new ArrayList<>();
        this.idToDataKey.clear();
        Map<ResourceLocation, Integer> keyMappings = message.getKeyMap();
        for(ResourceLocation key : keyMappings.keySet())
        {
            SyncedDataKey<?> syncedDataKey = this.registeredDataKeys.get(key);
            if(syncedDataKey == null)
            {
                missingKeys.add(key);
                continue;
            }
            int id = keyMappings.get(key);
            this.idToDataKey.put(id, syncedDataKey);
        }
        if(!missingKeys.isEmpty())
        {
            String keys = missingKeys.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"));
            Framework.LOGGER.info(SYNCED_PLAYER_DATA_MARKER, "Received unknown synced keys: {}", keys);
        }
        return missingKeys.isEmpty();
    }

    public void onLoadComplete(FMLLoadCompleteEvent event)
    {
        this.loaded = true;
    }

    private static class DataHolder
    {
        private Map<SyncedDataKey<?>, DataEntry<?>> dataMap = new HashMap<>();
        private boolean dirty = false;

        @SuppressWarnings("unchecked")
        private <T> boolean set(Player player, SyncedDataKey<T> key, T value)
        {
            DataEntry<T> entry = (DataEntry<T>) this.dataMap.computeIfAbsent(key, DataEntry::new);
            if(!entry.getValue().equals(value))
            {
                boolean dirty = !player.level.isClientSide() && entry.getKey().shouldSyncToClient();
                entry.setValue(value, dirty);
                this.dirty = dirty;
                return true;
            }
            return false;
        }

        @Nullable
        @SuppressWarnings("unchecked")
        private <T> T get(SyncedDataKey<T> key)
        {
            return (T) this.dataMap.computeIfAbsent(key, DataEntry::new).getValue();
        }

        private boolean isDirty()
        {
            return this.dirty;
        }

        private void clean()
        {
            this.dirty = false;
            this.dataMap.forEach((key, entry) -> entry.clean());
        }

        private List<DataEntry<?>> gatherDirty()
        {
            return this.dataMap.values().stream().filter(DataEntry::isDirty).filter(entry -> entry.getKey().shouldSyncToClient()).collect(Collectors.toList());
        }

        private List<DataEntry<?>> gatherAll()
        {
            return this.dataMap.values().stream().filter(entry -> entry.getKey().shouldSyncToClient()).collect(Collectors.toList());
        }
    }

    public static class DataEntry<T>
    {
        private SyncedDataKey<T> key;
        private T value;
        private boolean dirty;

        private DataEntry(SyncedDataKey<T> key)
        {
            this.key = key;
            this.value = key.getDefaultValueSupplier().get();
        }

        private SyncedDataKey<T> getKey()
        {
            return this.key;
        }

        private T getValue()
        {
            return this.value;
        }

        private void setValue(T value, boolean dirty)
        {
            this.value = value;
            this.dirty = dirty;
        }

        private boolean isDirty()
        {
            return this.dirty;
        }

        private void clean()
        {
            this.dirty = false;
        }

        public void write(FriendlyByteBuf buffer)
        {
            int id = SyncedPlayerData.instance().getId(this.key);
            buffer.writeVarInt(id);
            this.key.getSerializer().write(buffer, this.value);
        }

        public static DataEntry<?> read(FriendlyByteBuf buffer)
        {
            SyncedDataKey<?> key = SyncedPlayerData.instance().getKey(buffer.readVarInt());
            Validate.notNull(key, "Synced key does not exist for id");
            DataEntry<?> entry = new DataEntry<>(key);
            entry.readValue(buffer);
            return entry;
        }

        private void readValue(FriendlyByteBuf buffer)
        {
            this.value = this.getKey().getSerializer().read(buffer);
        }

        private Tag writeValue()
        {
            return this.key.getSerializer().write(this.value);
        }

        private void readValue(Tag nbt)
        {
            this.value = this.key.getSerializer().read(nbt);
        }
    }

    public static class Provider implements ICapabilitySerializable<ListTag>
    {
        final DataHolder holder = new DataHolder();
        final LazyOptional<DataHolder> optional = LazyOptional.of(() -> this.holder);

        @Override
        public ListTag serializeNBT()
        {
            ListTag list = new ListTag();
            this.holder.dataMap.forEach((key, entry) ->
            {
                if(key.shouldSave())
                {
                    CompoundTag keyTag = new CompoundTag();
                    keyTag.putString("Key", key.getKey().toString());
                    keyTag.put("Value", entry.writeValue());
                    list.add(keyTag);
                }
            });
            return list;
        }

        @Override
        public void deserializeNBT(ListTag listTag)
        {
            this.holder.dataMap.clear();
            listTag.forEach(entryTag ->
            {
                CompoundTag keyTag = (CompoundTag) entryTag;
                ResourceLocation key = ResourceLocation.tryParse(keyTag.getString("Key"));
                Tag value = keyTag.get("Value");
                SyncedDataKey<?> syncedDataKey = SyncedPlayerData.instance().registeredDataKeys.get(key);
                if(syncedDataKey != null && syncedDataKey.shouldSave())
                {
                    DataEntry<?> entry = new DataEntry<>(syncedDataKey);
                    entry.readValue(value);
                    this.holder.dataMap.put(syncedDataKey, entry);
                }
            });
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return CAPABILITY.orEmpty(cap, this.optional);
        }
    }
}