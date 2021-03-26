/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2021, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.DataUtilities;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.filter.IllegalFilterException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Function;

/** @author Erwan Bocher, CNRS 2021 */
public class TransformWithExpression {

    static FilterFactoryImpl ff;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ff = new FilterFactoryImpl();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        ff = null;
    }

    @Test
    public void testIThenElse1()
            throws IllegalFilterException, SchemaException, ParseException, IOException {
        MemoryDataStore memory = new MemoryDataStore();
        SimpleFeatureType type =
                DataUtilities.createType(
                        "testSchema", "name:String,gid:Integer,*the_geom:Geometry");
        WKTReader reader = new WKTReader();
        Geometry geom1 = reader.read("LINESTRING(0 0 0, 10 10 10)");
        SimpleFeature feature1 =
                SimpleFeatureBuilder.build(type, new Object[] {"testFeature1", 1, geom1}, null);
        memory.addFeature(feature1);
        List<Definition> definitions = new ArrayList<Definition>();
        Function if_ = ff.function("equalto", ff.property("gid"), ff.literal(1));
        Function then = ff.function("buffer", ff.property("the_geom"), ff.literal(20));
        Function if_then_else = ff.function("if_then_else", if_, then, ff.property("the_geom"));
        definitions.add(new Definition("THE_GEOM", if_then_else));
        SimpleFeatureSource transformed =
                TransformFactory.transform(
                        memory.getFeatureSource("testSchema"), "OUTPUT_TABLE_TEST_F", definitions);
        SimpleFeatureCollection simpleFeatureCollection = transformed.getFeatures();
        try (SimpleFeatureIterator features = simpleFeatureCollection.features()) {
            Assert.assertTrue(features.hasNext());
            SimpleFeature feature = features.next();
            Geometry geom = (Geometry) feature.getDefaultGeometry();
            Assert.assertTrue(geom instanceof Polygon);
        }
    }

    @Test
    public void testIThenElse2()
            throws IllegalFilterException, SchemaException, ParseException, IOException {
        MemoryDataStore memory = new MemoryDataStore();
        SimpleFeatureType type =
                DataUtilities.createType(
                        "testSchema", "name:String,gid:Integer,*the_geom:Geometry");
        WKTReader reader = new WKTReader();
        Geometry geom1 = reader.read("LINESTRING(0 0 0, 10 10 10)");
        SimpleFeature feature1 =
                SimpleFeatureBuilder.build(type, new Object[] {"testFeature1", 1, geom1}, null);
        Geometry geom2 = reader.read("LINESTRING(0 0 0, 10 10 10)");
        memory.addFeature(feature1);
        List<Definition> definitions = new ArrayList<Definition>();
        Function if_ = ff.function("equalto", ff.property("gid"), ff.literal(1));
        Function then =
                ff.function(
                        "geomFromWKT",
                        ff.literal("POLYGON ((150 330, 220 330, 220 230, 150 230, 150 330))"));
        Function if_then_else = ff.function("if_then_else", if_, then, ff.property("the_geom"));
        definitions.add(new Definition("THE_GEOM", if_then_else));
        SimpleFeatureSource transformed =
                TransformFactory.transform(
                        memory.getFeatureSource("testSchema"), "OUTPUT_TABLE_TEST_F", definitions);
        SimpleFeatureCollection simpleFeatureCollection = transformed.getFeatures();
        try (SimpleFeatureIterator features = simpleFeatureCollection.features()) {
            Assert.assertTrue(features.hasNext());
            SimpleFeature feature = features.next();
            Geometry geom = (Geometry) feature.getDefaultGeometry();
            Assert.assertTrue(geom instanceof Polygon);
        }
    }
}
