package com.example.drawsomething;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DrawSomethingTest {

    DrawSomething drawSomething;

    @Before
    public void setUp() throws Exception {
        drawSomething = new DrawSomething();
        drawSomething.setRondeCount(1);
        drawSomething.setSpeler("test");
    }

    @After
    public void tearDown() throws Exception {
        drawSomething = null;
    }

    @Test
    public void getRondeCount() {
        assertEquals(1, drawSomething.getRondeCount());
    }

    @Test
    public void setRondeCount() {
        drawSomething.setRondeCount(2);
        assertEquals(2, drawSomething.getRondeCount());
    }

    @Test
    public void getSpeler() {
        assertEquals("test", drawSomething.getSpeler());
    }

    @Test
    public void setSpeler() {
        drawSomething.setSpeler("testSetSpeler");
        assertEquals("testSetSpeler", drawSomething.getSpeler());
    }
}