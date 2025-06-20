package com.mrcrayfish.framework.api.sync;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

/**
 * Framework provided serializers used for creating a {@link SyncedDataKey}. This covers all
 * primitive types and common objects. You can create your custom serializer by implementing
 * {@link DataSerializer}.
 * <p>
 * Author: MrCrayfish
 */
public class Serializers
{
    public static final DataSerializer<Boolean> BOOLEAN = new DataSerializer<>(ByteBufCodecs.BOOL, Codec.BOOL);
    public static final DataSerializer<Byte> BYTE = new DataSerializer<>(ByteBufCodecs.BYTE, Codec.BYTE);
    public static final DataSerializer<Short> SHORT = new DataSerializer<>(ByteBufCodecs.SHORT, Codec.SHORT);
    public static final DataSerializer<Integer> INTEGER = new DataSerializer<>(ByteBufCodecs.INT, Codec.INT);
    public static final DataSerializer<Long> LONG = new DataSerializer<>(ByteBufCodecs.VAR_LONG, Codec.LONG);
    public static final DataSerializer<Float> FLOAT = new DataSerializer<>(ByteBufCodecs.FLOAT, Codec.FLOAT);
    public static final DataSerializer<Double> DOUBLE = new DataSerializer<>(ByteBufCodecs.DOUBLE, Codec.DOUBLE);
    public static final DataSerializer<String> STRING = new DataSerializer<>(ByteBufCodecs.STRING_UTF8, Codec.STRING);
    public static final DataSerializer<CompoundTag> COMPOUND_TAG = new DataSerializer<>(ByteBufCodecs.COMPOUND_TAG, CompoundTag.CODEC);
    public static final DataSerializer<Optional<CompoundTag>> OPTIONAL_COMPOUND_TAG = new DataSerializer<>(ByteBufCodecs.OPTIONAL_COMPOUND_TAG, ExtraCodecs.optionalEmptyMap(CompoundTag.CODEC));
    public static final DataSerializer<BlockPos> BLOCK_POS = new DataSerializer<>(BlockPos.STREAM_CODEC, BlockPos.CODEC);
    public static final DataSerializer<UUID> UUID = new DataSerializer<>(UUIDUtil.STREAM_CODEC, UUIDUtil.CODEC);
    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(ItemStack.STREAM_CODEC, ItemStack.OPTIONAL_CODEC);
    public static final DataSerializer<ItemStack> ITEM_STACK_NON_EMPTY = new DataSerializer<>(ItemStack.STREAM_CODEC, ItemStack.CODEC);
    public static final DataSerializer<ResourceLocation> RESOURCE_LOCATION = new DataSerializer<>(ResourceLocation.STREAM_CODEC, ResourceLocation.CODEC);
}
