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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import android.content.SharedPreferences;
import java.util.Calendar;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int ENVIA = 1;
    private Gson gson;

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
    private double data = 0, data_actual=0, data_escollida=0;
    private String ingredients="burger";
    private String hamburguesa="";
    private String hamburguesa_idioma="";
    private String beguda="";
    private String mida="";
    private String postres="";
    private int codi=0;
    private int preu=0;
    private int estat=0;
    private RadioButton radiobutton_cocacola;
    private RadioButton radiobutton_aigua;
    private RadioButton radiobutton_suc;
    private RadioButton radiobutton_cervesa;
    private RadioButton radiobutton_pastis;
    private RadioButton radiobutton_gelat;
    private RadioButton radiobutton_cupcake;
    private RadioButton radiobutton_fruita;
    private String rutabeguda="Coca-Cola";
    private String rutapostres="Cupcake";

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference comandaRef = db.collection("Comandes");
    private String userId, comandaId="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Es verifica si l'usuari ja està autenticat en l'aplicació. S'utilitzen les SharedPreferences.
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);

        userId = prefs.getString("id", null);
        if (userId != null) {
            //Si ja existeix l'usuari no es fa cap acció addicional.
            Log.e("ATENCIO",userId);
        } //Si no existeix l'usuari es mostra WelcomeActivity.
        else {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            intent.putExtra("id", userId);
            startActivityForResult(intent, ENVIA);
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
        radiobutton_cervesa = findViewById(R.id.btn_cervesa);
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
        radiobutton_cervesa.setOnClickListener(this);
        radiobutton_suc.setOnClickListener(this);

        radiobutton_cupcake.setOnClickListener(this);
        radiobutton_fruita.setOnClickListener(this);
        radiobutton_pastis.setOnClickListener(this);
        radiobutton_gelat.setOnClickListener(this);

        //Creació del dropdown de selecció de mida del menú.
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mida, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Glide.with(this).load("file:///android_asset/burger.png").into(burguer_icon);
        Glide.with(this).load("file:///android_asset/Cupcake.png").into(postres_icon);
        Glide.with(this).load("file:///android_asset/Coca-Cola.png").into(beguda_icon);

    }

    //Generació ruta obtenció imatges d'assets.
    private String asset (String imgName, boolean isGray){
        String gray="";
        if(isGray) gray="no_";
        return "file:///android_asset/"+gray+imgName+".png";
    }

    //Actualització de la miniatura del producte.
    private void updateImg(String imgName, boolean isGray, ImageView imgView){
        Glide.with(this).load(asset(imgName, isGray)).into(imgView);
        if(!isGray) {
            if (imgView.equals(beguda_icon)) rutabeguda=imgName;
            else if (imgView.equals(postres_icon)) rutapostres=imgName;
        }
    }

    public void onClick(View v) {
        //Generació dels diàlegs de selecció de data i hora.
        if (v == btnDatePicker) {
            //Obtenció data actual
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            data_escollida=0;
            data=data%1000;
            data=data*1000;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            //data=any+mes+dia+hora+minut
                            data = data+((double) year*100000000) +(((double)monthOfYear+1)*1000000)+(((double)dayOfMonth)*10000);
                            data_escollida=data_escollida+((double) year*100000000) +(((double)monthOfYear+1)*1000000)+(((double)dayOfMonth)*10000);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == btnTimePicker) {
            //Obtenció temps actual
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            data=data/10000;
            data=data*10000;

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

        //Verifica si l'usuari ha triat o no hamburguesa.
        if(v==checkbox_burger){
            if(!checkbox_burger.isChecked()){
                updateImg("burger", true, burguer_icon);
                hamburguesa="no_burger";
            }
            if(checkbox_burger.isChecked()){
                updateImg(ingredients, false, burguer_icon);
                hamburguesa="";
            }

            tomaquet_switch.setEnabled(checkbox_burger.isChecked());
            formatge_switch.setEnabled(checkbox_burger.isChecked());
            enciam_switch.setEnabled(checkbox_burger.isChecked());
        }

        //Verifica si l'usuari ha triat o no beguda.
        if (v == checkbox_beguda){
            if(!checkbox_beguda.isChecked()) updateImg(rutabeguda, true, beguda_icon);
            if(checkbox_beguda.isChecked()) updateImg(rutabeguda, false, beguda_icon);

            radiobutton_cocacola.setEnabled(checkbox_beguda.isChecked());
            radiobutton_aigua.setEnabled(checkbox_beguda.isChecked());
            radiobutton_cervesa.setEnabled(checkbox_beguda.isChecked());
            radiobutton_suc.setEnabled(checkbox_beguda.isChecked());
        }

        //Verifica si l'usuari ha triat o no postres.
        if (v==checkbox_postres){
            if(!checkbox_postres.isChecked()) updateImg(rutapostres, true, postres_icon);
            if(checkbox_postres.isChecked()) updateImg(rutapostres, false, postres_icon);

            radiobutton_pastis.setEnabled(checkbox_postres.isChecked());
            radiobutton_gelat.setEnabled(checkbox_postres.isChecked());
            radiobutton_fruita.setEnabled(checkbox_postres.isChecked());
            radiobutton_cupcake.setEnabled(checkbox_postres.isChecked());
        }

        //Generació nom arxiu imatges ingredients hamburguesa.
        if(v==enciam_switch | v==formatge_switch | v==tomaquet_switch){
            ingredients="";
            if(!enciam_switch.isChecked()){
                ingredients+="no_enciam";
                if(!formatge_switch.isChecked()){
                    ingredients+="_formatge";
                    if(!tomaquet_switch.isChecked()) ingredients+="_tomaquet";
                }
                else if(!tomaquet_switch.isChecked()) ingredients+="_tomaquet";
            }
            else{
                if(!tomaquet_switch.isChecked()){
                    ingredients+="no_tomaquet";
                    if(!formatge_switch.isChecked()) ingredients+="_formatge";
                }
                else if(!formatge_switch.isChecked()) ingredients+="no_formatge";
                else ingredients="burger";
            }
            updateImg(ingredients, false, burguer_icon);
        }

        //Actualitza les miniatures segons les opcions seleccionades.
        if(v==radiobutton_aigua) updateImg("Aigua", false, beguda_icon);
        if(v==radiobutton_cocacola) updateImg("Coca-Cola", false, beguda_icon);
        if(v==radiobutton_suc) updateImg("Suc", false, beguda_icon);
        if(v==radiobutton_cervesa) updateImg("Cervesa", false, beguda_icon);
        if (v == radiobutton_cupcake) updateImg("Cupcake", false, postres_icon);
        if (v == radiobutton_gelat) updateImg("Gelat", false, postres_icon);
        if (v == radiobutton_fruita) updateImg("Fruita", false, postres_icon);
        if (v == radiobutton_pastis) updateImg("Pastís", false, postres_icon);

    }

    //Desa la comanda seleccionada com a objecte. L'envia a Firebase.
    public void  saveComanda (View v){
        final Calendar c = Calendar.getInstance();
        data_actual=0;
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        data_actual = data_actual+((double) mYear*100000000) +(((double)mMonth+1)*1000000)+(((double)mDay)*10000);
        String data_actual_s=Double.toString(data_actual);

        Log.e("data_actual", Double.toString(data_actual));
        Log.e("data_escollida", Double.toString(data_escollida));

        if(data_actual>data_escollida){
            Toast.makeText(this, R.string.error1, Toast.LENGTH_SHORT).show();
        }
        else {
            if (data == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.error4);
                builder.setNeutralButton(android.R.string.cancel, null);
                builder.create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.error5);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkbox_burger.isChecked()) {
                            preu += 4;
                            if (formatge_switch.isChecked()) {
                                hamburguesa_idioma = hamburguesa_idioma + " " +formatge_switch.getText() + " ";
                                hamburguesa = hamburguesa + " formatge ";

                            }
                            if (enciam_switch.isChecked()) {
                                hamburguesa_idioma = hamburguesa_idioma + " " + enciam_switch.getText() + " ";
                                hamburguesa = hamburguesa + " enciam ";
                            }
                            if (tomaquet_switch.isChecked()) {
                                hamburguesa_idioma = hamburguesa_idioma + " " + tomaquet_switch.getText()+ " ";
                                hamburguesa = hamburguesa + " tomaquet ";
                            }
                        }
                        else{
                            ingredients="no_burger";
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

                        if(ingredients.equals("no_burger") && beguda.equals("") && postres.equals("")){
                            Toast.makeText(MainActivity.this, R.string.error2, Toast.LENGTH_SHORT).show();
                        }

                        else {
                            //Generació codi a partir de l'hora interna del telèfon i un nombre aleatori.
                            long milisegSys = System.currentTimeMillis();
                            milisegSys = milisegSys % 100;
                            int miliseg = (int) milisegSys;

                            final int random = new Random().nextInt(99);

                            codi = miliseg * 100 + random;

                            //Es desa l'objecte comanda.
                            final Comanda comanda = new Comanda(hamburguesa, beguda, postres, codi, data, preu, estat, mida, userId, comandaId);

                            //Es penja l'objecte comanda a Firebase.
                            comandaRef.add(comanda).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(MainActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                                    comandaId = documentReference.getId(); //Es recupera comandaId.
                                    comanda.setComandaId(comandaId); //S'afegeix comandaId a l'objecte local.
                                    documentReference.set(comanda); //S'afegeix comandaId a Firebase
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });

                            //S'obre ReceiptActivity.
                            Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                            intent.putExtra("codi", codi);
                            intent.putExtra("ingredients", ingredients);
                            intent.putExtra("hamburguesa", hamburguesa_idioma);
                            startActivityForResult(intent, ENVIA);
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.create().show();
            }
        }
    }

    //Mètodes necessaris spinner.
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

    //Lectura dades provinents de WelcomeActivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ENVIA:
                if (resultCode == RESULT_OK) {
                    userId = data.getStringExtra("userId");
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }
}