package com.example.checkinuai_2;

import android.app.ProgressDialog;
import android.content.Context;

public class Dialogs {
	
	Context context;
	ProgressDialog progressDialog;
	
	public Dialogs(Context c){
		context = c;
	}
	
	public void createLoadingUpdating (){
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}
	public void showLoadingUpdating(){
		progressDialog.show();
	}
	public void hideLoadingUpdating(){
		progressDialog.hide();
	}
	public void incrementLoadingUpdating(int increment){
		progressDialog.incrementProgressBy(increment);
	}
	public void setMaxLoadingUpdating(int max){
		progressDialog.setMax(max);
	}
	public void setProgressLoadingUpdating(int progress){
		progressDialog.setProgress(progress);
	}
	public void setMessageLoadingUpdating(String msg){
		progressDialog.setMessage(msg);
	}
}
