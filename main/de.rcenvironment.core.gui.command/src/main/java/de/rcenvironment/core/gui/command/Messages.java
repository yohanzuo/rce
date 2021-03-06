/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.command;

import org.eclipse.osgi.util.NLS;

/**
 * Contains the messages of Command Console GUI.
 * 
 * @author Marc Stammerjohann
 * 
 */
public class Messages extends NLS {

    /** Constant. */
    public static String clearConsoleActionContextMenuLabel;

    /** Constant. */
    public static String wrongCommand;

    /** Constant. */
    public static String defaultLabelText;

    /** Constant. */
    public static String copyActionContextMenuLabel;

    /** Constant. */
    public static String pasteActionContextMenuLabel;

    /** Constant. */
    public static String emptyOutputReceiver;

    /** Constant. */
    public static String helpCommand;

    /** Constant. */
    public static String helpDeveloperCommand;

    /** Constant. */
    public static String historyUsedCommand;
    

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
