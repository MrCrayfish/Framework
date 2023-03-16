package com.mrcrayfish.framework.util;

import com.mrcrayfish.framework.api.registry.RegistryEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ReflectionUtils
{
    public static Class<?> getClass(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch(ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static List<RegistryEntry<?>> findRegistryEntriesInClass(Class<?> targetClass)
    {
        List<RegistryEntry<?>> entries = new ArrayList<>();
        Field[] fields = targetClass.getDeclaredFields();
        for(Field field : fields)
        {
            if(field.getType() != RegistryEntry.class)
                continue;

            if(!Modifier.isPublic(field.getModifiers()))
                throw new RuntimeException("Unable to access RegistryEntry due to non-public modifier");

            if(!Modifier.isStatic(field.getModifiers()))
                throw new RuntimeException("Unable to access RegistryEntry due to non-static modifier");

            try
            {
                entries.add((RegistryEntry<?>) field.get(null));
            }
            catch(IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        return entries;
    }
}
