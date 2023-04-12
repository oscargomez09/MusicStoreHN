package uth.pmo1.musicstorehn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

public class UsuarioActivity extends AppCompatActivity {
    TextView tusuario, tcorreo, tpass;
    ImageView fotografia;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tusuario = findViewById(R.id.tUsuario);
        tcorreo = findViewById(R.id.tCorreo);
        tpass = findViewById(R.id.tPass);
        fotografia = findViewById(R.id.imgFotografia);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        storageReference = FirebaseStorage.getInstance().getReference("Usuarios");

        //OBTENEMOS LOS DATOS DEL USUARIO
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //SI EL USUARIO EXISTE
                if (snapshot.exists()){
                    String correo = ""+snapshot.child("correo").getValue();
                    String usuario = ""+snapshot.child("usuario").getValue();
                    String pass = ""+snapshot.child("password").getValue();
                    String foto = ""+snapshot.child("fotoUrl").getValue();

                    tusuario.setText(usuario);
                    tcorreo.setText(correo);
                    tpass.setText(pass);
                    Picasso.get().load(foto).into(fotografia);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.editar){
            Intent intent = new Intent(this, EditarUsuarioActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
            return true;
        }

        if (item.getItemId()==R.id.eliminar){
            elimarcuenta();
        }
        return super.onOptionsItemSelected(item);
    }

    private void elimarcuenta(){
        opendialog();
    }

    public void opendialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("¿Desea eliminar la cuenta?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FancyToast.makeText(UsuarioActivity.this,"Cuenta eliminada correctamente!",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();

                                Intent intent = new Intent(UsuarioActivity.this, IniciarSesionActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        });
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}