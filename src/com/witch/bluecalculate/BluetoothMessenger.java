package com.witch.bluecalculate;

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
	
	public static Integer solveString (String expression){
		if (expression==null) return 0;
		if (expression.equals(""))return 0;
		//only works with binary arithmetic expressions i.e. 2+2
		String[] splitExp;
		String op="";
		Integer ans;
		
		splitExp= expression.split(" ");
		ans=Integer.valueOf(splitExp[0]);
		
		op=splitExp[1];

		if (op.contentEquals("+"))
			ans+=Integer.valueOf(splitExp[2]);
		else if (op.contentEquals("-"))
			ans-=Integer.valueOf(splitExp[2]);
			//if (splitExp[i].matches("[0-9]+"))			
			
		return ans;
	}

}
