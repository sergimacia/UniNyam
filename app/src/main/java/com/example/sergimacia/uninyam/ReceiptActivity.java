package com.example.sergimacia.uninyam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private int codiburguer;
    private TextView hamburguesa_view;
    private TextView beguda_view;
    private TextView postres_view;
    private TextView ID_view;
    private TextView preu_view;
    private TextView data_view;
    private TextView hora_view;
    private ImageView estat_view;
    private TextView mida_view;
    private ImageView burguer_icon2;
    private ImageView postres_icon2;
    private ImageView beguda_icon2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_activity);

        hamburguesa_view = findViewById(R.id.hamburguesa_view);
        beguda_view = findViewById(R.id.beguda_view);
        postres_view = findViewById(R.id.postres_view);
        estat_view = findViewById(R.id.estat_icon);
        mida_view=findViewById(R.id.mida_view);
        ID_view=findViewById(R.id.ID_view);
        preu_view=findViewById(R.id.preu_view);
        data_view=findViewById(R.id.data_view);
        hora_view=findViewById(R.id.hora_view);

        burguer_icon2=findViewById(R.id.burguer_icon2);
        beguda_icon2=findViewById(R.id.beguda_icon2);
        postres_icon2=findViewById(R.id.postres_icon2);

        Intent intent = getIntent();
        if (intent!= null){
            codi=intent.getIntExtra("codi",-1);
            codiburguer=intent.getIntExtra("codiburguer", -1);
        }
        if(codiburguer==1){
            Glide.with(this).load("file:///android_asset/burger.png").into(burguer_icon2);
        }
        if(codiburguer==2){
            Glide.with(this).load("file:///android_asset/no_formatge_tomaquet_enciam.png").into(burguer_icon2);
        }
        if(codiburguer==3){
            Glide.with(this).load("file:///android_asset/no_tomaquet_enciam.png").into(burguer_icon2);
        }
        if(codiburguer==4){
            Glide.with(this).load("file:///android_asset/no_enciam.png").into(burguer_icon2);
        }
        if(codiburguer==5){
            Glide.with(this).load("file:///android_asset/no_tomaquet_formatge.png").into(burguer_icon2);
        }
        if(codiburguer==6){
            Glide.with(this).load("file:///android_asset/no_formatge.png").into(burguer_icon2);
        }
        if(codiburguer==7){
            Glide.with(this).load("file:///android_asset/no_tomaquet.png").into(burguer_icon2);
        }
        if(codiburguer==8){
            Glide.with(this).load("file:///android_asset/no_enciam_formatge.png").into(burguer_icon2);
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
                mida_view.setText(lamevacomanda.getMida());

                int preu = lamevacomanda.getPreu();
                preu_view.setText(Integer.toString(preu));

                int id = lamevacomanda.getCodi();
                ID_view.setText(Integer.toString(id));

                double data = lamevacomanda.getData();
                double anyL = data/100000000;
                int anyI=(int)anyL;
                String any = Integer.toString(anyI);

                double mesL = data/1000000;
                mesL = mesL%100;
                int mesI = (int)mesL;
                String mes = Integer.toString(mesI);

                double diaL = data/10000;
                diaL=diaL%100;
                int diaI=(int)diaL;
                String dia = Integer.toString(diaI);

                data_view.setText(dia + "/" + mes +"/" + any);

                double horaL=data/100;
                horaL=horaL%100;
                int horaI=(int)horaL;
                String hora = Integer.toString(horaI);

                double minutL=data%100;
                int minutI=(int)minutL;
                String minut=Integer.toString(minutI);

                hora_view.setText(hora +":" + minut);

                if(lamevacomanda.getBeguda().equals("Suc")){
                    Glide.with(ReceiptActivity.this).load("file:///android_asset/orange_juice2.png").into(beguda_icon2);
                }
                if(lamevacomanda.getBeguda().equals("Aigua")){
                    Glide.with(ReceiptActivity.this).load("file:///android_asset/water2.png").into(beguda_icon2);
                }
                if(lamevacomanda.getBeguda().equals("Coca-Cola")){
                    Glide.with(ReceiptActivity.this).load("file:///android_asset/soda2.png").into(beguda_icon2);
                }

                if(lamevacomanda.getPostres().equals("Cupcake")){
                    Glide.with(ReceiptActivity.this).load("file:///android_asset/cupcake2.png").into(postres_icon2);
                }
                if(lamevacomanda.getPostres().equals("Pastís")){
                    Glide.with(ReceiptActivity.this).load("file:///android_asset/cake2.png").into(postres_icon2);
                }
                if(lamevacomanda.getPostres().equals("Fruita")){
                    Glide.with(ReceiptActivity.this).load("file:///android_asset/banana2.png").into(postres_icon2);
                }
                if(lamevacomanda.getPostres().equals("Gelat")){
                    Glide.with(ReceiptActivity.this).load("file:///android_asset/icecream2.png").into(postres_icon2);
                }

            }
        });
    }
}