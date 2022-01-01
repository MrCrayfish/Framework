package com.mrcrayfish.framework.common.data;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.Reference;
import com.mrcrayfish.framework.api.data.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.data.sync.SyncedDataKey;
import com.mrcrayfish.framework.network.Network;
import com.mrcrayfish.framework.network.message.handshake.S2CSyncedEntityData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
public class SyncedEntityData
{
    private static final Marker SYNCED_ENTITY_DATA_MARKER = MarkerManager.getMarker("SYNCED_ENTITY_DATA");
    private static final Capability<DataHolder> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private static SyncedEntityData instance;

    private final Set<SyncedClassKey<?>> registeredClassKeys = new HashSet<>();
    private final Object2ReferenceMap<ResourceLocation, SyncedClassKey<?>> idToClassKey = new Object2ReferenceOpenHashMap<>();
    private final Reference2ReferenceMap<Class<?>, SyncedClassKey<?>> classToClassKey = new Reference2ReferenceOpenHashMap<>();
    private final Reference2BooleanMap<Class<?>> classCapabilityCache = new Reference2BooleanOpenHashMap<>();

    private final Set<SyncedDataKey<?, ?>> registeredDataKeys = new HashSet<>();
    private final Reference2ObjectMap<SyncedClassKey<?>, HashMap<ResourceLocation, SyncedDataKey<?, ?>>> classToKeys = new Reference2ObjectOpenHashMap<>();
    private final Reference2IntMap<SyncedDataKey<?, ?>> internalIds = new Reference2IntOpenHashMap<>();
    private final Int2ReferenceMap<SyncedDataKey<?, ?>> syncedIdToKey = new Int2ReferenceOpenHashMap<>();

    private final AtomicInteger nextIdTracker = new AtomicInteger();
    private final List<Entity> dirtyEntities = new ArrayList<>();
    private boolean dirty = false;

    private SyncedEntityData() {}

    public static SyncedEntityData instance()
    {
        if(instance == null)
        {
            instance = new SyncedEntityData();
        }
        return instance;
    }

    public static void onRegisterCapability(RegisterCapabilitiesEvent event)
    {
        event.register(DataHolder.class);
    }

    private <E extends Entity> void registerClassKey(SyncedClassKey<E> classKey)
    {
        if(!this.registeredClassKeys.contains(classKey))
        {
            this.registeredClassKeys.add(classKey);
            this.idToClassKey.put(classKey.id(), classKey);
            this.classToClassKey.put(classKey.entityClass(), classKey);
        }
    }

    /**
     * Registers a synced data key into the system.
     *
     * @param dataKey a synced data key instance
     */
    public synchronized <E extends Entity, T> void registerDataKey(SyncedDataKey<E, T> dataKey)
    {
        ResourceLocation keyId = dataKey.id();
        SyncedClassKey<E> classKey = dataKey.classKey();
        if(Framework.isGameLoaded())
        {
            throw new IllegalStateException(String.format("Tried to register synced data key %s for %s after game initialization", keyId, classKey.id()));
        }
        if(this.registeredDataKeys.contains(dataKey))
        {
            throw new IllegalArgumentException(String.format("The synced data key %s for %s is already registered", keyId, classKey.id()));
        }
        this.registerClassKey(dataKey.classKey()); // Attempt to register the class key. Will ignore if already registered.
        this.registeredDataKeys.add(dataKey);
        this.classToKeys.computeIfAbsent(classKey, c -> new HashMap<>()).put(keyId, dataKey);
        int nextId = this.nextIdTracker.getAndIncrement();
        this.internalIds.put(dataKey, nextId);
        this.syncedIdToKey.put(nextId, dataKey);
        Framework.LOGGER.info(SYNCED_ENTITY_DATA_MARKER, "Registered synced data key {} for {}", dataKey.id(), classKey.id());
    }

    /**
     * Sets the value of a synced data key to the specified player
     *
     * @param entity the player to assign the value to
     * @param key    a registered synced data key
     * @param value  a new value that matches the synced data key type
     */
    public <E extends Entity, T> void set(E entity, SyncedDataKey<?, ?> key, T value)
    {
        if(!this.registeredDataKeys.contains(key))
        {
            throw new IllegalArgumentException(String.format("The synced data key %s for %s is not registered!", key.id(), key.classKey().id()));
        }
        DataHolder holder = this.getDataHolder(entity);
        if(holder != null && holder.set(entity, key, value))
        {
            if(!entity.level.isClientSide())
            {
                this.dirty = true;
                this.dirtyEntities.add(entity);
            }
        }
    }

