package com.example.drawsomething.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drawsomething.R;

public class InlogAdmin extends AppCompatActivity {
    private final int STANDAARD_PINCODE = 1234;
    private Button inloggen;
    private EditText pincode;
    private String pincodeText;
    private int pincodeInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inlog_admin);
        invoerPincode();
    }

    public void invoerPincode() {
        inloggen = findViewById(R.id.button);
        pincode = findViewById(R.id.editText7);
        inloggen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincodeText = pincode.getText().toString();
                pincodeInt = Integer.parseInt(pincodeText);
                if (pincodeInt == STANDAARD_PINCODE) {
                    startActivity(new Intent(InlogAdmin.this, WoordenBeheren.class));
                } else {
                    Toast.makeText(InlogAdmin.this, " Pincode is niet correct", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
