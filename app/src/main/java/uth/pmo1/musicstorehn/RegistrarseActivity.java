package uth.pmo1.musicstorehn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;

public class RegistrarseActivity extends AppCompatActivity {

    EditText txtUsuarioR, txtEmailR, txtPassR;
    Button btnRegistrardatos;
    TextView cuentaregistrada;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference databaseReference;
    ImageView verPass;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        txtUsuarioR = findViewById(R.id.txtUsuarioR);
        txtPassR = findViewById(R.id.txtPassR);
        txtEmailR = findViewById(R.id.txtEmailR);

        cuentaregistrada = findViewById(R.id.cuentaRegistrada);
        btnRegistrardatos = findViewById(R.id.btnRegistrardatos);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        verPass = findViewById(R.id.verpass);
        verPass.setImageResource(R.drawable.esconder);

        verPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtPassR.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    txtPassR.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    verPass.setImageResource(R.drawable.esconder);
                }else{
                    txtPassR.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    verPass.setImageResource(R.drawable.ver);
                }
            }
        });

        cuentaregistrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrarseActivity.this, IniciarSesionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        btnRegistrardatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearCuenta();
            }
        });

        authStateListener = (FirebaseAuth.AuthStateListener)(firebaseAuth) ->{
            FirebaseUser user = firebaseAuth.getCurrentUser();

           if (user != null){
                //Toast.makeText(RegistrarseActivity.this, "Usuario creado exitosamente!", Toast.LENGTH_LONG).show();
            }
            else{
               // Toast.makeText(RegistrarseActivity.this, "El usuario no se pudo crear", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void crearCuenta() {

        String user = txtUsuarioR.getText().toString().trim();
        String email = txtEmailR.getText().toString().trim();
        String password = txtPassR.getText().toString().trim();

        if (user.isEmpty() && email.isEmpty() && password.isEmpty()){
            FancyToast.makeText(RegistrarseActivity.this,"Debe ingresar los datos!",FancyToast.LENGTH_SHORT,FancyToast.INFO,false).show();
            txtUsuarioR.requestFocus();
        }else{
            if (user.isEmpty()){
                txtUsuarioR.setError("Ingrese un usuario");
                txtUsuarioR.requestFocus();
            }else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                txtEmailR.setError("Correo no válido!");
                txtEmailR.requestFocus();
            }else if (password.isEmpty() || password.length() < 8 ){
                txtPassR.setError("Se necesitan 8 o más caracteres!");
                txtPassR.requestFocus();
            }else {

                progressDialog.setTitle("Creando cuenta");
                progressDialog.setMessage("Espere un momento por favor...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FancyToast.makeText(RegistrarseActivity.this,"Usuario " + txtUsuarioR.getText() + " creado exitosamente!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                            Map<String, Object> map = new HashMap<>();
                            map.put("usuario", user);
                            map.put("correo", email);
                            map.put("password", password);

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user.sendEmailVerification();
                            String id = firebaseAuth.getCurrentUser().getUid();

                            databaseReference.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(RegistrarseActivity.this, IniciarSesionActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }else {
                                        FancyToast.makeText(RegistrarseActivity.this,"No se pudieron crear datos!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                                    }
                                }
                            });
                        }else {
                            FancyToast.makeText(RegistrarseActivity.this,"Correo ya registrado!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                            txtEmailR.setError("Correo no válido!");
                            txtEmailR.requestFocus();
                            progressDialog.dismiss();
                        }
                    }
                });

            }
        }
    }
}