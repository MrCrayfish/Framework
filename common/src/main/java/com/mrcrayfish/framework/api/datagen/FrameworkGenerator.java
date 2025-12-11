package com.mrcrayfish.framework.api.datagen;

import com.google.common.annotations.Beta;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
@Beta
public abstract class FrameworkGenerator
{
    protected final Map<Block, BlockModelDefinitionGenerator> generators;
    protected final Map<Item, ClientItem> items;
    protected final Map<Identifier, ModelInstance> models;

    public FrameworkGenerator(Map<Block, BlockModelDefinitionGenerator> generators, Map<Item, ClientItem> items, Map<Identifier, ModelInstance> models)
    {
        this.generators = generators;
        this.items = items;
        this.models = models;
    }

    /**
     * Generates all blockstates, items, and models definitions
     */
    public abstract void generate();

    /**
     * Common method for generating an item model with a custom model
     *
     * @param item the item to generate the model for
     */
    protected void customItemModel(Item item)
    {
        this.items.put(item, this.createClientItem(ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item))));
    }

    /**
     * Common method for generating an item model with a flat model
     *
     * @param item the item to generate the model for
     */
    protected void flatItemModel(Item item)
    {
        this.items.put(item, this.createClientItem(ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(item), this.models::put))));
    }

    /**
     * Utility method to create the default client item with the given unbaked item model
     *
     * @param model the top level item model
     * @return a new client item with the default properties
     */
    protected ClientItem createClientItem(ItemModel.Unbaked model)
    {
        return new ClientItem(model, ClientItem.Properties.DEFAULT);
    }

    @FunctionalInterface
    public interface Factory<T extends FrameworkGenerator>
    {
         T apply(Map<Block, BlockModelDefinitionGenerator> generators, Map<Item, ClientItem> clientItems, Map<Identifier, ModelInstance> models);
    }
}
