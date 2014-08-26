package com.example.checkinuai_2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class clases_sql {
	public static final String KEY_ROWID = "clase_id";
	public static final String KEY_RUTPROFE = "profesor_rut";
	public static final String KEY_NAME = "clase_nombre";
	public static final String KEY_HORA = "clase_hora";
	public static final String KEY_SALA = "clase_sala";
	public static final String KEY_NAME_PROFESOR = "profesor_nombre";
	
	public static final String KEY_ROWID_CHECKIN = "checkin_id";
	public static final String KEY_ESTADO_CHECKIN = "checkin_estado";
	public static final String KEY_FECHA_CHECKIN = "checkin_fecha";
    public static final String KEY_REEMPLAZANTE_CHECKIN = "checkin_reemplazante";
	
	public static final String DATABASE_TABLE_CHECKIN = "Checkin";
	
	
	public static final String DATABASE_NAME = "CheckInUai";
	public static final String DATABASE_TABLE = "Clase";
	public static final int DATABASE_VERSION = 2;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + 
					KEY_ROWID + " INTEGER PRIMARY KEY, " +
					KEY_RUTPROFE + " TEXT NOT NULL, " +
					KEY_NAME + " TEXT NOT NULL, "+
					KEY_HORA + " DATETIME NOT NULL, "+
					KEY_SALA + " TEXT NOT NULL, "+
					KEY_NAME_PROFESOR + " TEXT NOT NULL);"
					);
			db.execSQL("CREATE TABLE " + DATABASE_TABLE_CHECKIN + " (" + 
					KEY_ROWID_CHECKIN + " INTEGER PRIMARY KEY, " +
					KEY_ESTADO_CHECKIN + " TEXT NOT NULL, " +
                    KEY_REEMPLAZANTE_CHECKIN + " TEXT NOT NULL, " +
					KEY_FECHA_CHECKIN + " DATETIME NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CHECKIN);
			onCreate(db);
		}
	}
	
	public clases_sql(Context c){
		ourContext = c;
	}
	
	public clases_sql open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}
	
	public long agregarClase(int id, String profesor_rut, String clase_nombre, String clase_hora, String clase_sala, String profesor_nombre) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEY_RUTPROFE, profesor_rut);
		cv.put(KEY_NAME, clase_nombre);
		cv.put(KEY_HORA, clase_hora);
		cv.put(KEY_SALA, clase_sala);
		cv.put(KEY_NAME_PROFESOR, profesor_nombre);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}
	
	public void borrarDatos(){
		ourDatabase.execSQL("DELETE FROM " + DATABASE_TABLE);
	}
	
	public String[] claseInfo(String id_clase){
		String[] columns = new String[]{ KEY_ROWID, KEY_RUTPROFE, KEY_NAME, KEY_HORA, KEY_SALA, KEY_NAME_PROFESOR};
		String[] info = new String[7];
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + id_clase, null, null, null, null, null);
		
		int iId = c.getColumnIndex(KEY_ROWID);
		int iRut = c.getColumnIndex(KEY_RUTPROFE);
		int iNombre_Clase = c.getColumnIndex(KEY_NAME);
		int iHora = c.getColumnIndex(KEY_HORA);
		int iSala = c.getColumnIndex(KEY_SALA);
		int iNombre_Profesor = c.getColumnIndex(KEY_NAME_PROFESOR);
		
		//si no es nulo
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			info[0] = c.getString(iId);
			info[1] = c.getString(iRut);
			info[2] = c.getString(iNombre_Clase);
			info[3] = c.getString(iHora);
			info[4] = c.getString(iNombre_Profesor);
			info[5] = "";
			info[6] = c.getString(iSala);
			c.close();
			return info;
		}
		c.close();
		return null;
	}
	
	public String[][] buscador(String query){
		Log.e("Buscador", "Se inicia el buscador con el termino: '" + query + "'");

		//String[] columns = new String[]{ KEY_ROWID, KEY_RUTPROFE, KEY_NAME, KEY_HORA, KEY_SALA, KEY_NAME_PROFESOR};
		Cursor c = ourDatabase.rawQuery(
				"SELECT " + KEY_ROWID + ", " + KEY_RUTPROFE + ", " + KEY_NAME + ", " + KEY_HORA + ", " + KEY_SALA + ", " + KEY_NAME_PROFESOR +
				" FROM " + DATABASE_TABLE +
				" WHERE " +
				"(" + KEY_NAME_PROFESOR + " LIKE '% " + query + "%' OR " + KEY_NAME_PROFESOR + " LIKE '" + query + "%') AND " + 
				//"DATE(" + KEY_HORA + ") = DATE('now') AND " +
				KEY_ROWID + " NOT IN (SELECT " + KEY_ROWID_CHECKIN + " FROM " + DATABASE_TABLE_CHECKIN + ") ORDER BY " + KEY_NAME_PROFESOR + "," + KEY_HORA
				, null);
		
		int iId = c.getColumnIndex(KEY_ROWID);
		int iRut = c.getColumnIndex(KEY_RUTPROFE);
		int iNombre_Clase = c.getColumnIndex(KEY_NAME);
		int iHora = c.getColumnIndex(KEY_HORA);
		int iSala = c.getColumnIndex(KEY_SALA);
		int iNombre_Profesor = c.getColumnIndex(KEY_NAME_PROFESOR);
		
		String[][] clases = new String[c.getCount()][7];
		int i = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			clases[i][0] = c.getString(iId);
			clases[i][1] = c.getString(iRut);
			clases[i][2] = c.getString(iNombre_Clase);
			
			//convierte la fecha en solo hora
			SimpleDateFormat formato1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat formato2 = new SimpleDateFormat("HH:mm");
			Date fecha = null;
			try {
				fecha = formato1.parse(c.getString(iHora));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			clases[i][3] = formato2.format(fecha);
			clases[i][6] = "";
			clases[i][4] = c.getString(iNombre_Profesor);
			clases[i][6] = c.getString(iSala);
			i++;
		}
		c.close();
		return clases;
	}
	
	//
	//
	//CHECKINS
	//
	//
	
	
	public long marcarAsistencia(String id, String reemplazante) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID_CHECKIN, id);
		//estado: 0 pentiente
		cv.put(KEY_ESTADO_CHECKIN, "0");
        cv.put(KEY_REEMPLAZANTE_CHECKIN, reemplazante);
		//fecha
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		cv.put(KEY_FECHA_CHECKIN, currentTime);
		
		Log.e("Marcar Asistencia", "Se marca la asistencia de la clase con id: " + id + ", en la fecha: " + currentTime);
		
		return ourDatabase.insert(DATABASE_TABLE_CHECKIN, null, cv);
	}
	
	public void marcarSubido(String id){
		ourDatabase.execSQL("UPDATE " + DATABASE_TABLE_CHECKIN + " SET " + KEY_ESTADO_CHECKIN + " = '1' WHERE " + KEY_ROWID_CHECKIN + " = '" + id + "'");
		Log.e("Marcar Asistencia", "Se marca como subido el checkin de la clase con id: " + id);
	}
	
	public String[] checkinInfo(String id_clase){
		String[] columns = new String[]{ KEY_ROWID_CHECKIN, KEY_ESTADO_CHECKIN, KEY_FECHA_CHECKIN};
		String[] info = new String[3];
		Cursor c = ourDatabase.query(DATABASE_TABLE_CHECKIN, columns, KEY_ROWID_CHECKIN + "=" + id_clase, null, null, null, null, null);
		
		int iId = c.getColumnIndex(KEY_ROWID_CHECKIN);
		int iEstado = c.getColumnIndex(KEY_ESTADO_CHECKIN);
		int iFecha = c.getColumnIndex(KEY_FECHA_CHECKIN);
		
		//si no es nulo
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			info[0] = c.getString(iId);
			info[1] = c.getString(iEstado);
			info[2] = c.getString(iFecha);
			c.close();
			
			Log.e("Informacion del Checkin", "Se obtiene la informacion del checkin de la clase de id: " + info[0]);
			return info;
		}
		c.close();
		return null;
	}
	
	public String[][] noSubidos(){
		Log.e("Checkins no Subidos", "Se inicia el buscador de los checkins no subidos");
		
		String[] columns = new String[]{KEY_ROWID_CHECKIN, KEY_ESTADO_CHECKIN, KEY_FECHA_CHECKIN, KEY_REEMPLAZANTE_CHECKIN};
		Cursor c = ourDatabase.query(DATABASE_TABLE_CHECKIN, columns,
				KEY_ESTADO_CHECKIN + " = '0'",
				null, null, null, null, null);

        Log.e("Checkins no Subidos", "Se hace el query");
		
		int iId = c.getColumnIndex(KEY_ROWID_CHECKIN);
		int iEstado = c.getColumnIndex(KEY_ESTADO_CHECKIN);
		int iFecha = c.getColumnIndex(KEY_FECHA_CHECKIN);
        int iReemplazante = c.getColumnIndex(KEY_REEMPLAZANTE_CHECKIN);

        Log.e("Checkins no Subidos", "Se sacan los indices de las columnas");
		
		String[][] checkins = new String[c.getCount()][4];
		int i = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			checkins[i][0] = c.getString(iId);
			checkins[i][1] = c.getString(iEstado);
			checkins[i][2] = c.getString(iFecha);
            checkins[i][3] = c.getString(iReemplazante);
			i++;
		}
		Log.e("Checkins no Subidos", "Se obtienen correctamente " + c.getCount() + " checkins no subidos");
		c.close();
		return checkins;
	}
}
