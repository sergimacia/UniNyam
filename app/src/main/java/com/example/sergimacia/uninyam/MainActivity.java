package com.example.sergimacia.uninyam;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int ENVIA = 1;

    private Switch formatge_switch;
    private Switch enciam_switch;
    private Switch tomaquet_switch;
    private CheckBox checkbox_burger;
    private CheckBox checkbox_beguda;
    private CheckBox checkbox_postres;
    private RadioGroup radiogroup_beguda;
    private RadioGroup radiogroup_postres;
    private RadioButton btn_begudatriada;
    private RadioButton btn_postrestriades;
    private ImageView burguer_icon;
    private ImageView postres_icon;
    private ImageView beguda_icon;
    private double data = 0;

    private Gson gson;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference comandaRef = db.collection("Comandes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_menu);

        gson= new Gson();

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtDate=(EditText)findViewById(R.id.in_date);
        txtTime=(EditText)findViewById(R.id.in_time);

        checkbox_beguda = findViewById(R.id.checkbox_beguda);
        checkbox_postres = findViewById(R.id.checkbox_postres);
        checkbox_burger = findViewById(R.id.checkbox_burger);
        formatge_switch = findViewById(R.id.formatge_switch);
        enciam_switch = findViewById(R.id.enciam_switch);
        tomaquet_switch = findViewById(R.id.tomaquet_switch);
        radiogroup_beguda = findViewById(R.id.radio_beguda);
        radiogroup_postres = findViewById(R.id.radio_postres);
        burguer_icon=findViewById(R.id.burguer_icon);
        postres_icon=findViewById(R.id.postres_icon);
        beguda_icon=findViewById(R.id.beguda_icon);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        Glide.with(this).load("file:///android_asset/burger.png").into(burguer_icon);
        Glide.with(this).load("file:///android_asset/cake.jpg").into(postres_icon);
        Glide.with(this).load("file:///android_asset/water.jpg").into(beguda_icon);


    }

    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            data = data+((double) year*100000000) +(((double)monthOfYear+1)*1000000)+(((double)dayOfMonth)*10000);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            txtTime.setText(hourOfDay + ":" + minute);
                            data = data + (((double)hourOfDay*100)+1) + ((double)minute*1);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    public void  saveComanda (View v){
        String hamburguesa="";
        String beguda="";
        String postres="";
        int codi=0;
        int preu=0;
        int estat=0;

        if (checkbox_burger.isChecked()){
            preu+=4;
            if (formatge_switch.isChecked()){
                hamburguesa = hamburguesa + " formatge ";
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

        long milisegSys = System.currentTimeMillis();
        milisegSys=milisegSys%100;
        int miliseg= (int)milisegSys;

        final int random=new Random().nextInt(99);

        codi=miliseg*100+random;


        Comanda comanda = new Comanda (hamburguesa, beguda, postres, codi, data, preu, estat);

        comandaRef.add(comanda).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(MainActivity.this, "Comandada guardada", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });

        Intent intent = new Intent(this,ReceiptActivity.class);
        intent.putExtra("codi", codi);
        startActivityForResult(intent,ENVIA);

    }

}