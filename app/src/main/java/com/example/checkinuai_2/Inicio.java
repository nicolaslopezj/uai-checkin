package com.example.checkinuai_2;

import java.util.Timer;
import java.util.TimerTask;

import com.example.checkinuai_2.ListViewClases.AdaptadorClasesList;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class Inicio extends Activity {
	
	static Content content;
	Context context;
	static boolean active = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        content = new Content(this, this);
        content.setContent();
        context = this;
        startTimer();
        initImageLoader();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);
    }
    
    @Override
	protected void onStart() {
		super.onStart();
		content.setList();
		active = true;
	}
    
    @Override
	protected void onStop() {
		super.onStop();
		content.setList();
		active = false;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "Actualizar");
        menu.add(0, 2, 0, "Ajustes");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == 1){ 
        	web_server_connect actualizar = new web_server_connect(this);
        	actualizar.todo();
    	}
    	if(item.getItemId() == 2) {
    		Intent intent = new Intent(this.getApplicationContext(), Ajustes.class);
			context.startActivity(intent);
    	}
    	return true;
    }
    
    public static void refreshList(){
    	content.setList();
    }
    
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
					if(active){
						web_server_connect actualizar = new web_server_connect(context);
	                	actualizar.todo();
					}else{
						Log.i("Timer", "Se intentan acualizar los datos, pero esta haciendo checkin");
					}
                }
            });
        }
    }
    
    public void startTimer(){
    	Log.e("Timer", "Se Empieza el Loop");
    	Timer myTimer = new Timer();
    	MyTimerTask myTimerTask= new MyTimerTask();
    	int minutos = 20;
    	myTimer.scheduleAtFixedRate(myTimerTask, 0, minutos * 60000);
    }
}
