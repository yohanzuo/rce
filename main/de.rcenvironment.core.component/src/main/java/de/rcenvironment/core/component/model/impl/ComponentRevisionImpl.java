/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.model.impl;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import de.rcenvironment.core.component.model.api.ComponentInterface;
import de.rcenvironment.core.component.model.api.ComponentRevision;
import de.rcenvironment.core.utils.common.StringUtils;

/**
 * A writable {@link ComponentRevision} implementation.
 * 
 * @author Robert Mischke
 * @author Doreen Seider
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentRevisionImpl implements ComponentRevision, Serializable {

    private static final long serialVersionUID = 6744720085980362367L;

    // Note: Contributes only a few things at this point; will be filled in later
    private ComponentInterfaceImpl componentInterface;
    
    private String className;

    @Override
    public ComponentInterface getComponentInterface() {
        return componentInterface;
    }

    public void setComponentInterface(ComponentInterfaceImpl componentInterface) {
        this.componentInterface = componentInterface;
    }

    @Override
    public String toString() {
        return StringUtils.format("ComponentRevision(ci=%s)", componentInterface);
    }

    @Override
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
}
