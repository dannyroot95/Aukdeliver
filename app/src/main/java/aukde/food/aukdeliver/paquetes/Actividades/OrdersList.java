package aukde.food.aukdeliver.paquetes.Actividades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import aukde.food.aukdeliver.R;
import aukde.food.aukdeliver.paquetes.Actividades.Pedidos.ListaPedidosAukdeliver;
import aukde.food.aukdeliver.paquetes.Adaptadores.AdapterPedidoPorLlamadaAukdeliver;
import aukde.food.aukdeliver.paquetes.Adaptadores.OrdersAdapter;
import aukde.food.aukdeliver.paquetes.Inclusiones.MiToolbar;
import aukde.food.aukdeliver.paquetes.Modelos.Order;
import aukde.food.aukdeliver.paquetes.Modelos.PedidoLlamada;
import aukde.food.aukdeliver.paquetes.Receptor.GpsReceiver;
import aukde.food.aukdeliver.paquetes.Receptor.NetworkReceiver;
import es.dmoral.toasty.Toasty;

public class OrdersList extends AppCompatActivity {

    DatabaseReference mDatabase;
    ArrayList<Order> orders;
    RecyclerView recyclerView;
    SearchView searchView;
    OrdersAdapter ordersAdapter;
    LinearLayoutManager linearLayoutManager;
    ProgressDialog mDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        MiToolbar.Mostrar(this,"Lista de pedidos",true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerViewOrders);
        searchView = findViewById(R.id.searchViewOrders);
        searchView.setBackgroundColor(Color.WHITE);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        orders = new ArrayList<>();
            ordersAdapter = new OrdersAdapter(orders,this);
        recyclerView.setAdapter(ordersAdapter);

        mDialog = new ProgressDialog(this,R.style.MyAlertDialogData);
        mDialog.setCancelable(false);
        mDialog.show();
        mDialog.setContentView(R.layout.dialog_data);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        /*mDatabase.child("Usuarios").child("Aukdeliver").child(mAuth.getCurrentUser().getUid()).child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    orders.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Order data = snapshot.getValue(Order.class);
                        orders.add(data);
                    }
                    Collections.reverse(orders);
                    ordersAdapter.notifyDataSetChanged();
                    mDialog.dismiss();
                }
                else{
                    Toasty.info(OrdersList.this, "Sin Pedidos", Toast.LENGTH_SHORT,true).show();
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(OrdersList.this, "Error de base de datos", Toast.LENGTH_SHORT,true).show();
            }
        });*/
        mFirestore.collection("orders").whereEqualTo("driver_id",mAuth.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                orders.clear();
                if (value != null){
                    for (DocumentSnapshot snapshot : value){
                        Order data = snapshot.toObject(Order.class);
                        data.setId(snapshot.getId());
                        orders.add(data);
                    }
                    Collections.reverse(orders);
                    ordersAdapter.notifyDataSetChanged();
                    mDialog.dismiss();
                }
                else {
                    Toasty.info(OrdersList.this, "Sin Pedidos", Toast.LENGTH_SHORT,true).show();
                    mDialog.dismiss();
                }

                if (error != null) {
                    Toasty.info(OrdersList.this, "Error", Toast.LENGTH_SHORT,true).show();
                    Log.w( "Listen failed.", error);
                }

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String title) {
                search(title);
                return true;
            }
        });

    }

    private void search(String title){
        ArrayList<Order> list = new ArrayList<>();
        for(Order object : orders){
            if (object.getTitle().toLowerCase().contains(title.toLowerCase()))
            {
                list.add(object);
            }
            else {
            }
        }
        OrdersAdapter adapter = new OrdersAdapter(list,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }

}