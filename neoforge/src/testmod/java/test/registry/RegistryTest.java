package test.registry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
    private static ResourceLocation rl(String name)
    {
        return ResourceLocation.fromNamespaceAndPath("registry_test", name);
    }

    public static final RegistryEntry<Attribute> MY_AWESOME_ATTRIBUTE = RegistryEntry.attribute(rl("awesome_attribute"), () -> new RangedAttribute("attribute.registry_test.generic.awesome", 0, 0, 1));
    public static final RegistryEntry<Block> MY_AWESOME_BLOCK = RegistryEntry.block(rl("awesome_block"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final RegistryEntry<Block> MY_AWESOME_BLOCK_WITH_ITEM = RegistryEntry.blockWithItem(rl("awesome_block_with_item"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final RegistryEntry<Block> MY_AWESOME_BLOCK_WITH_CUSTOM_ITEM = RegistryEntry.blockWithItem(rl("awesome_block_with_custom_item"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS), BlockItem::new, () -> new Item.Properties().stacksTo(1));
    public static final RegistryEntry<BlockEntityType<AwesomeBlockEntity>> MY_AWESOME_BLOCK_ENTITY = RegistryEntry.blockEntity(rl("awesome_block_entity"), AwesomeBlockEntity::new, () -> new Block[]{MY_AWESOME_BLOCK.get()});
    public static final RegistryEntry<SingletonArgumentInfo<AwesomeArgument>> MY_AWESOME_COMMAND_ARGUMENT_TYPE = RegistryEntry.commandArgumentType(rl("awesome_command_argument_type"), AwesomeArgument.class, () -> SingletonArgumentInfo.contextFree(AwesomeArgument::awesome));
    public static final RegistryEntry<CreativeModeTab> MY_AWESOME_CREATIVE_TAB = RegistryEntry.creativeModeTab(rl("tab"), builder -> {
        builder.title(Component.literal("Creative tabs are pretty cool!"));
        builder.icon(() -> new ItemStack(Items.STICK));
        builder.displayItems((params, output) -> {
            output.accept(new ItemStack(Items.STICK));
        });
    });
    public static final RegistryEntry<ResourceLocation> MY_AWESOME_CUSTOM_STAT = RegistryEntry.customStat(rl("awesome_stat"), StatFormatter.DEFAULT);
    public static final RegistryEntry<EntityType<Creeper>> MY_AWESOME_ENTITY_TYPE = RegistryEntry.entityType(rl("awesome_entity"), () -> EntityType.Builder.of(Creeper::new, MobCategory.AMBIENT));
    //public static final RegistryEntry<ResourceLocation> MY_AWESOME_FLUID = RegistryEntry.customStat(rl("awesome_stat"), StatFormatter.DEFAULT);
    public static final RegistryEntry<Item> MY_AWESOME_ITEM = RegistryEntry.item(rl("awesome_item"), Item::new, () -> new Item.Properties().food(new FoodProperties.Builder().nutrition(10).build()));
    public static final RegistryEntry<MobEffect> MY_AWESOME_MOB_EFFECT = RegistryEntry.mobEffect(rl("awesome_mob_effect"), AwesomeMobEffect::new);
    public static final RegistryEntry<SimpleParticleType> MY_AWESOME_PARTICLE_TYPE = RegistryEntry.particleType(rl("awesome_particle_type"), () -> new SimpleParticleType(false));
    public static final RegistryEntry<Potion> MY_AWESOME_POTION = RegistryEntry.potion(rl("awesome_potion"), () -> new Potion("awesome_potion", new MobEffectInstance(MY_AWESOME_MOB_EFFECT.holder(), 1)));
    public static final RegistryEntry<RecipeDisplay.Type<AwesomeRecipeDisplay>> MY_AWESOME_RECIPE_DISPLAY = RegistryEntry.recipeDisplay(rl("awesome_recipe_display"), () -> AwesomeRecipeDisplay.TYPE);
    public static final RegistryEntry<RecipeBookCategory> MY_AWESOME_RECIPE_BOOK_CATEGORY = RegistryEntry.recipeBookCategory(rl("awesome_recipe_book_category"));
    public static final RegistryEntry<RecipeType<AwesomeRecipe>> MY_AWESOME_RECIPE_TYPE = RegistryEntry.recipeType(rl("awesome_recipe_type"));
    public static final RegistryEntry<RecipeSerializer<AwesomeRecipe>> MY_AWESOME_RECIPE_SERIALIZER = RegistryEntry.recipeSerializer(rl("awesome_recipe_serializer"), AwesomeRecipe.AwesomeSerializer::new);
    public static final RegistryEntry<SoundEvent> MY_AWESOME_SOUND_EVENT = RegistryEntry.soundEvent(rl("awesome_sound_event"), id -> () -> SoundEvent.createVariableRangeEvent(id));
    public static final RegistryEntry<DataComponentType<Integer>> SIMPLE_COUNTER = RegistryEntry.dataComponentType(rl("simple_counter"), builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

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

        player.awardStat(MY_AWESOME_CUSTOM_STAT.get());
    }

    public static class AwesomeBlockEntity extends BlockEntity
    {
        public AwesomeBlockEntity(BlockPos pos, BlockState state)
        {
            super(MY_AWESOME_BLOCK_ENTITY.get(), pos, state);
        }
    }

    public record AwesomeHolder(int i) {}

    public static class AwesomeArgument implements ArgumentType<AwesomeHolder>
    {
        public static AwesomeArgument awesome()
        {
            return new AwesomeArgument();
        }

        @Override
        public AwesomeHolder parse(StringReader reader) throws CommandSyntaxException
        {
            return new AwesomeHolder(reader.readInt());
        }
    }

    public static class AwesomeMobEffect extends MobEffect
    {
        protected AwesomeMobEffect()
        {
            super(MobEffectCategory.BENEFICIAL, 0xFFFFFFFF);
        }
    }

    public static class AwesomeRecipe implements Recipe<CraftingInput>
    {
        @Override
        public boolean matches(CraftingInput input, Level level)
        {
            return true;
        }

        @Override
        public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public RecipeSerializer<? extends Recipe<CraftingInput>> getSerializer()
        {
            return MY_AWESOME_RECIPE_SERIALIZER.get();
        }

        @Override
        public RecipeType<? extends Recipe<CraftingInput>> getType()
        {
            return MY_AWESOME_RECIPE_TYPE.get();
        }

        @Override
        public PlacementInfo placementInfo()
        {
            return PlacementInfo.NOT_PLACEABLE;
        }

        @Override
        public RecipeBookCategory recipeBookCategory()
        {
            return MY_AWESOME_RECIPE_BOOK_CATEGORY.get();
        }

        public static class AwesomeSerializer implements RecipeSerializer<AwesomeRecipe>
        {
            private static final AwesomeRecipe RECIPE = new AwesomeRecipe();
            private static final MapCodec<AwesomeRecipe> CODEC = MapCodec.unit(RECIPE);
            private static final StreamCodec<RegistryFriendlyByteBuf, AwesomeRecipe> STREAM_CODEC = StreamCodec.unit(RECIPE);

            @Override
            public MapCodec<AwesomeRecipe> codec()
            {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, AwesomeRecipe> streamCodec()
            {
                return STREAM_CODEC;
            }
        }
    }

    public record AwesomeRecipeDisplay(SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay
    {
        private static final Type<AwesomeRecipeDisplay> TYPE = new Type<>(RecordCodecBuilder.mapCodec(builder -> {
            return builder.group(
                SlotDisplay.CODEC.fieldOf("result").forGetter(AwesomeRecipeDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AwesomeRecipeDisplay::craftingStation)
            ).apply(builder, AwesomeRecipeDisplay::new);
        }), StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            AwesomeRecipeDisplay::result,
            SlotDisplay.STREAM_CODEC,
            AwesomeRecipeDisplay::craftingStation,
            AwesomeRecipeDisplay::new
        ));

        @Override
        public SlotDisplay result()
        {
            return this.result;
        }

        @Override
        public SlotDisplay craftingStation()
        {
            return this.craftingStation;
        }

        @Override
        public Type<AwesomeRecipeDisplay> type()
        {
            return TYPE;
        }
    }
}
