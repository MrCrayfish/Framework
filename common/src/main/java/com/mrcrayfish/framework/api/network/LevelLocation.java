package com.mrcrayfish.framework.api.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public final class LevelLocation
{
    private final ServerLevel level;
    private final Vec3 pos;
    private final double range;

    private LevelLocation(ServerLevel level, Vec3 pos, double range)
    {
        this.level = level;
        this.pos = pos;
        this.range = range;
    }

    public ServerLevel level()
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

    public static LevelLocation create(ServerLevel level, BlockPos pos)
    {
        return new LevelLocation(level, pos.getCenter(), 16);
    }

    public static LevelLocation create(ServerLevel level, BlockPos pos, double range)
    {
        return new LevelLocation(level, pos.getCenter(), range);
    }

    public static LevelLocation create(ServerLevel level, Vec3 pos, double range)
    {
        return new LevelLocation(level, pos, range);
    }

    public static LevelLocation create(ServerLevel level, double x, double y, double z, double range)
    {
        return new LevelLocation(level, new Vec3(x, y, z), range);
    }
}
