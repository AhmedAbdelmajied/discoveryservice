package com.example.discoveryservice;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BroadcastingService extends Service {

	private final IBinder broadcastBinder = new MyBinder();
	private ProfileManger profile = null;

	static boolean isRunning = false;
	private DatagramSocket socket;
	private int portNumber = 9000;

	private final String broadcastIP = new String("255.255.255.255");
	private boolean isStop = false;

	private Runnable broadcastServer = new Runnable() {

		@Override
		public void run() {
			try {
				socket = new DatagramSocket(portNumber,
						InetAddress.getByName(broadcastIP));
				socket.setBroadcast(true);
				while (!isStop) {
					byte[] data = new byte[100];
					DatagramPacket pkt = new DatagramPacket(data, data.length);
					socket.receive(pkt);
					Log.d("receive message", pkt.getData().toString()
							+ " from " + pkt.getAddress().toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isRunning = true;
		Log.d("Service", "service start");
		Toast.makeText(this, "Service start", Toast.LENGTH_SHORT).show();
		Thread rec = new Thread(broadcastServer);
		rec.start();
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("Service", "service bind");
		return broadcastBinder;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service stop", Toast.LENGTH_SHORT).show();
		isRunning = false;
		close();
		super.onDestroy();
	}

	//
	// public String getTime() {
	// Toast.makeText(this, "service running state " + isRunning,
	// Toast.LENGTH_SHORT).show();
	// SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
	// return (df.format(new Date()));
	// }
	
	public ProfileManger getProfile(){
		return profile;
	}
	public void setProfile(ProfileManger profile){
		this.profile = profile;
	}

	private void close() {
		isStop = true;
		socket.close();
	}

	public class MyBinder extends Binder {
		BroadcastingService getService() {
			return BroadcastingService.this;
		}
	}

}
