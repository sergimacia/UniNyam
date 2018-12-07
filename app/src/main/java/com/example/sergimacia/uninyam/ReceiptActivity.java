package com.example.sergimacia.uninyam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class ReceiptActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference comandaRef = db.collection("Comandes");

    Comanda lamevacomanda;
    private int codi;
    private TextView hamburguesa_view;
    private TextView beguda_view;
    private TextView postres_view;
    private ImageView estat_view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_activity);

        hamburguesa_view = findViewById(R.id.hamburguesa_view);
        beguda_view = findViewById(R.id.beguda_view);
        postres_view = findViewById(R.id.postres_view);
        estat_view = findViewById(R.id.estat_icon);

        Intent intent = getIntent();
        if (intent!= null){
            codi=intent.getIntExtra("codi",-1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        comandaRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }

                for (DocumentSnapshot documentSnapshot : documentSnapshots){
                    Comanda comanda = documentSnapshot.toObject(Comanda.class); //Descarrega comanda del Firebase
                    if (codi == comanda.getCodi()){
                        lamevacomanda= comanda;
                    }
                }

                hamburguesa_view.setText(lamevacomanda.getHamburguesa());
                beguda_view.setText(lamevacomanda.getBeguda());
                postres_view.setText(lamevacomanda.getPostres());

            }
        });
    }
}