package test.registry;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod("registry_test")
@RegistryContainer
public class RegistryTest
{
    public static final RegistryEntry<Block> MY_AWESOME_BLOCK = RegistryEntry.blockWithItem(new ResourceLocation("registry_test", "awesome_block"), () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
}
