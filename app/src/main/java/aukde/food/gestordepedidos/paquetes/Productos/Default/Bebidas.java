package aukde.food.gestordepedidos.paquetes.Productos.Default;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import aukde.food.gestordepedidos.R;
import aukde.food.gestordepedidos.paquetes.Inclusiones.MiToolbar;
import aukde.food.gestordepedidos.paquetes.Productos.MenuAddProduct;
import aukde.food.gestordepedidos.paquetes.Utils.CompressorBitmapImage;
import aukde.food.gestordepedidos.paquetes.Utils.FileUtil;
import es.dmoral.toasty.Toasty;

public class Bebidas extends AppCompatActivity {

    Spinner spTipo , spDisponibilidad;
    private TextInputEditText edtNombreProducto , edtDescripcionProducto,
            edtTarifaConfidencial, edtTarifaPublicada , edtCodigoINEA , edtStock ;
    private Button btnRegProducto , btnAbrirGallery;
    private ImageView photoProducto;
    private EditText beb;
    private File mImageFile ;
    FirebaseAuth mAuth ;
    private final int GALLERY_REQUEST = 11;
    private EditText edtUrlPhoto,edtDisponibilidad;
    private ProgressDialog mDialog;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeRedCake);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bebidas);
        MiToolbar.Mostrar(this,"Agregar bebidas",false);
        edtDisponibilidad = findViewById(R.id.edtDisp);
        edtDisponibilidad.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        edtStock = findViewById(R.id.Stock);
        mDialog = new ProgressDialog(this);
        spDisponibilidad = findViewById(R.id.spinnerDispo);
        edtUrlPhoto = findViewById(R.id.pathUrlPhoto);
        edtUrlPhoto.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        photoProducto = findViewById(R.id.imgProducto);
        btnAbrirGallery = findViewById(R.id.btnAbrirGaleria);
        edtNombreProducto = findViewById(R.id.NombreProducto);
        edtDescripcionProducto = findViewById(R.id.DescripcionProducto);
        edtCodigoINEA = findViewById(R.id.CodigoInea);
        beb = findViewById(R.id.typeBebi);
        //edtCodigoINEA.setEnabled(false);
        edtTarifaConfidencial = findViewById(R.id.TarifaConfidencial);
        edtTarifaPublicada = findViewById(R.id.TarifaPublicada);
        btnRegProducto = findViewById(R.id.btnRegistroProductoDefault);

        spTipo = findViewById(R.id.spinnerTipo);
        ArrayAdapter<CharSequence> adapterSpinnerBebidas = ArrayAdapter.createFromResource(this,R.
                array.bebidas,R.layout.custom_spinner);
        spTipo.setAdapter(adapterSpinnerBebidas);
        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               beb.setText(parent.getItemAtPosition(position).toString());
                beb.setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapterSpinnerDisponibilidad = ArrayAdapter.createFromResource(this,R.
                array.disponibilidad,R.layout.custom_spinner);
        spDisponibilidad.setAdapter(adapterSpinnerDisponibilidad );
        spDisponibilidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edtDisponibilidad.setText(parent.getItemAtPosition(position).toString());
                edtDisponibilidad.setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Bebidas.this,R.style.ThemeOverlay);
                builder.setTitle("Advertencia!");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_alerta_producto);
                builder.setMessage("Está conforme con los datos? ");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickRegistro();
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

        });

        btnAbrirGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = edtNombreProducto.getText().toString();
                if (name.isEmpty()){
                    Toasty.info(Bebidas.this, "Agrege el nombre del producto", Toast.LENGTH_SHORT,true).show();
                }
                else {
                    openGallery();
                }
            }
        });

    }

    private void clickRegistro() {

        final String nombreProducto = edtNombreProducto.getText().toString();
        final String descripcionProducto = edtDescripcionProducto.getText().toString();
        final String tarifaConfidencial = edtTarifaConfidencial.getText().toString();
        final String tarifaPublicada = edtTarifaPublicada.getText().toString();
        final String codigoINEA = edtCodigoINEA.getText().toString();
        final String bebidaType = beb.getText().toString();
        final String urlPhoto = edtUrlPhoto.getText().toString();
        final String disp = edtDisponibilidad.getText().toString();
        final String stock = edtStock.getText().toString();


        if (!nombreProducto.isEmpty() && !descripcionProducto.isEmpty() && !tarifaConfidencial.isEmpty()  && !stock.isEmpty()
                && !tarifaPublicada.isEmpty() && !codigoINEA.isEmpty() && !urlPhoto.isEmpty() && !disp.isEmpty()){
            mDialog.show();
            mDialog.setCancelable(false);
            mDialog.setMessage("Registrando producto...");

            mDatabase.child("Usuarios").child("Proveedor").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String , Object> map = new HashMap<>();
                    map.put("nombreProducto",nombreProducto);
                    map.put("descripcionProducto",descripcionProducto);
                    map.put("tarifaConfidencial",tarifaConfidencial);
                    map.put("tarifaPublicada",tarifaPublicada);
                    map.put("tipo",bebidaType);
                    map.put("stock",stock);
                    map.put("disponibilidad",disp);
                    map.put("codigoINEA",codigoINEA);
                    map.put("urlPhoto",urlPhoto);

                    mDatabase.child("Usuarios").child("Proveedor").child(mAuth.getUid()).child("Bebidas").push().setValue(map);
                    mDialog.dismiss();
                    finish();
                    Toasty.success(Bebidas.this, "Producto Agregado!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else {
            Toasty.info(this, "Verifique sus Datos", Toast.LENGTH_SHORT,true).show();
        }

    }

    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            try {

                mImageFile = FileUtil.from(this, data.getData());
                photoProducto.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                saveImage();
            }catch(Exception e){
                Log.d("error","Mensaje" + e.getMessage());
            }

        }
    }

    private void saveImage(){
        mDialog.show();
        mDialog.setCancelable(false);
        mDialog.setMessage("Subiendo foto...");
        byte[] imageByte = CompressorBitmapImage.getImage(this,mImageFile.getPath(),500,500);
        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("bebidas").child(mAuth.getUid()).child(edtNombreProducto.getText().toString()+".jpg");
        UploadTask uploadTask = storage.putBytes(imageByte);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image = uri.toString();
                            edtUrlPhoto.setText(image);
                            mDialog.dismiss();
                        }
                    });
                }
                else {
                    Toasty.error(Bebidas.this, "Error al subir imagen", Toast.LENGTH_SHORT,true).show();
                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        String id = getIntent().getStringExtra("keyProduct");
        Intent intent = new Intent(Bebidas.this, MenuAddProduct.class);
        intent.putExtra("keyProduct",id);
        startActivity(intent);
        finish();
    }
}