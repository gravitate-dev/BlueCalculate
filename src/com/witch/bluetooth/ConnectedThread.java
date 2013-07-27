package com.witch.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.witch.bluecalculate.BluetoothMessenger;
import com.witch.bluecalculate.MainActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ConnectedThread extends Thread{
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private String tag = "ConnectedThread";
    public BluetoothHelper bluetoothHelper;
    private BluetoothMessenger bluetoothMessenger;
    public boolean bIsClient;
    private Context context;
    private Handler handler;
    public String printMe;
    public ConnectedThread(BluetoothHelper bth, BluetoothSocket socket, boolean bIsClient) {
    	bluetoothHelper = bth;
    	context = this.bluetoothHelper.context;
    	this.bIsClient = bIsClient;
    	bluetoothMessenger = new BluetoothMessenger();
    	Log.i(tag,"CONNETED THREAD STARTED");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        handler = new Handler();
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        Log.i(tag, "STARTING THIS");
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

	@Override
	public void run() {
    	byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        
        if (bIsClient) {
        //only send if i am a client
        String text = bluetoothHelper.getSendMessage();
        Log.i(tag,"Going to send:"+text);
        byte[] bytes_test = null;
		try {
			bytes_test = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        write(bytes_test);
        Log.i(tag,"Sent bytes");
        

        	try {
				mmSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //if i am a client
        }
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                String myText = new String(buffer,0,bytes,"UTF-8");
                //lets parse it mathmatically
                Integer x = bluetoothMessenger.solveString(myText);
                printMe = "Solution to: "+myText+" is "+x.toString();
                
                    handler.post(new Runnable() { // This thread runs in the UI
                        @Override
                        public void run() {
                        	((MainActivity) context).lol(printMe);
                        }
                    });
                
                //Toast.makeText(this.bluetoothHelper.ctx, "Message:"+myText, Toast.LENGTH_SHORT).show();
            	
            } catch (IOException e) {
            	Log.e(tag,"WIERD ERROR OMG"+e.getMessage());
                break;
            }
        }                  
        }

}
