package com.witch.bluetooth;

import java.io.IOException;
import java.util.UUID;

import com.witch.bluecalculate.MainActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private UUID MY_UUID;
    private final String NAME = "Robbie";
    private final String tag = "AcceptThread";
    private MainActivity ctx;
    private BluetoothHelper bluetoothHelper;
    private boolean shouldStayOpen = false; //Set true if want multiple connections
 
    public AcceptThread(BluetoothHelper bth, BluetoothAdapter bluetoothAdapter) {
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
 
    @SuppressLint("NewApi")
	public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        Log.i(tag,"Server started: waiting for connection...");
        while (true) {
        	
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
            	Log.e(tag,"Exception here!");
                break;
            }
            // If a connection was accepted
            if (socket != null) {
            	Log.i(tag,"Connection Established");
                // Do work to manage the connection (in a separate thread)
        			try {
        		ConnectedThread connectedThread = new ConnectedThread(this.bluetoothHelper,socket,false);
        		connectedThread.start();
        			} catch (Exception e){
        				e.printStackTrace();
        				
        			}
                try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
                if (shouldStayOpen==false)
                	return;
            }
        }
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}