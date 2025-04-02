package com.mrcrayfish.framework.util;

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

    public static <T> List<T> findPublicStaticObjects(Class<T> objectClass, Class<?> holderClass)
    {
        List<T> entries = new ArrayList<>();
        Field[] fields = holderClass.getDeclaredFields();
        for(Field field : fields)
        {
            if(!objectClass.isAssignableFrom(field.getType()))
                continue;

            if(!Modifier.isPublic(field.getModifiers()))
                throw new RuntimeException("Unable to access field due to non-public modifier: " + field.getName());

            if(!Modifier.isStatic(field.getModifiers()))
                throw new RuntimeException("Unable to access field due to non-static modifier: " + field.getName());

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
