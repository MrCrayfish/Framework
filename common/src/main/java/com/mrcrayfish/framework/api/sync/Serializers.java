package com.mrcrayfish.framework.api.sync;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
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
    public static final DataSerializer<Boolean> BOOLEAN = new DataSerializer<>(ByteBufCodecs.BOOL, (val, provider) -> ByteTag.valueOf(val), (tag, provider) -> ((ByteTag) tag).getAsByte() != 0);
    public static final DataSerializer<Byte> BYTE = new DataSerializer<>(ByteBufCodecs.BYTE, (val, provider) -> ByteTag.valueOf(val), (tag, provider) -> ((ByteTag) tag).getAsByte());
    public static final DataSerializer<Short> SHORT = new DataSerializer<>(ByteBufCodecs.SHORT, (val, provider) -> ShortTag.valueOf(val), (tag, provider) -> ((ShortTag) tag).getAsShort());
    public static final DataSerializer<Integer> INTEGER = new DataSerializer<>(ByteBufCodecs.INT, (val, provider) -> IntTag.valueOf(val), (tag, provider) -> ((IntTag) tag).getAsInt());
    public static final DataSerializer<Long> LONG = new DataSerializer<>(ByteBufCodecs.VAR_LONG, (val, provider) -> LongTag.valueOf(val), (tag, provider) -> ((LongTag) tag).getAsLong());
    public static final DataSerializer<Float> FLOAT = new DataSerializer<>(ByteBufCodecs.FLOAT, (val, provider) -> FloatTag.valueOf(val), (tag, provider) -> ((FloatTag) tag).getAsFloat());
    public static final DataSerializer<Double> DOUBLE = new DataSerializer<>(ByteBufCodecs.DOUBLE, (val, provider) -> DoubleTag.valueOf(val), (tag, provider) -> ((DoubleTag) tag).getAsDouble());
    public static final DataSerializer<String> STRING = new DataSerializer<>(ByteBufCodecs.STRING_UTF8, (val, provider) -> StringTag.valueOf(val), (tag, provider) -> tag.getAsString());
    public static final DataSerializer<CompoundTag> COMPOUND_TAG = new DataSerializer<>(ByteBufCodecs.COMPOUND_TAG, (val, provider) -> val, (tag, provider) -> (CompoundTag) tag);
    public static final DataSerializer<Optional<CompoundTag>> OPTIONAL_COMPOUND_TAG = new DataSerializer<>(ByteBufCodecs.OPTIONAL_COMPOUND_TAG, (val, provider) -> val.orElse(null), (tag, provider) -> tag != null ? Optional.of((CompoundTag) tag) : Optional.empty());
    public static final DataSerializer<BlockPos> BLOCK_POS = new DataSerializer<>(BlockPos.STREAM_CODEC, (val, provider) -> LongTag.valueOf(val.asLong()), (tag, provider) -> BlockPos.of(((LongTag) tag).getAsLong()));
    public static final DataSerializer<UUID> UUID = new DataSerializer<>(UUIDUtil.STREAM_CODEC, (val, provider) -> NbtUtils.createUUID(val), (tag, provider) -> NbtUtils.loadUUID(tag));
    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(ItemStack.STREAM_CODEC, ItemStack::saveOptional, (tag, provider) -> ItemStack.parse(provider, tag).orElse(ItemStack.EMPTY));
    public static final DataSerializer<ItemStack> ITEM_STACK_NON_EMPTY = new DataSerializer<>(ItemStack.STREAM_CODEC, ItemStack::saveOptional, (tag, provider) -> ItemStack.parse(provider, tag).orElse(ItemStack.EMPTY));
    public static final DataSerializer<ResourceLocation> RESOURCE_LOCATION = new DataSerializer<>(ResourceLocation.STREAM_CODEC, (val, provider) -> StringTag.valueOf(val.toString()), (tag, provider) -> ResourceLocation.tryParse(tag.getAsString()));
}
