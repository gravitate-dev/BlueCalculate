package com.witch.bluecalculate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.sax.StartElementListener;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class BluetoothHelper  {
	
	private static String tag = "BluetoothHelper";
	private BluetoothAdapter mBluetoothAdapter;
	public MainActivity ctx;
	private ArrayList<String> mArrayAdapter;
	private AcceptThread acceptThread; // Server
	private ConnectThread connectThread; // Client connecting
	private ConnectedThread connectedThread; // Communication thread (upon connecting the other two threads arent used)
	final int REQUEST_ENABLE_BT = 1000;
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	    	Toast.makeText(context, "Started", Toast.LENGTH_LONG).show();
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	        }
	    }
	};
	BluetoothHelper(MainActivity ctx) {
		this.ctx = ctx;
		mArrayAdapter = new ArrayList<String>();
	}
	
	private BluetoothAdapter init() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Log.e(tag,"Initalization failure, Device doesn't support bluetooth");
			return null;
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    ctx.startActivity(enableBtIntent);
		    Log.e(tag,"Initalization failure, Bluetooth needs user to turn on");
		    return null;
		}
		return mBluetoothAdapter;
		
	}
	public boolean initServer(){
		/*After initalized we will start the server */
		BluetoothAdapter btadapt = init();
		Log.i(tag,"Starting accept thread");
		if (btadapt == null) {
			Toast.makeText(ctx, "Press again start server", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (acceptThread==null){
			acceptThread = new AcceptThread(this,mBluetoothAdapter);
			acceptThread.run();
		}
		return true;
	}
	
	public void initClient(){
		BluetoothAdapter btadapt = init();
		BluetoothDevice btdevice = connectToOthers(btadapt);
		if (btdevice!=null) {
			Log.i(tag,"Found and connecting to "+btdevice.getName());
	    	Toast.makeText(ctx, "Going to connect to phone:", Toast.LENGTH_SHORT).show();
	        ConnectThread connectThread = new ConnectThread(this,btdevice);
	        connectThread.run();
		} else {
			Toast.makeText(ctx, "Failed WHAT", Toast.LENGTH_SHORT).show();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private BluetoothDevice connectToOthers(BluetoothAdapter btadapt){
		Set<BluetoothDevice> pairedDevices = btadapt.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		        Log.i(tag,"Selecting Device :" +device.getName());
		        return device;
		    }
		}
		return null;
	}
	
	 
	public void startDiscovery(){
		
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		ctx.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

	}
	// Create a BroadcastReceiver for ACTION_FOUND
	
}
