package com.example.drawsomething.activity;
/**
 * @author Raeef Ibrahim
 * @return een Test voor woordenbeheren test class
 */

import android.widget.EditText;

import com.example.drawsomething.DrawSomething;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class WoordenBeherenTest {
    DrawSomething drawSomething;
    DatabaseReference databaseReference;
    EditText woord;
    FirebaseApp firebaseApp;

    @Before
    public void setUp() throws Exception {
        drawSomething = new DrawSomething();
        drawSomething.voegWoord("apple");
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("woorden");
    }

    @After
    public void tearDown() throws Exception {
        drawSomething = null;
    }

    /**
     * @return setwoord
     */
    @Test
    public void setWoord() {
        String expected = "apple";
        String result = drawSomething.getWoord();
        assertEquals(expected, result);
    }
//nog om te fixen
    // @Test
    // public void FirebaseTest() {
    //firebaseApp.getApplicationContext();
    //woord.setText("apple");
    //databaseReference.push().setValue(woord);
    //assertEquals(woord,databaseReference);
    //}
}