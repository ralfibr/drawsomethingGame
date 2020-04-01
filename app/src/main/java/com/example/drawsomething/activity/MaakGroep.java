package com.example.drawsomething.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drawsomething.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MaakGroep extends AppCompatActivity {

    String name;
    int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maakgroep);

        // Aanmaken van een toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();

        // Als er een toolbar aanwezig is, wordt hier een menu knop aan toegevoegd
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //dit roept de createLobby functie aan, en maakt een lobby in de nosql DB
        createLobby();
    }

    /**
     *   createLobby
     *   Zorgt ervoor dat een lobby word aangemaakt in de nosql live database van het firebase project.
     */
    public void createLobby() {

        // De button en text inputs worden hier opgehaald met hun id en opgeslagen in variables
        Button create = (Button) findViewById(R.id.createLobby);
        final EditText maxUsers = (EditText) findViewById(R.id.maxPlayers);
        final EditText lobbyName = (EditText) findViewById(R.id.lobbyName);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // De value van de text inputs word hier in een variable opgeslagen die we eerder haden gedeclareerd.
                max = Integer.valueOf(maxUsers.getText().toString());
                name = lobbyName.getText().toString();

                //Hier maakt de applicatie de connectie met de live firebase nosql database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                DatabaseReference dbRef2 = database.getReference();

                //defineerd het pad naar de child max-users van de nieuwe lobby
                dbRef = database.getReference("/lobbies/" + name + "/maxUsers");
                dbRef.setValue(max);

                //defineerd het pad naar de child total-users van de nieuwe lobby
                dbRef2 = database.getReference("/lobbies/" + name + "/totalUsers");
                dbRef2.setValue(0);

                //redirect naar de main page.
                startActivity(new Intent(MaakGroep.this, MainActivity.class));
            }
        });

    }

    private void screenMessage(String text){
        Toast.makeText(MaakGroep.this, text, Toast.LENGTH_SHORT).show();
    }
}

