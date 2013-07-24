package com.witch.bluecalculate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ConnectedThread extends Thread {
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private String tag = "ConnectedThread";
    private BluetoothHelper bluetoothHelper;
 
    public ConnectedThread(BluetoothHelper bth, BluetoothSocket socket) {
    	this.bluetoothHelper = bth;
    	Log.i(tag,"CONNETED THREAD STARTED");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        String text = "hello robbies";
        byte[] bytes_test = null;
		try {
			bytes_test = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        write(bytes_test);
        Log.i(tag,"Sent bytes");
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
            	Toast.makeText(this.bluetoothHelper.ctx, "Client connected awaitng message", Toast.LENGTH_SHORT).show();
                bytes = mmInStream.read(buffer);
                String myText = new String(buffer, "UTF-8");
                Log.i(tag,"Message gotten!:"+myText);
                Toast.makeText(this.bluetoothHelper.ctx, "Message:"+myText, Toast.LENGTH_SHORT).show();
            	
            } catch (IOException e) {
                break;
            }
        }
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}
