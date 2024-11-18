package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.client.model.geometry.OpenModelGeometry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
@Mixin(BlockModel.class)
public class BlockModelMixin
{
    // TODO remove once Forge fixes custom geometry baking (https://github.com/MinecraftForge/MinecraftForge/issues/10178)
    @Inject(method = "Lnet/minecraft/client/renderer/block/model/BlockModel;bake(Lnet/minecraft/client/resources/model/ModelBaker;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At(value = "HEAD"), cancellable = true)
    private void openModelBake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, CallbackInfoReturnable<BakedModel> cir)
    {
        // Only patched for Framework Open Model
        BlockModel model = (BlockModel) (Object) this;
        if(model.customData.getCustomGeometry() instanceof OpenModelGeometry geometry)
        {
            cir.setReturnValue(geometry.bake(model.customData, baker, spriteGetter, state));
        }
    }
}
