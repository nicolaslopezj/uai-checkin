package com.example.checkinuai_2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Firma extends Activity{
	
	PaintHelper drawView;
	
	String nombre_profesor;
	String rut_profesor;
	String nombre_clase;
	String id_clase;
	String hora_clase;
	String sala_clase;

	Button c;

    EditText inptrmplz;
    CheckBox chkbx;
	EditText inputConfirmarConRut;
	
	SharedPreferences preferences;

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        preferences = this.getApplicationContext().getSharedPreferences("CheckInUAIPreferencias", MODE_PRIVATE);
        
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
        	id_clase = extras.getString("id_clase");
        }
        
        //se buscan los datos de la clase en el db
    	clases_sql sql = new clases_sql(this);
		sql.open();
		String [] clase_info = sql.claseInfo(id_clase);
		sql.close();
		//se obtienen los datos de la clase
		rut_profesor = clase_info[1];
		nombre_clase = clase_info[2];
		hora_clase = clase_info[3];
		nombre_profesor = clase_info[4];
		sala_clase = clase_info[6];

        drawView = new PaintHelper(this);
        setContentView(drawView);
        drawView.requestFocus();
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        
      //el input del reemplazante
        inputConfirmarConRut = new EditText(this);
        inputConfirmarConRut.setHint("Confirma con tu Rut");
        inputConfirmarConRut.setHintTextColor(-65536);
        inputConfirmarConRut.setSingleLine(true);
        inputConfirmarConRut.setInputType(InputType.TYPE_CLASS_PHONE);
        inputConfirmarConRut.setHeight(75);
        inputConfirmarConRut.setTextSize(28);
        inputConfirmarConRut.setWidth(getWindowManager().getDefaultDisplay().getWidth());
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(150, 0, 0, 0); //left, top, right, bottom
        params.gravity = Gravity.BOTTOM;
        getWindow().addContentView(inputConfirmarConRut,params);
        
        inputConfirmarConRut.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().equals(rut_profesor)) {
					inputConfirmarConRut.setTextColor(-16711936);
					checkIn();
				} else {
					inputConfirmarConRut.setTextColor(-65536);
				}
			}
		});
        
        
      //boton que dice Aceptar
        c = new Button(this);
        c.setText("Aceptar");
        c.setTextSize(28);
        c.setHeight(75);
        c.setWidth(getWindowManager().getDefaultDisplay().getWidth() - 110);
        params.setMargins(150, 0, 0, 0); //left, top, right, bottom
        params.gravity = Gravity.BOTTOM;
        getWindow().addContentView(c,params);
        
        c.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkIn();
			}
		});
        
        
        if (preferences.getBoolean("ConfirmarConRut", false)) {
        	c.setVisibility(View.GONE);
        } else {
        	inputConfirmarConRut.setVisibility(View.GONE);
        }
        
        
        //boton que dice cancelar
        Button can = new Button(this);
        can.setText("Cancelar");
        can.setTextSize(28);
        can.setHeight(75);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0); //left, top, right, bottom
        params.gravity = Gravity.BOTTOM;
        getWindow().addContentView(can,params);
        
        //el checkbox del reemplazante
        chkbx = new CheckBox(this);
        chkbx.setText("Soy Reemplazante");
        chkbx.setHeight(75);
        chkbx.setTextSize(22);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 0, 75); //left, top, right, bottom
        params.gravity = Gravity.BOTTOM;
        getWindow().addContentView(chkbx,params);
        
        //el input del reemplazante
        inptrmplz = new EditText(this);
        inptrmplz.setVisibility(View.INVISIBLE);
        inptrmplz.setHint("Si eres un reemplazante escribe tu rut sin dígito verificador");
        inptrmplz.setSingleLine(true);
        inptrmplz.setInputType(InputType.TYPE_CLASS_PHONE);
        inptrmplz.setHeight(75);
        inptrmplz.setTextSize(28);
        inptrmplz.setWidth(getWindowManager().getDefaultDisplay().getWidth());
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(230, 0, 0, 73); //left, top, right, bottom
        params.gravity = Gravity.BOTTOM;
        getWindow().addContentView(inptrmplz,params);
        
        //la hora de la clase
        TextView h = new TextView(this);
        h.setText(hora_clase);
        h.setTextSize(20);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.setMargins(10, 10, 0, 0); //left, top, right, bottom
        getWindow().addContentView(h, params);
        
        //la sala de la clase
        TextView s = new TextView(this);
        s.setText("Sala: " + sala_clase);
        s.setTextSize(30);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.gravity = Gravity.RIGHT;
        params.setMargins(500, 0, 0, 0); //left, top, right, bottom
        getWindow().addContentView(s, params);
        
        //nombre de la clase
        TextView nc = new TextView(this);
        nc.setText(nombre_clase);
        nc.setTypeface(null, Typeface.BOLD);
        nc.setTextSize(36);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.setMargins(10, 30, 0, 0); //left, top, right, bottom
        getWindow().addContentView(nc, params);
        
        //nombre del profesor
        TextView n = new TextView(this);
        n.setText(nombre_profesor);
        n.setTextSize(24);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.setMargins(10, 70, 0, 0); //left, top, right, bottom
        getWindow().addContentView(n, params);
        
        //linea de arriba del cuadrado de la firma
        View line1 = new View(this);
        line1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        line1.setBackgroundColor(Color.rgb(51, 51, 51));
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.TOP;
        params.setMargins(0, 110, 0, 0); //left, top, right, bottom
        getWindow().addContentView(line1, params);
        
        //text que dice firme aqui
        TextView f = new TextView(this);
        f.setText("Firme aquí:");
        f.setTypeface(null, Typeface.BOLD);
        f.setTextSize(22);
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.setMargins(10, 120, 0, 0); //left, top, right, bottom
        getWindow().addContentView(f, params);
        
        //linea de abajo del cuadrado de la firma
        View line2 = new View(this);
        line2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        line2.setBackgroundColor(Color.rgb(51, 51, 51));
        params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.BOTTOM;
        params.setMargins(0, 0, 0, 150); //left, top, right, bottom
        getWindow().addContentView(line2, params);
        
        can.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
        
        chkbx.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    inptrmplz.setVisibility(View.VISIBLE);
                } else {
                    inptrmplz.setVisibility(View.INVISIBLE);
                }

				if (preferences.getBoolean("ConfirmarConRut", false)) {
					if (isChecked) {
						inputConfirmarConRut.setVisibility(View.GONE);
						c.setVisibility(View.VISIBLE);
					} else {
						inputConfirmarConRut.setVisibility(View.VISIBLE);
						c.setVisibility(View.GONE);
					}
				}
			}
		});
    }
	
	 public void checkIn(){
		 	if(drawView.paths.size() == 1){
		 		Log.e("Firma", "El profesor " + nombre_profesor + " trato de hacer checkin sin firmar");
                if (chkbx.isChecked()) {
                    Toast.makeText(this, "Tienes que firmar para confirmar", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, nombre_profesor + ", tienes que firmar para confirmar", Toast.LENGTH_LONG).show();
                }

				if (preferences.getBoolean("ConfirmarConRut", false)) {
					inputConfirmarConRut.setText("");
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(inputConfirmarConRut.getWindowToken(), 0);
				}
		 	}else{
                String rutReemplazante = "";
                if (chkbx.isChecked()) {
                    rutReemplazante = inptrmplz.getText().toString();
                    if (rutReemplazante.length() < 6) {
                        Toast.makeText(this, "Tienes que escribir tu rut", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(this, "Reemplazante: " + rutReemplazante, Toast.LENGTH_LONG).show();
                }

		 		clases_sql checkin = new clases_sql(this);
		    	checkin.open();
				checkin.marcarAsistencia(id_clase, rutReemplazante);
				checkin.close();
				Log.e("Firma", "Se confirma la clase: " + nombre_clase + " id: " + id_clase + " reemplazante: " + rutReemplazante);
				Toast.makeText(this, "Se ha confirmado la clase " + nombre_clase, Toast.LENGTH_LONG).show();
				finish();
		 	}
	    }

}
