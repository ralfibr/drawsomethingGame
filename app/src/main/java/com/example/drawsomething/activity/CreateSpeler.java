package com.example.drawsomething.activity;
/**
 * @athour Raeef Ibrahim
 * Hier in dit class wordt een speler gemaakt met behulp van Firebase, de naam van speler wordt naar Firebase gestuurd en opgeslagen
 */

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drawsomething.DrawSomething;
import com.example.drawsomething.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateSpeler extends AppCompatActivity {
    String spelerNaam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_speler);

        //createSpeler roepen
        createspeler();

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
    }

    /**
     * @Return Hier in dit methoud wordt de naam in een edit text ingevuld en daarna
     * wordt database reference gevraagd om data te kunnen invullen
     */
    public void createspeler() {
        //initialize
        Button meedoen = (Button) findViewById(R.id.meedoen);
        final EditText createSpeler = (EditText) findViewById(R.id.create);
        //meedoen button on action
        meedoen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spelerNaam = createSpeler.getText().toString();

                if (!spelerNaam.isEmpty()) {
                    //Hier maakt de applicatie de connectie met de live firebase nosql database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    //splernaam naar Firebase sturen
                    DatabaseReference dbRef = database.getReference("/lobby/spelers/" + spelerNaam + "/score");
                    dbRef.setValue(0);
                    //hier wordt de spelernaam naar DrawSomething model gestuurd
                    ((DrawSomething) getApplication()).setSpeler(spelerNaam);
                    //hier wordt methoud DisplayYoast geroepen
                    displayToast();

                    finish();
                    // hier gaat van create speler naar groep
                    startActivity(new Intent(CreateSpeler.this, Groep.class));
                } else {
                    errorToast();
                }
            }
        });
    }

    /**
     * @return nadat de speler aangemaakt is wordt een Toast gezien dat de speler is aangemaakt
     */
    private void displayToast() {
        Toast.makeText(CreateSpeler.this, "Speler is aangemaakt", Toast.LENGTH_SHORT).show();
    }

    /**
     * @return Hier wordt een error duidlljk gezien als iets niet goed gegaan
     */
    private void errorToast() {
        Toast.makeText(CreateSpeler.this, "Voer een naam in", Toast.LENGTH_SHORT).show();
    }
}


