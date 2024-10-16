package test.registry;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatFormatter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Author: MrCrayfish
 */
@Mod("registry_test")
@RegistryContainer
public class RegistryTest
{
    public static final RegistryEntry<Block> MY_AWESOME_BLOCK = RegistryEntry.blockWithItem(ResourceLocation.fromNamespaceAndPath("registry_test", "awesome_block"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final RegistryEntry<ResourceLocation> CUSTOM_AWESOME_STAT = RegistryEntry.customStat(ResourceLocation.fromNamespaceAndPath("registry_test", "awesome_stat"), StatFormatter.DEFAULT);

    public static final RegistryEntry<CreativeModeTab> CUSTOM_TAB = RegistryEntry.creativeModeTab(ResourceLocation.fromNamespaceAndPath("registry_test", "tab"), builder -> {
        builder.title(Component.literal("Creative tabs are pretty cool!"));
        builder.icon(() -> new ItemStack(Items.STICK));
        builder.displayItems((params, output) -> {
            output.accept(new ItemStack(Items.STICK));
        });
    });

    public RegistryTest()
    {
        NeoForge.EVENT_BUS.addListener(this::onLeftClickBlock);
    }

    private void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getSide() != LogicalSide.SERVER)
            return;

        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer))
            return;

        player.awardStat(CUSTOM_AWESOME_STAT.get());
    }
}
