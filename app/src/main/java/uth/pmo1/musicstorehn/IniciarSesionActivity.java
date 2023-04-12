package uth.pmo1.musicstorehn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class IniciarSesionActivity extends AppCompatActivity {
    SharedPreferences preferences;
    EditText tEmail, tPass;
    TextView olvidastePass, registrate;
    Button btnIniciar;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    ImageView verPass;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        tEmail = findViewById(R.id.txtEmail);
        tPass = findViewById(R.id.txtPass);

        olvidastePass = findViewById(R.id.tvolvidastepass);
        registrate = findViewById(R.id.tvregistrarse);

        btnIniciar = findViewById(R.id.btnIniciar);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(IniciarSesionActivity.this);

        preferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        validarsesion();

        verPass = findViewById(R.id.verpass);
        verPass.setImageResource(R.drawable.esconder);
        verPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    tPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    verPass.setImageResource(R.drawable.esconder);
                }else{
                    tPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    verPass.setImageResource(R.drawable.ver);
                }
            }
        });


        olvidastePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IniciarSesionActivity.this, RecuperarPassActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IniciarSesionActivity.this, RegistrarseActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

       authStateListener = (FirebaseAuth.AuthStateListener)(firebaseAuth) ->{

            FirebaseUser user = firebaseAuth.getCurrentUser();
          if (user != null){
                if (!user.isEmailVerified()){
                    //Toast.makeText(IniciarSesionActivity.this, "Correo electronico no verificado", Toast.LENGTH_LONG).show();
                }else{
                    //Toast.makeText(IniciarSesionActivity.this, "Inicia sesion", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = tEmail.getText().toString();
                String password = tPass.getText().toString();

                if (email.isEmpty() && password.isEmpty()){
                    FancyToast.makeText(IniciarSesionActivity.this,"Debe ingresar los datos!",FancyToast.LENGTH_SHORT,FancyToast.INFO,false).show();
                    tEmail.requestFocus();
                }else {
                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        tEmail.setError("Correo no válido!");
                        tEmail.requestFocus();
                    }else if (password.isEmpty() || password.length() < 8 ){
                        tPass.setError("Se necesitan 8 o más caracteres");
                        tPass.requestFocus();
                    }else{

                        progressDialog.setTitle("Ingresando");
                        progressDialog.setMessage("Espere un momento por favor...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.commit();

                                    //FancyToast.makeText(IniciarSesionActivity.this,"Bienvenido a MusicStoreHN!",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                                    Intent intent = new Intent(IniciarSesionActivity.this,MenuPrincipal.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();

                                }else {
                                    FancyToast.makeText(IniciarSesionActivity.this,"Credenciales incorrectas!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                                    tEmail.setText("");
                                    tPass.setText("");
                                    tEmail.requestFocus();

                                    progressDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                }
            }
        });
    }

    private void validarsesion(){
        String email = preferences.getString("email", null);
        String password = preferences.getString("password", null);

        if (email !=null && password !=null){
            //FancyToast.makeText(IniciarSesionActivity.this,"Bienvenido a MusicStoreHN!",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
            Intent intent = new Intent(IniciarSesionActivity.this,MenuPrincipal.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}