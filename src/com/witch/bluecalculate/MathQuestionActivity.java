package com.witch.bluecalculate;

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
	if (event.getAction()!= MotionEvent.ACTION_DOWN)
		return false;
	
	setNumbers_finalDisplay_bundleStringOut('+');
	return false;
	}	
	});
	
	subButton.setOnTouchListener(new OnTouchListener(){
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event){
	if (event.getAction()!= MotionEvent.ACTION_DOWN)
		return false;
	setNumbers_finalDisplay_bundleStringOut('-');
	
	return false;
	}
	});
	
	}
	
	private void setNumbers_finalDisplay_bundleStringOut(char operator){
		try {
		number1 = Integer.parseInt(text1.getText().toString());
		} catch (NumberFormatException nfe){
		number1=0;
		}
		try {
		number2 = Integer.parseInt(text2.getText().toString());
		} catch (NumberFormatException nfe){
		number2=0;
		}
		String op= "" + operator; //making an equivalent String
		finalDisplay= returnAnswer(number1, number2, operator).toString();
		bundleStringOut = BluetoothMessenger.makeMessage(number1, number2, op);
		finish();
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
	
	@Override
	public void finish() {
		//this is where we get the data
	    Intent data = new Intent();
	    data.putExtra("sendString", bundleStringOut);
	    setResult(RESULT_OK, data);
	    super.finish();
	}

}
