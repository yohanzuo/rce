/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.gui.utils.common.configuration;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A {@link Dialog} to display a {@link BeanPropertyWidget}.
 * 
 * @author Christian Weiss
 */
public class BeanPropertyDialog extends Dialog {

    private Object object;

    /**
     * Instantiates a new bean property dialog.
     * 
     * @param parentShell the parent shell
     */
    public BeanPropertyDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Instantiates a new bean property dialog.
     * 
     * @param parentShell the parent shell
     */
    public BeanPropertyDialog(IShellProvider parentShell) {
        super(parentShell);
    }

    public void setObject(final Object object) {
        this.object = object;
    }

    protected Object getObject() {
        return object;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(object.toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        final BeanPropertyWidget contents = new BeanPropertyWidget(parent, SWT.NONE);
        contents.setObject(getObject());
        return contents;
    }

}
