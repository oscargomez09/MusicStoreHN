package uth.pmo1.musicstorehn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class RecuperarPassActivity extends AppCompatActivity {

    Button btnRecuperarCorreo, btnAtras;
    EditText recuperarEmail;
    String email = "";
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_pass);

        recuperarEmail = findViewById(R.id.txtRecuperarEmail);
        btnAtras = findViewById(R.id.btnAtras);
        btnRecuperarCorreo = findViewById(R.id.btnRecuperarCorreo);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IniciarSesionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        btnRecuperarCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = recuperarEmail.getText().toString();

                if (!email.isEmpty()){

                    progressDialog.setTitle("Verificando correo");
                    progressDialog.setMessage("Espere un momento por favor...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mAuth.setLanguageCode("es");
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                FancyToast.makeText(RecuperarPassActivity.this,"Correo enviado para restablecer contraseña!",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                                Intent intent = new Intent(RecuperarPassActivity.this, IniciarSesionActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }else {
                                FancyToast.makeText(RecuperarPassActivity.this,"Correo no válido!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                                recuperarEmail.setText("");
                                recuperarEmail.requestFocus();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }else {
                    FancyToast.makeText(RecuperarPassActivity.this,"Debe ingresar el correo!",FancyToast.LENGTH_SHORT,FancyToast.INFO,false).show();
                    recuperarEmail.requestFocus();
                }

            }
        });
    }
}