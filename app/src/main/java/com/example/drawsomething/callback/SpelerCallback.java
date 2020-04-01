package com.example.drawsomething.callback;

import com.example.drawsomething.model.Speler;

import java.util.List;

/**
 * Callback interface om te wachten totdat de spelers opgehaald zijn
 * @author R.G. Asciutto
 */

public interface SpelerCallback {
    /**
     * Wordt uitgevoerd om te wachten op het resultaat van de query
     * @param spelers List van de opgehaalde spelers
     */
    void onCallback(List<Speler> spelers);
}
