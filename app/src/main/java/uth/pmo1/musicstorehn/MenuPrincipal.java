package uth.pmo1.musicstorehn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    SharedPreferences preferences;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ImageButton perfil, usuarios, musica, musicaonline, informacion, salir;
    TextView tusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        tusuario = findViewById(R.id.textVUsuario);
        perfil = findViewById(R.id.btnPerfil);
        musica = findViewById(R.id.btnMusica);
        musicaonline = findViewById(R.id.btnMusicaonline);
        usuarios = findViewById(R.id.btnUsuarios);
        informacion = findViewById(R.id.btnInformacion);
        salir = findViewById(R.id.btnSalir);


        preferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        String email = preferences.getString("email", null);
        String password = preferences.getString("password", null);


        perfil.setOnClickListener(view ->  startActivity(new Intent(MenuPrincipal.this, UsuarioActivity.class)));
        musica.setOnClickListener(view ->  startActivity(new Intent(MenuPrincipal.this, MusicaActivity.class)));
        usuarios.setOnClickListener(view ->  startActivity(new Intent(MenuPrincipal.this, ListaUsuariosActivity.class)));
        musicaonline.setOnClickListener(view ->  startActivity(new Intent(MenuPrincipal.this, MusicaOnlineActivity.class)));

        informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformacionFragment infoF = new InformacionFragment();
                infoF.show(getSupportFragmentManager(),"ver");
            }
        });


        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String usuario = ""+snapshot.child("usuario").getValue();
                    if (email!=null && password!=null){
                        tusuario.setText(usuario);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipal.this);
                builder.setCancelable(false);
                builder.setMessage("¿Desea cerrar la sesión de MusicStoreHN?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                preferences.edit().clear().apply();
                                firebaseAuth.signOut();

                                Intent intent = new Intent(MenuPrincipal.this,IniciarSesionActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
    }
}