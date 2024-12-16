package com.mrcrayfish.framework.api.datagen;

import com.google.common.annotations.Beta;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Beta
public class FrameworkModelProvider implements DataProvider
{
    private final PackOutput.PathProvider blockstates;
    private final PackOutput.PathProvider items;
    private final PackOutput.PathProvider models;
    private final FrameworkGenerator.Factory<? extends FrameworkGenerator> generator;

    public FrameworkModelProvider(PackOutput output, FrameworkGenerator.Factory<? extends FrameworkGenerator> generator)
    {
        this.blockstates = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.items = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        this.generator = generator;
    }

    @Override
    @SuppressWarnings("deprecation")
    public CompletableFuture<?> run(CachedOutput output)
    {
        Map<Block, BlockStateGenerator> generators = new HashMap<>();
        Map<Item, ClientItem> clientItems = new HashMap<>();
        Map<ResourceLocation, ModelInstance> models = new HashMap<>();
        this.generator.apply(generators, clientItems, models).generate();
        return CompletableFuture.allOf(
            DataProvider.saveAll(output, Supplier::get, block -> {
                return this.blockstates.json(block.builtInRegistryHolder().key().location());
            }, generators),
            DataProvider.saveAll(output, ClientItem.CODEC, item -> {
                return this.items.json(item.builtInRegistryHolder().key().location());
            }, clientItems),
            DataProvider.saveAll(output, Supplier::get, this.models::json, models)
        );
    }

    @Override
    public String getName()
    {
        return "Models";
    }
}
