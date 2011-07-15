package net.refractions.udig.render.wms.experimental.preferences;


import net.refractions.udig.render.wms.experimental.ExperimentalWMSPlugin;
import net.refractions.udig.render.wms.experimental.internal.Messages;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page controling the basic wms renderer.
 * <p>
 * By subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into
 * JFace that allows us to create a page that is small and knows how to save, restore and apply
 * itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */
public class ExperimentalWMSRendererPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

    private BooleanFieldEditor checkbox;

   /**
	 * 
	 */
	public ExperimentalWMSRendererPreferencePage() {
		super(GRID);
		setPreferenceStore(ExperimentalWMSPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.ExperimentalWMSRendererPreferencePage_warning); 
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
   public void createFieldEditors() {
      checkbox = new BooleanFieldEditor(PreferenceConstants.P_USE_EXPERIMENTAL_RENDERER, 
                     Messages.ExperimentalWMSRendererPreferencePage_useExperimentalRenderer, 
                     getFieldEditorParent());
      addField( checkbox );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
      // nothing to do here
	}
 
	
}