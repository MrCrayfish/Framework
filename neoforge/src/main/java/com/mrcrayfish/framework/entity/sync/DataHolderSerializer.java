package com.mrcrayfish.framework.entity.sync;

import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

/**
 * Author: MrCrayfish
 */
public class DataHolderSerializer implements IAttachmentSerializer<ListTag, DataHolder>
{
    @Override
    public DataHolder read(ListTag list)
    {
        DataHolder holder = new DataHolder();
        holder.deserialize(list);
        return holder;
    }

    @Override
    public ListTag write(DataHolder holder)
    {
        return holder.serialize();
    }
}
