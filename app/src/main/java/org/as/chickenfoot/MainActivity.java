package org.as.chickenfoot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.as.chickenfoot.client.ClientListener;
import org.as.chickenfoot.client.ControlClient;
import org.as.mjpeg.MjpegView;

public class MainActivity extends Activity {

	private ControlClient client = new ControlClient();
	private MainClientListener listener = new MainClientListener();
	private MjpegView mv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        settings.edit().putString("host", "192.168.1.50").commit();
			                
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_main);
	
		playIntro();
		initVideoStreaming();
		initCommands();
	}
	
	private void playIntro() {
		Utils.playSoundFromAssets(this, "markyell1.mp3");
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
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
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
	
	private void initVideoStreaming() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		String host = settings.getString("host", "0.0.0.0");
		int portVideoStream = Integer.parseInt(settings.getString("port-video", "8090"));
		String URL = String.format("http://%s:%d/?action=stream", host, portVideoStream);
        Log.e("URLLL", URL);
        mv = new MjpegView(this);
        mv.setSource(URL);
        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        //mv.showFps(true);
        
        LinearLayout videoView = (LinearLayout) findViewById(R.id.stream_video);
        mv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.playHorn();
            }
        });
        videoView.addView(mv);
	}

	private void initCommands() {
        ((CheckBox) this.findViewById(R.id.lights_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    client.lightsOn();
                } else {
                    client.lightsOff();
                }
            }
        });
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
