package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.entity.sync.DataEntry;
import com.mrcrayfish.framework.network.message.configuration.S2CSyncedEntityData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class FrameworkCodecs
{
    public static final StreamCodec<FriendlyByteBuf, byte[]> BYTE_ARRAY = new StreamCodec<>()
    {
        @Override
        public byte[] decode(FriendlyByteBuf buf)
        {
            return buf.readByteArray();
        }

        @Override
        public void encode(FriendlyByteBuf buf, byte[] data)
        {
            buf.writeByteArray(data);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<DataEntry<?, ?>>> DATA_ENTRIES = new StreamCodec<>()
    {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, List<DataEntry<?, ?>> entries)
        {
            buf.writeVarInt(entries.size());
            entries.forEach(entry -> entry.write(buf));
        }

        @Override
        public List<DataEntry<?, ?>> decode(RegistryFriendlyByteBuf buf)
        {
            int size = buf.readVarInt();
            List<DataEntry<?, ?>> entries = new ArrayList<>();
            for(int i = 0; i < size; i++)
            {
                entries.add(DataEntry.read(buf));
            }
            return entries;
        }
    };

    public static final StreamCodec<FriendlyByteBuf, Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>>> ENTITY_DATA_KEYS = new StreamCodec<>()
    {
        @Override
        public void encode(FriendlyByteBuf buf, Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> map)
        {
            buf.writeInt(map.size());
            map.forEach((classId, value) -> {
                buf.writeResourceLocation(classId);
                buf.writeVarInt(value.size());
                value.forEach(pair -> {
                    buf.writeResourceLocation(pair.getKey());
                    buf.writeVarInt(pair.getValue());
                });
            });
        }

        @Override
        public Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> decode(FriendlyByteBuf buf)
        {
            Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> map = new HashMap<>();
            int keySize = buf.readInt();
            for(int i = 0; i < keySize; i++)
            {
                ResourceLocation classId = buf.readResourceLocation();
                int entrySize = buf.readVarInt();
                for(int j = 0; j < entrySize; j++)
                {
                    ResourceLocation keyId = buf.readResourceLocation();
                    int id = buf.readVarInt();
                    map.computeIfAbsent(classId, c -> new ArrayList<>()).add(Pair.of(keyId, id));
                }
            }
            return map;
        }
    };
}
