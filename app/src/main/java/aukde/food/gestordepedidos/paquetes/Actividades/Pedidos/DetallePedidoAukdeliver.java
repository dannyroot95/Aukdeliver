package aukde.food.gestordepedidos.paquetes.Actividades.Pedidos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import aukde.food.gestordepedidos.R;
import aukde.food.gestordepedidos.paquetes.Modelos.PedidoLlamada;

public class DetallePedidoAukdeliver extends AppCompatActivity {
    TextView listNumPedido, listNumPedido2, listNombreCliente, listTelefonoCliente, listHoraRegistro, listFechaRegistro, listHoraEntrega, listFechaEntrega, listTotalPagoProducto, listDireccion, listPagoCliente, listTotalACobrar, listVuelto, listRepartidor, listProveedores, listProducto, listDescripcion, listPrecio1, listPrecio2, listPrecio3, listDelivery1, listDelivery2, listDelivery3;

    Button mButtonShow;
    Button mButtonShow2;

    private LinearLayout mLinearProductos, mLinearProductos1, mLinearProductos2, mLinearProductos3;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeDark);
        setContentView(R.layout.activity_detalle_pedido_aukdeliver);
        listHoraRegistro = findViewById(R.id.detalleHoraRegistro);
        listFechaRegistro = findViewById(R.id.detalleFechaRegistro);
        listHoraEntrega = findViewById(R.id.detalleHoraEntrega);
        listFechaEntrega = findViewById(R.id.detalleFechaEntrega);
        listTotalPagoProducto = findViewById(R.id.detalleTotalPago);
        listNumPedido = findViewById(R.id.detalleNumPedido);
        listNumPedido2 = findViewById(R.id.detalleNumPedido2);
        listNombreCliente = findViewById(R.id.detalleNombreCliente);
        listTelefonoCliente = findViewById(R.id.detalleTelefonoCliente);
        listDireccion = findViewById(R.id.detalleDireccionCliente);
        listPagoCliente = findViewById(R.id.detallePagoCliente);
        listTotalACobrar = findViewById(R.id.detalleTotalCobro);
        listVuelto = findViewById(R.id.detalleVuelto);
        listRepartidor = findViewById(R.id.detallevRepartidor);
        listProveedores = findViewById(R.id.detalleProveedor);
        listProducto = findViewById(R.id.detalleProductos);
        listDescripcion = findViewById(R.id.detalleProductosDescripcion);
        listPrecio1 = findViewById(R.id.detalleProductoprecio1);
        listPrecio2 = findViewById(R.id.detalleProductoprecio2);
        listPrecio3 = findViewById(R.id.detalleProductoprecio3);
        listDelivery1 = findViewById(R.id.detalleDelivery1);
        listDelivery2 = findViewById(R.id.detalleDelivery2);
        listDelivery3 = findViewById(R.id.detalleDelivery3);

        mButtonShow = findViewById(R.id.showProducto);
        mButtonShow2 = findViewById(R.id.showDetalle);

        int alto = 0;
        mLinearProductos = findViewById(R.id.linearProductos);
        mLinearProductos1 = findViewById(R.id.idLinearProducto1);
        mLinearProductos2 = findViewById(R.id.idLinearProducto2);
        mLinearProductos3 = findViewById(R.id.idLinearProducto3);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, alto);
        mLinearProductos.setLayoutParams(params);

        mButtonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mLinearProductos.setLayoutParams(params);
            }
        });

        mButtonShow2.setOnClickListener(new View.OnClickListener() {
            int alto1 = 0;

            @Override
            public void onClick(View v) {
                CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, alto1);
                mLinearProductos.setLayoutParams(params);
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        PedidoLlamada pedidoLlamada = (PedidoLlamada) bundle.getSerializable("key");
        ArrayList<String> arrayList = new ArrayList<>();


        arrayList.add("#" + pedidoLlamada.getNumPedido());
        arrayList.add(pedidoLlamada.getHoraPedido());
        arrayList.add(pedidoLlamada.getFechaPedido());
        arrayList.add(pedidoLlamada.getHoraEntrega());
        arrayList.add(pedidoLlamada.getFechaEntrega());
        arrayList.add(pedidoLlamada.getTotalPagoProducto());
        arrayList.add(pedidoLlamada.getNombreCliente());
        arrayList.add(pedidoLlamada.getTelefono());
        arrayList.add("S/" + pedidoLlamada.getConCuantoVaAPagar());
        arrayList.add("S/" + pedidoLlamada.getTotalCobro());
        arrayList.add(pedidoLlamada.getVuelto());
        arrayList.add(pedidoLlamada.getEncargado());
        arrayList.add(pedidoLlamada.getDireccion());
        arrayList.add(pedidoLlamada.getProveedores());
        arrayList.add(pedidoLlamada.getProductos());
        arrayList.add(pedidoLlamada.getDescripcion());
        arrayList.add(pedidoLlamada.getPrecio1());
        arrayList.add(pedidoLlamada.getPrecio2());
        arrayList.add(pedidoLlamada.getPrecio3());
        arrayList.add(pedidoLlamada.getDelivery1());
        arrayList.add(pedidoLlamada.getDelivery2());
        arrayList.add(pedidoLlamada.getDelivery3());


        listNumPedido.setText(arrayList.get(0));
        listNumPedido2.setText(arrayList.get(0));
        listHoraRegistro.setText(arrayList.get(1));
        listFechaRegistro.setText(arrayList.get(2));
        listHoraEntrega.setText(arrayList.get(3));
        listFechaEntrega.setText(arrayList.get(4));
        listTotalPagoProducto.setText(arrayList.get(5));
        listNombreCliente.setText(arrayList.get(6));
        listTelefonoCliente.setText(arrayList.get(7));
        listPagoCliente.setText(arrayList.get(8));
        listTotalACobrar.setText(arrayList.get(9));
        listVuelto.setText(arrayList.get(10));
        listRepartidor.setText(arrayList.get(11));
        listDireccion.setText(arrayList.get(12));
        listProveedores.setText(arrayList.get(13));
        listProducto.setText(arrayList.get(14));
        listDescripcion.setText(arrayList.get(15));
        listPrecio1.setText(arrayList.get(16));
        listPrecio2.setText(arrayList.get(17));
        listPrecio3.setText(arrayList.get(18));
        listDelivery1.setText(arrayList.get(19));
        listDelivery2.setText(arrayList.get(20));
        listDelivery3.setText(arrayList.get(21));


        String stPrecio1 = listPrecio1.getText().toString();
        String stPrecio2 = listPrecio2.getText().toString();
        String stPrecio3 = listPrecio3.getText().toString();
        String stDelivery1 = listDelivery1.getText().toString();
        String stDelivery2 = listDelivery2.getText().toString();
        String stDelivery3 = listDelivery3.getText().toString();

        if (stPrecio1.equals("0") && stDelivery1.equals("0")) {
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, alto);
            mLinearProductos1.setLayoutParams(params1);
        }

        if (stPrecio2.equals("0") && stDelivery2.equals("0")) {
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, alto);
            mLinearProductos2.setLayoutParams(params2);
        }

        if (stPrecio3.equals("0") && stDelivery3.equals("0")) {
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, alto);
            mLinearProductos3.setLayoutParams(params3);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetallePedidoAukdeliver.this);
        builder.setTitle("Confirmar");
        builder.setCancelable(false);
        builder.setMessage("Deseas volver a la lista de pedidos? ");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

}