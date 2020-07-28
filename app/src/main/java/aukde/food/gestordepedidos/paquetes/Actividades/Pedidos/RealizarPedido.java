package aukde.food.gestordepedidos.paquetes.Actividades.Pedidos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import aukde.food.gestordepedidos.R;
import aukde.food.gestordepedidos.paquetes.Menus.MenuAdmin;
import aukde.food.gestordepedidos.paquetes.Modelos.PedidoLlamada;
import aukde.food.gestordepedidos.paquetes.Providers.GoogleApiProvider;
import aukde.food.gestordepedidos.paquetes.Providers.PedidoProvider;
import aukde.food.gestordepedidos.paquetes.Utils.DecodePoints;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealizarPedido extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener {

    private ProgressDialog mDialog;
    SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm");
    SimpleDateFormat simpleDateFormatFecha = new SimpleDateFormat("dd/MM/yyy");
    int dia, mes, year, horaRel, minutoRel;
    String formatoHora = simpleDateFormatHora.format(new Date());
    String formatoFecha = simpleDateFormatFecha.format(new Date());
    TextView horaPedido, fechaPedido, fechaEntrega , horaEntrega
            , precioProductoTotal, precioNetoTotal, vuelto;
    private Button btnCalcularTotal, btnCalcularVuelto;
    public EditText edtNumPedido ,edtProveedor , edtProductos , edtDescripcion , edtPrecioProducto1,
            edtPrecioProducto2, edtPrecioProducto3, edtPrecioDelivery1, edtPrecioDelivery2,
            edtPrecioDelivery3, edtMontoCliente , edtNombreCliente, edtTelefono , edtDireccion ;

    private LinearLayout mLinearMap;
    PedidoProvider mpedidoProvider;

    private MapView mapView;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "RealizarPedido";
    private GoogleApiProvider mGoogleapiProvider;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    private LatLng origen;
    private LatLng destino;

    //variables de poscicion actual
    private LocationRequest mLocationRequest;
    public static final int LOCATION_REQUEST_CODE = 1;
    public static final int SETTINGS_REQUEST_CODE = 2;
    private FusedLocationProviderClient mFusedLocation;
    DatabaseReference mUsuarioAukdeliver;
    DatabaseReference pedidos;
    private DatabaseReference pedidoParaAukdeliver;
    Spinner mSpinner, mSpinnerEstado;
    FloatingActionButton mFloatingButton , mFloatingMap;
    TextView estado ;
    TextView txtEncargado , idAukdeliver;
    String stEncargado = "";

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                origen = new LatLng(location.getLatitude(),location.getLongitude());
                if (getApplicationContext() != null) {
                    //Toast.makeText(RealizarPedido.this, "Tu poscicion : "+otro, Toast.LENGTH_LONG).show();
                    //obtener locatizacion en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));
                }
            }
        }
    };

    //----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_pedido);
        horaPedido = findViewById(R.id.horaPedido);
        fechaPedido = findViewById(R.id.fechaPedido);
        horaEntrega = findViewById(R.id.horaEntrega);
        fechaEntrega = findViewById(R.id.fechaEntrega);
        horaEntrega.setText(formatoHora);
        fechaEntrega.setText(formatoFecha);
        vuelto = findViewById(R.id.vuelto);
        horaPedido.setText(formatoHora);
        fechaPedido.setText(formatoFecha);
        edtNombreCliente = findViewById(R.id.nombreCliente);
        edtProveedor = findViewById(R.id.nombresProveedor);

        edtProductos = findViewById(R.id.nombresProducto);
        edtDescripcion = findViewById(R.id.descripcion);
        edtNumPedido = findViewById(R.id.numPedido);
        edtNumPedido.setEnabled(false);

        btnCalcularTotal = findViewById(R.id.calcularTotal);
        btnCalcularVuelto = findViewById(R.id.calcularVuelto);
        mDialog = new ProgressDialog(this);
        edtPrecioProducto1 = findViewById(R.id.precioProducto1);
        edtPrecioProducto2 = findViewById(R.id.precioProducto2);
        edtPrecioProducto3 = findViewById(R.id.precioProducto3);
        edtPrecioDelivery1 = findViewById(R.id.precioDelivery1);
        edtPrecioDelivery2 = findViewById(R.id.precioDelivery2);
        edtPrecioDelivery3 = findViewById(R.id.precioDelivery3);
        edtMontoCliente = findViewById(R.id.montoPagarcliente);
        precioProductoTotal = findViewById(R.id.precioProductoTotal);
        precioNetoTotal = findViewById(R.id.neto);
        edtTelefono = findViewById(R.id.telefonoCliente);
        edtDireccion = findViewById(R.id.direcionCliente);
        txtEncargado = findViewById(R.id.txtRepartidor);
        idAukdeliver = findViewById(R.id.txtIdAukdeliver);
        pedidoParaAukdeliver = FirebaseDatabase.getInstance().getReference();

        Query ultimoDato = FirebaseDatabase.getInstance().getReference().child("PedidosPorLlamada").child("pedidos").orderByKey().limitToLast(1);
        ultimoDato.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String num = childSnapshot.child("numPedido").getValue().toString();
                    int numToString = Integer.parseInt(num);
                    int newNumPedido = numToString + 1;
                    String stNewNumPedido = String.valueOf(newNumPedido);
                    edtNumPedido.setText(stNewNumPedido);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        int alto = 0;
        mLinearMap =findViewById(R.id.map_container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,alto);
        mLinearMap.setLayoutParams(params);

        mpedidoProvider = new PedidoProvider();


        mSpinner = findViewById(R.id.spinnerAukdeliver);
        mSpinnerEstado = findViewById(R.id.spEstado);
        ArrayAdapter<CharSequence> adapterSpinnerEstado = ArrayAdapter.createFromResource(this,R.
                array.estado,android.R.layout.simple_spinner_item);
        estado = findViewById(R.id.txtEstado);


        mSpinnerEstado.setAdapter(adapterSpinnerEstado);
        mSpinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estado.setText(parent.getItemAtPosition(position).toString());
                String stSpinnerEstado = estado.getText().toString();
                if (stSpinnerEstado.equals("En espera")){
                    estado.setTextColor(Color.parseColor("#2E86C1"));
                }
                if (stSpinnerEstado.equals("Completado")){
                    estado.setTextColor(Color.parseColor("#5bbd00"));
                }
                if (stSpinnerEstado.equals("Cancelado")){
                    estado.setTextColor(Color.parseColor("#E74C3C"));
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mUsuarioAukdeliver = FirebaseDatabase.getInstance().getReference();
        pedidos = FirebaseDatabase.getInstance().getReference("PedidosPorLlamada").child("pedidos");

        obtenerUsuarioAukdeliver();

        mFloatingButton = findViewById(R.id.floatRegister);
        mFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_googgreen)));
        mFloatingMap = findViewById(R.id.booleanMap);
        mFloatingMap.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_googred)));

        geocoder = new Geocoder(this);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mGoogleapiProvider = new GoogleApiProvider(RealizarPedido.this);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        fechaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickFecha();
            }
        });
        horaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickHora();
            }
        });
        btnCalcularTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCalcularTotal();
            }
        });
        btnCalcularVuelto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCalcularVuelto();
            }
        });

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RealizarPedido.this);
                builder.setTitle("Confirmacion de pedido");
                builder.setCancelable(false);
                builder.setMessage("Deseas guardar este pedido? ");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickRegistroPedido();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(RealizarPedido.this, "Pedido Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create();
                builder.show();
            }
        });

        mFloatingMap.setOnClickListener(new View.OnClickListener() {
            int alto1 = 0;
            private boolean state = false;
            @Override
            public void onClick(View v) {
                if ( state ) {
                    state = false;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,alto1);
                    mLinearMap.setLayoutParams(params);
                } else {
                    state = true;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    mLinearMap.setLayoutParams(params);
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }


    private void ClickFecha() {
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RealizarPedido.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (month < 10 && dayOfMonth < 10) {
                    fechaEntrega.setText("0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                } else if (month > 10 && dayOfMonth < 10) {
                    fechaEntrega.setText("0" + dayOfMonth + "/" + (month + 1) + "/" + year);
                } else if (month < 10 && dayOfMonth > 10) {
                    fechaEntrega.setText(dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                } else {
                    fechaEntrega.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }
        }, year, mes, dia);
        datePickerDialog.show();
    }

    private void ClickHora() {

        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.HOUR_OF_DAY);
        mes = c.get(Calendar.MINUTE);

        TimePickerDialog PickerHora = new TimePickerDialog(RealizarPedido.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    horaEntrega.setText("0" + hourOfDay + ":" + "0" + minute);
                } else if (hourOfDay < 10 && minute > 10) {
                    horaEntrega.setText("0" + hourOfDay + ":" + minute);
                } else if (hourOfDay > 10 && minute < 10) {
                    horaEntrega.setText(hourOfDay + ":" + "0" + minute);
                } else {
                    horaEntrega.setText(hourOfDay + ":" + minute);
                }
            }
        }, horaRel, minutoRel, false);

        PickerHora.show();

    }

    private void ClickCalcularTotal() {

        String precioP1 = edtPrecioProducto1.getText().toString();
        String precioP2 = edtPrecioProducto2.getText().toString();
        String precioP3 = edtPrecioProducto3.getText().toString();
        String deliveryP1 = edtPrecioProducto1.getText().toString();
        String deliveryP2 = edtPrecioProducto1.getText().toString();
        String deliveryP3 = edtPrecioProducto1.getText().toString();

        double PrecioProducto, PrecioDelivery, neto;

        if (TextUtils.isEmpty(precioP1) || TextUtils.isEmpty(precioP2) || TextUtils.isEmpty(precioP3)
                && TextUtils.isEmpty(deliveryP1) || TextUtils.isEmpty(deliveryP2) || TextUtils.isEmpty(deliveryP3)) {
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show();
        } else {

            double precio1 = Double.parseDouble(edtPrecioProducto1.getText().toString());
            double precio2 = Double.parseDouble(edtPrecioProducto2.getText().toString());
            double precio3 = Double.parseDouble(edtPrecioProducto3.getText().toString());
            double delivery1 = Double.parseDouble(edtPrecioDelivery1.getText().toString());
            double delivery2 = Double.parseDouble(edtPrecioDelivery2.getText().toString());
            double delivery3 = Double.parseDouble(edtPrecioDelivery3.getText().toString());

            PrecioProducto = precio1 + precio2 + precio3;
            PrecioDelivery = delivery1 + delivery2 + delivery3;
            neto = PrecioProducto + PrecioDelivery;

            precioProductoTotal.setText("S/" + obtieneDosDecimales(PrecioProducto));
            precioNetoTotal.setText(obtieneDosDecimales(neto));

        }

    }

    private String obtieneDosDecimales(double valor) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        return format.format(valor);
    }

    private void ClickCalcularVuelto() {

        String montoCliente = edtMontoCliente.getText().toString();
        String netoMonto = precioNetoTotal.getText().toString();

        if (TextUtils.isEmpty(montoCliente)) {
            Toast.makeText(this, "¿Con cuanto va a pagar? (Cliente)", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(netoMonto)) {
            Toast.makeText(this, "Error : Calcule el total", Toast.LENGTH_SHORT).show();
        } else {
            double vueltoNeto;
            double txtmonto = Double.parseDouble(edtMontoCliente.getText().toString());
            double txtneto = Double.parseDouble(precioNetoTotal.getText().toString());
            vueltoNeto = txtmonto - txtneto;
            vuelto.setText("S/" + obtieneDosDecimales(vueltoNeto));
        }
    }

    public void obtenerUsuarioAukdeliver(){
        final List<aukde.food.gestordepedidos.paquetes.Modelos.Spinner> aukdelivers = new ArrayList<>();
        mUsuarioAukdeliver.child("Usuarios").child("Aukdeliver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren() ){
                        String id = ds.getKey();
                        String nombres = ds.child("nombres").getValue().toString();
                        aukdelivers.add(new aukde.food.gestordepedidos.paquetes.Modelos.Spinner(id,nombres));
                    }

                    final ArrayAdapter<aukde.food.gestordepedidos.paquetes.Modelos.Spinner> arrayAdapter
                            = new ArrayAdapter<>(RealizarPedido.this , android.R.layout.simple_dropdown_item_1line,aukdelivers);
                    mSpinner.setAdapter(arrayAdapter);
                    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            stEncargado = parent.getItemAtPosition(position).toString();
                            String idx = aukdelivers.get(position).getId();
                            txtEncargado.setText(stEncargado);
                            idAukdeliver.setText(idx);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(7000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
        startLocacion();

        try {

            List<Address> addresses = geocoder.getFromLocation(-12.5879997, -69.1930283, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng aukde = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(aukde)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.aukdemarker))
                        .title("OFICINA AUKDE");
                mMap.addMarker(markerOptions).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aukde, 16));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //obtener poscicion

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(gpsActive()){
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                    else { showAlertDialog(); }
                } else {
                    checkLocationPermision();
                }
            } else {
                checkLocationPermision();
            }
        }
    }

    // Gps Activo


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActive()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        else {
            showAlertDialog();
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicación para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActive(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }

        return isActive;
    }

    //-----------
    //verificar version de android

    public void startLocacion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
               if(gpsActive())
               {
                   mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                   mMap.setMyLocationEnabled(true);
               }
               else{
                   showAlertDialog();
               }

            }

            else {
                checkLocationPermision();
            }

        }
        else {
            if(gpsActive()){
                mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);

            }
            else {
                showAlertDialog();
            }

        }
    }

    //------------------------------

    public void checkLocationPermision(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicación requiere los permisos para continuar")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             ActivityCompat.requestPermissions(RealizarPedido.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        })
                .create()
                .show();
            }
            else {
                ActivityCompat.requestPermissions(RealizarPedido.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }
    //----------

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                );
                destino = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(final Marker marker){
                AlertDialog.Builder builder = new AlertDialog.Builder(RealizarPedido.this);
                builder.setTitle("Alerta!");
                builder.setCancelable(false);
                builder.setMessage("Desea borrar esta poscición?");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.remove();
                        drawRoute();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawRoute();
                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawRoute(){

        Toast.makeText(this, "Tu poscicion : "+origen, Toast.LENGTH_LONG).show();

        mGoogleapiProvider.getDirections(origen , destino).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try{

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");

                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions).setPoints(mPolylineList);
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);

                }catch (Exception e){
                    Log.d("Error","Error encontrado"+ e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }

        });
    }


    private void clickRegistroPedido(){

        final String numeroPedido = edtNumPedido.getText().toString();
        final String stHoraPedido = horaPedido.getText().toString();
        final String stFechaPedido = fechaPedido.getText().toString();
        final String stHoraEntrega = horaEntrega.getText().toString();
        final String stFechaEntrega= fechaEntrega.getText().toString();
        final String producto = edtProductos.getText().toString();
        final String proveedor = edtProveedor.getText().toString();
        final String descripción = edtDescripcion.getText().toString();
        final String precio1 = edtPrecioProducto1.getText().toString();
        final String precio2 = edtPrecioProducto2.getText().toString();
        final String precio3 = edtPrecioProducto3.getText().toString();
        final String delivery1 = edtPrecioDelivery1.getText().toString();
        final String delivery2 = edtPrecioDelivery2.getText().toString();
        final String delivery3 = edtPrecioDelivery3.getText().toString();
        final String totalPagoProducto = precioProductoTotal.getText().toString();
        final String telefono = edtTelefono.getText().toString();
        final String totalCobro = precioNetoTotal.getText().toString();
        final String stVuelto = vuelto.getText().toString();
        final String nombreCliente = edtNombreCliente.getText().toString();
        final String telefonoCliente = edtTelefono.getText().toString();
        final String conCuantoVaAPagar = edtMontoCliente.getText().toString();
        final String direccion = edtDireccion.getText().toString();
        final String encargado = txtEncargado.getText().toString();
        final String estadoPedido = estado.getText().toString();



        if( !nombreCliente.isEmpty() && !telefonoCliente.isEmpty() && !conCuantoVaAPagar.isEmpty() && !direccion.isEmpty()){
            if(!numeroPedido.isEmpty()){
            mDialog.show();
            mDialog.setMessage("Registrando pedido...");
            //metodos
            registrarPedido(stHoraPedido, stFechaPedido, stHoraEntrega, stFechaEntrega, proveedor,
                    producto, descripción, precio1, precio2, precio3, delivery1, delivery2, delivery3,
                    totalPagoProducto, nombreCliente, telefono, conCuantoVaAPagar, totalCobro, stVuelto, direccion,numeroPedido,encargado,estadoPedido);
                clickRegistroPedidoAukdeliver();
        }
        else {
                Toast.makeText(this, "Agrege el NÚMERO DE PEDIDO", Toast.LENGTH_SHORT).show();
            }
        }

        else {
            mDialog.dismiss();
            Toast.makeText(this, "Verifique que los campos no estén vacios", Toast.LENGTH_SHORT).show();
        }

    }

    private void registrarPedido(final String horaPedido, final String fechaPedido,
                                 final String horaEntrega, final String fechaEntrega, final String proveedor,
                                 final String producto, final String descripción, final String precio1,
                                 final String precio2, final String precio3, final String delivery1,
                                 final String delivery2, final String delivery3, final String totalPagoProducto,
                                 final String nombreCliente, final String telefono, final String conCuantoVaAPagar,
                                 final String totalCobro, final String stVuelto, final String direccion,final String numPedido , final String encargado ,final String estadoPedido){



        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        PedidoLlamada pedidoLlamada = new  PedidoLlamada(id,horaPedido,  fechaPedido, horaEntrega,  fechaEntrega,  proveedor,
            producto,  descripción,  precio1, precio2,  precio3,  delivery1, delivery2,  delivery3,
            totalPagoProducto, nombreCliente,  telefono,  conCuantoVaAPagar, totalCobro,  stVuelto,  direccion,numPedido,encargado ,estadoPedido);
            mapear(pedidoLlamada);

    }

    void mapear(PedidoLlamada pedidoLlamada){

        mpedidoProvider.Mapear(pedidoLlamada).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                mDialog.dismiss();
                startActivity(new Intent( RealizarPedido.this , MenuAdmin.class));
                finish();
                Toast.makeText(RealizarPedido.this, "Registro exitoso", Toast.LENGTH_LONG).show();
                  }

              else {
                mDialog.dismiss();
                Toast.makeText(RealizarPedido.this, "No se pudo registar el pedido", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void clickRegistroPedidoAukdeliver(){

        String StAukdeliver = idAukdeliver.getText().toString();

                Map<String, Object> map = new HashMap<>();
                map.put("numPedido", edtNumPedido.getText().toString());
                map.put("horaEntrega", horaEntrega.getText().toString());
                map.put("fechaEntrega", fechaEntrega.getText().toString());
                map.put("proveedores",edtProveedor .getText().toString());
                map.put("productos",edtProductos.getText().toString());
                map.put("descripcion", edtDescripcion.getText().toString());
                map.put("precio1",edtPrecioProducto1.getText().toString());
                map.put("precio2",edtPrecioProducto2.getText().toString());
                map.put("precio3",edtPrecioProducto3.getText().toString());
                map.put("delivery1", edtPrecioDelivery1.getText().toString());
                map.put("delivery2", edtPrecioDelivery2.getText().toString());
                map.put("delivery3", edtPrecioDelivery3.getText().toString());
                map.put("totalPagoProducto",precioProductoTotal.getText().toString());
                map.put("nombreCliente", edtNombreCliente.getText().toString());
                map.put("telefono",edtTelefono.getText().toString());
                map.put("conCuantoVaAPagar",edtMontoCliente.getText().toString());
                map.put("totalCobro",precioNetoTotal.getText().toString());
                map.put("vuelto",vuelto.getText().toString());
                map.put("direccion",edtDireccion.getText().toString());
                map.put("estado",estado.getText().toString());

                pedidoParaAukdeliver.child("Usuarios").child("Aukdeliver").child(StAukdeliver).child("pedidos").push().setValue(map);
    }


    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(RealizarPedido.this);
        builder.setTitle("Alerta!");
        builder.setCancelable(false);
        builder.setMessage("Deseas Salir? ");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(RealizarPedido.this,MenuAdmin.class));
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