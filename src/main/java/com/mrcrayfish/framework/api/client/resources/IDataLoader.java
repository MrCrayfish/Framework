package com.mrcrayfish.framework.api.client.resources;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * <p>Data Loaders allow JSON files to be loaded from the client's assets. These resources can be
 * overridden by resource packs. To register a data loader, see
 * {@link FrameworkClientAPI#registerDataLoader(IDataLoader)}</p>
 *
 * Author: MrCrayfish
 */
public interface IDataLoader<T extends IResourceSupplier>
{
    /**
     * @return A list of resources the load. Must be JSON format only.
     */
    List<T> getResourceSuppliers();

    /**
     * Processes the loaded resources. If a resource was not available, the result will be an empty
     * data object.
     *
     * @param results a list with resource suppliers and their associated data object
     */
    void process(List<Pair<T, DataObject>> results);

    /**
     * @return True if resources should simply be ignored if not available
     */
    default boolean ignoreMissing()
    {
        return false;
    }
}
