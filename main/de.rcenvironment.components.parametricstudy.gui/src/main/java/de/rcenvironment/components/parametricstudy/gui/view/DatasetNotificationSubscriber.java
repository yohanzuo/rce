/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.components.parametricstudy.gui.view;

import java.io.Serializable;

import de.rcenvironment.components.parametricstudy.common.StudyDataset;
import de.rcenvironment.core.notification.DefaultNotificationSubscriber;
import de.rcenvironment.core.notification.Notification;
import de.rcenvironment.core.notification.NotificationSubscriber;

/**
 * Used to subscribe to {@link Dataset}s.
 * @author Christian Weiss
 */
public class DatasetNotificationSubscriber extends DefaultNotificationSubscriber {

    private static final long serialVersionUID = 7984538979387371048L;

    private final transient StudyDatastore datastore;

    public DatasetNotificationSubscriber(final StudyDatastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public Class<? extends Serializable> getInterface() {
        return NotificationSubscriber.class;
    }

    @Override
    public void processNotification(Notification notification) {
        final StudyDataset dataset = (StudyDataset) notification.getBody();
        datastore.addDataset(dataset);
    }
    
}
