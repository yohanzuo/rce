/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.common;

/**
 * Exception class for failures related to instance, instance session, or logical node identifiers. Typical examples include attempts to
 * parse invalid string representations back to identifier objects, or trying to resolve an identifier to a more specific one when no
 * matching candidate exists.
 * 
 * @author Robert Mischke
 */
public class IdentifierException extends Exception {

    private static final long serialVersionUID = 6185049208074449664L;

    public IdentifierException(String message) {
        super(message);
    }

}