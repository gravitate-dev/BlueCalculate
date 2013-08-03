package com.witch.bluecalculate;

import com.witch.bluecalculate.R;
import com.witch.bluetooth.BluetoothHelper;

import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends Activity implements View.OnTouchListener {
	
	private enum INTENTCODE {
		SET_MATH_PROBLEM
	}
	private String tag = "witch.MainActivity";
	private Button buttonConnect;
	private BluetoothHelper bluetoothHelper;
	private TextView tv_Answer;
	public static Activity activity;
	/*start*/
	private Button addButton, subButton;
	private EditText text1, text2;
	private TextView resultBox;
	private MotionEvent event = null;
	private int number1, number2;
	public String finalDisplay;
	private String bundleStringOut;
	private NotificationManager mNotificationManager;
	private Notification.Builder mNotificationBuilder;
	/*end*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = this;
		

		bluetoothHelper = new BluetoothHelper(MainActivity.this);
	/*start*/	
		addButton = (Button) findViewById(R.id.addition);
		subButton= (Button) findViewById(R.id.subtraction);
		text1= (EditText) findViewById(R.id.editText1);
		text2= (EditText) findViewById(R.id.editText2);
		
		addButton.setOnTouchListener(new OnTouchListener(){
		
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {	
		if (event.getAction()!= MotionEvent.ACTION_DOWN)
			return false;
		
		setNumbers_finalDisplay_bundleStringOut('+');
		sendIT(bundleStringOut);
		return false;
		}	
		});
		
		subButton.setOnTouchListener(new OnTouchListener(){
		
		@Override
		public boolean onTouch(View arg0, MotionEvent event){
		if (event.getAction()!= MotionEvent.ACTION_DOWN)
			return false;
		setNumbers_finalDisplay_bundleStringOut('-');
		sendIT(bundleStringOut);
		return false;
		}
		});
		/*end*/
		
		if (bluetoothHelper.initServer()==true)
			Log.i(tag,"Started Activity Successfully");
		Log.i(tag,"Started Activity Successfully");
		
		
		
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Sets an ID for the notification, so it can be updated
		mNotificationBuilder =
		        new Notification.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!")
		        .setTicker("Testing this");
		

	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    	    if (requestCode == 1005) {
	    	//this means start bluetooth
	    	if (bluetoothHelper.initServer()==false)
			{
	    		Log.e(tag,"bad error even after bluetooth is on!");
			}
	    }
	}	
	
	public void sendIT(String sendMe) {
    	bluetoothHelper.setSendMessage(sendMe);
    	if (bluetoothHelper.isConnectedAsClient()==false)
    		bluetoothHelper.initClient();
    	else
    		bluetoothHelper.sendMessage(sendMe);
	}
	public void updateMainTextAnswer(String s)
	{
		//Used a textview
		//tv_Answer.setText(s);
		//Used a toast
		//Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
		//Now using notification!
		
		int notifyID = 151;
		//cancel any old ones
		mNotificationManager.cancel(notifyID);
		mNotificationBuilder.setTicker(s);
	    mNotificationManager.notify(
	            notifyID,
	            mNotificationBuilder.build());
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
		default:
			break;
		}
		return false;
	}
	/*start*/
	private void setNumbers_finalDisplay_bundleStringOut(char operator){
		number1=0;
		number2=0;
		try {number1 = Integer.parseInt(text1.getText().toString());} catch (NumberFormatException nfe){}
		try {number2 = Integer.parseInt(text2.getText().toString());} catch (NumberFormatException nfe){}
		
		String op= "" + operator; //making an equivalent String
		finalDisplay= returnAnswer(number1, number2, operator).toString();
		bundleStringOut = BluetoothMessenger.makeMessage(number1, number2, op);
	}
	
	public Integer returnAnswer(int a, int b, char operator){
		int ans_int=0;
		switch (operator){
			case '+':
				ans_int= a+b;
				break;
			case '-':
				ans_int=a-b;
				break;
			default:
				break;
		}
		Integer answer= Integer.valueOf(ans_int);
		return answer;
	}
	/*end*/
	
	/*public void onBackPressed() {
		   Log.i("HA", "Finishing");
		   finish();
		 }*/
	
	@Override
	protected void onPause(){
		super.onPause();
		//bluetoothHelper.endAnyOpenConnections();
		//here i will set up a boolean that will be read when the android will resume
		//
		Log.i(tag, "On pause called!");
		bluetoothHelper.endAnyOpenConnections();
		
		
		//finish();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.i(tag,"Calling Resume!");
		if (bluetoothHelper.shouldRestartOnResume()) {
			bluetoothHelper.endAnyOpenConnections();
			bluetoothHelper.safeResetServer();
			bluetoothHelper.setShouldRestartOnResume(false);
			Log.i(tag,"restarting server here!");
		}
	}
	
	
	public void requestBluetoothOn(){
		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1005);
	}
}
