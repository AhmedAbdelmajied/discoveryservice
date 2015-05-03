package com.example.discoveryservice;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.example.discoveryservice.BroadcastingService.MyBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	// remove it .....for test only
	private Button start, stop, getTime, bind, unbind, send;
	private TextView time;
	private DatagramSocket socket;
	private int portNumber = 9000;
	private final String broadcastIP = new String("255.255.255.255");
	// *********
	private Button setProf;
	private ToggleButton state;

	private BroadcastingService bService;
	private boolean isBound = false;

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;
			bService = binder.getService();
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
		}

	};

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.editprofile:
				startActivity(new Intent(getBaseContext(),
						ProfileActivity.class));
				break;
			case R.id.state:
				if (!state.isChecked())
					stopService(new Intent(getBaseContext(),
							BroadcastingService.class));
				else
					startService(new Intent(getBaseContext(),
							BroadcastingService.class));
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (!BroadcastingService.isRunning)
			startService(new Intent(getBaseContext(), BroadcastingService.class));

		setProf = (Button) findViewById(R.id.editprofile);
		state = (ToggleButton) findViewById(R.id.state);
		state.setChecked(true);

		setProf.setOnClickListener(listener);
		state.setOnClickListener(listener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private Runnable broadCastSender = new Runnable() {

		@Override
		public void run() {
			try {
				socket = new DatagramSocket();
				socket.setBroadcast(true);
				byte[] data = "Hi".getBytes();
				DatagramPacket pkt = new DatagramPacket(data, data.length,
						InetAddress.getByName(broadcastIP), portNumber);
				socket.send(pkt);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
