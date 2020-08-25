package aukde.food.gestordepedidos.paquetes.Actividades.Registros;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import aukde.food.gestordepedidos.R;
import aukde.food.gestordepedidos.paquetes.Inclusiones.MiToolbar;

public class MenuRegistros extends AppCompatActivity {

    private ProgressDialog mDialog;
    private Button btnRegistroAdmin,btnRegistroAukdeliver,btnRegistroProveedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeDark);
        setContentView(R.layout.activity_menu_registros);
        MiToolbar.Mostrar(this,"Registro de usuarios",true);

        btnRegistroAdmin = findViewById(R.id.btnRegistroAdmin);
        btnRegistroAukdeliver = findViewById(R.id.btnRegistroAukdeliver);
        btnRegistroProveedor = findViewById(R.id.btnRegistroProveedor);
        mDialog = new ProgressDialog(this);

        btnRegistroAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuRegistros.this,RegistroAdmin.class));
            }
        });

        btnRegistroAukdeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuRegistros.this,RegistroAukdeliver.class));
            }
        });

        btnRegistroProveedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                mDialog.setMessage("Cargando...");
                startActivity(new Intent(MenuRegistros.this,RegistroProveedor.class));
            }
        });

    }

// actualizar activity
    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

}