package com.example.checkinuai_2;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class Content {
	
	Context context;
	Activity activity;
	
	EditText buscador;
	ListViewClases list;
	Button okButton;
	
	public Content(Context c, Activity a){
		context = c;
		activity = a;
		
		list = new ListViewClases(context, activity);
	}
	
	public void setContent(){
		setBuscador();
		setList();
	}
	
	public void setList(){
		String query = buscador.getText().toString();
		list.setList(query);
	}
	
	public void setBuscador(){
		buscador = new EditText(context);
		buscador.setHint("Busque por su nombre");
		buscador.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
		buscador.setMaxLines(1);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10)); //left, top, right, bottom
        params.gravity = Gravity.LEFT;
        activity.getWindow().addContentView(buscador,params);
        @SuppressWarnings("deprecation")
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		buscador.setWidth(width);
		buscador.setSingleLine(true);
		
        buscador.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            	list.setList(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
	}
	
	private int dpToPx(int dp)
	{
	    float density = activity.getApplicationContext().getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}
}
