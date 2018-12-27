package com.example.sergimacia.uninyam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String ingredients;
    private TextView hamburguesa_view;
    private TextView beguda_view;
    private TextView postres_view;
    private TextView ID_view;
    private TextView preu_view;
    private TextView data_view;
    private TextView estatescrit_view;
    private TextView hora_view;
    private ImageView estat_view;
    private TextView mida_view;
    private ImageView burguer_icon2;
    private ImageView postres_icon2;
    private ImageView beguda_icon2;

    private String asset (String imgName){
        return "file:///android_asset/"+imgName+".png";
    }

    private void updateImg(String imgName, ImageView imgView){
        Glide.with(this).load(asset(imgName)).into(imgView);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_activity);

        hamburguesa_view = findViewById(R.id.hamburguesa_view);
        beguda_view = findViewById(R.id.beguda_view);
        postres_view = findViewById(R.id.postres_view);
        estat_view = findViewById(R.id.estat_icon);
        mida_view=findViewById(R.id.mida_view);
        estatescrit_view=findViewById(R.id.estatescrit_view);
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
            ingredients=intent.getStringExtra("ingredients");
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Espera a rebre la teva comanda", Toast.LENGTH_SHORT).show();
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

                //Snapshot de la comanda especifica
                for (DocumentSnapshot documentSnapshot : documentSnapshots){
                    Comanda comanda = documentSnapshot.toObject(Comanda.class); //Descarrega comanda del Firebase
                    if (codi == comanda.getCodi()){
                        lamevacomanda= comanda;
                    }
                }

                //Estats comanda
                if(lamevacomanda.getEstat()==0) {
                    updateImg("cuinant", estat_view);
                    estatescrit_view.setText("Cuinant");
                }
                else if(lamevacomanda.getEstat()==1){
                    updateImg("per menjar", estat_view);
                    estatescrit_view.setText("Reculli la seva comanda");
                }

                //Text comanda
                if(!lamevacomanda.getHamburguesa().equals("no_burger")) hamburguesa_view.setText(lamevacomanda.getHamburguesa());
                beguda_view.setText(lamevacomanda.getBeguda());
                postres_view.setText(lamevacomanda.getPostres());
                if(!lamevacomanda.getBeguda().equals("")) mida_view.setText(lamevacomanda.getMida());

                //Carrega imatges
                updateImg(lamevacomanda.getBeguda(), beguda_icon2);
                updateImg(lamevacomanda.getPostres(), postres_icon2);
                if(!ingredients.equals("no_burger"))updateImg(ingredients, burguer_icon2);
                else updateImg("", burguer_icon2);

                //Carrega preu i ID
                int preu = lamevacomanda.getPreu();
                preu_view.setText(Integer.toString(preu));
                int id = lamevacomanda.getCodi();
                ID_view.setText(Integer.toString(id));

                //Carrega data
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

                //Carrega hora
                double horaL=data/100;
                horaL=horaL%100;
                int horaI=(int)horaL;
                String hora = Integer.toString(horaI);

                double minutL=data%100;
                int minutI=(int)minutL;
                String minut=Integer.toString(minutI);

                hora_view.setText(hora +":" + minut);

            }
        });
    }
}