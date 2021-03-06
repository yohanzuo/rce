/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.switchcmp.execution;

import static org.easymock.EasyMock.anyObject;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.rcenvironment.components.switchcmp.common.SwitchComponentConstants;
import de.rcenvironment.core.component.api.ComponentConstants;
import de.rcenvironment.core.component.api.ComponentException;
import de.rcenvironment.core.component.datamanagement.api.ComponentDataManagementService;
import de.rcenvironment.core.component.execution.api.Component;
import de.rcenvironment.core.component.execution.api.ComponentContext;
import de.rcenvironment.core.component.testutils.ComponentContextMock;
import de.rcenvironment.core.component.testutils.ComponentTestWrapper;
import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.datamodel.api.TypedDatumFactory;
import de.rcenvironment.core.datamodel.api.TypedDatumService;
import de.rcenvironment.core.datamodel.types.api.FileReferenceTD;
import de.rcenvironment.core.scripting.ScriptingService;
import de.rcenvironment.core.scripting.testutils.ScriptingServiceStubFactory;
import de.rcenvironment.core.utils.common.TempFileServiceAccess;

/**
 * 
 * Integration test for {@link SwitchComponent}.
 * 
 * @author David Scholz
 * @author Doreen Seider
 */
public class SwitchComponentTest {

    private static final String SHIFT = " << 3";

    private static final String AND = " and ";

    private static final String INPUT_X = "x";

    private static final String INPUT_Y = "y";

    /**
     * Expected exception if script/validation fails.
     */
    @Rule
    public ExpectedException scriptException = ExpectedException.none();

    private ComponentTestWrapper component;

    private ComponentContextMock context;

    private TypedDatumFactory typedDatumFactory;

    /**
     * Common setup.
     * 
     * @throws IOException e
     */
    @Before
    public void setUp() throws IOException {
        context = new ComponentContextMock();
        component = new ComponentTestWrapper(new SwitchComponent(), context);
        TempFileServiceAccess.setupUnitTestEnvironment();
        typedDatumFactory = context.getService(TypedDatumService.class).getFactory();

        ComponentDataManagementService componentDataManagementServiceMock = EasyMock.createMock(ComponentDataManagementService.class);
        FileReferenceTD dummyFileReference = typedDatumFactory.createFileReference("", "");

        EasyMock.expect(componentDataManagementServiceMock.createFileReferenceTDFromLocalFile(anyObject(ComponentContext.class),
            anyObject(File.class), anyObject(String.class))).andReturn(dummyFileReference);
        EasyMock.replay(componentDataManagementServiceMock);

        context.addService(ScriptingService.class, ScriptingServiceStubFactory.createDefaultInstance());
        context.addService(ComponentDataManagementService.class, componentDataManagementServiceMock);
    }

    /**
     * Common cleanup.
     */
    @After
    public void tearDown() {
        component.tearDown(Component.FinalComponentState.FINISHED);
        component.dispose();
    }

