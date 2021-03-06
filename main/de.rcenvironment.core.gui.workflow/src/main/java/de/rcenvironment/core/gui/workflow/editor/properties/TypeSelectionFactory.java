/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.workflow.editor.properties;

import java.util.List;

/**
 * Represents a source of type selection lists. Used to fill user selection elements (lists,
 * dropdown boxes) when choosing types at runtime (for example, dynamic endpoints).
 * 
 * TODO move this type to a non-gui package?
 * 
 * @author Robert Mischke
 */
public interface TypeSelectionFactory {

    /**
     * @return a list of available type options
     */
    List<TypeSelectionOption> getTypeSelectionOptions();
}
