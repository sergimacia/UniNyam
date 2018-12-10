package com.example.sergimacia.uninyam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class WelcomeActivity extends AppCompatActivity {

    EditText nom_view;
    EditText email_view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        nom_view=findViewById(R.id.nom_view);
        email_view=findViewById(R.id.email_view);
    }

    public void onLogin (View v) {
        String nom = nom_view.getText().toString();
        String email= email_view.getText().toString();

        // TODO: Escriure a Firebase l'objecte usuari i obtenir l'id.
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        // Exemple per llegir el userId.
        String currentUser = prefs.getString("userId", null);

        // Escriure el userId
        prefs.edit().putString("userId", "29487239487239487").commit();
    }


}
