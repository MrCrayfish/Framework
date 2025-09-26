package com.mrcrayfish.framework.api.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: MrCrayfish
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryContainer
{
    /**
     * @return True if this registry container should only be detected if in a client environment
     */
    @SuppressWarnings("unused")
    boolean clientOnly() default false;
}
