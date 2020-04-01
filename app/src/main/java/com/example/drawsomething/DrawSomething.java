package com.example.drawsomething;

import android.app.Application;

/**
 * Centrale DrawSomething class om globale variabelen mee de getten en setten
 * @author R.G. Asciutto & R. Ibrahim
 */

public class DrawSomething extends Application {

    private int rondeCount = 1;
    private String speler;
    private String woord;

    /**
     * Getter voor het aantal rondes
     * @return int van het huidige aantal rondes
     */
    public int getRondeCount() {
        return rondeCount;
    }

    /**
     * Setter voor het aantal rondes
     * @param rondeCount int van het huidige aantal rondes
     */
    public void setRondeCount(int rondeCount) {
        this.rondeCount = rondeCount;
    }

    /**
     * Getter voor de huidige speler
     * @return naam van de speler
     */
    public String getSpeler() {
        return speler;
    }

    public void voegWoord(String woord) {
        this.woord = woord;
    }

    public String getWoord() {
        return woord;
    }

    /**
     * Setter voor de huidige speler
     * @param speler naam van de speler
     */
    public void setSpeler(String speler) {
        this.speler = speler;
    }

}
