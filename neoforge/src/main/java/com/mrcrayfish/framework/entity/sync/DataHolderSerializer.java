package com.mrcrayfish.framework.entity.sync;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

/**
 * Author: MrCrayfish
 */
public class DataHolderSerializer implements IAttachmentSerializer<ListTag, DataHolder>
{
    @Override
    public DataHolder read(IAttachmentHolder holder, ListTag list, HolderLookup.Provider provider)
    {
        DataHolder data = new DataHolder();
        data.deserialize(list, provider);
        return data;
    }

    @Override
    public ListTag write(DataHolder holder, HolderLookup.Provider provider)
    {
        return holder.serialize(provider);
    }
}
