package com.example.drawsomething.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drawsomething.R;
import com.example.drawsomething.helper.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Dit is de class voor de canvas Activity
 * @author A. Razzak, R.G. Asciutto
 */

public class CanvasActivity extends AppCompatActivity {

    private PaintView paintView;
    private int defaultColor;
    private int STORAGE_PERMISSION_CODE = 1;
    private int amountOfFirebaseCalls = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_on_pad);

        //hier worden alle buttons aangeroepen een aangemaakt
        Button[] buttons = new Button[] {
            findViewById(R.id.change_color_button),
            findViewById(R.id.clear_button),
            findViewById(R.id.undo_button),
            findViewById(R.id.redo_button),
            findViewById(R.id.save_button),
            findViewById(R.id.done_button)
        };

        paintView = findViewById(R.id.paintView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        SeekBar seekBar = findViewById(R.id.seekBar);
        final TextView textView = findViewById(R.id.current_pen_size);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        paintView.initialise(displayMetrics);

        textView.setText("Lijn dikte: " + seekBar.getProgress());

        setButtonActions(buttons);

        // Listener voor de lijn dikte bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update lijn dikte
                paintView.setStrokeWidth(seekBar.getProgress());
                textView.setText("Lijn dikte: " + seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * Storage permission opvragen van de gebruiker
     */
    private void requestStoragePermission () {
        //Dit is een check voor het geval dat er geen toegang is tot de opslag
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //hier word de alert aangemaakt als je geen toegang hebt tot de opslag
            new AlertDialog.Builder(this)
                    .setTitle("Toegang geweigerd")
                    .setMessage("Toegang tot de opslag is vereist voor het opslaan")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(CanvasActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                        //bij het klikken op anuleren zorgt dit er voor dat de alert weg gehaald word
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }

                    })
                    .create().show();

        } else {
            //anders vraagt het de toegang tot de opslag aan
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //wanneer er gevraagd word om de toegangs code(ja of nee)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //wanneer er wel toegang is tot het opslaan op de opslag runt het deze code
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Opslag toegang toegestaan", Toast.LENGTH_LONG).show();
            //wanneer er niet toegang is tot het opslaan op de opslag runt het deze code
            } else {

                Toast.makeText(this, "Opslag toegang geweigerd", Toast.LENGTH_LONG).show();

            }

        }

    }

    /**
     * Hier worden de cases voor de buttons gedefineerd
     * @param buttons Array van buttons
     */
    public void setButtonActions(Button[] buttons) {
        // Door de buttons heen loopen
        for (final Button button : buttons) {
            // Listener op elke button zetten
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = (String) button.getTag();

                    switch (tag) {
                        //bij deze case vraagt het de code aan die met een library een colour picker opend hier kun je de kleur van de lijn aanpassen
                        case "change_color_button":
                            openColourPicker();
                            break;
                        //bij deze case word de paintview leeg gemaakt(dit is een wit vlak in de view van het tekenen)
                        case "clear_button":
                            paintView.clear();
                            break;
                        //bij deze case gaat het systeem 1 stap terug (dus naar voor de meest recente lijn)
                        case "undo_button":
                            paintView.undo();
                            break;
                        //bij deze case gaat het systeem een stap vooruit als er een undo is gebruikt
                        case "redo_button":
                            paintView.redo();
                            break;
                        //bij deze case checkt het systeem of er permission is als die er niet is dan vraagt de applicatie het aan.
                        case "save_button":
                            if (ContextCompat.checkSelfPermission(CanvasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestStoragePermission();
                            }
                            //na de check (als de check gelukt is) word de paint view opgeslagen
                            paintView.saveImage();
                            break;
                        //bij deze case word er een value in de firebase database verandert van false naar true. Dit zorgt ervoor dat de tier begint
                        //voor de "guessers" om te gaan raden wat de tekening is
                        case "done_button":
                            DatabaseManager databaseManager = new DatabaseManager();
                            databaseManager.setIsWoordGekozen(true);

                            checkIsWoordGeraden();
                            break;
                    }
                }
            });
        }
    }

    /**
     * Methode om te kijken of het woord geraden is
     */
    public void checkIsWoordGeraden() {
        // Reference naar de isWoordGeraden variabele
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference isWoordGeradenRef = database.getReference("/lobby/isWoordGeraden");

        // Deze listener kijkt of het woord geraden is en stopt de ronde
        isWoordGeradenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                amountOfFirebaseCalls++;

                // Deze listener moet pas worden uitgevoerd na een verandering, daarom moet eerst gekeken
                // worden naar het aantal calls
                if (amountOfFirebaseCalls == 2) {
                    // WoordenKiezer class Intent
                    Intent intent = new Intent(CanvasActivity.this, WoordenKiezer.class);
                    // Variabele aan deze Intent toevoegen zodat je in de activity weet dan je vanaf deze class komt
                    intent.putExtra("fromCanvasActivity", true);

                    // Activity starten
                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Met behulp van de ambilwarna library word er een colour picket aangemaakt zodra je op een knop drukt
     */
    private void openColourPicker () {
        //hier word de nieuwe colour picker aangemaakt
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            //wanneer je op cancel drukt sluit het de colour picker en krijg je een message op het scherm
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

                Toast.makeText(CanvasActivity.this, "Niet beschikbaar", Toast.LENGTH_LONG).show();

            }

            //hier word de oude kleur verandert naar de nieuwe kleur
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                defaultColor = color;

                paintView.setColor(color);

            }

        });
        //hier word de colour picker getoont
        ambilWarnaDialog.show();

    }
}