package com.example.drawsomething.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.drawsomething.R;

/**
 * Deze class is een Activity voor het startscherm
 * @author R. Ibrahim, R.G. Asciutto, A. Razzak, H. Kooshani
 */

public class MainActivity extends AppCompatActivity {

    /**
     * Met deze method wordt de activity gestart en listeners op de knoppen gezet in het scherm
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set listeners op de knoppen
        tekenen();
        raden();
        woordenVeranderen();
    }

    /**
     * Method om een listener op de teken knop te zetten
     */
    public void tekenen() {
        // Hier geef je de ID van de button aan voor de functie
        Button tekenen = (Button) findViewById(R.id.tekenen);

        // onClick event listener
        tekenen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // De eerste is de current class & de tweede is waar je naar toe wilt gaan
                startActivity(new Intent(MainActivity.this, WoordenKiezer.class));
            }
        });
    }

    /**
     * Method om een listener op de raden knop te zetten
     */
    public void raden() {
        // Hier geef je de ID van de button aan voor de functie
        Button raden = (Button) findViewById(R.id.raden);

        // onClick event listener
        raden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // De eerste is de current class & de tweede is waar je naar toe wilt gaan
                startActivity(new Intent(MainActivity.this, CreateSpeler.class));
            }
        });
    }

    /**
     * Method om een listener op de admin knop te zetten
     */
    public void woordenVeranderen() {
        // Hier geef je de ID van de button aan voor de functie
        ImageButton buttonInlog = findViewById(R.id.inlog_button);

        // onClick event listener
        buttonInlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // De eerste is de current class & de tweede is waar je naar toe wilt gaan
                startActivity(new Intent(MainActivity.this, InlogAdmin.class));
            }
        });
    }
}
