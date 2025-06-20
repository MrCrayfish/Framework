package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.entity.sync.DataHolder;
import com.mrcrayfish.framework.entity.sync.ISyncedDataHolder;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(Entity.class)
public class EntityMixin implements ISyncedDataHolder
{
    @Unique
    @Nullable
    private DataHolder holder;

    @Override
    @SuppressWarnings("DataFlowIssue")
    public DataHolder framework$GetDataHolder()
    {
        if(this.holder == null)
        {
            Entity entity = (Entity) (Object) this;
            if(SyncedEntityData.instance().hasSyncedDataKey(entity))
            {
                this.holder = new DataHolder(entity);
            }
            else
            {
                this.holder = DataHolder.EMPTY;
            }
        }
        return this.holder;
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"))
    private void frameworkOnLoadData(ValueInput input, CallbackInfo ci)
    {
        ValueInput holderInput = input.childOrEmpty("FrameworkDataHolder");
        this.framework$GetDataHolder().deserialize(holderInput);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"))
    private void frameworkOnSaveData(ValueOutput output, CallbackInfo ci)
    {
        ValueOutput holderOutput = output.child("FrameworkDataHolder");
        this.framework$GetDataHolder().serialize(holderOutput);
    }
}
