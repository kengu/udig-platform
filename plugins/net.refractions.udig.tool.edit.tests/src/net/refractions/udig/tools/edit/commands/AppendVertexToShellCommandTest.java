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
package net.refractions.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.AffineTransform;

import net.refractions.udig.core.StaticBlockingProvider;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;

public class AppendVertexToShellCommandTest {
    AffineTransform transform=AffineTransform.getTranslateInstance(0,0);
    private MathTransform layerToWorld;
    

    java.awt.Point SCREEN=new java.awt.Point(500,500);
    
    @Before
    public void setUp() throws Exception {
        layerToWorld=CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);    
    }
    
    @Test
    public void testRunAndUndo() throws Exception {
        EditBlackboard map=new EditBlackboard(SCREEN.x, SCREEN.y, transform, layerToWorld);
        
        AddVertexCommand command1=new AddVertexCommand(new TestHandler(), map, new StaticBlockingProvider<PrimitiveShape>(map.getGeoms().get(0).getShell()), Point.valueOf(10,10), true );
        AddVertexCommand command2=new AddVertexCommand(new TestHandler(), map, new StaticBlockingProvider<PrimitiveShape>(map.getGeoms().get(0).getShell()), Point.valueOf(10,15), true );

        assertEquals(0, map.getCoords(10,10).size());
        assertEquals(0, map.getCoords(10,15).size());        
        
        command1.run(new NullProgressMonitor());
        command2.run(new NullProgressMonitor());
        
        assertEquals(1, map.getCoords(10,10).size());
        assertEquals(1, map.getCoords(10,15).size());
        
        command2.rollback(new NullProgressMonitor());
        assertTrue(map.getCoords(10,15)==null || 0==map.getCoords(10,15).size());
        assertTrue(0==map.getGeoms(10,15).size());
        assertEquals(Point.valueOf(10,10), map.getGeoms().get(0).getShell().getPoint(0));
        assertEquals(1, map.getGeoms().get(0).getShell().getNumPoints());
        assertEquals(1, map.getGeoms().get(0).getShell().getNumCoords());
        
        command1.rollback(new NullProgressMonitor());
        assertTrue(0==map.getCoords(10,10).size());
        assertTrue( 0==map.getGeoms(10,10).size());
        assertEquals(0, map.getGeoms().get(0).getShell().getNumPoints());
        assertEquals(0, map.getGeoms().get(0).getShell().getNumCoords());
    }

    @Test
    public void testRunAndUndoWhenVerticesAreSnappedIntoOne() throws Exception {
        EditBlackboard map=new EditBlackboard(SCREEN.x, SCREEN.y, transform, layerToWorld);
        
        AddVertexCommand command1=new AddVertexCommand(new TestHandler(), map, new StaticBlockingProvider<PrimitiveShape>(map.getGeoms().get(0).getShell()), Point.valueOf(10,10), true );
        AddVertexCommand command2=new AddVertexCommand(new TestHandler(), map, new StaticBlockingProvider<PrimitiveShape>(map.getGeoms().get(0).getShell()), Point.valueOf(10,11), true );

        assertEquals(0, map.getCoords(10,10).size());
        assertEquals(0, map.getCoords(10,11).size());        
        
        command1.run(new NullProgressMonitor());
        command2.run(new NullProgressMonitor());

        assertEquals(1, map.getCoords(10,10).size());
        assertEquals(1, map.getCoords(10,11).size());
        assertEquals(2, map.getGeoms().get(0).getShell().getNumPoints());
        assertEquals(2, map.getGeoms().get(0).getShell().getNumCoords());
        
        command2.rollback(new NullProgressMonitor());
        assertEquals(1, map.getCoords(10,10).size());
        assertEquals(0, map.getCoords(10,11).size());
        assertEquals(Point.valueOf(10,10), map.getGeoms().get(0).getShell().getPoint(0));
        assertEquals(1, map.getGeoms().get(0).getShell().getNumPoints());
        assertEquals(1, map.getGeoms().get(0).getShell().getNumCoords());
        
        command1.rollback(new NullProgressMonitor());
        assertTrue(0==map.getCoords(10,10).size());
        assertTrue( 0==map.getGeoms(10,10).size());
        assertEquals(0, map.getGeoms().get(0).getShell().getNumPoints());
        assertEquals(0, map.getGeoms().get(0).getShell().getNumCoords());
    }
    
}
