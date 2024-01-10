package test.openmodel;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod("open_model_test")
@RegistryContainer
public class OpenModelTest
{
    public static final RegistryEntry<Block> OPEN_MODEL_BLOCK = RegistryEntry.blockWithItem(new ResourceLocation("open_model_test", "open_model"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistryEntry<Block> CHILD_OPEN_MODEL_BLOCK = RegistryEntry.blockWithItem(new ResourceLocation("open_model_test", "child_open_model"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
}
