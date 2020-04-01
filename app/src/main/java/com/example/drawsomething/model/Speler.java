package com.example.drawsomething.model;

/**
 * Model class voor de spelers
 * @author R.G. Asciutto & R. Ibrahim
 */

public class Speler implements Comparable<Speler> {

    private String naam;
    private int score;

    /**
     * Getter voor de naam van de speler
     * @return String naam
     */
    public String getNaam() {
        return naam;
    }

    /**
     * Setter voor de naam van de speler
     * @param naam String van de spelernaam
     */
    public void setNaam(String naam) {
        this.naam = naam;
    }

    /**
     * Getter voor de score van een speler
     * @return int score
     */
    public int getScore() {
        return score;
    }

    /**
     * Setter voor de score van een speler
     * @param score int score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Deze method wordt gebruikt om spelers aflopend te sorteren op score
     * @param speler Speler object
     * @return positieve of negatieve int op basis van score vergelijking
     */
    @Override
    public int compareTo(Speler speler) {
        return speler.getScore() - this.getScore();
    }
}
