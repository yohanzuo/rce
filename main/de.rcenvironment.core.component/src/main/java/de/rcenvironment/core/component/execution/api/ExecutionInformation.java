/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.component.execution.api;

import java.io.Serializable;

import de.rcenvironment.core.communication.common.InstanceNodeSessionId;
import de.rcenvironment.core.communication.common.LogicalNodeId;

/**
 * Provides information about an executing instance like a component or a workflow.
 *
 * @author Doreen Seider
 */
public interface ExecutionInformation extends Serializable {

    /**
     * @return identifier of the associates instance
     */
    String getExecutionIdentifier();

    /**
     * @return name of the associates instance
     */
    String getInstanceName();

    /**
     * @return {@link InstanceNodeSessionId} of host node
     */
    LogicalNodeId getNodeId();
    
    /**
     * @return {@link InstanceNodeSessionId} of the node which is the storage node for the execution
     */
    LogicalNodeId getDefaultStorageNodeId();
}
