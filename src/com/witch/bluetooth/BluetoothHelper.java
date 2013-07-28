package com.witch.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.witch.bluecalculate.MainActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class BluetoothHelper  {
	
	private static String tag = "witch.BluetoothHelper";
	private BluetoothAdapter mBluetoothAdapter;
	private ArrayList<String> mArrayAdapter;
	private String sendMe = "";
	final int REQUEST_ENABLE_BT = 1000;
	final int REQUEST_PAIR_BT = 1001;
	public Context context;
	private ConnectedThread connectedThread;
	private AcceptTask acceptTask;
	public BluetoothHelper(Context context) {
		this.context = context;
		mArrayAdapter = new ArrayList<String>();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Log.e(tag,"Initalization failure, Device doesn't support bluetooth");
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    context.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
		}
	}
	
	private BluetoothAdapter getAdapter() {
		return mBluetoothAdapter;		
	}
	public boolean initServer(){
		/*After initalized we will start the server */
		BluetoothAdapter btadapt = getAdapter();
		Log.i(tag,"Starting accept thread");
		if (btadapt == null) {
			Log.i(tag, "Press again start server");
			return false;
		}
		
		acceptTask = new AcceptTask(this, btadapt);
		acceptTask.execute(); //this starts listening for connection
		return true;
	}
	
	public void killServer() {
		if (acceptTask!=null) {
		acceptTask.cancel();
		Log.i(tag, "HAULT FIEND!");
		}
	}
	public void initClient(){
		BluetoothAdapter btadapt = getAdapter();
		final String[] items = showOthers(btadapt);
		showAvailableDevices(btadapt, items);
	}
	
	public void establishConnectionAsServer(BluetoothSocket btsocket) {
		// TODO Auto-generated method stub
		Log.i(tag,"Starting Client");
		connectedThread = new ConnectedThread(this,btsocket,false);
		connectedThread.start();
	}
	
	public void startConnection(BluetoothDevice btdevice){
        ConnectThread connectThread = new ConnectThread(this,btdevice);
        connectThread.connect();
	}
	
	public void showAvailableDevices(final BluetoothAdapter btadapt, final String[] items){

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle("Pick a Device to Connect to");
		builder.setItems(items, new DialogInterface.OnClickListener() {

		   public void onClick(DialogInterface dialog, int item) {
		        Toast.makeText(context, items[item], Toast.LENGTH_SHORT).show();
		        
		        //if the device is NOT LISTED then start discovery
		        if (items[item].equals("Find nearby Devices")) {
		        	startDiscovery();
		        	return;
		        }
		        
		        //this is if the device is found
		        BluetoothDevice btdevice = connectToDeviceByIndex(btadapt,items[item]);
				if (btdevice!=null) {
					Log.i(tag,"Found and connecting to "+btdevice.getName());
			    	Toast.makeText(context, "Going to connect to phone:"+ btdevice.getName(), Toast.LENGTH_SHORT).show();
			    	startConnection(btdevice);

				} else {
					Toast.makeText(context, "Failed WHAT", Toast.LENGTH_SHORT).show();
				}
		   }

		});

		AlertDialog alert = builder.create();

		alert.show();
	}
	

	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private BluetoothDevice connectToDeviceByIndex(BluetoothAdapter btadapt, String name){
		Set<BluetoothDevice> pairedDevices = btadapt.getBondedDevices();
		Log.i(tag,"Looking for: " + name);
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	if (device.getName().contains(name))
		        	return device;
		    }
		    
		    //here we prompt the user to pick a result;
		    
		} else {
			Log.i(tag,"You need to pair a device first by scanning for devices!");
			Toast.makeText(context, "You need to pair a device first by scanning for devices!", Toast.LENGTH_SHORT).show();
		}
		//here we can prompt the user to pick a device
		
		return null;
	}
	
	/* this shows the other devices for bluetooth */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private String[] showOthers(BluetoothAdapter btadapt){
		
		Set<BluetoothDevice> pairedDevices = btadapt.getBondedDevices();
		if (pairedDevices.size()==0) {
			String[] errStr = new String[1];
			errStr[0] = "Find nearby Devices";
			return errStr;
		}
		String[] returnMe = new String[pairedDevices.size()+1];
		int count = 0;
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	returnMe[count++] = new String(device.getName());
		    }
		    returnMe[count] = "Find nearby Devices";
		    
		    //here we prompt the user to pick a result;
		    
		} else {
			Log.i(tag,"You need to pair a device first by scanning for devices!");
			Toast.makeText(context, "You need to pair a device first by scanning for devices!", Toast.LENGTH_SHORT).show();
		}
		//here we can prompt the user to pick a device
		
		return returnMe;
	}
	 
	public void startDiscovery(){
		Toast.makeText(context, "Plese start bluetooth and pair devices then run application", Toast.LENGTH_LONG).show();
		Log.i(tag,"starting discovery");
		Intent btSettingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
	    ((MainActivity)context).startActivityForResult(btSettingsIntent, REQUEST_PAIR_BT);
		/*List<String> lstring = getBluetoothDevicesNearMe(context);
		for (String s: lstring)
		{
			Log.i(tag,"Here we have :"+s);
		}
		*/

	}
	protected List<String> getBluetoothDevicesNearMe(Context context) {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.startDiscovery();
		// Create a BroadcastReceiver for ACTION_FOUND
		final List<String> discoverableDevicesList = new ArrayList<String>();

		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
		            // Add the name and address to an array adapter to show in a ListView
		            System.out.println(device.getName());
		            discoverableDevicesList.add(device.getName() + "\n" + device.getAddress() + "\n" + rssi);   
		        }
		    }
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

		return discoverableDevicesList;
		}

	public void setSendMessage(String sendMe) {
		this.sendMe = sendMe;
	}
	public String getSendMessage(){
		return this.sendMe;
	}


	
}
