package com.witch.bluecalculate;

import java.util.Stack;

import com.witch.bluecalculate.R;
import com.witch.bluetooth.BluetoothHelper;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
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
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends Activity implements View.OnTouchListener {
	
	private enum INTENTCODE {
		SET_MATH_PROBLEM
	}
	private String tag = "witch.MainActivity";
	private Button buttonConnect;
	private BluetoothHelper bluetoothHelper;
	public static Activity activity;
	/*start*/
	private EditText mainEditText;
	private MotionEvent event = null;
	public String finalDisplay;
	private NotificationManager mNotificationManager;
	private Notification.Builder mNotificationBuilder;
	/*end*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jessicalc_layout);
		activity = this;
		bluetoothHelper = new BluetoothHelper(MainActivity.this);
		
		if (bluetoothHelper.initServer()==true)
			Log.i(tag,"Started Activity Successfully");
		Log.i(tag,"Started Activity Successfully");

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Sets an ID for the notification, so it can be updated
		mNotificationBuilder =
		        new Notification.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("")
		        .setTicker("");
		
		//set up the mainText
		mainEditText = (EditText)findViewById(R.id.outputText);
		
		//set up the buttons!
		findViewById(R.id.zero).setOnTouchListener(this);
		findViewById(R.id.one).setOnTouchListener(this);
		findViewById(R.id.two).setOnTouchListener(this);
		findViewById(R.id.three).setOnTouchListener(this);
		findViewById(R.id.four).setOnTouchListener(this);
		findViewById(R.id.five).setOnTouchListener(this);
		findViewById(R.id.six).setOnTouchListener(this);
		findViewById(R.id.seven).setOnTouchListener(this);
		findViewById(R.id.eight).setOnTouchListener(this);
		findViewById(R.id.nine).setOnTouchListener(this);
		findViewById(R.id.decimal).setOnTouchListener(this);
		findViewById(R.id.division).setOnTouchListener(this);
		findViewById(R.id.multiplication).setOnTouchListener(this);
		findViewById(R.id.minus).setOnTouchListener(this);
		findViewById(R.id.plus).setOnTouchListener(this);
		findViewById(R.id.left_parenthesis).setOnTouchListener(this);
		findViewById(R.id.right_parenthesis).setOnTouchListener(this);
		findViewById(R.id.equals).setOnTouchListener(this);
		findViewById(R.id.bluetooth_equals).setOnTouchListener(this);
		
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
		case R.id.zero:
			/* Case 1 : GOOD
			 * input : null
			 * Case 2 : GOOD
			 * input .
			 * Case 3 : BAD
			 * input 0 
			 * Case 4 :
			 * input - okay
			 */
			canAddZero(mainEditText.getText().toString());
			break;
		case R.id.one:
		case R.id.two:
		case R.id.three:
		case R.id.four:
		case R.id.five:
		case R.id.six:
		case R.id.seven:
		case R.id.eight:
		case R.id.nine:
		case R.id.decimal:
		case R.id.division:
		case R.id.multiplication:
		case R.id.minus:
		case R.id.plus:
		case R.id.left_parenthesis:
		case R.id.right_parenthesis:
			Button b = (Button)v;
		    String buttonText = b.getText().toString();
		    appendMainDisplayText(buttonText);
			break;
		case R.id.equals:
			setMainDisplayText(solveExpression(mainEditText.getText().toString()));
			break;
		case R.id.bluetooth_equals:
			clearMainDisplayText();
			break;
		default:
			break;
		}
		return false;
	}
	
	
	public boolean canAddZero(String expression){
		//tokenize based on operator
		String[] tokens = expression.split("[\\D|^\\.]+");
		for (String toke : tokens)
			Log.i(tag,"tokens"+toke);
		return true;
	}
	public String solveExpression(String expression)
	{
		Evaluator evaluator = new Evaluator();
		String ans;
		
		try {
			ans= evaluator.evaluate(expression);

			
		} catch (EvaluationException ee) {
			return "Invalid expression";
		}
		return ans;
	}
	
	public void clearMainDisplayText(){
		mainEditText.setText("");
	}
	
	public void appendMainDisplayText( String s ) {
		mainEditText.append(s);
		mainEditText.setSelection(mainEditText.getText().length());
	}
	public void setMainDisplayText(String text){
		mainEditText.setText(text);
		mainEditText.setSelection(mainEditText.getText().length());
	}
	
	
	@Override
	protected void onPause(){
		super.onPause();
		//bluetoothHelper.endAnyOpenConnections();
		//here i will set up a boolean that will be read when the android will resume
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
