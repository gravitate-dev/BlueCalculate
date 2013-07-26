package com.witch.bluecalculate;

import android.util.Log;

public class BluetoothMessenger {
	
	private String finalString, secondNum, firstNum;
	public static enum Code {
		ADDITION,
		SUBTRACTION
		}
	
	BluetoothMessenger(){
		Log.i("BluetoothMessenger", "created");

	}
	
	public String makeMessage(int first, int second, String operator){
		
		firstNum=  Integer.valueOf(first).toString();
		secondNum=  Integer.valueOf(second).toString();
		finalString= firstNum + " " + operator + " " + secondNum;
		return finalString;
	}
	
	public Integer solveString (String expression){
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
