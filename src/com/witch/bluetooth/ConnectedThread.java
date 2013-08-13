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
    private String tag = "witch.ConnectedThread";
    public BluetoothHelper bluetoothHelper;
    public boolean bIsClient;
    private Context context;
    private Handler handler;
    public String printMe;
    public ConnectedThread(BluetoothHelper bth, BluetoothSocket socket, boolean bIsClient) {
    	bluetoothHelper = bth;
    	//bluetoothHelper.killServer();
    	context = this.bluetoothHelper.context;
    	this.bIsClient = bIsClient;
    	if (bIsClient){
    		Log.i(tag,"I AM CLIENT!");
    		bluetoothHelper.setIsClientConnected(true);
    	} else { //if i am not the client then i will have to connect to the guy who connected to me!
    		//THIS CAN CAUSE BUGS but it auto connects back to the guy who connects
    		//So phone_A connects to phone_B and thats it, BUT WITH THIS LINE OF CODE
    		//it will auto conenct phone_B back to phone_A!
    		bluetoothHelper.startConnection(socket.getRemoteDevice());
    	}
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
        } catch (IOException e) { Log.e(tag,"Interesting error here buddy"); }
    }
    
    public void write(String s){
    	Log.i(tag,"Trying this");
        byte[] bytes_test = null;
		try {
			bytes_test = s.getBytes("UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(tag,"Someting happened"+e.getMessage());
		}
        write(bytes_test);
        Log.i(tag,"Sent bytes");
    	if (s.contains("kill")) {
    		Log.i(tag,"Killing server because i sent kill!");
    		bluetoothHelper.safeResetServer();
        	return;
    	}
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
        
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	write(bluetoothHelper.getSendMessage());
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                String myText = new String(buffer,0,bytes,"UTF-8");
                if (myText.contains("kill")){
                	Log.e(tag,"SUCCESS Ended Thread here!");
                	throw new IOException(); //sneaky way of killing server
                }
                	
                //lets parse it mathmatically
                printMe = myText+"="+ BluetoothMessenger.solveString(myText);
                if (myText.contains("Invalid")==false){
                Log.i(tag,printMe);
                    handler.post(new Runnable() { // This thread runs in the UI
                        @Override
                        public void run() {
                        	((MainActivity) context).updateMainTextAnswer(printMe);
                        }
                    });
                } else {
                	Log.i(tag,"invalid expression, ignoring");
                }
                //Toast.makeText(this.bluetoothHelper.ctx, "Message:"+myText, Toast.LENGTH_SHORT).show();
            	
            } catch (IOException e) {
            	Log.e(tag,"Connection lost restarting daemeon!"+e.getMessage()); //this is important because once one device disconnects both phones must restart their passive open listening server
            	bluetoothHelper.safeResetServer();
            	break;
            }
        }                  
        }

}
