/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.execution.internal;

import java.util.concurrent.atomic.AtomicReference;

import de.rcenvironment.core.component.execution.api.BatchingConsoleRowsForwarder;
import de.rcenvironment.core.component.execution.api.Component;
import de.rcenvironment.core.component.execution.api.ComponentExecutionContext;
import de.rcenvironment.core.component.execution.api.WorkflowExecutionControllerCallbackService;

/**
 * Stores instances that are used when executing a {@link Component} and that need to be shared by multiple instances. This approach is not
 * intended to be a perfect solution but to be a migration path. It was introduced to purge ComponentExecutionControllerImpl.
 * 
 * @author Doreen Seider
 */
public class ComponentExecutionRelatedInstances {

    protected AtomicReference<Component> component = new AtomicReference<Component>(null);

    protected ComponentContextBridge compCtxBridge;

    protected ComponentExecutionContext compExeCtx;

    protected ComponentStateMachine compStateMachine;

    protected ComponentExecutionScheduler compExeScheduler;

    protected ComponentExecutionStorageBridge compExeStorageBridge;

    protected ComponentExecutionRelatedStates compExeRelatedStates;

    protected ConsoleRowsSender consoleRowsSender;

    protected TypedDatumToOutputWriter typedDatumToOutputWriter;

    protected BatchingConsoleRowsForwarder batchingConsoleRowsForwarder;

    protected WorkflowExecutionControllerCallbackService wfExeCtrlBridge;

    protected WorkflowExecutionControllerBridgeDelegator wfExeCtrlBridgeDelegator;

    protected int timestampOffsetToWorkfowNode = 0;

    protected boolean isNestedLoopDriver;

}
