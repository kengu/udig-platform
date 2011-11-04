/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tool.select.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.render.impl.ViewportModelImpl;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.select.SelectPlugin;
import net.refractions.udig.tool.select.internal.BoundaryLayerStrategy;
import net.refractions.udig.tool.select.internal.SelectionToolPreferencePage;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Queries the current boundary layer for selection and updates the BoundaryLayerBoundaryService
 * 
 * @see net.refractions.udig.tools.internal.BoundaryLayerBoundaryService
 * @author leviputna
 * @since 1.2.3
 */
public class SetBoundaryLayerCommand extends AbstractCommand implements UndoableMapCommand {

    private Envelope bbox = null;
    private ReferencedEnvelope bounds;
    private MapMouseEvent mouseEvent;
    private static String BOUNDARY_LAYER_ID = "net.refractions.udig.tool.default.BoundaryLayerService";

    /**
     * Creates a new instance of SetConndaryCommand
     * 
     * @param bbox
     */
    public SetBoundaryLayerCommand( MapMouseEvent e, Envelope bbox ) {
        this.bbox = bbox;
        this.mouseEvent = e;
    }

    /**
     * @see net.refractions.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {

    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {

        // Get Preference
//        boolean zoomToSelection = SelectPlugin.getDefault().getPreferenceStore()
//                .getBoolean(SelectionToolPreferencePage.ZOOM_TO_SELECTION);
        boolean navigateLayer = SelectPlugin.getDefault().getPreferenceStore()
                .getBoolean(SelectionToolPreferencePage.NAVIGATE_SELECTION);
        
        ILayer oldLayer = getBoundaryLayerStrategy().getActiveLayer();
        ILayer selectedLayer;
        if((mouseEvent.button == MapMouseEvent.BUTTON3) && navigateLayer){
            selectedLayer = getBoundaryLayerStrategy().selectPreviousLayer();
//            navigateLayer = false; //don't navigate to next layer we have already moved back one layer
        }
        else if ((mouseEvent.button == MapMouseEvent.BUTTON1) && navigateLayer) {
            // move to next boundary layer
            selectedLayer = getBoundaryLayerStrategy().selectNextLayer();
        }
        else {
            selectedLayer = oldLayer;
        }
            

        if (!selectedLayer.isApplicable(ILayer.Interaction.BOUNDARY)) return;

        selectedLayer.getCRS();
        SimpleFeatureCollection featureCollection = getFeaturesInBbox(selectedLayer, bbox, monitor);

//        getBoundaryLayerStrategy().setFeatures(featureCollection);
        
        

        if (featureCollection.isEmpty()) {
            getBoundaryLayerStrategy().setActiveLayer(oldLayer);
            return;
        }
        
        bounds = featureCollection.getBounds();

        Geometry newBoundary = unionGeometry(featureCollection);
        CoordinateReferenceSystem crs = featureCollection.getSchema()
                .getCoordinateReferenceSystem();
        updateBoundaryService(newBoundary, crs);

        if (navigateLayer) {
            ViewportModelImpl vmi = (ViewportModelImpl) selectedLayer.getMap().getViewportModel();
            vmi.zoomToBox(bounds);
            
        }
        
    }

    /*
     * returns a BoundaryLayerStrategy object for quick access
     */
    private BoundaryLayerStrategy getBoundaryLayerStrategy() {
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        IBoundaryStrategy boundaryStrategy = boundaryService.findProxy(BOUNDARY_LAYER_ID)
                .getStrategy();

        if (boundaryStrategy instanceof BoundaryLayerStrategy) {
            return (BoundaryLayerStrategy) boundaryStrategy;
        }
        return null;
    }
    
    private SimpleFeatureCollection getFeaturesInBbox( ILayer layer, Envelope bbox,
            IProgressMonitor monitor ) throws IOException {

        SimpleFeatureSource featureSource = (SimpleFeatureSource) layer.getResource(
                FeatureSource.class, new SubProgressMonitor(monitor, 1));

        if (featureSource == null) return null;

        Filter bboxFilter = layer.createBBoxFilter(bbox, monitor);
        return featureSource.getFeatures(bboxFilter);
    }

    private Geometry unionGeometry( SimpleFeatureCollection featureCollection ) {
        if (featureCollection.size() < 0) return null;

        List<Geometry> geoms = new ArrayList<Geometry>();
        SimpleFeatureIterator featureIterator = featureCollection.features();

        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geoms.add(geometry.reverse());

        }

        GeometryFactory factory = new GeometryFactory();
        Geometry combined = factory.buildGeometry(geoms);

        return combined.union();
    }

    private void updateBoundaryService( Geometry newBoundary, CoordinateReferenceSystem crs )
            throws IOException {
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        BoundaryProxy boundaryLayerProxy = boundaryService.findProxy(BOUNDARY_LAYER_ID);
        
        BoundaryLayerStrategy boundaryLayerStrategy = (BoundaryLayerStrategy)boundaryLayerProxy.getStrategy();
        boundaryLayerStrategy.setCrs(crs);
        boundaryLayerStrategy.setGeometry(newBoundary);
        
        // if the current stragegy does not equal the bounary layer strategy set it
        if (!boundaryService.getProxy().equals(boundaryLayerProxy)) {
            boundaryService.setProxy(boundaryLayerProxy);
        }

    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.BBoxSelectionCommand_boxSelection;
    }

}