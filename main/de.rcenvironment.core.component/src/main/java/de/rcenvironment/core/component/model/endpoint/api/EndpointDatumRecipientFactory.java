/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.component.model.endpoint.api;

import de.rcenvironment.core.communication.common.InstanceNodeSessionId;
import de.rcenvironment.core.communication.common.LogicalNodeId;
import de.rcenvironment.core.component.model.endpoint.impl.EndpointDatumRecipientImpl;

/**
 * Creates {@link EndpointDatumRecipient} objects.
 * 
 * @author Doreen Seider
 */
public final class EndpointDatumRecipientFactory {

    private EndpointDatumRecipientFactory() {}
    
    /**
     * Creates {@link EndpointDatumRecipient} objects.
     * 
     * @param inputIdentifier target input identifier (is the name of the input)
     * @param componentExecutionIdentifier execution identifier of the target component instance
     * @param componentInstanceName name of the target component instance
     * @param inputsNode {@link InstanceNodeSessionId} of the target node
     * @return {@link EndpointDatumRecipient} instance
     */
    public static EndpointDatumRecipient createEndpointDatumRecipient(String inputIdentifier, String componentExecutionIdentifier,
        String componentInstanceName, LogicalNodeId inputsNode) {
        EndpointDatumRecipientImpl endpointDatumRecipient = new EndpointDatumRecipientImpl();
        endpointDatumRecipient.setIdentifier(inputIdentifier);
        endpointDatumRecipient.setInputsComponentExecutionIdentifier(componentExecutionIdentifier);
        endpointDatumRecipient.setInputsComponentInstanceName(componentInstanceName);
        endpointDatumRecipient.setInputsNodeId(inputsNode);
        return endpointDatumRecipient;
    }
}
