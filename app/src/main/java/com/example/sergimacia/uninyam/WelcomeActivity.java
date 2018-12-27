package com.example.sergimacia.uninyam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    EditText nom_view;
    EditText email_view;
    private int despesa=0;
    private String nom="";
    private String email="";
    private String userId="";
    private ImageView app_icon;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usuarisRef = db.collection("Usuaris");

    private static final String TAG = "WelcomeActivity";

    private String asset (String imgName){
        return "file:///android_asset/"+imgName+".png";
    }

    private void updateImg(String imgName, ImageView imgView){
        Glide.with(this).load(asset(imgName)).into(imgView);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        nom_view=findViewById(R.id.nom_view);
        email_view=findViewById(R.id.email_view);
        app_icon=findViewById(R.id.app_icon);

        updateImg("portada", app_icon);
    }

    public void onLogin (View v) {
        nom = nom_view.getText().toString();
        email= email_view.getText().toString();

        //Es crea l'usuari
        Usuari usuari = new Usuari(nom, email, despesa);

        //Es desa l'usuari a Firebase
        usuarisRef.add(usuari).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(WelcomeActivity.this, "Usuari registrat", Toast.LENGTH_SHORT).show();

                //Es desa l'usuari a SharedPreferences del mòbil
                SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
                userId = documentReference.getId();
                prefs.edit()
                        .putString("id", userId)
                        .putString("nom", nom)
                        .commit();
                Intent data = new Intent();
                data.putExtra("userId", userId);
                setResult(RESULT_OK, data);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WelcomeActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });
    }
}