package com.witch.bluecalculate;

import com.witch.bluecalculate.R;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String tag = "MainActivity";
	private TextView textViewOutput;
	private Button buttonConnect, buttonServer;
	private BluetoothHelper bluetoothHelper;
	public static Activity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = this;
		
		textViewOutput = (TextView)findViewById(R.id.debugText);
		buttonConnect = (Button)findViewById(R.id.buttonConnect);
		buttonServer = (Button)findViewById(R.id.buttonServer);
		textViewOutput.setText("what the");
		
		bluetoothHelper = new BluetoothHelper(this);
		//bluetoothHelper.initServer();
		//SERVER
		buttonServer.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() != MotionEvent.ACTION_UP) return false;
				Log.i(tag,"Server button clicked");
				bluetoothHelper.initServer();
				
				return false;
			}
			
		});
		//CLIENT
		buttonConnect.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() != MotionEvent.ACTION_UP) return false;
				Log.i(tag,"Client button clicked");
				Toast.makeText(MainActivity.this, "Trying to connect", Toast.LENGTH_SHORT).show();
				bluetoothHelper.initClient();
				return false;
			}
		}
		);
		
		//bluetoothHelper.scanForOthers(); //gets other devices
		
		//bluetoothHelper.startDiscovery(); //discovers other phones
		Log.i(tag,"Success");
		
	}
	
	
	
	public void lol(String s)
	{
		/*String old = textViewOutput.getText().toString();
		old +="\n"+s;
		textViewOutput.setText(old);
		*/
		Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
