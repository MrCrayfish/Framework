package com.mrcrayfish.framework.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.api.client.resources.IDataLoader;
import com.mrcrayfish.framework.api.client.resources.IResourceSupplier;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public final class JsonDataManager extends SimplePreparableReloadListener<List<Pair<IDataLoader<IResourceSupplier>, List<Pair<IResourceSupplier, DataObject>>>>>
{
    private static final Gson GSON = (new GsonBuilder()).create();

    private static JsonDataManager instance;

    public static JsonDataManager getInstance()
    {
        if(instance == null)
        {
            instance = new JsonDataManager();
        }
        return instance;
    }

    private final List<IDataLoader<IResourceSupplier>> loaders = new ArrayList<>();

    private JsonDataManager() {}

    @SuppressWarnings("unchecked")
    public void addLoader(IDataLoader<?> loader)
    {
        this.loaders.add((IDataLoader<IResourceSupplier>) loader);
    }

    @Override
    protected List<Pair<IDataLoader<IResourceSupplier>, List<Pair<IResourceSupplier, DataObject>>>> prepare(ResourceManager manager, ProfilerFiller filler)
    {
        List<Pair<IDataLoader<IResourceSupplier>, List<Pair<IResourceSupplier, DataObject>>>> list = new ArrayList<>();
        this.loaders.forEach(loader ->
        {
            List<Pair<IResourceSupplier, DataObject>> pairs = new ArrayList<>();
            loader.getResourceSuppliers().forEach(supplier ->
            {
                ResourceLocation location = supplier.getLocation();
                if(!manager.hasResource(location) && loader.ignoreMissing())
                {
                    pairs.add(Pair.of(supplier, DataObject.EMPTY));
                    return;
                }

                try(Resource resource = manager.getResource(location); BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream())))
                {
                    JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    if(element == null)
                        return;

                    DataObject dataObject = DataObject.convert(element);
                    if(dataObject == null)
                        return;

                    pairs.add(Pair.of(supplier, dataObject));
                }
                catch(IOException e)
                {
                    Framework.LOGGER.error("Failed to json data", e);
                    pairs.add(Pair.of(supplier, DataObject.EMPTY));
                }
            });
            list.add(Pair.of(loader, pairs));
        });
        return list;
    }

    @Override
    protected void apply(List<Pair<IDataLoader<IResourceSupplier>, List<Pair<IResourceSupplier, DataObject>>>> list, ResourceManager p_10794_, ProfilerFiller p_10795_)
    {
        list.forEach(pair -> pair.getLeft().process(pair.getRight()));
    }

}
