package uth.pmo1.musicstorehn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {

    Context context;
    ArrayList<Usuarios> usuariosLista;

    public UsuariosAdapter(Context con, ArrayList<Usuarios> user){
        context = con;
        usuariosLista = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.lista_usuarios, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Usuarios user = usuariosLista.get(position);

        holder.usuarioD.setText(user.getUsuario());
        holder.correoD.setText(user.getCorreo());
        Picasso.get().load(user.getFotoUrl()).into(holder.imgFoto);

    }

    @Override
    public int getItemCount() {
        return usuariosLista.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView usuarioD, correoD;
        ImageView imgFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usuarioD = itemView.findViewById(R.id.tvUsuario);
            correoD = itemView.findViewById(R.id.tvCorreo);
            imgFoto = itemView.findViewById(R.id.imgVFotografia);

        }
    }
}
