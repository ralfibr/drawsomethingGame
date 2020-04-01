package com.example.drawsomething.callback;

/**
 * Callback interface zodat er gewacht wordt totdat een woord gekozen is door de tekenaar
 * @author R.G. Asciutto
 */

public interface GekozenWoordCallback {
    /**
     * Wordt uitgevoerd om te wachten op het resultaat van de query
     * @param gekozenWoord string van het gekozen woord
     */
    void onCallback(String gekozenWoord);

}
