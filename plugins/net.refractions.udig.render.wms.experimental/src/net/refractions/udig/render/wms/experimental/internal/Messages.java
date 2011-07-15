/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.render.wms.experimental.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.render.wms.experimental.internal.messages"; //$NON-NLS-1$
	public static String ExperimentalWMSRenderer2_error;
	public static String ExperimentalWMSRenderer2_errorObtainingImage;
	public static String ExperimentalWMSRenderer2_no_layers_to_render;
	public static String ExperimentalWMSRenderer2_unable_to_decode_image;
    public static String ExperimentalWMSRendererPreferencePage_setOrder;
	public static String ExperimentalWMSRendererPreferencePage_useExperimentalRenderer;
	public static String ExperimentalWMSRenderer2_refreshJob_title;
	public static String ExperimentalWMSRendererPreferencePage_warning;
    public static String projectionwarning0;
    
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