    /**
     * Tests behavior if no script is defined.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testNoScriptInput() throws ComponentException {
        context.addSimulatedInput(SwitchComponentConstants.DATA_INPUT_NAME, SwitchComponentConstants.DATA_INPUT_NAME, DataType.Float,
            false,
            null);
        context.setInputValue(SwitchComponentConstants.DATA_INPUT_NAME, typedDatumFactory.createFloat(3.0));
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, "");
        scriptException.expect(ComponentException.class);
        scriptException.expectMessage("No condition is defined");
        component.start();
    }

    /**
     * 
     * Test behavior if condition is true. (with historyDataItem)
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testTrueScript() throws ComponentException {
        addSimpleInputAndOutput();
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, INPUT_X + " < " + INPUT_Y);
        context.setConfigurationValue(ComponentConstants.CONFIG_KEY_STORE_DATA_ITEM, Boolean.toString(true));
        component.start();
        component.processInputs();

        Assert.assertEquals(1, context.getCapturedOutput(SwitchComponentConstants.TRUE_OUTPUT).size());
        Assert.assertEquals(0, context.getCapturedOutput(SwitchComponentConstants.FALSE_OUTPUT).size());
    }

    /**
     * 
     * Test behavior if condition is false. (with historyDataItem)
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testFalseScript() throws ComponentException {
        addSimpleInputAndOutput();
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, INPUT_X + " > " + INPUT_Y);
        context.setConfigurationValue(ComponentConstants.CONFIG_KEY_STORE_DATA_ITEM, Boolean.toString(true));
        component.start();
        component.processInputs();

        Assert.assertEquals(0, context.getCapturedOutput(SwitchComponentConstants.TRUE_OUTPUT).size());
        Assert.assertEquals(1, context.getCapturedOutput(SwitchComponentConstants.FALSE_OUTPUT).size());
    }

    /**
     * 
     * Test if syntax errors in script are recognized.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testSyntaxErrorInScript() throws ComponentException {
        addSimpleInputAndOutput();
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, "(" + INPUT_Y + ">" + INPUT_X);
        scriptException.expect(ComponentException.class);
        scriptException.expectMessage("Syntax error: ");
        component.start();
    }

    /**
     * Test if invalid input names are recognized.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testUseOfInvalidInputNames() throws ComponentException {
        addSimpleInputAndOutput();
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, INPUT_Y + ">" + "EvilName");
        scriptException.expect(ComponentException.class);
        scriptException.expectMessage("not defined");
        component.start();
    }

    /**
     * 
     * Test behavior if invalid data types are used.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testUseOfInvalidDataTypesInScript() throws ComponentException {
        context.addSimulatedInput(SwitchComponentConstants.DATA_INPUT_NAME, SwitchComponentConstants.DATA_INPUT_NAME,
            DataType.Matrix, false, null);
        context.setInputValue(SwitchComponentConstants.DATA_INPUT_NAME, typedDatumFactory.createMatrix(2, 2));

        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, SwitchComponentConstants.DATA_INPUT_NAME + " < 3");
        scriptException.expect(ComponentException.class);
        scriptException.expectMessage("not supported in script");
        component.start();
    }

    /**
     * Test operators.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testAllOperators() throws ComponentException {
        addSimpleInputAndOutput();
        StringBuilder sb = new StringBuilder();

        for (String operator : SwitchComponentConstants.OPERATORS) {
            sb.append(INPUT_X);
            if (operator.equals("not")) {
                sb.append(AND + operator + " ");
            } else if (operator.equals("False") || operator.equals("True")) {
                sb.append("==" + operator + " or ");
            } else {
                sb.append(" " + operator + " ");
            }
            sb.append(INPUT_Y);
            sb.append(AND);
        }
        sb.append(INPUT_Y);
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, sb.toString());

        component.start();
        component.processInputs();
    }

    /**
     * 
     * Test valid data types in script.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testValidDataTypes() throws ComponentException {
        context.addSimulatedInput(INPUT_X, SwitchComponentConstants.CONDITION_INPUT_ID, DataType.Float, true, null);
        context.addSimulatedInput(INPUT_Y, SwitchComponentConstants.CONDITION_INPUT_ID, DataType.Integer, true, null);
        context.addSimulatedInput(SwitchComponentConstants.DATA_INPUT_NAME, SwitchComponentConstants.DATA_INPUT_NAME,
            DataType.Boolean, false, null);
        context.addSimulatedOutput(SwitchComponentConstants.TRUE_OUTPUT, SwitchComponentConstants.TRUE_OUTPUT, DataType.Boolean, false,
            null);
        context.addSimulatedOutput(SwitchComponentConstants.FALSE_OUTPUT, SwitchComponentConstants.FALSE_OUTPUT, DataType.Boolean, false,
            null);
        context.setInputValue(INPUT_X, typedDatumFactory.createFloat(1.0));
        context.setInputValue(INPUT_Y, typedDatumFactory.createInteger(2));
        context.setInputValue(SwitchComponentConstants.DATA_INPUT_NAME, typedDatumFactory.createBoolean(true));

        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, INPUT_X + " < " + INPUT_Y + " or "
            + SwitchComponentConstants.DATA_INPUT_NAME);

        component.start();
        component.processInputs();
    }

    /**
     * 
     * Test behavior if script is null.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testNullSwitch() throws ComponentException {
        context.addSimulatedInput(SwitchComponentConstants.DATA_INPUT_NAME, SwitchComponentConstants.DATA_INPUT_NAME, DataType.Float,
            false,
            null);
        context.setInputValue(SwitchComponentConstants.DATA_INPUT_NAME, typedDatumFactory.createFloat(3.0));
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, null);

        scriptException.expect(ComponentException.class);
        scriptException.expectMessage("No condition is defined");
        component.start();
    }
    
    /**
     * 
     * Test behavior if script contains "<<" or ">>".
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testScriptWithShiftAndFloatInputs() throws ComponentException {
        context.addSimulatedInput(INPUT_X, SwitchComponentConstants.CONDITION_INPUT_ID, DataType.Float, true, null);
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, INPUT_X + SHIFT);
        scriptException.expect(ComponentException.class);
        scriptException.expectMessage("Syntax error: ");
        component.start();
    }
    
    /**
     * 
     * Test behavior if script contains "<<" or ">>".
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testScriptWithShiftAndIntegerInputs() throws ComponentException {
        context.addSimulatedInput(INPUT_X, SwitchComponentConstants.CONDITION_INPUT_ID, DataType.Integer, true, null);
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, INPUT_X + SHIFT);
        component.start();
    }
    
    
    /**
     * 
     * Test behavior if script contains "<<" or ">>".
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testScriptWithShiftAndBooleanInputs() throws ComponentException {
        context.addSimulatedInput(INPUT_X, SwitchComponentConstants.CONDITION_INPUT_ID, DataType.Boolean, true, null);
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, INPUT_X + SHIFT);
        component.start();
    }
    
    
    /**
     * 
     * Tests if outputs are closed properly based on the configuration set.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testOutputsNotClosed() throws ComponentException {
        testClosingOutputs(SwitchComponentConstants.NEVER_CLOSE_OUTPUTS_KEY, true, false, false, false);
    }
    
    /**
     * 
     * Tests if outputs are closed properly based on the configuration set.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testClosingOutputsOnTrue() throws ComponentException {
        testClosingOutputs(SwitchComponentConstants.CLOSE_OUTPUTS_ON_TRUE_KEY, false, false, true, true);
    }
    
    /**
     * 
     * Tests if outputs are closed properly based on the configuration set.
     * 
     * @throws ComponentException on unexpected component failures.
     */
    @Test
    public void testClosingOutputsOnFalse() throws ComponentException {
        testClosingOutputs(SwitchComponentConstants.CLOSE_OUTPUTS_ON_FALSE_KEY, true, false, false, true);
    }
    
