package org.as.chickenfoot.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class TemperatureService {
	private Socket socket;
	private String host;
	private int port;
	BufferedReader in;

	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			InetAddress serverAddr = InetAddress.getByName(host);
			socket = new Socket(serverAddr, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return;
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public float readTemperature() {
		float temp = 0.0f;
		try {
			String line = in.readLine();
			if (line != null) {
				temp = Float.parseFloat(in.readLine());
			}
			Log.e("TEMPERATURE", "Temperature " + temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
}
