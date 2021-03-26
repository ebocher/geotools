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
package org.geotools.filter;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;
import org.opengis.filter.expression.Function;

/**
 * @author Erwan Bocher, CNRS 2021
 */
public class IfThenElseFunctionTest {

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
    public void testIThenElse1() throws IllegalFilterException {
        Function if_ = ff.function("equalto", ff.literal("1"), ff.literal(2));
        Function if_then_else = ff.function("if_then_else", if_, ff.literal(1), ff.literal(-1));
        Object result = if_then_else.evaluate(new Object());
        Assert.assertEquals(-1, result);
    }

    @Test
    public void testIThenElse2() throws IllegalFilterException {
        Function if_ = ff.function("equalto", ff.literal("1"), ff.literal(2));
        Function if_then_else = ff.function("if_then_else", if_, ff.literal(1), ff.literal(-1.55));
        Object result = if_then_else.evaluate(new Object());
        Assert.assertEquals(-1.55, result);
    }

    @Test
    public void testIThenElse3() throws IllegalFilterException, ParseException {
        Function if_ = ff.function("equalto", ff.literal("1"), ff.literal(2));
        Function geom
                = ff.function(
                        "geomFromWKT",
                        ff.literal("POINT (0 0)"));
        Function else_ = ff.function("toWKT", ff.function("buffer", geom, ff.literal(20)));
        Function if_then_else = ff.function("if_then_else", if_, ff.literal(1), else_);
        Object result = if_then_else.evaluate(new Object());
        Assert.assertEquals("POLYGON ((20 0, 19.61570560806461 -3.9018064403225647, 18.477590650225736 -7.653668647301796, 16.629392246050905 -11.111404660392044, 14.142135623730951 -14.14213562373095, 11.111404660392045 -16.629392246050905, 7.653668647301797 -18.477590650225736, 3.9018064403225665 -19.61570560806461, 0.0000000000000012 -20, -3.901806440322564 -19.61570560806461, -7.653668647301794 -18.477590650225736, -11.11140466039204 -16.629392246050905, -14.14213562373095 -14.142135623730951, -16.629392246050905 -11.111404660392044, -18.477590650225736 -7.653668647301798, -19.61570560806461 -3.9018064403225723, -20 -0.0000000000000024, -19.61570560806461 3.9018064403225674, -18.477590650225736 7.653668647301793, -16.62939224605091 11.11140466039204, -14.142135623730955 14.14213562373095, -11.111404660392044 16.629392246050905, -7.6536686473018065 18.47759065022573, -3.901806440322573 19.615705608064605, -0.0000000000000037 20, 3.901806440322566 19.61570560806461, 7.6536686473018 18.477590650225732, 11.111404660392036 16.62939224605091, 14.142135623730947 14.142135623730955, 16.629392246050905 11.111404660392044, 18.47759065022573 7.653668647301808, 19.615705608064605 3.9018064403225745, 20 0))", result);
    }
   
}
