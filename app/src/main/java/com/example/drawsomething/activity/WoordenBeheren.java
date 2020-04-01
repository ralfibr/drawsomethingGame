package com.example.drawsomething.activity;
/**
 * @athour Raeef Ibrahim
 * In dit class kan een bherder de gelegenheid krijgen om worden te kunnen toevoegen en verwijderen
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drawsomething.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class WoordenBeheren extends AppCompatActivity {
    private String woord;
    private String woordverwijderd;
    private EditText woordtext;
    private DatabaseReference woordenRef;
    TextView mValueView;
    Button verwijderworden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woorden_beheren);

        //initialize
        woordenRef = FirebaseDatabase.getInstance().getReference().child("woorden");
        mValueView = findViewById(R.id.woordenlijst);
        //scroling functie
        mValueView.setMovementMethod(new ScrollingMovementMethod());
        verwijderworden = (Button) findViewById(R.id.verwijderenWoorden);
        woordtext = (EditText) findViewById(R.id.textwoord);

        //verwijder button set on action
        verwijderworden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                woordverwijderd = woordtext.getText().toString();
                verwijderwoorden(woordverwijderd);
                displayVerwijderd();
            }
        });


        //Hier wordt een lijst van alle woorden gemaakt
        woordenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mValueView.setText("");

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot woordenSnapshot : dataSnapshot.getChildren()) {
                        mValueView.setText(mValueView.getText() + (String) woordenSnapshot.getValue() + "\n");
                    }
                } else {
                    finish();
                    startActivity(new Intent(WoordenBeheren.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // worden toevoegen methoud roepen
        woordToevoegen();
    }

    /**
     * @return Hier wordt een woord aan worden lijst toegevoegd
     */
    public void woordToevoegen() {
        Button toevoegen = (Button) findViewById(R.id.toevoegen);
        toevoegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                woord = woordtext.getText().toString();

                if (!woord.isEmpty()) {
                    woordenRef.push().setValue(woord);
                    displayToast();
                } else {
                    errorToast();
                }
            }
        });
    }

    /**
     * @return Hier geef ik een Toesat als het woord toegevoed is
     */
    private void displayToast() {
        Toast.makeText(WoordenBeheren.this, "Woord is toegevoegd", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param wrd woord die wordt in text field ingevuld
     * @return Hier wordt de woord die in editText ingevuld verwijderd
     */
    public void verwijderwoorden(final String wrd) {
        woordenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot woordenSnapshot : dataSnapshot.getChildren()) {
                    if (woordenSnapshot.getValue().equals(wrd)) {
                        woordenSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * @return Hier geef ik een toesat als het woord verwijderd is
     */
    private void displayVerwijderd() {
        Toast.makeText(this, "Woord is verwijderd", Toast.LENGTH_LONG).show();
    }

    /**
     * @return error melding om de gebruiker nog ker een woord te gaan typen
     */
    private void errorToast() {
        Toast.makeText(WoordenBeheren.this, "Voer een woord in", Toast.LENGTH_SHORT).show();
    }

}