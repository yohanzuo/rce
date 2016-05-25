/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.execution.internal;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;

import de.rcenvironment.core.communication.api.CommunicationService;
import de.rcenvironment.core.communication.api.PlatformService;
import de.rcenvironment.core.communication.common.NodeIdentifier;
import de.rcenvironment.core.component.execution.api.ComponentExecutionController;
import de.rcenvironment.core.component.execution.api.EndpointDatumSerializer;
import de.rcenvironment.core.component.execution.api.ExecutionControllerException;
import de.rcenvironment.core.component.execution.api.LocalExecutionControllerUtilsService;
import de.rcenvironment.core.component.execution.api.RemotableComponentExecutionControllerService;
import de.rcenvironment.core.component.execution.api.RemotableEndpointDatumDispatcher;
import de.rcenvironment.core.component.model.endpoint.api.EndpointDatum;
import de.rcenvironment.core.utils.common.StringUtils;
import de.rcenvironment.core.utils.common.concurrent.AsyncCallbackExceptionPolicy;
import de.rcenvironment.core.utils.common.concurrent.AsyncOrderedExecutionQueue;
import de.rcenvironment.core.utils.common.concurrent.SharedThreadPool;
import de.rcenvironment.core.utils.common.rpc.RemoteOperationException;
import de.rcenvironment.core.utils.common.security.AllowRemoteAccess;

/**
 * Implementation of {@link RemotableEndpointDatumDispatcher}.
 * 
 * @author Doreen Seider
 */
public class EndpointDatumDispatcherImpl implements EndpointDatumDispatcher, RemotableEndpointDatumDispatcher {

    private static final String FAILED_TO_SEND_ENDPOINT_DATUM = "Failed to send endpoint datum %s";

    private static final Log LOG = LogFactory.getLog(EndpointDatumDispatcherImpl.class);
    
    private static final int CACHE_SIZE = 20;
    
    private SharedThreadPool threadPool = SharedThreadPool.getInstance();

    private AsyncOrderedExecutionQueue executionQueue = new AsyncOrderedExecutionQueue(
        AsyncCallbackExceptionPolicy.LOG_AND_PROCEED, threadPool);

    private Map<String, WeakReference<ComponentExecutionController>> compExeCtrls = new LRUMap<>(CACHE_SIZE);
    
    private Map<NodeIdentifier, WeakReference<RemotableEndpointDatumDispatcher>> endpointDatumProcessors = new LRUMap<>(CACHE_SIZE);

    private BundleContext bundleContext;

    private CommunicationService communicationService;
    
    private LocalExecutionControllerUtilsService exeCtrlUtilsService;
    
    private PlatformService platformService;
    
    private EndpointDatumSerializer endpointDatumSerializer;

    protected void activate(BundleContext context) {
        bundleContext = context;
    }

    @Override
    public void dispatchEndpointDatum(final EndpointDatum endpointDatum) {
        final String executionId = endpointDatum.getInputsComponentExecutionIdentifier();
        executionQueue.enqueue(new Runnable() {

            @Override
            public void run() {
                if (platformService.isLocalNode(endpointDatum.getInputsNodeId())) {
                    processEndpointDatum(executionId, endpointDatum);
                } else {
                    if (communicationService.getReachableNodes().contains(endpointDatum.getInputsNodeId())) {
                        forwardEndpointDatum(endpointDatum.getInputsNodeId(), endpointDatum);
                    } else {
                        if (!platformService.isLocalNode(endpointDatum.getWorkflowNodeId())) {
                            forwardEndpointDatum(endpointDatum.getWorkflowNodeId(), endpointDatum);
                        } else {
                            tryToForwardEndpointDatumToActualTarget();
                        }
                    }
                }
            }
            
            private void tryToForwardEndpointDatumToActualTarget() {
                int failureCount = 0;
                while (true) {
                    if (communicationService.getReachableNodes().contains(endpointDatum.getInputsNodeId())) {
                        forwardEndpointDatum(endpointDatum.getInputsNodeId(), endpointDatum);
                        ComponentExecutionUtils.logCallbackSuccessAfterFailure(LOG, StringUtils.format("Sending endpoint datum %s",
                            endpointDatum), failureCount);
                        break;
                    } else {
                        if (++failureCount < ComponentExecutionUtils.MAX_RETRIES) {
                            ComponentExecutionUtils.waitForRetryAfterCallbackFailure(LOG, failureCount, StringUtils.format(
                                FAILED_TO_SEND_ENDPOINT_DATUM, endpointDatum), "Target node not reachable: "
                                    + endpointDatum.getInputsNodeId());
                        } else {
                            RemoteOperationException e = new RemoteOperationException("Target node not reachable: "
                                + endpointDatum.getInputsNodeId());
                            ComponentExecutionUtils.logCallbackFailureAfterRetriesExceeded(LOG, 
                                StringUtils.format(FAILED_TO_SEND_ENDPOINT_DATUM, endpointDatum), e);
                            callbackComponentExecutionController(endpointDatum, e);
                            break;
                        }
                    }
                }
            }
        });
    }
    
    @Override
    @AllowRemoteAccess
    public void dispatchEndpointDatum(String serializedEndpointDatum) {
        dispatchEndpointDatum(endpointDatumSerializer.deserializeEndpointDatum(serializedEndpointDatum));
    }
    
