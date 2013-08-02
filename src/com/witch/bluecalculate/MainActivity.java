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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
	/*end*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = this;
		
		tv_Answer = (TextView)findViewById(R.id.textView_Answer);
		tv_Answer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
		
		bluetoothHelper = new BluetoothHelper(MainActivity.this);
	/*start*/	
		addButton = (Button) findViewById(R.id.addition);
		subButton= (Button) findViewById(R.id.subtraction);
		text1= (EditText) findViewById(R.id.editText1);
		text2= (EditText) findViewById(R.id.editText2);
		resultBox= (TextView) findViewById(R.id.TextView01);
		
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
		
		//start server when its loaded 
		bluetoothHelper.initServer();
		Log.i(tag,"Started Activity Successfully");
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == INTENTCODE.SET_MATH_PROBLEM.ordinal()) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	
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
		/*String old = textViewOutput.getText().toString();
		old +="\n"+s;
		textViewOutput.setText(old);
		*/
		//Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
		tv_Answer.setText(s);
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
		try {
		number1 = Integer.parseInt(text1.getText().toString());
		number2 = Integer.parseInt(text2.getText().toString());
		} catch (NumberFormatException nfe){}
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
}
