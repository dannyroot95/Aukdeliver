package aukde.food.aukdeliver.paquetes.Firestore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

import aukde.food.aukdeliver.R;
import aukde.food.aukdeliver.paquetes.Actividades.OrdersList;
import aukde.food.aukdeliver.paquetes.Modelos.FCMBody;
import aukde.food.aukdeliver.paquetes.Modelos.FCMResponse;
import aukde.food.aukdeliver.paquetes.Modelos.Order;
import aukde.food.aukdeliver.paquetes.Providers.NotificationProvider;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirestoreClass {

    FirebaseFirestore mFirestore;
    private ProgressDialog mDialog;
    NotificationProvider notificationProvider;

    public FirestoreClass(){
        mFirestore = FirebaseFirestore.getInstance();
        notificationProvider = new NotificationProvider();

    }

    public void updateStatusItem(final Context context,String title,Integer position){

        mDialog = new ProgressDialog(context, R.style.ThemeOverlay);
        mDialog.setMessage("Actualizando...");
        mDialog.setCancelable(false);
        mDialog.show();


        mFirestore.collection("orders")
                .whereEqualTo("title",title)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snap : queryDocumentSnapshots){

                    String key = snap.getId();
                    Order order = snap.toObject(Order.class);

                    Map<String,Object> map = new HashMap<>();
                    map.put("cart_quantity",order.getItems().get(position).getCart_quantity());
                    map.put("id",order.getItems().get(position).getId());
                    map.put("image",order.getItems().get(position).getImage());
                    map.put("price",order.getItems().get(position).getPrice());
                    map.put("product_id",order.getItems().get(position).getProduct_id());
                    map.put("provider_id",order.getItems().get(position).getProvider_id());
                    map.put("status",order.getItems().get(position).getStatus());
                    map.put("stock_quantity",order.getItems().get(position).getStock_quantity());
                    map.put("title",order.getItems().get(position).getTitle());
                    map.put("user_id",order.getItems().get(position).getUser_id());

                    Map<String,Object> map2 = new HashMap<>();
                    map2.put("cart_quantity",order.getItems().get(position).getCart_quantity());
                    map2.put("id",order.getItems().get(position).getId());
                    map2.put("image",order.getItems().get(position).getImage());
                    map2.put("price",order.getItems().get(position).getPrice());
                    map2.put("product_id",order.getItems().get(position).getProduct_id());
                    map2.put("provider_id",order.getItems().get(position).getProvider_id());
                    map2.put("status",2);
                    map2.put("stock_quantity",order.getItems().get(position).getStock_quantity());
                    map2.put("title",order.getItems().get(position).getTitle());
                    map2.put("user_id",order.getItems().get(position).getUser_id());

                    mFirestore.collection("orders").document(key)
                            .update("items",FieldValue.arrayRemove(map) ,
                                    "items",FieldValue.arrayUnion(map2)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            context.startActivity(new Intent(context, OrdersList.class));
                            ((Activity)context).finish();
                            mDialog.dismiss();
                            Toasty.success(context,"Item actualizado!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });
    }


    //actualizar estado del item y del proveedor

    public void updateStatusDriverToClient(Activity activity , String tokenIDser ,
                                            String numOrder , String photo , String document , Integer status){

        mDialog = new ProgressDialog(activity, R.style.ThemeOverlay);
        mDialog.setMessage("Actualizando...");
        mDialog.setCancelable(false);
        mDialog.show();

        mFirestore.collection("orders").document(document).update("status",status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFirestore.collection("token").document(tokenIDser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()){
                            String token = documentSnapshot.getString("token");
                            Map<String , String> map = new HashMap<>();
                            map.put("title","Pedido "+numOrder);
                            map.put("body","ha sido completado satisfactoriamente!");
                            map.put("path",photo);
                            FCMBody fcmBody = new FCMBody(token, "high", map);
                            notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                                @Override
                                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                    if (response.body() != null) {
                                        if (response.body().getSuccess() == 1){
                                            activity.startActivity(new Intent(activity,OrdersList.class));
                                            activity.finish();
                                            mDialog.dismiss();
                                            Toasty.success(activity, "Actualizado!", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            mDialog.dismiss();
                                            Toasty.error(activity, "NO se pudo ENVIAR la notificaci??n!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else{
                                        mDialog.dismiss();
                                        Toasty.error(activity, "NO se pudo ENVIAR la notificaci??n!", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<FCMResponse> call, Throwable t) {
                                    mDialog.dismiss();
                                    Toasty.error(activity, "ERROR!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toasty.error(activity, "ERROR!", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                Toasty.error(activity, "ERROR!", Toast.LENGTH_LONG).show();
            }
        });


    }

}
