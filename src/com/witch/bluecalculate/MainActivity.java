package com.witch.bluecalculate;

import com.witch.bluecalculate.R;
import com.witch.bluetooth.BluetoothHelper;

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

public class MainActivity extends Activity implements View.OnTouchListener {
	
	private enum INTENTCODE {
		SET_MATH_PROBLEM
	}
	private String tag = "witch.MainActivity";
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
		
		bluetoothHelper = new BluetoothHelper(MainActivity.this);
		buttonServer.setOnTouchListener(this);
		buttonConnect.setOnTouchListener(this);
		Log.i(tag,"Started Activity Successfully");
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == INTENTCODE.SET_MATH_PROBLEM.ordinal()) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	String sendMe = data.getStringExtra("sendString");
	        	bluetoothHelper.setSendMessage(sendMe);
	            bluetoothHelper.initClient();
	        }
	    }
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


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) return false;
		switch(v.getId())
		{
		case R.id.buttonConnect:
			Log.i(tag,"Client button clicked starting math activity");
			//the client will be started after the intent is returned
			Intent intent = new Intent(getBaseContext(),MathQuestionActivity.class);
			startActivityForResult(intent,INTENTCODE.SET_MATH_PROBLEM.ordinal());
			break;
		case R.id.buttonServer:
			Log.i(tag,"Server button clicked");
			bluetoothHelper.initServer();
			break;
		default:
			break;
		}
		return false;
	}

}
