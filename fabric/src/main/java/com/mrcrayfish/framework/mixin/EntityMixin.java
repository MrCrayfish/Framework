package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.entity.sync.DataHolder;
import com.mrcrayfish.framework.entity.sync.ISyncedDataHolder;
import com.mrcrayfish.framework.entity.sync.LazyDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
@Mixin(Entity.class)
public class EntityMixin implements ISyncedDataHolder
{
    @Unique
    @Nullable
    private LazyDataHolder frameworkLazyDataHolder;

    @Nullable
    @Override
    @SuppressWarnings("DataFlowIssue")
    public DataHolder framework$GetDataHolder()
    {
        if(this.frameworkLazyDataHolder == null)
        {
            Entity entity = (Entity) (Object) this;
            this.frameworkLazyDataHolder = new LazyDataHolder(new CompoundTag(), entity);
        }
        return this.frameworkLazyDataHolder.get();
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void frameworkOnLoadData(CompoundTag tag, CallbackInfo ci)
    {
        Entity entity = (Entity) (Object) this;
        this.frameworkLazyDataHolder = new LazyDataHolder(tag.getCompound("FrameworkDataHolder"), entity);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void frameworkOnSaveData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir)
    {
        if(this.frameworkLazyDataHolder != null)
        {
            Entity entity = (Entity) (Object) this;
            tag.put("FrameworkDataHolder", this.frameworkLazyDataHolder.serialize());
        }
    }
}
