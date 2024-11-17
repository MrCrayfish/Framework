package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.client.StandaloneModelManager;
import com.mrcrayfish.framework.platform.ForgeClientHelper;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

/**
 * Author: MrCrayfish
 */
@Mixin(ModelDiscovery.class)
public abstract class ModelDiscoveryMixin
{
    @Shadow
    abstract void registerTopModel(ModelResourceLocation location, UnbakedModel model);

    @Shadow
    abstract UnbakedModel getBlockModel(ResourceLocation location);

    // TODO axe once Forge adds back in RegisterAdditional event
    @Inject(method = "listMandatoryModels", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onGatherModels(CallbackInfoReturnable<Set<ModelResourceLocation>> ci, Set<ModelResourceLocation> set)
    {
        StandaloneModelManager.getInstance().load(set::add);
    }

    @Inject(method = "registerStandardModels", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onRegisterModels(BlockStateModelLoader.LoadedModels loadedModels, CallbackInfo info, Set<ModelResourceLocation> set)
    {
        set.removeIf(model -> {
            if(model.variant().equals(ForgeClientHelper.STANDALONE_VARIANT)) {
                this.registerTopModel(model, this.getBlockModel(model.id()));
                return true;
            }
            return false;
        });
    }
}
