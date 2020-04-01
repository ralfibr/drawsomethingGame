package com.example.drawsomething.callback;

/**
 * Callback interface zodat er gewacht wordt totdat een speler punten gekregen heeft
 * @author R.G. Asciutto
 */

public interface GeefPuntenCallback {
    /**
     * Wordt uitgevoerd om te wachten op het resultaat van de query
     */
    void onCallback();
}
