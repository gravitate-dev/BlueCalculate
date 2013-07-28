package com.witch.bluecalculate;

//hi there
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MathQuestionActivity extends Activity {
	
	private Button addButton, subButton;
	private String tag = "witch.MainActivity";
	private EditText text1, text2;
	private TextView resultBox;
	private MotionEvent event = null;
	private int number1, number2;
	public String finalDisplay;
	private String bundleStringOut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_question);
	
	addButton = (Button) findViewById(R.id.addition);
	subButton= (Button) findViewById(R.id.subtraction);
	text1= (EditText) findViewById(R.id.editText1);
	text2= (EditText) findViewById(R.id.editText2);
	resultBox= (TextView) findViewById(R.id.TextView01);
	
	addButton.setOnTouchListener(new OnTouchListener(){
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
	// TODO Auto-generated method stub
	
	if (event.getAction()!= MotionEvent.ACTION_DOWN)
	return false;
	
	try {
	number1 = Integer.parseInt(text1.getText().toString());
	} catch (NumberFormatException nfe){
	System.out.println("Could not parse " + nfe);
	number1=0;
	}
	
	try {
	number2 = Integer.parseInt(text2.getText().toString());
	} catch (NumberFormatException nfe){
	System.out.println("Could not parse " + nfe);	
	number2=0;
	}
	
	finalDisplay= returnAdd(number1, number2).toString();
	
	bundleStringOut = BluetoothMessenger.makeMessage(number1, number2, "+");
	finish();
	return false;
	}
	
	});
	
	subButton.setOnTouchListener(new OnTouchListener(){
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event){
	
	if (event.getAction()!= MotionEvent.ACTION_DOWN)
		return false;
	
	try {
		number1 = Integer.parseInt(text1.getText().toString());
	} catch (NumberFormatException nfe){
		System.out.println("Could not parse " + nfe);
		number1=0;
	}
	
	try {
		number2 = Integer.parseInt(text2.getText().toString());
	} catch (NumberFormatException nfe){
		System.out.println("Could not parse " + nfe);	
		number2=0;
	}
	
	finalDisplay= returnSub(number1, number2).toString();
	
	bundleStringOut = BluetoothMessenger.makeMessage(number1, number2, "-");
	finish();
	
	return false;
	}
	});
	
	}
	
	public Integer returnAdd(int a, int b){
		int sum=0;	
		sum= a+b;	
		Integer result= Integer.valueOf(sum);
		
		return result;
	}
	
	public Integer returnSub(int a, int b){
		int sum=0;	
		sum= a-b;	
		Integer result= Integer.valueOf(sum);
		
		return result;
	}
	
	@Override
	public void finish() {
		//this is where we get the data
	    Intent data = new Intent();
	    data.putExtra("sendString", bundleStringOut);
	    setResult(RESULT_OK, data);
	    super.finish();
	}

}
