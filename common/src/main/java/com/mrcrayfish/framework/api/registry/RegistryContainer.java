package com.mrcrayfish.framework.api.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: MrCrayfish
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryContainer
{
    /**
     * @return True if this registry container should only be detected if in a client environment
     */
    @SuppressWarnings("unused")
    boolean clientOnly() default false;
}
