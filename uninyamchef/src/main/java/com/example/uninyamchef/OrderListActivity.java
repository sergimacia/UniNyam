package com.example.uninyamchef;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class OrderListActivity extends AppCompatActivity {

    List<Comanda> comandes;

    private Adapter adapter;
    private RecyclerView order_list_view;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference comandaRef = db.collection("Comandes");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        comandes= new ArrayList<>();

        order_list_view=findViewById(R.id.order_list_view);

        order_list_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        order_list_view.setAdapter(adapter);
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
                    Comanda comanda = documentSnapshot.toObject(Comanda.class);
                    comandes.add(comanda);
                }
                adapter.notifyDataSetChanged();
            }
        });

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredientsburger_view;
        private TextView beguda_view;
        private TextView postre_view;
        public ViewHolder(View itemView) {
            super(itemView);
            ingredientsburger_view = itemView.findViewById(R.id.ingredientsburger_view);
            beguda_view=itemView.findViewById(R.id.beguda_view);
            postre_view=itemView.findViewById(R.id.postre_view);

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
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Comanda comanda = comandes.get(position);
            holder.ingredientsburger_view.setText(comanda.getHamburguesa());
            holder.beguda_view.setText(comanda.getBeguda());
            holder.postre_view.setText(comanda.getPostres());

        }

        @Override
        public int getItemCount() {
            return comandes.size();
        }
    }
}
