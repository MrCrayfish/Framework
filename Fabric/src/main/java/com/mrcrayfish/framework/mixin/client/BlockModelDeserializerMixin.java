package com.mrcrayfish.framework.mixin.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.ClientFrameworkFabric;
import com.mrcrayfish.framework.client.model.FabricOpenBlockModel;
import com.mrcrayfish.framework.client.model.OpenModelDeserializer;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin
{
    // Unfortunately Fabric doesn't have any way to create custom loaders

    @SuppressWarnings("unchecked")
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void frameworkCreateBlockModel(JsonElement element, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockModel> cir, JsonObject object, List elements, String string, Map materials, boolean ambientOcc, ItemTransforms transforms, List overrides, BlockModel.GuiLight light, ResourceLocation resourceLocation)
    {
        if(this.isFrameworkOpenModel(object))
        {
            cir.setReturnValue(new FabricOpenBlockModel(resourceLocation, elements, materials, ambientOcc, light, transforms, overrides, DataObject.convert(object.get("data"))));
        }
    }

    @Inject(method = "getElements", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void frameworkOpenModelLoadElements(JsonDeserializationContext context, JsonObject object, CallbackInfoReturnable<List<BlockElement>> cir)
    {
        if(this.isFrameworkOpenModel(object))
        {
            cir.setReturnValue(OpenModelDeserializer.INSTANCE.getElements(context, object));
        }
    }
    
    private boolean isFrameworkOpenModel(JsonObject object)
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
