/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.gui.xpathchooser;

/**
 * Represents all contens of the last attribute value.
 *
 * @author Arne Bachmann
 * @author Markus Kunde
 */
public class XPathValue extends XPathStep {


    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "=" + xValue;
    }   

}
