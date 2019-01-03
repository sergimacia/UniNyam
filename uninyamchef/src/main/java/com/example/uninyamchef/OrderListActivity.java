package com.example.uninyamchef;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;


public class OrderListActivity extends AppCompatActivity {
    List<Comanda> comandes;
    List<Usuari>  usuaris;

    private Adapter adapter;
    private RecyclerView order_list_view;
    private String userId;
    private ImageView background_view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference comandaRef = db.collection("Comandes");
    private CollectionReference usuariRef = db.collection("Usuaris");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        comandes= new ArrayList<>();
        usuaris = new ArrayList<>();

        order_list_view=findViewById(R.id.order_list_view);
        order_list_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        order_list_view.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Recupera de Firebase el conjunt de comandes disponibles. Les ordena de forma creixent, segons la data.
        comandaRef.orderBy("data", Query.Direction.ASCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }
                comandes.clear();
                for (DocumentSnapshot documentSnapshot : documentSnapshots){
                    Comanda comanda = documentSnapshot.toObject(Comanda.class);
                    comandes.add(comanda);
                }
                adapter.notifyDataSetChanged();


                //El que es fa en les següents línies es traduir el userId dels diversos usuaris en els noms
                //de la persona que representen. Per això, es consulta a Firebase cada userId a quin nom correspon.
                //S'aniran creant en local objectes usuari, segons les comandes disponibles.
                //Com que es vol que la llista d'usuaris sigui igual de llarga que la de comandes, s'igualaran afegint nulls.
                usuaris.clear();
                for (int i = 0; i < comandes.size(); i++) {
                    usuaris.add(null);
                }
                for (int i = 0; i < comandes.size(); i++) {
                    final int index = i;
                    userId = comandes.get(i).getUserId();
                    db.document("Usuaris/" + userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            usuaris.set(index, documentSnapshot.toObject(Usuari.class));
                            adapter.notifyItemChanged(index);
                        }
                    });
                }
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
        private TextView visualitzacioestat_view;
        private ImageView background_view;

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
            visualitzacioestat_view=itemView.findViewById(R.id.visualitzacioestat_view);
            background_view=itemView.findViewById(R.id.background_view);

            //Definició de la interacció amb l'usuari: onClick i OnLongClick.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickItem(getAdapterPosition());
                    return true;
                }
            });
        }
    }

    //En cas de fer click sobre d'un ítem, es canviarà l'estat de la comanda.
    //Aquest estat es canvia en l'objecte en local i s'actualitza a Firebase.
    public void onClickItem(int position) {
        Comanda comanda = comandes.get(position);
        int estat = comanda.getEstat();
        if(estat==0) estat=1;
        comanda.setEstat(estat);
        DocumentReference docref = db.collection("Comandes").document(comanda.getComandaId());
        docref.set(comanda);

    }

    //En cas de fer un long-click, s'entén que l'usuari vol eliminar la comanda perquè ja ha estat
    //lliurada. Un quadre de diàleg en demana confirmar l'eliminació.
    //S'esborra l'objecte en local de la comanda i també de Firebase.
    public void onLongClickItem(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Segur que vols esborrar '" + comandes.get(position).getCodi() + "'?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(position);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    //Aquest mètode és cridat des de long-click. Elimina la comanda en qüestió.
    private void removeItem(int position) {
        db.document("Comandes/"+ comandes.get(position).getComandaId()).delete();
        comandes.remove(position);
        adapter.notifyItemRemoved(position);
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {
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
            if(comanda.getBeguda().equals("Zumo") || comanda.getBeguda().equals("Juice")) comanda.setBeguda("Suc");
            else if(comanda.getBeguda().equals("Water") || comanda.getBeguda().equals("Agua")) comanda.setBeguda("Aigua");
            else if(comanda.getBeguda().equals("Orange Juice") || comanda.getBeguda().equals("Fanta de Naranja")) comanda.setBeguda("Fanta de Taronja");
            else if(comanda.getBeguda().equals("Beer") || comanda.getBeguda().equals("Cerveza")) comanda.setBeguda("Cervesa");
            if(comanda.getPostres().equals("Pie") || comanda.getPostres().equals("Pastel")) comanda.setPostres("Pastís");
            if(comanda.getPostres().equals("Pie") || comanda.getPostres().equals("Pastel")) comanda.setPostres("Pastís");
            else if(comanda.getPostres().equals("Fruit") || comanda.getPostres().equals("Fruta")) comanda.setPostres("Fruita");
            else if(comanda.getPostres().equals("Ice Cream") || comanda.getPostres().equals("Helado")) comanda.setPostres("Gelat");

            Usuari usuari = usuaris.get(position);
            if (usuari != null) {
                holder.name_view.setText(usuari.getNom());
            } else {
                holder.name_view.setText("");
            }

            if(comanda.getEstat()==0) {
                holder.background_view.setVisibility(View.INVISIBLE);
                holder.visualitzacioestat_view.setText("Cuinant");
            }
            else if (comanda.getEstat()==1) {
                holder.background_view.setVisibility(View.VISIBLE);
                holder.visualitzacioestat_view.setText("Per recollir");
            }

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