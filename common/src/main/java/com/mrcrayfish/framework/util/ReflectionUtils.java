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

    public static <T> List<T> findPublicStaticObjects(Class<T> objectClass, Class<?> holderClass)
    {
        List<T> entries = new ArrayList<>();
        Field[] fields = holderClass.getDeclaredFields();
        for(Field field : fields)
        {
            if(!objectClass.isAssignableFrom(field.getType()))
                continue;

            // Allows non-public fields to be registered
            field.setAccessible(true);

            if(!Modifier.isStatic(field.getModifiers()))
                throw new RuntimeException("Registration objects must be static. Please update the field: " + holderClass.getName() + "." + field.getName());

            if(!Modifier.isFinal(field.getModifiers()))
                throw new RuntimeException("Registration objects must be final. Please update the field: " + holderClass.getName() + "." + field.getName());

            try
            {
                //noinspection unchecked
                entries.add((T) field.get(null));
            }
            catch(IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        return entries;
    }
}
