package com.example.drawsomething.callback;

/**
 * Callback interface om te wachten totdat de winnaar opgehaald is
 * @author R.G. Asciutto
 */

public interface WinnerCallback {
    /**
     * Wordt uitgevoerd om te wachten op het resultaat van de query
     * @param speler naam van de winnaar
     */
    void onCallback(String speler);
}
