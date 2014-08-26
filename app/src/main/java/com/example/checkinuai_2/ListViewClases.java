package com.example.checkinuai_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ListViewClases {
	
	Context context;
	Activity activity;
	claseList[] clases;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
	
	public ListViewClases(Context c, Activity a){
		context = c;
		activity = a;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
	}
	
	public void setList(String q){
		clases_sql sql = new clases_sql(context);
		sql.open();
		String [][] clases_array = sql.buscador(q);
		sql.close();
		
		clases = new claseList[clases_array.length];
		for(int i = 0; i < clases_array.length; i++){
			clases[i] = new claseList(clases_array[i][0], clases_array[i][2], clases_array[i][4], clases_array[i][3], clases_array[i][6], clases_array[i][1]);
		}
    	
    	AdaptadorClasesList adaptador = new AdaptadorClasesList((Activity) context);
    	ListView listView = (ListView) activity.findViewById(R.id.ListClases);
    	listView.setAdapter(adaptador);
    	
    	//cuando hace clic en una clase
    	listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
            	//se obtiene el id
            	String id = (String) v.findViewById(R.id.infoId).getTag();
            	Log.e("Lista de Clases", "Se hace clic en la clase con id: " + id);
            	//se manda a firmar
        		Intent intent = new Intent("com.example.checkinuai_2.FIRMA");
        		intent.putExtra("id_clase", id);
				context.startActivity(intent);
            }
        });
    }

	public class claseList{
    	public String id;
    	public String nombre;
    	public String nombre_profesor;
        public String rut;
    	public String hora;
    	public String sala;
    	
    	public claseList(String i, String n, String np, String h, String s, String _rut){
            id = i;
            nombre = n;
            nombre_profesor = np;
            hora = h;
            sala = s;
            rut = _rut;
        }
    }
    
    class AdaptadorClasesList extends ArrayAdapter<Object> {
    	 
        Activity context;
     
		AdaptadorClasesList(Activity context) {
            super(context, R.layout.clases_list, clases);
            this.context = context;
        }
     
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.clases_list, null);

            TextView infoId = (TextView)item.findViewById(R.id.infoId);
            infoId.setText(clases[position].id);
            infoId.setTag(clases[position].id);

            TextView labelNombreProfesor = (TextView)item.findViewById(R.id.labelNombreProfesor);
            labelNombreProfesor.setText(clases[position].nombre_profesor);

            TextView labelNombre = (TextView)item.findViewById(R.id.labelNombre);
            labelNombre.setText(clases[position].nombre);

            TextView labelHora = (TextView)item.findViewById(R.id.labelHora);
            labelHora.setText(clases[position].hora);

            TextView labelSala = (TextView)item.findViewById(R.id.labelSala);
            labelSala.setText(clases[position].sala);

            ImageView teacherImageView = (ImageView)item.findViewById(R.id.teacherImageView);
            String url = "http://firma.uai.cl/auxiliares/fotos/" + clases[position].rut + ".jpg";
            imageLoader.displayImage(url, teacherImageView, options);
        
            return(item);
        }
    }
}
