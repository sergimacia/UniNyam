package com.example.uninyamchef;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class OrderListActivity extends AppCompatActivity {

    List<Comanda> comandes;

    private Adapter adapter;
    private RecyclerView order_list_view;
    private String userId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference comandaRef = db.collection("Comandes");
    private CollectionReference usuariRef = db.collection("Usuaris");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        comandes= new ArrayList<>();

        order_list_view=findViewById(R.id.order_list_view);

        order_list_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        order_list_view.setAdapter(adapter);

        /*imageView.setOnTouchListener(new OnSwipeTouchListener(MyActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MyActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(MyActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MyActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(MyActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });*/
    }


    @Override
    protected void onStart() {
        super.onStart();

        comandaRef.orderBy("data", Query.Direction.DESCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }

                for (DocumentSnapshot documentSnapshot : documentSnapshots){
                    Comanda comanda = documentSnapshot.toObject(Comanda.class);
                    comandes.add(comanda);
                }
                adapter.notifyDataSetChanged();
                //trobaUsuari();
            }
        });

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredientsburger_view;
        private TextView beguda_view;
        private TextView postre_view;
        private TextView codi_view;
        private TextView preu_view;
        private TextView data_view;
        private TextView hora_view;
        private TextView name_view;

        public ViewHolder(View itemView) {
            super(itemView);
            ingredientsburger_view = itemView.findViewById(R.id.ingredientsburger_view);
            beguda_view=itemView.findViewById(R.id.beguda_view);
            postre_view=itemView.findViewById(R.id.postre_view);
            codi_view=itemView.findViewById(R.id.codi_view);
            preu_view=itemView.findViewById(R.id.preu_view);
            data_view=itemView.findViewById(R.id.data_view);
            hora_view=itemView.findViewById(R.id.hora_view);
            name_view = itemView.findViewById(R.id.name_view);
            //onClick per fer els swipe
        }
    }




    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        //El adapatador hace 3 cosas (3 metodos):
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.order_view, parent, false);//el inflador te crea los objetos a partir del layout
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            Comanda comanda = comandes.get(position);
            holder.ingredientsburger_view.setText(comanda.getHamburguesa());
            holder.beguda_view.setText(comanda.getBeguda());
            holder.postre_view.setText(comanda.getPostres());

            userId = comanda.getUserId();
            db.document("Usuaris/" + userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String nom = documentSnapshot.getString("nom");
                    holder.name_view.setText(nom);
                    adapter.notifyDataSetChanged();
                    //
                }
            });

            int preuI=comanda.getPreu();
            String preu=Integer.toString(preuI);
            holder.preu_view.setText(preu);

            int codiI=comanda.getCodi();
            String codi = Integer.toString(codiI);
            holder.codi_view.setText(codi);

            double data = comanda.getData();

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

            holder.data_view.setText(dia+"/"+mes+"/"+any);

            double horaL=data/100;
            horaL=horaL%100;
            int horaI=(int)horaL;
            String hora = Integer.toString(horaI);

            double minutL=data%100;
            int minutI=(int)minutL;
            String minut=Integer.toString(minutI);

            holder.hora_view.setText(hora +":" + minut);

        }

        @Override
        public int getItemCount() {
            return comandes.size();
        }
    }
}
