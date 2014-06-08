package org.as.chickenfoot;

import java.util.Timer;
import java.util.TimerTask;

import org.as.chickenfoot.client.ClientListener;
import org.as.chickenfoot.client.ControlClient;
import org.as.chickenfoot.client.TemperatureService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ControlClient client = new ControlClient();
	private MainClientListener listener = new MainClientListener();
	private TemperatureService tempService = new TemperatureService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (BuildConfig.DEBUG) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		initCommands();
		startConnection();
	}

	private class MainClientListener implements ClientListener {

		@Override
		public void onConnectionError() {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "Connection error. Retry",
							Toast.LENGTH_SHORT).show();
					(MainActivity.this.findViewById(R.id.connection_status_panel)).setVisibility(View.INVISIBLE);
					(MainActivity.this.findViewById(R.id.btn_reconnect)).setVisibility(View.VISIBLE);
				}
			});
		}

		@Override
		public void onConnect() {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					((TextView)MainActivity.this.findViewById(R.id.connection_status)).setText(client.getFormattedAddress());
					(MainActivity.this.findViewById(R.id.connection_status_panel)).setVisibility(View.VISIBLE);
					(MainActivity.this.findViewById(R.id.btn_reconnect)).setVisibility(View.INVISIBLE);
				}
			});
		}

	}
	
	public void startTemperatureTask() {
		TemperatureTask temperatureTask = new TemperatureTask();
		temperatureTask.execute();
	}
	
	private class TemperatureTask extends AsyncTask<String, Float, Float> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Float doInBackground(String... params) {
			float temperature = tempService.readTemperature();
			return temperature;
		}

		@Override
		protected void onPostExecute(Float temperature) {
			Log.e("TEMPERATURE", "Temperature " + temperature);
			((TextView)MainActivity.this.findViewById(R.id.temperature)).setText("" + temperature + "°C");
			startTemperatureTask();
		}

		
	}

	private class ConnectionTask extends AsyncTask<String, Void, Void> {

		private final ProgressDialog dialog = new ProgressDialog(
				MainActivity.this);

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Connecting...");
			this.dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			String host = settings.getString("host", "0.0.0.0");
			int port = 0;
			try {
				port = Integer.parseInt(settings.getString("port", "5005"));
			} catch (NumberFormatException e) {
				
			}
			client.connect(host, port);
			tempService.connect(host, 5006);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			startTemperatureTask();
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	
	public void openSettings(View v) {
		startActivity(new Intent(this, SettingsActivity.class));
	}
	
	public void startConnection(View v) {
		startConnection();
	}

	private void startConnection() {
		client.addListener(listener);
		(new ConnectionTask()).execute();
	}

	private void initCommands() {
		((ImageButton) this.findViewById(R.id.btn_up))
			.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						((ImageButton) v)
								.setImageResource(R.drawable.up_on);
						client.fw();
						return true;
					}

					if (event.getAction() == MotionEvent.ACTION_UP) {
						((ImageButton) v).setImageResource(R.drawable.up);
						client.stopBackMotor();
						return true;
					}

					return true;
				}
			});

		((ImageButton) this.findViewById(R.id.btn_down))
			.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						((ImageButton) v)
								.setImageResource(R.drawable.up_on);
						client.rw();
						return true;
					}

					if (event.getAction() == MotionEvent.ACTION_UP) {
						((ImageButton) v).setImageResource(R.drawable.up);
						client.stopBackMotor();
						return true;
					}

					return true;
				}
			});

		((ImageButton) this.findViewById(R.id.btn_left))
			.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						((ImageButton) v)
								.setImageResource(R.drawable.up_on);
						client.rl();
						return true;
					}

					if (event.getAction() == MotionEvent.ACTION_UP) {
						((ImageButton) v).setImageResource(R.drawable.up);
						client.stopFrontMotor();
						return true;
					}

					return true;
				}
			});

		((ImageButton) this.findViewById(R.id.btn_right))
			.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						((ImageButton) v)
								.setImageResource(R.drawable.up_on);
						client.rr();
						return true;
					}

					if (event.getAction() == MotionEvent.ACTION_UP) {
						((ImageButton) v).setImageResource(R.drawable.up);
						client.stopFrontMotor();
						return true;
					}

					return true;
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
