/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.transport.jms.common;

import de.rcenvironment.core.communication.transport.spi.HandshakeInformation;

/**
 * An extension of {@link HandshakeInformation} with JMS-specific fields.
 * 
 * @author Robert Mischke
 */
public class JMSHandshakeInformation extends HandshakeInformation {

    private String temporaryQueueInformation;

    public String getTemporaryQueueInformation() {
        return temporaryQueueInformation;
    }

    public void setTemporaryQueueInformation(String newValue) {
        this.temporaryQueueInformation = newValue;
    }
}
