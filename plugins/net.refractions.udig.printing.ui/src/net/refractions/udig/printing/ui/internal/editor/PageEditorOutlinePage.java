/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.printing.ui.internal.editor;

import net.refractions.udig.printing.ui.internal.Messages;
import net.refractions.udig.printing.ui.internal.editor.parts.TreePartFactory;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;

/**
 * An adapter for the outline view in eclipse
 * 
 * @author jesse
 * @since 1.1.0
 */
public class PageEditorOutlinePage extends ContentOutlinePage {

    private PageEditor editor;
    private PageBook pagebook;

    /**
     * Construct <code>PageEditorOutlinePage</code>.
     * 
     * @param viewer
     */
    public PageEditorOutlinePage( PageEditor editor, EditPartViewer viewer ) {
        super(viewer);

        if (editor == null) {
            throw new IllegalArgumentException(Messages.PageEditor_error_nullEditor);
        }
        this.editor = editor;
    }

    public void createControl( Composite parent ) {
        pagebook = new PageBook(parent, SWT.NONE);
        Control outline = getViewer().createControl(pagebook);
        getViewer().setEditDomain(editor.getEditDomain());
        getViewer().setEditPartFactory(new TreePartFactory());
        // getViewer().setKeyHandler(editor.getCommonKeyHandler());

        ContextMenuProvider cmProvider = new PageContextMenuProvider(getViewer(), editor
                .getActionRegistry());
        getViewer().setContextMenu(cmProvider);
        getSite().registerContextMenu("net.refractions.udig.printing.outline.contextmenu", //$NON-NLS-1$
                cmProvider, getSite().getSelectionProvider());
        editor.getSelectionSynchronizer().addViewer(getViewer());
        getViewer().setContents(editor.getModel());
        pagebook.showPage(outline);

    }

    public Control getControl() {
        return pagebook;
    }

    public void dispose() {
        editor.getSelectionSynchronizer().removeViewer(getViewer());
        super.dispose();
        editor.disposeOutlinePage();
    }
}