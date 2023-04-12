package uth.pmo1.musicstorehn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MusicaActivity extends AppCompatActivity{

    private boolean checkPermisos = false;
    Uri uri;
    String nombreCancion, urlCancion;
    ListView listviewMusic;
    ArrayList<String> arrayListNombreCanciones = new ArrayList<>();
    ArrayList<String> arrayListUrlCanciones = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    JcPlayerView jcPlayerView;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musica);

        listviewMusic = findViewById(R.id.listviewMusic);
        jcPlayerView = findViewById(R.id.jcplayer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //FancyToast.makeText(MusicaActivity.this,"Suscrito a enviar a todos!",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
            }
        });

        recuperCancion();

        listviewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                jcPlayerView.playAudio(jcAudios.get(position));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification(R.drawable.baseline_music_note_24);
            }
        });
    }

    private void llamarnotificacion(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("to", "/topics/"+"enviaratodos");
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Nueva Canción");
            notificacion.put("detalle", "Hay una nueva canción, escúchala!!!");

            jsonObject.put("data",notificacion);
            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,URL,jsonObject,null,null){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> header = new HashMap<>();

                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAseGmxuI:APA91bGRCs1Si4LqiPLvYzpLU19Rv1Y1M3nnMGEFGrx9ew9MWiYj5-9-ad8zLAPfx42FxQNluwRJMNWbu_iyScdm4QMVgd5M2DLgBq4sk973JHHDhd_Jao7Yx6jPYLHKpN21iL8uOpDO");
                    return header;
                }
            };
            requestQueue.add(jsonObjectRequest);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    //METODO PARA RECUPERAR LAS CANCIONES
    private void recuperCancion() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Canción");
        databaseReference.addValueEventListener(new ValueEventListener() {
            //SE RECUPERA TODA LA DIRECCION QUE ESTA EN FIREBASE, PARA PODER RECUPERAR LA CANCION
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jcAudios.clear();
                arrayListNombreCanciones.clear();
                arrayListUrlCanciones.clear();

                for (DataSnapshot ds :dataSnapshot.getChildren()){
                    Cancion cancionObj = ds.getValue(Cancion.class);
                    arrayListNombreCanciones.add(cancionObj.getNombreCancion());
                    arrayListUrlCanciones.add(cancionObj.getUrlCancion());

                    //REPRODUCIR MUSICA EN EL PLAYER
                    jcAudios.add(JcAudio.createFromURL(cancionObj.getNombreCancion(),cancionObj.getUrlCancion()));

                }

                arrayAdapter = new ArrayAdapter<String>(MusicaActivity.this, android.R.layout.simple_list_item_1,arrayListNombreCanciones){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position,convertView,parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        textView.setSingleLine(true);
                        textView.setMaxLines(1);

                        return view;
                    }
                };
                jcPlayerView.initPlaylist(jcAudios,null);
                listviewMusic.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //METODO PARA LA ELECCION DE LA CANCION
    private void elegircancion() {
        Intent intent_subir = new Intent();
        intent_subir.setType("audio/*");
        intent_subir.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_subir, 1);
    }


    //METODO DEL RESULTADO PARA EVALUAR SI YA HAY UN ARCHIVO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1){
            //SI EL RESULTADO ES CORRECTO, ACCEDEMOS AL ALMACENAMIENTO DEL TELEFONO PARA ELEGI LA CANCION
            if(resultCode == RESULT_OK){
                uri = data.getData();
                Cursor mcursor = getApplicationContext().getContentResolver().query(uri,null,null,null,null);

                int nombreindex = mcursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                mcursor.moveToFirst();
                nombreCancion = mcursor.getString(nombreindex);
                mcursor.close();

                //LLAMAR METODO PARA SUBIR CANCION
                subircancionFirebase();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //METODO SUBIR CANCION A FIREBASE
    private void subircancionFirebase() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Canción").child(uri.getLastPathSegment());

        ProgressDialog progressDialog = new ProgressDialog(MusicaActivity.this);
        progressDialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri cancionurl = uriTask.getResult();
                urlCancion = cancionurl.toString();

                //LLAMAR METODO ENVIAR DETALLES A LA BASE DE DATOS
                enviardetallesDB();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MusicaActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progreso = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int progresoactual = (int)progreso;
                progressDialog.setMessage("Subiendo: " +progresoactual+ "%");
            }
        });
    }


    //METODO DE ENVIAR DETALLES A LA BASE DE DATOS
    private void enviardetallesDB() {
        Cancion cancionObj = new Cancion(nombreCancion, urlCancion);
        FirebaseDatabase.getInstance().getReference("Canción").push().setValue(cancionObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FancyToast.makeText(MusicaActivity.this,"Canción subida con éxito!",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                            llamarnotificacion();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FancyToast.makeText(MusicaActivity.this,"No se pudo subir la canción!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    }
                });
    }


    //VALIDAMOS LOS PERMISOS
    private boolean validarPermisos(){
        Dexter.withActivity(MusicaActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        checkPermisos = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        checkPermisos = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        return checkPermisos;
    }


    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_musica,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.subir_archivo){
            if (validarPermisos()){
                //LLAMAMOS EL METODO PARA ELEGIR LA CANCION
                elegircancion();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}