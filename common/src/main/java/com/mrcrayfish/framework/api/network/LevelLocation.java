package com.mrcrayfish.framework.api.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public final class LevelLocation
{
    private final Level level;
    private final Vec3 pos;
    private final double range;

    private LevelLocation(Level level, Vec3 pos, double range)
    {
        this.level = level;
        this.pos = pos;
        this.range = range;
    }

    public Level level()
    {
        return this.level;
    }

    public Vec3 pos()
    {
        return this.pos;
    }

    public double range()
    {
        return this.range;
    }

    public static LevelLocation create(Level level, BlockPos pos)
    {
        return new LevelLocation(level, Vec3.atCenterOf(pos), 16);
    }

    public static LevelLocation create(Level level, BlockPos pos, double range)
    {
        return new LevelLocation(level, Vec3.atCenterOf(pos), range);
    }

    public static LevelLocation create(Level level, Vec3 pos, double range)
    {
        return new LevelLocation(level, pos, range);
    }

    public static LevelLocation create(Level level, double x, double y, double z, double range)
    {
        return new LevelLocation(level, new Vec3(x, y, z), range);
    }
}
