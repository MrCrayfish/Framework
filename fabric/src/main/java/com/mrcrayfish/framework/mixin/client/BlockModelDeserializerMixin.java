package com.mrcrayfish.framework.mixin.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mrcrayfish.framework.client.ClientFrameworkFabric;
import com.mrcrayfish.framework.client.model.OpenModelDeserializer;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin
{
    // Unfortunately Fabric doesn't have any way to create custom loaders
    /*@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At(value = "RETURN"), cancellable = true)
    private void frameworkCreateBlockModel(JsonElement element, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockModel> cir, @Local JsonObject jsonObject, @Local UnbakedGeometry unbakedGeometry, @Local TextureSlots.Data data, @Local Boolean boolean_, @Local UnbakedModel.GuiLight guiLight, @Local ItemTransforms itemTransforms, @Local ResourceLocation resourceLocation)
    {
        if(this.isFrameworkOpenModel(jsonObject))
        {
            cir.setReturnValue(new OpenBlockModel(unbakedGeometry, guiLight, boolean_, itemTransforms, data, resourceLocation, DataObject.convertNonNull(jsonObject.get("data"))));
        }
    }*/

    @Inject(method = "getElements", at = @At(value = "HEAD"), cancellable = true)
    private void frameworkOpenModelLoadElements(JsonDeserializationContext context, JsonObject object, CallbackInfoReturnable<UnbakedGeometry> cir)
    {
        if(this.isOpenModel(object))
        {
            cir.setReturnValue(OpenModelDeserializer.INSTANCE.getElements(context, object));
        }
    }
    
    @Unique
    private boolean isOpenModel(JsonObject object)
    {
        if(object.has("loader") && object.get("loader").isJsonPrimitive())
        {
            JsonPrimitive primitive = object.getAsJsonPrimitive("loader");
            if(!primitive.isString())
                return false;

            String rawLoader = primitive.getAsString();
            ResourceLocation loader = ResourceLocation.tryParse(rawLoader);
            return ClientFrameworkFabric.OPEN_MODEL_ID.equals(loader);
        }
        return false;
    }
}
