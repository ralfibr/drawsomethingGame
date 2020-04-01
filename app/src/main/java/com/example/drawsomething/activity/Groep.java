package com.example.drawsomething.activity;
/**
 * @athour Raeef Ibrahim
 * In dit class wordt een groep van spelers gemaakt en wordt gezien in een view met behulp van Firebase Database
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drawsomething.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Groep extends AppCompatActivity {
    private TextView mValueView;
    private Button begin;
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groep);

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

        //initialize
        mValueView = (TextView) findViewById(R.id.textSpeler);
        begin = (Button) findViewById(R.id.bgn);

        //begin button set on action
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Groep.this, GuessWord.class));
                displayToast();
            }
        });

        // delete button set on action
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //een stap terug
                startActivity(new Intent(Groep.this, CreateSpeler.class));
                displayVerwijderd();
                verwijderspelers();
            }
        });

        //Spelers View
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("/lobby/spelers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mValueView.setText("");

                if (dataSnapshot.hasChildren()) {
                    //Door alle spelers lopen
                    for (DataSnapshot spelerSnapshot : dataSnapshot.getChildren()) {
                        mValueView.setText(mValueView.getText() + spelerSnapshot.getKey() + "\n");
                    }
                } else {
                    finish();
                    //een stap terug naar CreateSpeler class
                    startActivity(new Intent(Groep.this, CreateSpeler.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * @return Geef een melding dat het spel begonnen is
     */
    private void displayToast() {
        Toast.makeText(Groep.this, "Spel is begonnen", Toast.LENGTH_LONG).show();
    }

    /**
     * @return Geef een medling dat de spelijrs verwijderd zijn
     */
    private void displayVerwijderd() {
        Toast.makeText(this, "Spelers zijn verwijderd", Toast.LENGTH_LONG).show();
    }

    /**
     * @return een methoud om de spelers te kunnen verwijderen
     */
    private void verwijderspelers() {
        DatabaseReference spelers = FirebaseDatabase.getInstance().getReference("lobby").child("spelers");
        spelers.removeValue();
    }

}
