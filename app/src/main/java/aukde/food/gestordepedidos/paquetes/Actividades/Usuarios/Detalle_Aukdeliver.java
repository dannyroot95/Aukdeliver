package aukde.food.gestordepedidos.paquetes.Actividades.Usuarios;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import aukde.food.gestordepedidos.R;
import aukde.food.gestordepedidos.paquetes.Modelos.ListaAukdelivery;
import aukde.food.gestordepedidos.paquetes.Providers.NotificationProvider;
import aukde.food.gestordepedidos.paquetes.Providers.TokenProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Detalle_Aukdeliver extends AppCompatActivity {

    TextView txtNombreAukdeliver , txtApellidoAukdeliver , txtNombreUsuarioAukdeliver ,
            txtDniAukdeliver , txtTelefonoAukdeliver , txtCorreoAukdeliver ,txtCategoriaLicencia ;

    CircleImageView clrPhotoAukdeliver;
    Button BtnEditarAukdeliver;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private ProgressDialog mDialog;
    private Vibrator vibrator;
    long tiempo = 100;
    private NotificationProvider notificationProvider;
    private TokenProvider tokenProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeRedCake);
        setContentView(R.layout.activity_detalle_aukdeliver);

        tokenProvider = new TokenProvider();
        mDialog = new ProgressDialog(this,R.style.ThemeOverlay);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        clrPhotoAukdeliver = findViewById(R.id.photoDetalleAukdeliver);

        txtNombreAukdeliver= findViewById(R.id.detalleAukdeliverNombre);
        txtApellidoAukdeliver=findViewById(R.id.detalleAukdeliverApellido);

        txtNombreUsuarioAukdeliver=findViewById(R.id.detalleAukdeliverUsuario);
        txtCorreoAukdeliver=findViewById(R.id.detalleAukdeliverCorreo);
        txtDniAukdeliver=findViewById(R.id.detalleAukdeliverDni);
        txtTelefonoAukdeliver=findViewById(R.id.detalleAukdeliverTelefono);
        txtCategoriaLicencia=findViewById(R.id.detalleAukdeliverCateL);
        BtnEditarAukdeliver=findViewById(R.id.btnEditarAukdeliver);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        ListaAukdelivery AukdeliverDefault = (ListaAukdelivery) bundle.getSerializable("key");
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add(AukdeliverDefault.getFoto());
        arrayList.add(AukdeliverDefault.getNombres());
        arrayList.add(AukdeliverDefault.getApellidos());
        arrayList.add(AukdeliverDefault.getUsername());
        arrayList.add(AukdeliverDefault.getDni());
        arrayList.add(AukdeliverDefault.getTelefono());
        arrayList.add(AukdeliverDefault.getEmail());
        arrayList.add(AukdeliverDefault.getCategoria_licencia());

        Picasso
                .get()
                .load(arrayList.get(0))
                .fit()
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.loaderrosa)
                .centerCrop()
                .into(clrPhotoAukdeliver);

        txtNombreAukdeliver.setText(arrayList.get(1));
        txtApellidoAukdeliver.setText(arrayList.get(2));
        txtNombreUsuarioAukdeliver.setText(arrayList.get(3));
        txtDniAukdeliver.setText(arrayList.get(4));
        txtTelefonoAukdeliver.setText(arrayList.get(5));
        txtCorreoAukdeliver.setText(arrayList.get(6));
        txtCategoriaLicencia.setText(arrayList.get(7));

        BtnEditarAukdeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(tiempo);
                Toasty.success(Detalle_Aukdeliver.this, "Activity", Toast.LENGTH_SHORT,true).show();
            }
        });


    }

    public void onBackPressed() {
        super.onBackPressed();
        mDialog.dismiss();
    }
}