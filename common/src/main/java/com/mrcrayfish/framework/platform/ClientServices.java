package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.platform.services.IClientHelper;

/**
 * Author: MrCrayfish
 */
public class ClientServices
{
    public static final IClientHelper CLIENT = Services.load(IClientHelper.class);
}
