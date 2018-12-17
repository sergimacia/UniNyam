package com.example.sergimacia.uninyam;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
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
import android.content.SharedPreferences;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
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
    private String hamburguesa="";
    private String beguda="";
    private String mida="";
    private String postres="";
    private int codi=0;
    private int preu=0;
    private int estat=0;
    private int codiburguer=1;
    private RadioButton radiobutton_cocacola;
    private RadioButton radiobutton_aigua;
    private RadioButton radiobutton_fanta;
    private RadioButton radiobutton_suc;
    private RadioButton radiobutton_pastis;
    private RadioButton radiobutton_gelat;
    private RadioButton radiobutton_cupcake;
    private RadioButton radiobutton_fruita;
    private String ruta="file:///android_asset/burger.png";
    private Gson gson;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference comandaRef = db.collection("Comandes");
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Es verifica si l'usuari ja està autenticat en l'aplicació.
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);

        userId = prefs.getString("id", null);
        if (userId != null) {
            // Ja existeix un usuari
            Log.e("ATENCIO",userId);
        } else {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            intent.putExtra("id", userId);
            startActivityForResult(intent, ENVIA);
            userId = intent.getStringExtra("userId");
        }


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
        radiobutton_cocacola = findViewById(R.id.btn_cocacola);
        radiobutton_aigua = findViewById(R.id.btn_aigua);
        radiobutton_fanta = findViewById(R.id.btn_fanta);
        radiobutton_suc = findViewById(R.id.btn_suc);
        radiobutton_cupcake = findViewById(R.id.btn_cupcake);
        radiobutton_fruita = findViewById(R.id.btn_fruita);
        radiobutton_gelat = findViewById(R.id.btn_gelat);
        radiobutton_pastis = findViewById(R.id.btn_pastis);
        radiogroup_postres = findViewById(R.id.radio_postres);
        burguer_icon=findViewById(R.id.burguer_icon);
        postres_icon=findViewById(R.id.postres_icon);
        beguda_icon=findViewById(R.id.beguda_icon);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        checkbox_burger.setOnClickListener(this);
        checkbox_beguda.setOnClickListener(this);
        checkbox_postres.setOnClickListener(this);

        formatge_switch.setOnClickListener(this);
        tomaquet_switch.setOnClickListener(this);
        enciam_switch.setOnClickListener(this);

        radiobutton_aigua.setOnClickListener(this);
        radiobutton_cocacola.setOnClickListener(this);
        radiobutton_fanta.setOnClickListener(this);
        radiobutton_suc.setOnClickListener(this);

        radiobutton_cupcake.setOnClickListener(this);
        radiobutton_fruita.setOnClickListener(this);
        radiobutton_pastis.setOnClickListener(this);
        radiobutton_gelat.setOnClickListener(this);

        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mida, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Glide.with(this).load("file:///android_asset/burger.png").into(burguer_icon);
        Glide.with(this).load("file:///android_asset/cupcake2.png").into(postres_icon);
        Glide.with(this).load("file:///android_asset/soda2.png").into(beguda_icon);

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
        if(v==checkbox_burger){
            if(!checkbox_burger.isChecked()) Glide.with(this).load("file:///android_asset/no_burger.png").into(burguer_icon);
            if(checkbox_burger.isChecked()) Glide.with(this).load(ruta).into(burguer_icon);

            tomaquet_switch.setEnabled(checkbox_burger.isChecked());
            formatge_switch.setEnabled(checkbox_burger.isChecked());
            enciam_switch.setEnabled(checkbox_burger.isChecked());

        }
        if (v == checkbox_beguda){
            radiobutton_cocacola.setEnabled(checkbox_beguda.isChecked());
            radiobutton_aigua.setEnabled(checkbox_beguda.isChecked());
            radiobutton_fanta.setEnabled(checkbox_beguda.isChecked());
            radiobutton_suc.setEnabled(checkbox_beguda.isChecked());
        }

        if (v==checkbox_postres){
            radiobutton_pastis.setEnabled(checkbox_postres.isChecked());
            radiobutton_gelat.setEnabled(checkbox_postres.isChecked());
            radiobutton_fruita.setEnabled(checkbox_postres.isChecked());
            radiobutton_cupcake.setEnabled(checkbox_postres.isChecked());

            if(checkbox_postres.isChecked()){
                radiogroup_postres.setEnabled(true);
            }
            else{
                radiogroup_postres.setEnabled(false);
            }
        }

        if(v==enciam_switch | v==formatge_switch | v==tomaquet_switch){
            if(enciam_switch.isChecked() && tomaquet_switch.isChecked() && formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/burger.png").into(burguer_icon);
                ruta="file:///android_asset/burger.png";
                codiburguer=1;
            }
            if(!enciam_switch.isChecked() && !tomaquet_switch.isChecked() && !formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/no_formatge_tomaquet_enciam.png").into(burguer_icon);
                ruta="file:///android_asset/no_formatge_tomaquet_enciam.png";
                codiburguer=2;
            }
            if(!enciam_switch.isChecked() && !tomaquet_switch.isChecked() && formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/no_tomaquet_enciam.png").into(burguer_icon);
                ruta ="file:///android_asset/no_tomaquet_enciam.png";
                codiburguer=3;
            }
            if(!enciam_switch.isChecked() && tomaquet_switch.isChecked() && formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/no_enciam.png").into(burguer_icon);
                ruta ="file:///android_asset/no_enciam.png";
                codiburguer=4;
            }
            if(enciam_switch.isChecked() && !tomaquet_switch.isChecked() && !formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/no_tomaquet_formatge.png").into(burguer_icon);
                ruta ="file:///android_asset/no_tomaquet_formatge.png";
                codiburguer=5;
            }
            if(enciam_switch.isChecked() && tomaquet_switch.isChecked() && !formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/no_formatge.png").into(burguer_icon);
                ruta ="file:///android_asset/no_formatge.png";
                codiburguer=6;
            }
            if(enciam_switch.isChecked() && !tomaquet_switch.isChecked() && formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/no_tomaquet.png").into(burguer_icon);
                ruta ="file:///android_asset/no_tomaquet.png";
                codiburguer=7;
            }
            if(!enciam_switch.isChecked() && tomaquet_switch.isChecked() && !formatge_switch.isChecked()){
                Glide.with(this).load("file:///android_asset/no_enciam_formatge.png").into(burguer_icon);
                ruta ="file:///android_asset/no_enciam_formatge.png";
                codiburguer=8;
            }
        }

        if(v==radiobutton_aigua){
            Glide.with(this).load("file:///android_asset/water2.png").into(beguda_icon);
        }
        if(v==radiobutton_cocacola){
            Glide.with(this).load("file:///android_asset/soda2.png").into(beguda_icon);
        }
        if(v==radiobutton_suc){
            Glide.with(this).load("file:///android_asset/orange_juice2.png").into(beguda_icon);
        }

        if (v == radiobutton_cupcake) {
            Glide.with(this).load("file:///android_asset/cupcake2.png").into(postres_icon);
        }
        if (v == radiobutton_gelat) {
            Glide.with(this).load("file:///android_asset/icecream2.png").into(postres_icon);
        }
        if (v == radiobutton_fruita) {
            Glide.with(this).load("file:///android_asset/banana2.png").into(postres_icon);
        }
        if (v == radiobutton_pastis) {
            Glide.with(this).load("file:///android_asset/cake2.png").into(postres_icon);
        }

    }

    public void  saveComanda (View v){
        if (data ==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No has introduït les dades correctament");
            builder.setNeutralButton(android.R.string.cancel, null);
            builder.create().show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ja ho tens tot? Segur que vols enviar la comanda?");
            builder.setPositiveButton("Sí!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (checkbox_burger.isChecked()) {
                        preu += 4;
                        if (formatge_switch.isChecked()) {
                            hamburguesa = hamburguesa + " formatge ";
                        }
                        if (enciam_switch.isChecked()) {
                            hamburguesa = hamburguesa + " enciam ";
                        }
                        if (tomaquet_switch.isChecked()) {
                            hamburguesa = hamburguesa + " tomaquet ";
                        }
                    }

                    if (checkbox_beguda.isChecked()) {
                        preu += 2;
                        int selectedId = radiogroup_beguda.getCheckedRadioButtonId();
                        btn_begudatriada = (RadioButton) findViewById(selectedId);
                        beguda = btn_begudatriada.getText().toString();
                    }

                    if (checkbox_postres.isChecked()) {
                        preu += 3;
                        int selectedId = radiogroup_postres.getCheckedRadioButtonId();
                        btn_postrestriades = (RadioButton) findViewById(selectedId);
                        postres = btn_postrestriades.getText().toString();
                    }

                    long milisegSys = System.currentTimeMillis();
                    milisegSys = milisegSys % 100;
                    int miliseg = (int) milisegSys;

                    final int random = new Random().nextInt(99);

                    codi = miliseg * 100 + random;

                    Comanda comanda = new Comanda(hamburguesa, beguda, postres, codi, data, preu, estat, mida, userId);

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

                    Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                    intent.putExtra("codi", codi);
                    intent.putExtra("codiburguer", codiburguer);
                    startActivityForResult(intent, ENVIA);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mida = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}