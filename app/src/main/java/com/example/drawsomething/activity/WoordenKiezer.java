package com.example.drawsomething.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.drawsomething.R;
import com.example.drawsomething.callback.WoordenCallback;
import com.example.drawsomething.helper.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Deze Class zorgt dat de gebruiker uit 4 woorden kan kiezen om te raden.
 * @author Hooshang Kooshani
 */

public class WoordenKiezer extends AppCompatActivity {

    private final int MAX_WOORDEN_VOOR_KEUZE = 4;
    private final int TIJD_TUSSEN_RONDES = 10;

    private DatabaseManager databaseManager;
    private List<String> woordenlijst = new ArrayList<>();
    private Button[] buttons;
    private Button tabletButton;
    private Button kinectButton;
    private int woordNum;
    private static String[] checkWoord;
    private String randomWoord;
    private int amountOfFirebaseCalls = 0;

    /**
     * In deze method wordt de activity gestart, onClick listeners op de kinect en tablet buttons gezet
     * en worden de woorden ingeladen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

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

        tabletButton = findViewById(R.id.tablet_button);
        kinectButton = findViewById(R.id.kinect_button);

        tabletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dit start de CanvasActivity op op de tablet te tekenen
                finish();
                startActivity(new Intent(WoordenKiezer.this, CanvasActivity.class));
            }
        });

        kinectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Laat zien aan de speler dat het woord gekozen is en hij kan tekenen
                findViewById(R.id.keuze_title).setVisibility(View.GONE);
                findViewById(R.id.woord_gekozen).setVisibility(View.VISIBLE);

                // Maak de buttons onzichtbaar na de keuze
                tabletButton.setVisibility(View.GONE);
                kinectButton.setVisibility(View.GONE);

                // Geef aan in Firebase dat het woord gekozen is
                databaseManager.setIsWoordGekozen(true);

                // Disable de woord buttons
                for (Button button : buttons) {
                    button.setEnabled(false);
                }
            }
        });

        databaseManager = new DatabaseManager();

        // Hier worden de woorden opgehaald voor in de knoppen
        databaseManager.getWoorden(new WoordenCallback() {
            @Override
            public void onCallback(List<String> woorden) {
                woordenlijst.addAll(woorden);
                woordenCheckerMethod();

                buttons = getButtons();

                // Loopt door de woord buttons heen en zet een onClick listener
                for (Button button : buttons) {
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Sla in Firebase op welk woord er gekozen is
                            databaseManager.setGekozenWoord((String) ((Button) v).getText());

                            // Maakt de transparante overlay visible
                            findViewById(R.id.transparent_overlay).setVisibility(View.VISIBLE);
                            findViewById(R.id.keuze_title).setVisibility(View.VISIBLE);

                            // Maakt de tablet en kinect buttons visible
                            tabletButton.setVisibility(View.VISIBLE);
                            kinectButton.setVisibility(View.VISIBLE);
                        }
                    });
                }

                // Zet een listener om te kijken of het woord geraden is
                checkIsWoordGeraden();

                // Als de speler vanuit de CanvasActivity komt, wordt endRound uitgevoerd
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean("fromCanvasActivity")) endRound();
                }
            }
        });
    }

    /**
     * Dit zorgt ervoor dat er 4 woorden worden gekozen
     */
    public void woordenCheckerMethod() {
        checkWoord = new String[MAX_WOORDEN_VOOR_KEUZE];

        for (int i = 0; i < MAX_WOORDEN_VOOR_KEUZE; i++) {
            woordNum = (int) (Math.random() * woordenlijst.size());
            randomWoord = woordenlijst.get(woordNum);
            woordenlijst.remove(woordNum);
            checkWoord[i] = randomWoord;
        }
    }

    /**
     * Dit zorgt ervoor dat de knoppen de woorden krijgen
     * @return
     */
    public Button[] getButtons() {
        Button[] buttons = new Button[MAX_WOORDEN_VOOR_KEUZE];

        // Loopt door de woord buttons heen
        for (int i = 0; i < MAX_WOORDEN_VOOR_KEUZE; i++) {
            // Haalt de ID op van een woord button
            String woordID = "woord_kiezen" + (i + 1);
            int resID = getResources().getIdentifier(woordID, "id", getPackageName());
            buttons[i] = findViewById(resID);

            // Set de text van de button naar 1 van de random gekozen woorden
            buttons[i].setText(checkWoord[i]);
        }

        return buttons;
    }

    /**
     * Checkt of het woord is geraden
     */
    public void checkIsWoordGeraden() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference isWoordGeradenRef = database.getReference("/lobby/isWoordGeraden");

        isWoordGeradenRef.addValueEventListener(new ValueEventListener() {
            /**
             * Geeft aan dat het woord is geraden en de resterende tijd van de ronde
             * @param dataSnapshot de data dat opgehaald is
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                amountOfFirebaseCalls++;

                // Deze listener moet pas worden uitgevoerd na een verandering, daarom moet eerst gekeken
                // worden naar het aantal calls
                if (amountOfFirebaseCalls == 2) {
                    endRound();
                }
            }

            /**
             * Als er een fout optreedt in de database query, handelt dit hem af
             * @param databaseError Firebase query foutmelding
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Dit zorgt ervoor dat er een timer wordt gestart tussen de rondes en de tekenaar daarna
     * opnieuw een woord kan kiezen
     */
    public void endRound() {
        final TextView status = findViewById(R.id.woord_gekozen);

        // Zet de transparante overlay visible
        findViewById(R.id.transparent_overlay).setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);

        // Reset de game status variabelen
        databaseManager.setIsWoordGekozen(false);
        databaseManager.setIsWoordGeraden(false);

        // Disable alle buttons
        for (Button button : buttons) {
            button.setEnabled(false);
        }

        // Start een timer tussen de rondes
        new CountDownTimer(TIJD_TUSSEN_RONDES * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                status.setText("Het woord is geraden, over " + millisUntilFinished / 1000 + " seconden kan er weer een woord gekozen worden om te tekenen");
            }

            /**
             * Als het woord is geraden dan mag er weer een woord worden gekozen en wordt het vorige gekozen woord gereset
             */
            public void onFinish() {
                finish();
                startActivity(new Intent(WoordenKiezer.this, WoordenKiezer.class));
            }
        }.start();
    }
}