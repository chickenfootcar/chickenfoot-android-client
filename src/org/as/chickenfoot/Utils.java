package org.as.chickenfoot;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;

public class Utils {
	public static void playSoundFromAssets(Context ctx, String fileName) {
		try {
			AssetFileDescriptor afd;
			afd = ctx.getAssets().openFd(fileName);
			MediaPlayer player = new MediaPlayer();
			player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			player.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer player) {
					player.start();
				}
			});
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
