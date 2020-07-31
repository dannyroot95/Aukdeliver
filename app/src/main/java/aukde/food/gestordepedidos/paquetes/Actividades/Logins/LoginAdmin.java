package aukde.food.gestordepedidos.paquetes.Actividades.Logins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import aukde.food.gestordepedidos.R;
import aukde.food.gestordepedidos.paquetes.Actividades.Inicio;
import aukde.food.gestordepedidos.paquetes.Actividades.Pedidos.RealizarPedido;
import aukde.food.gestordepedidos.paquetes.Actividades.Registros.MenuRegistros;
import aukde.food.gestordepedidos.paquetes.Inclusiones.MiToolbar;
import aukde.food.gestordepedidos.paquetes.Menus.MenuAdmin;
import aukde.food.gestordepedidos.paquetes.Providers.AuthProviders;
import es.dmoral.toasty.Toasty;

public class LoginAdmin extends AppCompatActivity {

    TextInputEditText edtEmail , edtPassword;
    Button btnlogin;
    private ProgressDialog mDialog;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;
    SharedPreferences mSharedPreference;
    AuthProviders authProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeDark);
        setContentView(R.layout.activity_login_admin);
        MiToolbar.Mostrar(this,"Login Admin",true);

        edtEmail = findViewById(R.id.logcorreo);
        edtPassword = findViewById(R.id.logContrasena);
        btnlogin = (Button) findViewById(R.id.btnLogin);
        authProviders = new AuthProviders();
        mSharedPreference = getApplicationContext().getSharedPreferences("tipoUsuario",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDialog = new ProgressDialog(this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {
        final SharedPreferences.Editor editor = mSharedPreference.edit();
        String correo = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();

        if (!correo.isEmpty() && !pass.isEmpty()){
            mDialog.show();
            mDialog.setMessage("Iniciando sesión...");
              if (pass.length()>=6)
                 {
                   mAuth.signInWithEmailAndPassword(correo,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()) {
                                   mDatabaseReference.child("Usuarios").child("Administrador").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.exists()){
                                               editor.putString("usuario","administrador");
                                               editor.apply();
                                               Intent intent = new Intent(LoginAdmin.this,MenuAdmin.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                               startActivity(intent);
                                           }
                                           else {
                                               editor.putString("","");
                                               editor.apply();
                                               mAuth.signOut();
                                               startActivity(new Intent(LoginAdmin.this, Inicio.class));
                                               finish();
                                               Toasty.error(LoginAdmin.this, "No es un usuario permitido", Toast.LENGTH_SHORT, true).show();
                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {
                                           mDialog.dismiss();
                                           Toasty.error(LoginAdmin.this, "Error de servidor", Toast.LENGTH_SHORT, true).show();
                                       }
                                   });

                           }
                           //task
                           else {
                               Toasty.error(LoginAdmin.this, "El correo o la contraseña son incorrectos", Toast.LENGTH_SHORT, true).show();
                               mDialog.dismiss();
                           }

                       }
                   });
                 }

              else {
                  Toasty.error(LoginAdmin.this, "La contraseña debe tener más de 6 caracteres", Toast.LENGTH_SHORT, true).show();
                  mDialog.dismiss();
              }

        }

        else {
            Toasty.error(LoginAdmin.this, "Complete los campos", Toast.LENGTH_SHORT, true).show();
            mDialog.dismiss();
        }
    }



}