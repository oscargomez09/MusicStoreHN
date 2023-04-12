package uth.pmo1.musicstorehn;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditarUsuarioActivity extends AppCompatActivity {

    EditText usuario, correo, password;
    ImageButton editar, cancelar;
    ImageView tomarFoto, verPass;;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri uri;
    String imgUrl;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        usuario = findViewById(R.id.txtUsuario);
        correo = findViewById(R.id.txtCorreo);
        password = findViewById(R.id.txtPassword);

        editar = findViewById(R.id.btneditar);
        cancelar = findViewById(R.id.btnCancelar);
        tomarFoto = findViewById(R.id.imgtomarFoto);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        storageReference = FirebaseStorage.getInstance().getReference("Usuarios");

        progressDialog = new ProgressDialog(EditarUsuarioActivity.this);


        verPass = findViewById(R.id.verpass2);
        verPass.setImageResource(R.drawable.esconder);
        verPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    verPass.setImageResource(R.drawable.esconder);
                }else{
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    verPass.setImageResource(R.drawable.ver);
                }
            }
        });

        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //SI EL USUARIO EXISTE
                if (snapshot.exists()){
                    String correoDatos = ""+snapshot.child("correo").getValue();
                    String usuarioDatos = ""+snapshot.child("usuario").getValue();
                    String passDatos = ""+snapshot.child("password").getValue();
                    String foto = ""+snapshot.child("fotoUrl").getValue();

                    usuario.setText(usuarioDatos);
                    correo.setText(correoDatos);
                    password.setText(passDatos);
                    Picasso.get().load(foto).into(tomarFoto);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDatos();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario.setText("");
                correo.setText("");
                password.setText("");

                Intent intent = new Intent(EditarUsuarioActivity.this, UsuarioActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            tomarFoto.setImageURI(uri);
                        }
                    }
                });

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photo = new Intent(Intent.ACTION_PICK);
                photo.setType("image/*");
                activityResultLauncher.launch(photo);
            }
        });
    }

    private void guardarDatos(){

        progressDialog.setTitle("Actualizando datos!");
        progressDialog.setMessage("Espere un momento por favor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Usuarios").child(uri.getLastPathSegment());

        storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imgUrl = urlImage.toString();
                editarDatos();
                progressDialog.dismiss();
            }
        });
    }

    private void editarDatos() {

        String userId = (FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("usuario", usuario.getText().toString().trim());
        map.put("correo", correo.getText().toString().trim());
        map.put("password", password.getText().toString().trim());
        map.put("fotoUrl", imgUrl);

        reference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                usuario.setText("");
                correo.setText("");
                password.setText("");

                FancyToast.makeText(EditarUsuarioActivity.this,"Datos actualizados correctamente!",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();

                Intent intent = new Intent(EditarUsuarioActivity.this, UsuarioActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                FancyToast.makeText(EditarUsuarioActivity.this,"Error al actualizar los datos!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();

                Intent intent = new Intent(EditarUsuarioActivity.this, UsuarioActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
}