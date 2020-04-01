package com.example.drawsomething.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.drawsomething.DrawSomething;
import com.example.drawsomething.callback.GeefPuntenCallback;
import com.example.drawsomething.callback.GekozenWoordCallback;
import com.example.drawsomething.callback.SpelerCallback;
import com.example.drawsomething.callback.WinnerCallback;
import com.example.drawsomething.callback.WoordenCallback;
import com.example.drawsomething.helper.DatabaseManager;
import com.example.drawsomething.R;
import com.example.drawsomething.model.Speler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deze class is een Activity die gebruikt wordt om woorden mee te raden
 * @author R.G. Asciutto
 */

public class GuessWord extends AppCompatActivity {

    // Constante integers voor de game settings
    private final int KIEZEN_COOLDOWN_SECONDS = 10;
    private final int LENGTE_ROUND_SECONDS = 60;
    private final int LENGTE_TUSSEN_RONDES_SECONDS = 10;
    private final int AANTAL_WOORDEN = 16;
    private final int AANTAL_RONDES = 5;

    // Game variabelen
    private DatabaseManager databaseManager;
    private List<String> woordenlijst = new ArrayList<>();
    private Button[] buttons;
    private int rondeCount;
    private CountDownTimer rondeTimer;
    private String juisteWoord;

    // Deze variabelen worden gebruikt om het aantal calls bij te houden
    private int isWoordGeradenCalls = 0;
    private int isWoordGekozenCalls = 0;

