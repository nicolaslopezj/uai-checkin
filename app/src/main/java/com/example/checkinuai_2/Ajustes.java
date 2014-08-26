package com.example.checkinuai_2;

import java.lang.reflect.Array;

import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Ajustes extends Activity{
	
	EditText editTextLinkSubidas;
	EditText editTextLinkDescargas;
	EditText editTextClave;
	ToggleButton toggleButtonConfirmarConRut;
	
	Button cargarConPregradoSantiago;
	Button cargarConPregradoVina;
	Button cargarConFIC;
	
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_activity);
        
        preferences = this.getApplicationContext().getSharedPreferences("CheckInUAIPreferencias", MODE_PRIVATE);  
        editor = preferences.edit();
        
        editTextLinkSubidas = (EditText)findViewById(R.id.editLinkSubidas);
        editTextLinkDescargas = (EditText)findViewById(R.id.editTextLinkDescargas);
        editTextClave = (EditText)findViewById(R.id.editTextClave);
        toggleButtonConfirmarConRut = (ToggleButton)findViewById(R.id.toggleButtonConfirmarConRut);
        
        cargarConPregradoSantiago = (Button)findViewById(R.id.buttonCargarConPregradoSantiago);
        cargarConPregradoVina = (Button)findViewById(R.id.buttonCargarConPregradoVina);
        cargarConFIC = (Button)findViewById(R.id.buttonCargarConFIC);
        
        editTextLinkSubidas.setMaxLines(1);
        editTextLinkDescargas.setMaxLines(1);
        editTextClave.setMaxLines(1);
        
        editTextLinkSubidas.setText(preferences.getString("LinkSubidas", ""));
        editTextLinkDescargas.setText(preferences.getString("LinkDescargas", ""));
        toggleButtonConfirmarConRut.setChecked(preferences.getBoolean("ConfirmarConRut", false));
        
        editTextClave.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().equals("uai123uai")) {
					
					editor.putString("LinkSubidas", editTextLinkSubidas.getText().toString());
					editor.putString("LinkDescargas", editTextLinkDescargas.getText().toString());
					editor.putBoolean("ConfirmarConRut", toggleButtonConfirmarConRut.isChecked());
					editor.commit();
					
					Toast.makeText(getApplicationContext(), "Ajustes Guardados", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});
    }
	
	public void cambiarAPregradoSantiago(View v) {
		editTextLinkSubidas.setText("http://firma.uai.cl/firma/checkin.php");
        editTextLinkDescargas.setText("http://firma.uai.cl/firma/clasesTablet.php?sede=santiago");
        toggleButtonConfirmarConRut.setChecked(true);
	}
	
	public void cambiarAPregradoVina(View v) {
		editTextLinkSubidas.setText("http://firma.uai.cl/firma/checkin.php");
        editTextLinkDescargas.setText("http://firma.uai.cl/firma/clasesTablet.php?sede=vina");
        toggleButtonConfirmarConRut.setChecked(false);
	}
	
	public void cambiarAFIC(View v) {
		editTextLinkSubidas.setText("http://firma.uai.cl/firma/checkin.php");
        editTextLinkDescargas.setText("http://firma.uai.cl/firma/clasesTablet.php?sede=fic");
        toggleButtonConfirmarConRut.setChecked(false);
	}

}