    /**
     * Gets the value for the synced data key from the specified player. It is best to check that
     * the player is alive before getting the value.
     *
     * @param entity the player to retrieve the data from
     * @param key    a registered synced data key
     */
    public <E extends Entity, T> T get(E entity, SyncedDataKey<E, T> key)
    {
        if(!this.registeredDataKeys.contains(key))
        {
            throw new IllegalArgumentException(String.format("The synced data key %s for %s is not registered!", key.id(), key.classKey().id()));
        }
        DataHolder holder = this.getDataHolder(entity);
        return holder != null ? holder.get(key) : key.defaultValueSupplier().get();
    }

    @OnlyIn(Dist.CLIENT)
    public <E extends Entity, T> void updateClientEntry(Entity entity, DataEntry<E, T> entry)
    {
        SyncedEntityData.instance().set(entity, entry.getKey(), entry.getValue());
    }

    public int getInternalId(SyncedDataKey<?, ?> key)
    {
        return this.internalIds.get(key);
    }

    @Nullable
    private SyncedDataKey<?, ?> getKey(int id)
    {
        return this.syncedIdToKey.get(id);
    }

    public Set<SyncedDataKey<?, ?>> getKeys()
    {
        return ImmutableSet.copyOf(this.registeredDataKeys);
    }

