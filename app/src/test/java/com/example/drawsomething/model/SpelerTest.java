package com.example.drawsomething.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpelerTest {

    Speler speler;

    @Before
    public void setUp() throws Exception {
        speler = new Speler();
        speler.setNaam("test");
        speler.setScore(1);
    }

    @After
    public void tearDown() throws Exception {
        speler = null;
    }

    @Test
    public void getNaam() {
        assertEquals("test", speler.getNaam());
    }

    @Test
    public void setNaam() {
        speler.setNaam("testSetNaam");
        assertEquals("testSetNaam", speler.getNaam());
    }

    @Test
    public void getScore() {
        assertEquals(1, speler.getScore());
    }

    @Test
    public void setScore() {
        speler.setScore(2);
        assertEquals(2, speler.getScore());
    }

    @Test
    public void compareToLess() {
        Speler tweedeSpeler = new Speler();
        tweedeSpeler.setScore(0);
        assertEquals(-1, speler.compareTo(tweedeSpeler));
    }

    @Test
    public void compareToEquals() {
        Speler tweedeSpeler = new Speler();
        tweedeSpeler.setScore(1);
        assertEquals(0, speler.compareTo(tweedeSpeler));
    }

    @Test
    public void compareToMore() {
        Speler tweedeSpeler = new Speler();
        tweedeSpeler.setScore(2);
        assertEquals(1, speler.compareTo(tweedeSpeler));
    }
}