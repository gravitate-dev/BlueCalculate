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
    private BluetoothServerSocket mmServerSocket;
    private UUID MY_UUID;
    private final String NAME = "Robbie";
    private final String tag = "witch.AcceptThread";
    private BluetoothHelper bluetoothHelper;
    BluetoothSocket socket = null;
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
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
	
	protected void onPostExecute(BluetoothSocket socket){
		Log.i(tag,"What does this do");
		/*
		 * i call this here IMPORTANT! Because otherwise it will 
		 * A. crash because i create a thread inside an asyncTask
		*/
		bluetoothHelper.establishConnectionAsServer(socket); 
	}

	@Override
	protected BluetoothSocket doInBackground(Object... arg0) {
		Log.i(tag,"Server started: waiting for connection...");
            try {
                socket = mmServerSocket.accept();
                
            } catch (IOException e) {
            	Log.e(tag,"Exception here!");
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