    @Nullable
    private DataHolder getDataHolder(Entity entity)
    {
        return entity.getCapability(CAPABILITY, null).orElse(null);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if(this.hasSyncedDataKey(event.getObject().getClass()))
        {
            Provider provider = new Provider();
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "synced_entity_data"), provider);
            event.addListener(provider::invalidate);
        }
    }

    private boolean hasSyncedDataKey(Class<? extends Entity> entityClass)
    {
        /* It's possible that the entity doesn't have a key but it's superclass or subsequent does
         * have a synced data key. In order to prevent checking this every time we attach the
         * capability, a simple one time check can be performed then cache the result. */
        return this.classCapabilityCache.computeIfAbsent(entityClass, c ->
        {
            Class<?> targetClass = entityClass;
            while(targetClass != CapabilityProvider.class) // Should be good enough
            {
                if(this.classToClassKey.containsKey(targetClass))
                {
                    return true;
                }
                targetClass = targetClass.getSuperclass();
            }
            return false;
        });
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(!event.getPlayer().level.isClientSide())
        {
            Entity entity = event.getTarget();
            DataHolder holder = this.getDataHolder(entity);
            if(holder != null)
            {
                List<DataEntry<?, ?>> entries = holder.gatherAll();
                entries.removeIf(entry -> !entry.getKey().syncMode().isTracking());
                if(!entries.isEmpty())
                {
                    Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new S2CUpdateEntityData(entity.getId(), entries));
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
                List<DataEntry<?, ?>> entries = holder.gatherAll();
                if(!entries.isEmpty())
                {
                    Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new S2CUpdateEntityData(player.getId(), entries));
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

        Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>(oldHolder.dataMap);
        if(event.isWasDeath())
        {
            dataMap.entrySet().removeIf(entry -> !entry.getKey().persistent());
        }
        newHolder.dataMap = dataMap;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.side != LogicalSide.SERVER)
            return;

        if(event.phase != TickEvent.Phase.END)
            return;

        if(!this.dirty)
            return;

        if(this.dirtyEntities.isEmpty())
        {
            this.dirty = false;
            return;
        }

        for(Entity entity : this.dirtyEntities)
        {
            DataHolder holder = this.getDataHolder(entity);
            if(holder == null || !holder.isDirty())
                continue;

            List<DataEntry<?, ?>> entries = holder.gatherDirty();
            if(entries.isEmpty())
                continue;

            List<DataEntry<?, ?>> selfEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isSelf()).collect(Collectors.toList());
            if(!selfEntries.isEmpty() && entity instanceof ServerPlayer)
            {
                Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new S2CUpdateEntityData(entity.getId(), selfEntries));
            }

            List<DataEntry<?, ?>> trackingEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isTracking()).collect(Collectors.toList());
            if(!trackingEntries.isEmpty())
            {
                Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new S2CUpdateEntityData(entity.getId(), trackingEntries));
            }
            holder.clean();
        }
        this.dirtyEntities.clear();
        this.dirty = false;
    }

    public boolean updateMappings(S2CSyncedEntityData message)
    {
        this.syncedIdToKey.clear();

        List<Pair<ResourceLocation, ResourceLocation>> missingKeys = new ArrayList<>();
        message.getKeyMap().forEach((classId, list) ->
        {
            SyncedClassKey<?> classKey = this.idToClassKey.get(classId);
            if(classKey == null || !this.classToKeys.containsKey(classKey))
            {
                list.forEach(pair -> missingKeys.add(Pair.of(classId, pair.getLeft())));
                return;
            }

            Map<ResourceLocation, SyncedDataKey<?, ?>> keys = this.classToKeys.get(classKey);
            list.forEach(pair ->
            {
                SyncedDataKey<?, ?> syncedDataKey = keys.get(pair.getLeft());
                if(syncedDataKey == null)
                {
                    missingKeys.add(Pair.of(classId, pair.getLeft()));
                    return;
                }
                this.syncedIdToKey.put((int) pair.getRight(), syncedDataKey);
            });
        });

        if(!missingKeys.isEmpty())
        {
            String keys = missingKeys.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"));
            Framework.LOGGER.info(SYNCED_ENTITY_DATA_MARKER, "Received unknown synced keys: {}", keys);
        }

        return missingKeys.isEmpty();
    }

    private static class DataHolder
    {
        private Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>();
        private boolean dirty = false;

        @SuppressWarnings("unchecked")
        private <E extends Entity, T> boolean set(E entity, SyncedDataKey<?, ?> key, T value)
        {
            DataEntry<E, T> entry = (DataEntry<E, T>) this.dataMap.computeIfAbsent(key, DataEntry::new);
            if(!entry.getValue().equals(value))
            {
                boolean dirty = !entity.level.isClientSide() && entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE;
                entry.setValue(value, dirty);
                this.dirty = dirty;
                return true;
            }
            return false;
        }

        @Nullable
        @SuppressWarnings("unchecked")
        private <E extends Entity, T> T get(SyncedDataKey<E, T> key)
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

        private List<DataEntry<?, ?>> gatherDirty()
        {
            return this.dataMap.values().stream().filter(DataEntry::isDirty).filter(entry -> entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE).collect(Collectors.toList());
        }

        private List<DataEntry<?, ?>> gatherAll()
        {
            return this.dataMap.values().stream().filter(entry -> entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE).collect(Collectors.toList());
        }
    }

    public static class DataEntry<E extends Entity, T>
    {
        private final SyncedDataKey<E, T> key;
        private T value;
        private boolean dirty;

        private DataEntry(SyncedDataKey<E, T> key)
        {
            this.key = key;
            this.value = key.defaultValueSupplier().get();
        }

        private SyncedDataKey<E, T> getKey()
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
            int id = SyncedEntityData.instance().getInternalId(this.key);
            buffer.writeVarInt(id);
            this.key.serializer().write(buffer, this.value);
        }

        public static DataEntry<?, ?> read(FriendlyByteBuf buffer)
        {
            SyncedDataKey<?, ?> key = SyncedEntityData.instance().getKey(buffer.readVarInt());
            Validate.notNull(key, "Synced key does not exist for id");
            DataEntry<?, ?> entry = new DataEntry<>(key);
            entry.readValue(buffer);
            return entry;
        }

        private void readValue(FriendlyByteBuf buffer)
        {
            this.value = this.getKey().serializer().read(buffer);
        }

        private Tag writeValue()
        {
            return this.key.serializer().write(this.value);
        }

        private void readValue(Tag nbt)
        {
            this.value = this.key.serializer().read(nbt);
        }
    }

    public static class Provider implements ICapabilitySerializable<ListTag>
    {
        final DataHolder holder = new DataHolder();
        final LazyOptional<DataHolder> optional = LazyOptional.of(() -> this.holder);

        public void invalidate()
        {
            this.optional.invalidate();
        }

        @Override
        public ListTag serializeNBT()
        {
            ListTag list = new ListTag();
            this.holder.dataMap.forEach((key, entry) ->
            {
                if(key.save())
                {
                    CompoundTag keyTag = new CompoundTag();
                    keyTag.putString("ClassKey", key.classKey().id().toString());
                    keyTag.putString("DataKey", key.id().toString());
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
                ResourceLocation classKey = ResourceLocation.tryParse(keyTag.getString("ClassKey"));
                ResourceLocation dataKey = ResourceLocation.tryParse(keyTag.getString("DataKey"));
                Tag value = keyTag.get("Value");

                SyncedClassKey<?> syncedClassKey = SyncedEntityData.instance().idToClassKey.get(classKey);
                if(syncedClassKey == null)
                    return;

                Map<ResourceLocation, SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().classToKeys.get(syncedClassKey);
                if(keys == null)
                    return;

                SyncedDataKey<?, ?> syncedDataKey = keys.get(dataKey);
                if(syncedDataKey == null || !syncedDataKey.save())
                    return;

                DataEntry<?, ?> entry = new DataEntry<>(syncedDataKey);
                entry.readValue(value);
                this.holder.dataMap.put(syncedDataKey, entry);
            });
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return this.optional.cast();
        }
    }
}