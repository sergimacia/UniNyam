package com.example.sergimacia.uninyam;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Switch ketchup_switch;
    private Switch enciam_switch;
    private Switch tomaquet_switch;
    private CheckBox checkbox_burger;
    private CheckBox checkbox_beguda;
    private CheckBox checkbox_postres;
    private RadioGroup radiogroup_beguda;
    private RadioGroup radiogroup_postres;
    private RadioButton btn_begudatriada;
    private RadioButton btn_postrestriades;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference comandaRef = db.document("Comandes/comanda");

    /*@Override
    protected void onStart() {
        super.onStart();
        comandaRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(e!= null){
                    Toast.makeText(MainActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
                if (documentSnapshot.exists()){
                    Comanda comanda = documentSnapshot.toObject(Comanda.class);

                    String title = comanda.getTitle();
                    String title = comanda.getTitle();
                    String title = comanda.getTitle();
                    String title = comanda.getTitle();
                    String description = comanda.getDescription();

                    textViewData.setText("Title: " + title + "\n" + "Description: " + description);

                }else {
                    textViewData.setText("");
                }
            }
        });
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_menu);
        //setContentView(R.layout.activity_main);

        checkbox_beguda = findViewById(R.id.checkbox_beguda);
        checkbox_postres = findViewById(R.id.checkbox_postres);
        checkbox_burger = findViewById(R.id.checkbox_burger);
        ketchup_switch = findViewById(R.id.ketchup_switch);
        enciam_switch = findViewById(R.id.enciam_switch);
        tomaquet_switch = findViewById(R.id.tomaquet_switch);
        radiogroup_beguda = findViewById(R.id.radio_beguda);
        radiogroup_postres = findViewById(R.id.radio_postres);
    }

    public void  saveComanda (View v){
        String hamburguesa="";
        String beguda="";
        String postres="";
        int codi=0;
        int data=0;
        int preu=0;
        int estat=0;

        if (checkbox_burger.isChecked()){
            preu+=4;
            if (ketchup_switch.isChecked()){
                hamburguesa = hamburguesa + " ketchup ";
            }
            if (enciam_switch.isChecked()){
                hamburguesa = hamburguesa + " enciam ";
            }
            if (tomaquet_switch.isChecked()){
                hamburguesa = hamburguesa + " tomaquet ";
            }
        }

        if(checkbox_beguda.isChecked()){
            preu+=2;
            int selectedId= radiogroup_beguda.getCheckedRadioButtonId();
            btn_begudatriada=(RadioButton)findViewById(selectedId);
            beguda = btn_begudatriada.getText().toString();
        }

        if(checkbox_postres.isChecked()){
            preu+=3;
            int selectedId= radiogroup_postres.getCheckedRadioButtonId();
            btn_postrestriades=(RadioButton)findViewById(selectedId);
            postres = btn_postrestriades.getText().toString();
        }

        Comanda comanda = new Comanda (hamburguesa, beguda, postres, codi, data, preu, estat);

        comandaRef.set(comanda)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    /*public void deleteComanda(View v){
        comandaRef.delete();
    }*/

}