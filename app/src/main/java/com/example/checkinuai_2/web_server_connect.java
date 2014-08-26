package com.example.checkinuai_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class web_server_connect {
	
	private final Context context;
	SharedPreferences preferences;
	
	public web_server_connect(Context c){
		context = c;
		preferences = c.getSharedPreferences("CheckInUAIPreferencias", c.MODE_PRIVATE);  
	}
	
	public void todo(){
		final Dialogs dialog = new Dialogs(context);
		
		class Actualizar extends AsyncTask<Void, Void, Void> {
			//bajando clases
			@Override
		    protected Void doInBackground(Void... params) {
		        try {
		        	Log.e("Descargar Clases", "Se empiezan a descargar las clases");
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpGet httppost = new HttpGet(preferences.getString("LinkDescargas", ""));

		            // Execute HTTP Post Request
		            Log.e("Descargar Clases", "Se conecta a " + preferences.getString("LinkDescargas", ""));
		            HttpResponse response = httpclient.execute(httppost);
		            Log.e("Descargar Clases", "Se conecto al web service");
		            
		            //se lee lo que se devuelve
		            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
		            StringBuilder sb = new StringBuilder();
		            sb.append(reader.readLine() + "\n");
		            String line = "0";
		            while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		            }
		            reader.close();
		            String result11 = sb.toString();
                    Log.v("Descargar Clases", result11);
		            Log.e("Descargar Clases", "Se lee el resultado en JSON");

		            JSONArray JSON = new JSONArray(result11);
		            
		            dialog.setMaxLoadingUpdating(JSON.length());
		            dialog.setProgressLoadingUpdating(0);
		            
		    		clases_sql sql = new clases_sql(context);
		    		Log.e("Descargar Clases", "Se abre la coneccion a la base de datos");
		    		sql.open();
		    		sql.borrarDatos();
		    		Log.e("Descargar Clases", "Se borran los datos antiguos");
		            for(int i = 0; i < JSON.length(); i++){
		            	dialog.incrementLoadingUpdating(1);
		            	JSONObject row = JSON.getJSONObject(i);
                        int id = row.getInt("idclase");
                        String rut = row.getString("rutProfesor");
                        String nombre = row.getString("evento");
                        String fecha = row.getString("fechaHoraInicio");
                        String sala = row.getString("sala");
                        String profesor = row.getString("apellidoProfesor") + " " + row.getString("nombreProfesor");
		            	sql.agregarClase(id,
                                rut,
                                nombre,
                                fecha,
                                sala,
                                profesor);
		                Log.e("Descargar Clases", "Se agrega la clase: " + nombre + " de " + profesor);
		            }
		            Log.e("Descargar Clases", "Se terminan de bajar las clases");
		            sql.close();
		        } catch (JSONException e){
		        	Log.e("Descargar Clases", "Error leyendo el JSON");
		            return null;
		        } catch (SQLException e) {
		        	Log.e("Descargar Clases", "Error con la base de datos");
		            return null;
		        } catch (IOException e) {
		        	Log.e("Descargar Clases", "Error, no hay internet");
		            return null;
		        } catch (Exception e) {
		        	Log.e("Descargar Clases", "Error desconocido");
		            return null;
		        }


		        //subiendo checkins
		        try{
		    		//define HTTP Post
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpPost httppost = new HttpPost(preferences.getString("LinkSubidas", ""));

                    if (preferences.getString("LinkSubidas", "").equals("")) {
                        Log.e("subirCheckIns", "No hay link para cargar las clases");
                        Toast.makeText(context, "No hay link para cargar las clases", Toast.LENGTH_LONG).show();
                        return null;
                    }
		            
		            //Se agregan las Clases
		            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		            //aca se deben agregar los id de las clases
		            clases_sql sql = new clases_sql(context);
		            sql.open();
		            String[][] noSubidos = sql.noSubidos();
		            sql.close();
		            for(int i = 0; i < noSubidos.length; i++){
		            	nameValuePairs.add(new BasicNameValuePair("id[]", noSubidos[i][0]));
			            nameValuePairs.add(new BasicNameValuePair("fecha[]", noSubidos[i][2]));
                        nameValuePairs.add(new BasicNameValuePair("reemplazante[]", noSubidos[i][3]));
			            Log.e("subirCheckIns", "Se agrega al post la clase de id: " + noSubidos[i][0] + " reemplazante: " + noSubidos[i][3]);
		            }
		            Log.e("subirCheckIns", "Se acaban las clases sin subir");
		            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		            
		            HttpResponse httpresponse = httpclient.execute(httppost);
					Log.e("subirCheckIns", "Se hace el HTTP Post");
					String response = EntityUtils.toString(httpresponse.getEntity());
					if(!response.equals("1")){
						Log.e("subirCheckIns", "Error, el servidor ha entregado el siguente mensaje: " + response);
					}else{
						Log.e("subirCheckIns", "Se han subido las clases correctamente");
						sql.open();
						for(int i = 0; i < noSubidos.length; i++){
							sql.marcarSubido(noSubidos[i][0]);
				            Log.e("subirCheckIns", "Se marca como subida la clase de id: " + noSubidos[i][0]);
			            }
						sql.close();
					}
		    	} catch(SQLException e){
					Log.e("subirCheckIns", "Error con la base de datos");
					return null;
		    	} catch(IOException e){
					Log.e("subirCheckIns", "Error, no hay internet");
					return null;
		    	} catch(Exception e){
					Log.e("subirCheckIns", "Error subiendo los checkins: " + e.toString());
					return null;
		    	}
				return null;
		    }

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				dialog.hideLoadingUpdating();
				Log.e("Actualizar", "Se termina el proceso");
				Inicio.refreshList();
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.createLoadingUpdating();
				dialog.setMessageLoadingUpdating("Descargando Clases...");
	            dialog.showLoadingUpdating();
			}
		}
		new Actualizar().execute();
	}
}