    // OnClickListener variabele die gebruikt wordt in een callback
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            woordClickListener((Button) v);
        }
    };

    /**
     * Hier wordt het spel in gestart waarbij de woorden en spelers worden opgehaald
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_word);

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

        // Nieuwe DatabaseManager instance voor Firebase calls
        databaseManager = new DatabaseManager();

        // Het spel begint door deze method aan te roepen
        initGame();

        // Set de speler namen in de scorelijst
        setSpelers();
    }

    /**
     * Deze method wordt gebruikt om het spel mee te starten
     */
    public void initGame() {
        findViewById(R.id.transparent_overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.begin_ronde).setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Listener zetten op de isWoordGeraden variabele en de game starten
        checkIsWoordGeraden(database);
        startGame(database);
    }

    /**
     * Deze method zet een listener op isWoordGeraden om de ronde te stoppen als het juiste woord
     * geraden is
     * @param database FirebaseDatabase instance
     */
    public void checkIsWoordGeraden(FirebaseDatabase database) {
        DatabaseReference isWoordGeradenRef = database.getReference("/lobby/isWoordGeraden");

        // Deze listener kijkt of het woord geraden is en stopt de ronde
        isWoordGeradenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isWoordGeradenCalls++;

                // Deze listener moet pas worden uitgevoerd na een verandering, daarom moet eerst gekeken
                // worden naar het aantal calls
                if (isWoordGeradenCalls == 2) {
                    endRound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Met deze method wordt een listener gezet op isWoordGekozen om het spel te beginnen zodra de
     * tekenaar een woord gekozen heeft. Zodra een woord gekozen is worden alle woorden opgehaald
     * en in de buttons gezet
     * @param database FirebaseDatabase instance
     */
    public void startGame(FirebaseDatabase database) {
        DatabaseReference isWoordGekozenRef = database.getReference("/lobby/isWoordGekozen");

        // Deze listener kijkt of er ee woord gekozen is door de tekenaar
        isWoordGekozenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isWoordGekozenCalls++;

                // Deze listener moet pas worden uitgevoerd na een verandering, daarom moet eerst gekeken
                // worden naar het aantal calls
                if (isWoordGekozenCalls == 2) {
                    findViewById(R.id.transparent_overlay).setVisibility(View.GONE);
                    findViewById(R.id.begin_ronde).setVisibility(View.GONE);

                    // Callback om te wachten tot het gekozen woord opgehaald is
                    databaseManager.getGekozenWoord(new GekozenWoordCallback() {
                        @Override
                        public void onCallback(String gekozenWoord) {
                            juisteWoord = gekozenWoord;

                            // Callback om alle woorden op te halen en in de buttons te stoppen
                            databaseManager.getWoorden(new WoordenCallback() {
                                @Override
                                public void onCallback(List<String> woorden) {
                                    woordenlijst.addAll(woorden);
                                    buttons = getWordButtons();
                                    setJuisteWoordButton();

                                    for (Button button : buttons) {
                                        button.setOnClickListener(onClickListener);
                                    }

                                    // Een ronde begint door een countdown te starten
                                    startCountdown();
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    /**
     * Deze method haalt alle buttons op en vult deze met random woorden
     * @return array van Button objecten
     */
    public Button[] getWordButtons() {
        Button[] buttons = new Button[AANTAL_WOORDEN];

        // Haalt alle buttons op in een loop en voegt ze toe aan de array
        for (int i = 0; i < AANTAL_WOORDEN; i++) {
            String woordID = "woord" + (i + 1);
            int resID = getResources().getIdentifier(woordID, "id", getPackageName());
            buttons[i] = findViewById(resID);

            // Set een random woord uit de woordenlijst als tekst op de button
            buttons[i].setText(getRandomWord());
        }

        return buttons;
    }

    /**
     * Haalt een random woord op uit de lijst
     * @return String random woord
     */
    public String getRandomWord() {
        int randomIndex = (int) (Math.random() * woordenlijst.size());
        String randomWoord = woordenlijst.get(randomIndex);
        woordenlijst.remove(randomIndex);

        return randomWoord;
    }

    /**
     * Nadat alle buttons een random woord hebben, wordt deze method aangeroepen om 1 van
     * de buttons te vervangen met het juiste woord
     */
    public void setJuisteWoordButton() {
        List<String> randomWoorden = new ArrayList<>();

        // Haalt alle random woorden van de buttons op
        for (Button button : buttons) {
            randomWoorden.add((String) button.getText());
        }

        // Als het juiste woord niet al bestaat, dan wordt deze in een random button gezet
        if (!randomWoorden.contains(juisteWoord)) {
            int randomIndex = (int) (Math.random() * AANTAL_WOORDEN) + 1;

            String buttonID = "woord" + randomIndex;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button button = findViewById(resID);

            button.setText(juisteWoord);
        }

    }

    /**
     * Deze method zet een on click op een button met een cooldown
     * @param button de item waar de listener op staat, in dit geval de buttons
     */
    public void woordClickListener(Button button) {
        final TextView status = findViewById(R.id.raden_status);

        // Zodra er geklikt is, wordt de knop grijs
        button.setBackgroundColor(ContextCompat.getColor(button.getContext(), android.R.color.darker_gray));
        button.setTextColor(ContextCompat.getColor(button.getContext(), android.R.color.tertiary_text_light));

        // Kijken of het juiste woord gekozen is
        if (button.getText().equals(juisteWoord)) {
            // Winnaar van de ronde wordt in de database gezet en de ronde eindigt met setIsWoordGeraden
            databaseManager.setWinner(((DrawSomething) getApplication()).getSpeler());
            databaseManager.setIsWoordGeraden(true);
            return;
        }

        // Cooldown timer zodat men moet wachten bij het raden
        new CountDownTimer(KIEZEN_COOLDOWN_SECONDS * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                status.setText("Woord geraden, u kunt over " + millisUntilFinished / 1000 + " seconden weer een woord kiezen");

                for (Button button : buttons) {
                    button.setEnabled(false);
                }
            }

            public void onFinish() {
                status.setText("");

                for (Button button : buttons) {
                    button.setEnabled(true);
                }
            }
        }.start();
    }

    /**
     * Als iemand het juiste woord heeft geraden of de tijd is verlopen, wordt deze
     * method uitgevoerd
     */
    public void endRound() {
        final TextView status = findViewById(R.id.aantal_seconden);

        // Huidige ronde count wordt incremented
        ((DrawSomething) getApplication()).setRondeCount(rondeCount + 1);

        // Als het max aantal rondes bereikt is, stopt de game
        if (((DrawSomething) getApplication()).getRondeCount() > AANTAL_RONDES) {
            stopGame();
            return;
        }

        // Geeft een punt aan de winnaar van een ronde
        geefPuntAanWinnaar();

        rondeTimer.cancel();
        databaseManager.setIsWoordGeraden(true);

        status.setText("");

        // Zet de elementen actief om aan te geven dat een ronde voorbij is
        findViewById(R.id.transparent_overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.einde_ronde_scorelijst).setVisibility(View.VISIBLE);
        findViewById(R.id.einde_ronde_title).setVisibility(View.VISIBLE);

        final TextView eindeRondeStatus = findViewById(R.id.einde_ronde_status);
        eindeRondeStatus.setVisibility(View.VISIBLE);

        // Laat zien wat het juiste woord was
        TextView eindeRondeWoord = findViewById(R.id.einde_ronde_woord);
        eindeRondeWoord.setText(String.format(getResources().getString(R.string.einde_ronde_woord), juisteWoord));
        eindeRondeWoord.setVisibility(View.VISIBLE);

        // Een countdown tussen rondes wordt gestart
        new CountDownTimer(LENGTE_TUSSEN_RONDES_SECONDS * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                eindeRondeStatus.setText(String.format(getResources().getString(R.string.einde_ronde_status), millisUntilFinished / 1000));
            }

            public void onFinish() {
                // Start nieuwe instance van deze Activity
                finish();
                startActivity(getIntent());
            }
        }.start();
    }

    /**
     * Method om de winnaar van een ronde een punt te geven
     */
    public void geefPuntAanWinnaar() {
        // Winnaar van de ronde wordt opgehaald en krijgt een punt
        databaseManager.getWinner(new WinnerCallback() {
            @Override
            public void onCallback(String speler) {
                // Callback om de winnaar een punt te geven en daarna de spelers te refreshen in de View
                databaseManager.geefPunten(speler, new GeefPuntenCallback() {
                    @Override
                    public void onCallback() {
                        getSpelers(true);
                    }
                });
            }
        });
    }

    /**
     * Method voor de countdown timer van elke ronde
     */
    public void startCountdown() {
        final TextView status = findViewById(R.id.aantal_seconden);
        final TextView rondeDisplay = findViewById(R.id.ronde_display);

        // Haalt de huidige ronde count op
        rondeCount = ((DrawSomething) getApplication()).getRondeCount();

        // Countdown tussen de rondes
        rondeTimer = new CountDownTimer(LENGTE_ROUND_SECONDS * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                rondeDisplay.setText("Ronde " + rondeCount + " van " + AANTAL_RONDES);
                status.setText("Nog " + millisUntilFinished / 1000 + " seconden over");
            }

            public void onFinish() {
                // Huidige aantal rondes
                ((DrawSomething) getApplication()).setRondeCount(rondeCount + 1);

                // Kijken of het max aantal rondes bereikt is en de game stoppen
                if (((DrawSomething) getApplication()).getRondeCount() > AANTAL_RONDES) {
                    stopGame();
                } else {
                    databaseManager.setIsWoordGeraden(true);
                }
            }
        }.start();
    }

    /**
     * Deze method wordt gebruikt om de gehele game mee te stoppen
     */
    public void stopGame() {
        final TextView eindeGame = findViewById(R.id.einde_game);
        TextView status = findViewById(R.id.aantal_seconden);
        TextView rondeDisplay = findViewById(R.id.ronde_display);

        rondeTimer.cancel();
        status.setText("Game voorbij");
        rondeDisplay.setText("");

        // Ronde count wordt gereset
        ((DrawSomething) getApplication()).setRondeCount(1);

        findViewById(R.id.transparent_overlay).setVisibility(View.VISIBLE);

        // Alle spelers ophalen om de winnaar eruit te kunnen halen
        databaseManager.readSpelers(new SpelerCallback() {
            @Override
            public void onCallback(final List<Speler> spelers) {
                // Spelers worden gesorteerd op score en de winnaar wordt weergeven
                Collections.sort(spelers);

                eindeGame.setText(String.format(getResources().getString(R.string.einde_game), spelers.get(0).getNaam()));
                eindeGame.setVisibility(View.VISIBLE);

                databaseManager.setIsWoordGekozen(false);
            }
        });
    }

    /**.
     * Deze method haalt de spelers op uit Firebase
     */
    public void setSpelers() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference spelersRef = database.getReference("/lobby/spelers");

        // Haalt alle spelers op en zet ze in het main scherm
        getSpelers(false);

        // Zodra de score geüpdate wordt, worden de spelers opnieuw opgehaald met de nieuwe scores
        spelersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Update de spelers in het main scorelijst en in het scherm van het eind van een ronde
                getSpelers(true);
                getSpelers(false);
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /**
     * Deze method maakt de spelers en scores zichtbaar in de Views
     * @param eindeRonde boolean om aan te geven of het om het einde van een ronde gaat of niet
     */
    public void getSpelers(final boolean eindeRonde) {
        // Callback om de spelers op te halen uit Firebase
        databaseManager.readSpelers(new SpelerCallback() {
            @Override
            public void onCallback(List<Speler> spelers) {
                // Ternary operators om de juiste Views op mee te halen
                TextView spelerNamen = (TextView) ((eindeRonde) ? findViewById(R.id.einde_scorelijst_namen) : findViewById(R.id.scorelijst_namen));
                TextView scores = (TextView) ((eindeRonde) ? findViewById(R.id.einde_scorelijst_scores) : findViewById(R.id.scorelijst_scores));

                // Als het om het einde van een ronde gaat, moeten de Views visible worden
                if (eindeRonde) {
                    spelerNamen.setVisibility(View.VISIBLE);
                    scores.setVisibility(View.VISIBLE);
                }

                spelerNamen.setText("");
                scores.setText("");

                int spelerCount = 1;

                // Spelers sorteren op score
                Collections.sort(spelers);

                // Door alle spelers loopen en de text setten
                for (Speler speler : spelers) {
                    spelerNamen.setText(spelerNamen.getText() + "#" + spelerCount + "\t" + speler.getNaam() + "\n\n");
                    scores.setText((String) scores.getText() + speler.getScore() + "\n\n");

                    spelerCount++;
                }
            }
        });
    }
}
