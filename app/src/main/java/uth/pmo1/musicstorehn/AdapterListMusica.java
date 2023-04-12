package uth.pmo1.musicstorehn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import uth.pmo1.musicstorehn.Servicios_Modelo.Result;

public class AdapterListMusica extends BaseAdapter {

    private Context context = null;
    private List<Result> results = null;

    public AdapterListMusica(Context newContext, List<Result> newResults){
        context = newContext;
        results = newResults;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int i) {
        return results.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        final int ROW_RESOURCE = R.layout.lista_view_canciones;
        ViewHolder viewHolder = null;

        //ConvertView es null entonces no tiene elementos creados
        if (convertView == null){
            LayoutInflater layout = LayoutInflater.from(context);
            convertView = layout.inflate(ROW_RESOURCE, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            //En caso de que este creado se lo recupera
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Result result = results.get(pos);
        try {
            viewHolder.nombreCancion.setText(result.getTrackName());
            viewHolder.nombreArtista.setText(result.getArtistName());
            Picasso.get().load(result.getArtworkUrl100()).error(R.drawable.baseline_music_note_24).into(viewHolder.imgPhoto);
            String destFilename = context.getCacheDir() + "/" + result.getTrackId() + ".m4a";
            if (new File(destFilename).exists()) {
                viewHolder.imgAction.setImageResource(R.drawable.baseline_play_circle_outline_24);
                result.setState(2);
            } else {
                viewHolder.imgAction.setImageResource(R.drawable.baseline_arrow_circle_down_24);
                result.setState(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public static class ViewHolder{

        ImageView imgPhoto = null;
        TextView nombreCancion = null;
        TextView nombreArtista = null;
        ImageView imgAction = null;

        public ViewHolder(View view){
            imgPhoto = view.findViewById(R.id.imgPhoto);
            nombreCancion = view.findViewById(R.id.txtNombreCancion);
            nombreArtista = view.findViewById(R.id.txtNombreArtista);
            imgAction = view.findViewById(R.id.imgAction);

        }
    }
}
