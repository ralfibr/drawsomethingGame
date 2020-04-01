package com.example.drawsomething.callback;

import java.util.List;

/**
 * Callback interface om te wachten tot alle woorden opgehaald zijn
 * @author R.G. Asciutto
 */

public interface WoordenCallback {
    /**
     * Wordt uitgevoerd om te wachten op het resultaat van de query
     * @param woorden List van alle woorden
     */
    void onCallback(List<String> woorden);
}