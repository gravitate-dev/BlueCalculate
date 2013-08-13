package com.witch.bluecalculate;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import android.util.Log;

public class BluetoothMessenger {
	
	
	public static enum Code {
		ADDITION,
		SUBTRACTION
		}
	
	public BluetoothMessenger(){
		Log.i("BluetoothMessenger", "created");

	}
	
	public static String makeMessage(int first, int second, String operator){
		String finalString, secondNum, firstNum;
		firstNum=  Integer.valueOf(first).toString();
		secondNum=  Integer.valueOf(second).toString();
		finalString= firstNum + " " + operator + " " + secondNum;
		return finalString;
	}
	
	public static String solveString (String expression){
		if (expression==null) return "Invalid expression";
		if (expression.equals(""))return "Invalid expression";
		Evaluator evaluator = new Evaluator();
		String ans;
		
		try {ans= evaluator.evaluate(expression);} 
		catch (EvaluationException ee) {return "Invalid expression";}
		return ans;
	}

}
