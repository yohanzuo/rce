/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.rpc.internal;

import java.io.Serializable;

import de.rcenvironment.core.communication.common.InstanceNodeSessionId;

/**
 * Interface which needs to be implemented by proxy objects, which are transfered to another platform
 * in order to enable callbacks.
 * 
 * @author Doreen Seider
 */
public interface CallbackProxy extends Serializable {

    /**
     * Returns the identifier of the object.
     * 
     * @return The identifier of the object.
     */
    String getObjectIdentifier();

    /**
     * Returns the {@link InstanceNodeSessionId} of the home platform, i.e. the platform to call back.
     * 
     * @return The {@link InstanceNodeSessionId} of the home platform..
     */
    InstanceNodeSessionId getHomePlatform();

}
