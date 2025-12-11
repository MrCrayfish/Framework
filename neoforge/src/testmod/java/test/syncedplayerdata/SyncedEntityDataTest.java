package test.syncedplayerdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.sync.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
@Mod("synced_entity_data_test")
public class SyncedEntityDataTest
{
    private BlockPos lastClickedPos = BlockPos.ZERO;

    private static final SyncedDataKey<Player, Boolean> TOUCHED_GRASS = SyncedDataKey.builder(SyncedClassKey.PLAYER, Serializers.BOOLEAN)
            .id(Identifier.fromNamespaceAndPath("synced_entity_data_test", "touched_grass"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .saveToFile()
            .syncMode(SyncedDataKey.SyncMode.SELF_ONLY)
            .build();

    private static final SyncedDataKey<Animal, Integer> HIT_COUNT = SyncedDataKey.builder(SyncedClassKey.ANIMAL, Serializers.INTEGER)
            .id(Identifier.fromNamespaceAndPath("synced_entity_data_test", "hit_count"))
            .defaultValueSupplier(() -> 0)
            .saveToFile()
            .syncMode(SyncedDataKey.SyncMode.TRACKING_ONLY)
            .build();

    private static final SyncedDataKey<Zombie, TestCounter> STRIKE_COUNT = SyncedDataKey.builder(SyncedClassKey.ZOMBIE, TestCounter.SERIALIZER)
        .id(Identifier.fromNamespaceAndPath("synced_entity_data_test", "strike_count"))
        .defaultValueSupplier(() -> new TestCounter(0))
        .saveToFile()
        .syncMode(SyncedDataKey.SyncMode.TRACKING_ONLY)
        .build();

    public SyncedEntityDataTest(IEventBus bus)
    {
        NeoForge.EVENT_BUS.addListener(this::onTouchBlock);
        NeoForge.EVENT_BUS.addListener(this::onHitEntity);
    }

    private void onTouchBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getSide() != LogicalSide.SERVER)
            return;

        if(this.lastClickedPos.equals(event.getPos()))
            return;

        Player player = event.getEntity();
        BlockState state = player.level().getBlockState(event.getPos());
        if(state.getBlock() == Blocks.GRASS_BLOCK)
        {
            this.lastClickedPos = event.getPos();
            if(TOUCHED_GRASS.getValue(player))
            {
                player.displayClientMessage(Component.literal("You've already touched grass!"), true);
            }
            else
            {
                TOUCHED_GRASS.setValue(player, true);
                player.displayClientMessage(Component.literal("Well done, you've finally touched grass!"), true);
            }
        }
    }

    private void onHitEntity(AttackEntityEvent event)
    {
        if(event.getTarget() instanceof Pig animal && !animal.level().isClientSide())
        {
            int newCount = HIT_COUNT.getValue(animal) + 1;
            HIT_COUNT.setValue(animal, newCount);
            event.getEntity().displayClientMessage(Component.literal("This animal has been hit " + newCount + " times!"), true);
        }

        if(event.getTarget() instanceof Zombie zombie && !zombie.level().isClientSide())
        {
            TestCounter counter = STRIKE_COUNT.getValue(zombie);
            counter.increment();
            event.getEntity().displayClientMessage(Component.literal("This zombie has been hit " + counter.getCount() + " times!"), true);
        }
    }

    private static class TestCounter extends SyncedObject
    {
        public static final Codec<TestCounter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("Count").forGetter(testCounter -> testCounter.count)
        ).apply(instance, TestCounter::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, TestCounter> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            TestCounter::getCount,
            TestCounter::new
        );
        public static final DataSerializer<TestCounter> SERIALIZER = new DataSerializer<>(STREAM_CODEC, CODEC);

        private int count;

        public TestCounter(int count)
        {
            this.count = count;
        }

        public void increment()
        {
            this.count++;
            this.markDirty();
        }

        public int getCount()
        {
            return count;
        }

        private Tag write(HolderLookup.Provider provider)
        {
            return IntTag.valueOf(this.count);
        }

        private static TestCounter read(Tag tag, HolderLookup.Provider provider)
        {
            return new TestCounter(((IntTag) tag).intValue());
        }
    }
}
