package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.FrameworkNeoForge;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

/**
 * Author: MrCrayfish
 */
public class DataHolderSerializer implements IAttachmentSerializer<DataHolder>
{
    @Override
    public DataHolder read(IAttachmentHolder holder, ValueInput input)
    {
        DataHolder data = holder.getData(FrameworkNeoForge.DATA_HOLDER); // Do this to trigger default supplier
        data.deserialize(input);
        return data;
    }

    @Override
    public boolean write(DataHolder holder, ValueOutput output)
    {
        return holder.serialize(output);
    }
}
