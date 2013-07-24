package com.witch.bluecalculate;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final String tag = "ConnectThread";
    private BluetoothHelper bluetoothHelper;
    
 
    public ConnectThread(BluetoothHelper bth, BluetoothDevice device) {
    	this.bluetoothHelper = bth; 
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
        //mBluetoothAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        setupConnection(mmSocket);
    }
    
    public void setupConnection(BluetoothSocket socket){
		Log.i(tag,"Starting a thread because we connected!");
		if (socket != null) {
			try {
		ConnectedThread connectedThread = new ConnectedThread(this.bluetoothHelper,socket);
    	connectedThread.run();
			} catch (Exception e){
				e.printStackTrace();
				
			}
		} else {
			Log.e(tag,"PREVENTED:SOCKET WAS NULL");
		}
	}
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
