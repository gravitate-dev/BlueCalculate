package com.witch.bluetooth;

import java.io.IOException;
import java.util.UUID;

import com.witch.bluecalculate.MainActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class AcceptTask extends AsyncTask<Object,Void , BluetoothSocket> {
    public BluetoothServerSocket mmServerSocket;
    private UUID MY_UUID;
    private final String NAME = "BlueCalculate";
    private final String tag = "witch.AcceptThread";
    private BluetoothHelper bluetoothHelper;
    BluetoothSocket socket = null;
    private boolean bAvoidConnect = false;
    private boolean shouldStayOpen = false; //Set true if want multiple connections
 
    public AcceptTask(BluetoothHelper bth, BluetoothAdapter bluetoothAdapter) {
    	this.bluetoothHelper = bth;
    	
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { Log.e(tag,"Another exception lols");}
        mmServerSocket = tmp;
        
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
    	if (mmServerSocket!=null) {
    		bAvoidConnect = true;
        try {mmServerSocket.close();} catch (IOException e) { Log.e(tag, "Server killing error"); }
        Log.i(tag, "Server killed");
    	}
    }
	
	protected void onPostExecute(BluetoothSocket socket){
		Log.i(tag,"What does this do");
		/*
		 * i call this here IMPORTANT! Because otherwise it will 
		 * A. crash because i create a thread inside an asyncTask
		 * Here we also have to make sure socket is not closed
		*/
		if (socket!=null && bAvoidConnect==false) {
		bluetoothHelper.establishConnectionAsServer(socket); 
		}
	}
	@Override
	protected BluetoothSocket doInBackground(Object... arg0) {
		Log.i(tag,"Server started: waiting for connection...");
            try {
                socket = mmServerSocket.accept();
                
            } catch (IOException e) {
            	Log.e(tag,"Closed It!");
                return null;
            }
                try {
    				mmServerSocket.close();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
                Log.i(tag,"Connection found now leaving task");
		return socket;
	}
}