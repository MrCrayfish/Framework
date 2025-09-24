package com.mrcrayfish.framework.api.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: MrCrayfish
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryContainer
{
    boolean clientOnly() default false;
}
