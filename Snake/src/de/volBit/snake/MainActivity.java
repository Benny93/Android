package de.volBit.snake;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private StartFragment sF;
	private MainActivity acty;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         sF = new StartFragment();
         acty = this;
        
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, sF)
                    .commit();
        }
        
         
       
       
     
    }

    	
    public void updateUI(){
    	System.out.println("Hello");
    	TextView tV = (TextView) sF.getRootView().findViewById(R.id.title_view);
    	tV.setText("Updated !");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    public static class StartFragment extends Fragment {
    	private View rootView;

        public StartFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
             rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
             //On click Listiner
              Button btn = (Button) rootView.findViewById(R.id.button1);
              btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),SnakeGameActivity.class);
					startActivity(intent);			
				}
			});
             
             
             
             
             return rootView;
        }

		public View getRootView() {
			return rootView;
		}

		public void setRootView(View rootView) {
			this.rootView = rootView;
		}
    }

}
