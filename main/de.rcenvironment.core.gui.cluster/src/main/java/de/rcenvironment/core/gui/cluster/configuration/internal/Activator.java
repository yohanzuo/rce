/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.cluster.configuration.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator for the gui cluster {@link Bundle}.
 *
 * @author Doreen Seider
 */
public class Activator extends AbstractUIPlugin {

    private static Activator instance = null;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        instance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        instance = null;
    }

    public static Activator getInstance() {
        return instance;
    }
}