    private void testClosingOutputs(String config, boolean firstValue, boolean outputsClosedAfterFirstRun,
        boolean secondValue, boolean outputsClosedAfterSecondRun) throws ComponentException {
        context.addSimulatedInput(SwitchComponentConstants.DATA_INPUT_NAME, null,
            DataType.Boolean, false, null);
        context.addSimulatedOutput(SwitchComponentConstants.TRUE_OUTPUT, null, DataType.Boolean, false, null);
        context.addSimulatedOutput(SwitchComponentConstants.FALSE_OUTPUT, null, DataType.Boolean, false, null);
        context.setConfigurationValue(SwitchComponentConstants.CONDITION_KEY, SwitchComponentConstants.DATA_INPUT_NAME);
        context.setConfigurationValue(config, "true");
        component.start();

        context.setInputValue(SwitchComponentConstants.DATA_INPUT_NAME, typedDatumFactory.createBoolean(firstValue));
        component.processInputs();
        
        if (outputsClosedAfterFirstRun) {
            Assert.assertEquals(2, context.getCapturedOutputClosings().size());            
        } else {
            Assert.assertEquals(0, context.getCapturedOutputClosings().size());                        
        }
        
        context.setInputValue(SwitchComponentConstants.DATA_INPUT_NAME, typedDatumFactory.createBoolean(secondValue));
        component.processInputs();
        
        if (outputsClosedAfterSecondRun) {
            Assert.assertEquals(2, context.getCapturedOutputClosings().size());            
        } else {
            Assert.assertEquals(0, context.getCapturedOutputClosings().size());                        
        }
    }

    private void addSimpleInputAndOutput() {
        context.addSimulatedInput(INPUT_X, SwitchComponentConstants.CONDITION_INPUT_ID, DataType.Float, true, null);
        context.addSimulatedInput(INPUT_Y, SwitchComponentConstants.CONDITION_INPUT_ID, DataType.Float, true, null);
        context.addSimulatedInput(SwitchComponentConstants.DATA_INPUT_NAME, SwitchComponentConstants.DATA_INPUT_NAME, DataType.Float,
            false,
            null);
        context.addSimulatedOutput(SwitchComponentConstants.TRUE_OUTPUT, SwitchComponentConstants.TRUE_OUTPUT, DataType.Float, false, null);
        context.addSimulatedOutput(SwitchComponentConstants.FALSE_OUTPUT, SwitchComponentConstants.FALSE_OUTPUT, DataType.Float, false,
            null);
        context.setInputValue(INPUT_X, typedDatumFactory.createFloat(1.0));
        context.setInputValue(INPUT_Y, typedDatumFactory.createFloat(2.0));
        context.setInputValue(SwitchComponentConstants.DATA_INPUT_NAME, typedDatumFactory.createFloat(3.0));
    }

}
