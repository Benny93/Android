package de.volBit.snake;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class TimerThread implements Runnable {
	private SnakeGameActivity activity;
	private final Handler h;

	public TimerThread(SnakeGameActivity activity, Handler h) {
		this.activity = activity;
		this.h = h;
	}

	@Override
	public void run() {

		// Run thread
		try {

			while (activity.getdGF().isRunning()) {
				
				h.post(new Runnable() {

					@Override
					public void run() {
						// System.out.println("Runned");
						activity.getdGF().updateUI();

					}
				});
				Thread.sleep(180);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
