package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ForgeSyncedEntityDataHandler
{
    public static final Capability<DataHolder> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(DataHolder.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if(SyncedEntityData.instance().hasSyncedDataKey(event.getObject()))
        {
            Provider provider = new Provider(event.getObject());
            event.addCapability(Utils.rl("synced_entity_data"), provider);
            if(!(event.getObject() instanceof ServerPlayer)) // Don't add invalidate to server player since it's persistent
            {
                event.addListener(provider::invalidate);
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<ListTag>
    {
        final Entity entity;
        final DataHolder holder;
        final LazyOptional<DataHolder> optional;

        public Provider(Entity entity)
        {
            this.entity = entity;
            this.holder = new DataHolder().setup(entity);
            this.optional = LazyOptional.of(() -> this.holder);
        }

        public void invalidate()
        {
            this.optional.invalidate();
        }

        @Override
        public ListTag serializeNBT(HolderLookup.Provider provider)
        {
            return this.holder.serialize(provider);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, ListTag tag)
        {
            this.holder.deserialize(tag, provider);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return CAPABILITY.orEmpty(cap, this.optional);
        }
    }
}
