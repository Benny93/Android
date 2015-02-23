package de.volBit.snake;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class SnakeGameActivity extends Activity {
	private DisplayGameFragment dGF;
	private int playerScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snake_game);

		dGF = new DisplayGameFragment();
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, dGF)
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.snake_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class DisplayGameFragment extends Fragment {
		private View rootView;
		private boolean isRunning;
		private int moveIndicator;
		private MediaPlayer mediaplayer = null;
		private SnakeGameActivity sGA;
		private ImageView snakeHeadView;
		private LayoutParams snakeHeadLayout;
		private LayoutParams snakeTailLayout;
		private LayoutParams gameZoneLayout;
		private ArrayList<ImageView> snakeBody;

		public DisplayGameFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_snake_game,
					container, false);
			
			
			//GameZone scale
			
			RelativeLayout rL = (RelativeLayout) rootView.findViewById(R.id.relativeLayout1);
			gameZoneLayout = (LayoutParams) rL.getLayoutParams();			
			

			moveIndicator = 0; // start Move direction = 0: left
			final Handler h = new Handler();

			// Score
			sGA = (SnakeGameActivity) getActivity();
			sGA.setPlayerScore(0);
			TextView score = (TextView) rootView.findViewById(R.id.score_view);
			score.setText(getString(R.string.score_string) + sGA.playerScore);

			// snake head
			snakeHeadView = (ImageView) rootView
					.findViewById(R.id.snakeHead_view);

			snakeHeadLayout = (LayoutParams) snakeHeadView.getLayoutParams();

			snakeBody = new ArrayList<ImageView>();

			TimerThread tT = new TimerThread(sGA, h);
			Thread t = new Thread(tT);

			isRunning = true;
			t.start();

			Button btn_right = (Button) rootView.findViewById(R.id.right_btn);
			btn_right.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// go right
					moveIndicator = ((moveIndicator + 1) % 4);

				}
			});

			Button btn_left = (Button) rootView.findViewById(R.id.left_btn);
			btn_left.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// go left
					// moveIndicator = ((moveIndicator - 1) % 4);
					moveIndicator = (((moveIndicator - 1) % 4) + 4) % 4;
					// System.out.println("moveID: " + moveIndicator);
				}
			});

			return rootView;
		}

		@TargetApi(Build.VERSION_CODES.KITKAT)
		public void updateUI() {
			
			

			snakeTailLayout = new LayoutParams(snakeHeadLayout);
			
			
			//Snake movement
			
			switch (moveIndicator) {
			case 0:// go left
				snakeHeadLayout.leftMargin -= snakeHeadLayout.width;
				if (snakeHeadLayout.leftMargin < 0) {
					snakeHeadLayout.leftMargin = gameZoneLayout.width-snakeHeadLayout.width;
				}
				break;
			case 1:// go up
				snakeHeadLayout.topMargin -= snakeHeadLayout.height;
				// System.out.println(snakeHeadLayout.topMargin);
				if (snakeHeadLayout.topMargin < 0) {
					snakeHeadLayout.topMargin = gameZoneLayout.height-snakeHeadLayout.height;
				}
				break;
			case 2:// go right
				snakeHeadLayout.leftMargin += snakeHeadLayout.width;
				if (snakeHeadLayout.leftMargin >= gameZoneLayout.width) {
					snakeHeadLayout.leftMargin = 0;
				}
				break;
			case 3:// go down
				snakeHeadLayout.topMargin += snakeHeadLayout.height;
				if (snakeHeadLayout.topMargin >= gameZoneLayout.height) {
					snakeHeadLayout.topMargin = 0;
				}
				break;

			}

			// set new position
			snakeHeadView.setLayoutParams(snakeHeadLayout);

			if (snakeAteFruit()) {
				playBiteSound();
				
				moveFoodToRandomLocation();
				sGA.setPlayerScore(sGA.getPlayerScore() + 1);
				// display score
				TextView score = (TextView) rootView
						.findViewById(R.id.score_view);
				score.setText(getString(R.string.score_string)
						+ sGA.playerScore);

				// add snake body cube
				// KITKAT required
				addSnakeBodyCube();

			}
			
			if (snakeBitItself()) {
				//game over
				performGameOver();
			}
			
			// KITKAT required alternative finden
			performSnakeBodyMovement();

		}
		
		private void playBiteSound(){
			mediaplayer = MediaPlayer.create(getActivity(), R.raw.a_bite);
			mediaplayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();

				}
			});
			mediaplayer.start();
		}

		public boolean snakeAteFruit() {

			ImageView snakeHead = (ImageView) rootView
					.findViewById(R.id.snakeHead_view);
			ImageView cherry = (ImageView) rootView
					.findViewById(R.id.cherry_view);

			LayoutParams snakeHeadLayout = (LayoutParams) snakeHead
					.getLayoutParams();
			LayoutParams cherryLayout = (LayoutParams) cherry.getLayoutParams();

			if (snakeHeadLayout.leftMargin == cherryLayout.leftMargin
					&& snakeHeadLayout.topMargin == cherryLayout.topMargin) {
				// snake ate fruit
				return true;
			}
			return false;
		}

		public boolean snakeBitItself() {
			LayoutParams bodyPartLPs;

			for (ImageView sBV : snakeBody) {
				bodyPartLPs = (LayoutParams) sBV.getLayoutParams();
				if (bodyPartLPs.leftMargin == snakeHeadLayout.leftMargin
						&& bodyPartLPs.topMargin == snakeHeadLayout.topMargin) {
					return true;
				}
			}
			//has never been the case
			return false;
		}

		public void moveFoodToRandomLocation() {
			int rTop = 0;
			int rLeft = 0;

			rTop = (int) (Math.random() * ((gameZoneLayout.height/snakeHeadLayout.height)-1) + 1);
			rLeft = (int) (Math.random() * ((gameZoneLayout.width/snakeHeadLayout.width)-1) + 1);
			// System.out.println("rTop: " + rTop + "; rLeft: " + rLeft);
			ImageView cherry = (ImageView) rootView
					.findViewById(R.id.cherry_view);
			LayoutParams par = (LayoutParams) cherry.getLayoutParams();

			par.topMargin = snakeHeadLayout.height * rTop;
			par.leftMargin = snakeHeadLayout.width * rLeft;
			cherry.setLayoutParams(par);
		}

		public void addSnakeBodyCube() {
			// Adds one Cube to the snakes body

			ImageView snakeBodyView = new ImageView(sGA);

			snakeBodyView.setImageResource(R.drawable.snake_body);

			LayoutParams buffLPs = snakeTailLayout;

			snakeBodyView.setLayoutParams(buffLPs);

			RelativeLayout rL = (RelativeLayout) sGA
					.findViewById(R.id.relativeLayout1);
			rL.addView(snakeBodyView);
			// add to body
			snakeBody.add(snakeBodyView);

		}

		@TargetApi(Build.VERSION_CODES.KITKAT)
		public void performSnakeBodyMovement() {
			LayoutParams currentLPs;
			LayoutParams newLPs;
			LayoutParams bufferLPs;

			newLPs = snakeTailLayout;
			for (ImageView sBV : snakeBody) {
				currentLPs = (LayoutParams) sBV.getLayoutParams();
				bufferLPs = new LayoutParams(currentLPs);
				currentLPs = newLPs;
				newLPs = new LayoutParams(bufferLPs);

				sBV.setLayoutParams(currentLPs);
			}
		}
		
		public void performGameOver(){
			
			//stop snake
			isRunning = false;
			playBiteSound();
			TextView gameOverView = (TextView) rootView.findViewById(R.id.gameover_view);
			gameOverView.setText(getString(R.string.gameover_string));
			
			
		}

		public boolean isRunning() {
			return isRunning;
		}

		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}
	}

	public DisplayGameFragment getdGF() {
		return dGF;
	}

	public void setdGF(DisplayGameFragment dGF) {
		this.dGF = dGF;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public void setPlayerScore(int playerScore) {
		this.playerScore = playerScore;
	}

}