    protected void forwardEndpointDatum(NodeIdentifier node, EndpointDatum endpointDatum) {
        RemotableEndpointDatumDispatcher dispatcher = null;

        synchronized (endpointDatumProcessors) {
            if (endpointDatumProcessors.containsKey(node)) {
                dispatcher = endpointDatumProcessors.get(node).get();
            }
            if (dispatcher == null) {
                dispatcher = communicationService.getRemotableService(RemotableEndpointDatumDispatcher.class, node);
                endpointDatumProcessors.put(node, new WeakReference<RemotableEndpointDatumDispatcher>(dispatcher));
            }
        }
        // retrying disabled as long as methods called are not robust against multiple calls
        
//        int failureCount = 0;
//        while (true) {
        try {
            dispatcher.dispatchEndpointDatum(endpointDatumSerializer.serializeEndpointDatum(endpointDatum));
//                ComponentExecutionUtils.logCallbackSuccessAfterFailure(LOG, StringUtils.format("Sending endpoint datum %s",
//                    endpointDatum), failureCount);
//                break;
        } catch (RemoteOperationException e) {
//                if (++failureCount < ComponentExecutionUtils.MAX_RETRIES) {
//                    ComponentExecutionUtils.waitForRetryAfterCallbackFailure(LOG, failureCount, StringUtils.format(
//                        FAILED_TO_SEND_ENDPOINT_DATUM, endpointDatum), e.toString());
//                } else {
//                    ComponentExecutionUtils.logCallbackFailureAfterRetriesExceeded(LOG, StringUtils.format(FAILED_TO_SEND_ENDPOINT_DATUM,
//                        endpointDatum), e);
            callbackComponentExecutionController(endpointDatum, e);
//                    break;
//                }
        }
        
    }
    
    protected void callbackComponentExecutionController(EndpointDatum endpointDatum, RemoteOperationException e) {
        if (platformService.isLocalNode(endpointDatum.getOutputsNodeId())) {
            callbackComponentExecutionControllerLocally(endpointDatum, e);
        } else {
            callbackComponentExecutionControllerRemotely(endpointDatum, e);
        }
    }
    
    private void callbackComponentExecutionControllerLocally(EndpointDatum endpointDatum, RemoteOperationException e) {
        String executionId = endpointDatum.getOutputsComponentExecutionIdentifier();
        ComponentExecutionController compExeCtrl = null;
        try {
            compExeCtrl = getComponentExecutionController(executionId);
        } catch (ExecutionControllerException e1) {
            LOG.warn(StringUtils.format("Failed to announce that sending endpoint datum '%s'; failed cause: %s",
                endpointDatum.toString(), e1.toString()));
            return;
        }
        compExeCtrl.onSendingEndointDatumFailed(endpointDatum, e);
    }
    
    private void callbackComponentExecutionControllerRemotely(EndpointDatum endpointDatum, RemoteOperationException e) {
        String outputCompExeId = endpointDatum.getOutputsComponentExecutionIdentifier();
        RemotableComponentExecutionControllerService compExeCtrlService;
        compExeCtrlService = communicationService.getRemotableService(RemotableComponentExecutionControllerService.class,
            endpointDatum.getOutputsNodeId());
        try {
            compExeCtrlService.onSendingEndointDatumFailed(outputCompExeId, endpointDatum, e);
        } catch (ExecutionControllerException | RemoteOperationException e1) {
            LOG.warn(StringUtils.format("Failed to announce that sending endpoint datum '%s' failed; cause: %s",
                endpointDatum, e1.toString()));
        }
    }
    
    protected void processEndpointDatum(String executionId, EndpointDatum endpointDatum) {
        ComponentExecutionController compExeCtrl = null;
        try {
            compExeCtrl = getComponentExecutionController(executionId);
        } catch (ExecutionControllerException e) {
            LOG.warn(StringUtils.format("Endpoint datum '%s' not processed; cause: %s",
                endpointDatum.toString(), e.toString()));
            return;
        }
        compExeCtrl.onEndpointDatumReceived(endpointDatum);
    }
    
    private ComponentExecutionController getComponentExecutionController(String executionId) throws ExecutionControllerException {
        ComponentExecutionController compExeCtrl = null;
        synchronized (compExeCtrls) {
            if (compExeCtrls.containsKey(executionId)) {
                compExeCtrl = compExeCtrls.get(executionId).get();
            }
            if (compExeCtrl == null) {
                compExeCtrl = exeCtrlUtilsService.getExecutionController(ComponentExecutionController.class, executionId, bundleContext);
                compExeCtrls.put(executionId, new WeakReference<ComponentExecutionController>(compExeCtrl));
            }
        }
        return compExeCtrl;
    }

    protected void bindCommunicationService(CommunicationService newService) {
        communicationService = newService;
    }
    
    protected void bindLocalExecutionControllerUtilsService(LocalExecutionControllerUtilsService newService) {
        exeCtrlUtilsService = newService;
    }
    
    protected void bindPlatformService(PlatformService newService) {
        platformService = newService;
    }
    
    protected void bindEndpointDatumSerializer(EndpointDatumSerializer newService) {
        endpointDatumSerializer = newService;
    }

}