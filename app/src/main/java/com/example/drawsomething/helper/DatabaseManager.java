package com.example.drawsomething.helper;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.example.drawsomething.DrawSomething;
import com.example.drawsomething.activity.GuessWord;
import com.example.drawsomething.callback.GeefPuntenCallback;
import com.example.drawsomething.callback.GekozenWoordCallback;
import com.example.drawsomething.callback.SpelerCallback;
import com.example.drawsomething.callback.WinnerCallback;
import com.example.drawsomething.callback.WoordenCallback;
import com.example.drawsomething.model.Speler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Deze class wordt gebruikt om Firebase queries mee uit te voeren als centrale database manager
 * @author R.G. Asciutto
 */

public class DatabaseManager {

    // Alle gebruikte database references
    private FirebaseDatabase database;
    private DatabaseReference woordenRef;
    private DatabaseReference spelersRef;
    private DatabaseReference gekozenWoordRef;
    private DatabaseReference isWoordGekozenRef;
    private DatabaseReference isWoordGeradenRef;
    private DatabaseReference isWoordGetekendRef;
    private DatabaseReference winnerRef;

    /**
     * Constructor om een nieuwe FirebaseDatabase instance te maken en de references te setten
     */
    public DatabaseManager() {
        this.database = FirebaseDatabase.getInstance();
        this.woordenRef = database.getReference().child("woorden");
        this.spelersRef = database.getReference().child("/lobby/spelers");
        this.gekozenWoordRef = database.getReference("/lobby/gekozenWoord");
        this.isWoordGekozenRef = database.getReference("/lobby/isWoordGekozen");
        this.isWoordGeradenRef = database.getReference("/lobby/isWoordGeraden");
        this.isWoordGetekendRef = database.getReference("/lobby/isWoordGetekend");
        this.winnerRef = database.getReference("/lobby/winner");
    }

    /**
     * Deze method wordt gebruikt om de spelers mee op te halen uit Firebase
     * @param spelerCallback callback om te wachten tot de query uitgevoerd is
     */
    public void readSpelers(final SpelerCallback spelerCallback) {
        spelersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Speler> spelers = new ArrayList<>();

                // Er wordt door de spelers heen gelooped en deze worden toegevoegd aan een ArrayList
                for (DataSnapshot spelerSnapshot : dataSnapshot.getChildren()) {
                    Speler speler = spelerSnapshot.getValue(Speler.class);
                    speler.setNaam(spelerSnapshot.getKey());
                    spelers.add(speler);
                }

                // Callback wordt uitgevoerd en spelers worden meegegeven
                spelerCallback.onCallback(spelers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Met deze method worden alle woorden opgehaald uit Firebase
     * @param woordenCallback  callback om te wachten tot de query uitgevoerd is
     */
    public void getWoorden(final WoordenCallback woordenCallback) {
        woordenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> woorden = new ArrayList<>();

                // Alle woorden worden toegevoegd aan de ArrayList
                for (DataSnapshot woordSnapshot : dataSnapshot.getChildren()) {
                    woorden.add((String) woordSnapshot.getValue());
                }

                // Callback wordt uitgevoerd met de woorden die opgehaald zijn
                woordenCallback.onCallback(woorden);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Deze method wordt gebruikt om het gekozen woord op te halen uit Firebase
     * @param gekozenWoordCallback callback om te wachten tot de query uitgevoerd is
     */
    public void getGekozenWoord(final GekozenWoordCallback gekozenWoordCallback) {
        gekozenWoordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Callback wordt uitgevoerd met het resultaat van het gekozen woord
                gekozenWoordCallback.onCallback((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Deze method geeft punten aan spelers na een ronde
     * @param speler de naam van de speler zodat
     * @param geefPuntenCallback callback om te wachten tot de query uitgevoerd is
     */
    public void geefPunten(String speler, final GeefPuntenCallback geefPuntenCallback) {
        // Reference naar de database met de score van de speler
        final DatabaseReference scoreRef = database.getReference("/lobby/spelers/" + speler + "/score");

        scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Score van de speler wordt incremented
                scoreRef.setValue((long) dataSnapshot.getValue() + 1);

                // Callback wordt uitgevoerd zodat er gewacht wordt totdat te speler een nieuwe score heeft
                geefPuntenCallback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Method om de winnaar van een ronde mee op te halen uit Firebase
     * @param winnerCallback callback om te wachten tot de query uitgevoerd is
     */
    public void getWinner(final WinnerCallback winnerCallback) {
        winnerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Callback wordt uitgevoerd met de naam van de speler
                winnerCallback.onCallback((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Method om de winnaar mee te setten in de database
     * @param speler naam van de speler
     */
    public void setWinner(String speler) {
        winnerRef.setValue(speler);
    }

    /**
     * Method om het gekozen woord de setten in de database
     * @param gekozenWoord het woord dat de tekenaar gekozen heeft
     */
    public void setGekozenWoord(String gekozenWoord) {
        gekozenWoordRef.setValue(gekozenWoord);
    }

    /**
     * Method om de boolean van isGekozenWoord mee te setten
     * @param gekozen woord wel of niet gekozen
     */
    public void setIsWoordGekozen(boolean gekozen) {
        isWoordGekozenRef.setValue(gekozen);
    }

    /**
     * Method om de boolean van isWoordGeraden mee te setten
     * @param geraden woord wel of niet geraden
     */
    public void setIsWoordGeraden(boolean geraden) {
        isWoordGeradenRef.setValue(geraden);
    }
}
