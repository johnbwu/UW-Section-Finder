package ca.johnwu.UWSectionFinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DisplaySections extends Activity {
	public String subject;
	public String course;
	public ArrayList<String> a;
	public TextView[] b = new TextView[50];
	public float f =  (float) 15.0;
	public InputStream inputStream ;
	public BufferedReader br;

	 private class getData extends AsyncTask<String, String, String> {
		   @Override
		   
		   protected void onPreExecute() {
		   }
		 
		   @Override
		   protected String doInBackground(String... params) {
			  
			   Query q = new Query();
			   System.out.println("Course"+ params[0]);
			   System.out.println("sub"+ params[1]);
				try {
					q.requestData(params[0], params[1]);

				
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 System.out.println("HI FRED");
				 a = q.getData();

					for (int i = 0 ; i<(a.size()); i++){
						System.out.println(a.size());
						inputStream = getResources().openRawResource(R.raw.info);
						b[i] = new TextView (DisplaySections.this);
						b[i].setTextColor(Color.WHITE);
						b[i].setText(a.get(i));
						b[i].setTextSize(f);
						b[i].setPadding(10,0,0,0);
						if (i%2 ==1){
							if (a.get(i).indexOf(",")!= -1){
								int index = a.get(i).indexOf(" ", a.get(i).indexOf(" ") + 1);
								
								String prof = a.get(i).substring(index);
								prof = prof.substring(1, prof.indexOf(",")) + ", " + prof.substring((prof.indexOf(",")+1));
								//System.out.println(prof);
								String score = "0";
								try {

									score = q.getScore(prof,inputStream);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								b[i].setText(a.get(i)+ " - Prof Rating: "+ score);
							}
							
					
							
						}
						else{
							b[i].setPadding(10, 20, 0, 20);
						}

					}
	
				 
				return "hi";
		   }
		 
		   protected void onProgressUpdate(Integer... values) {
		   }
		 
		   @Override
		   protected void onPostExecute(String result) {
				LinearLayout linearLayout = (LinearLayout) findViewById(R.id.act);
				for (int i =0 ; i<(a.size()); i++){

					linearLayout.addView(b[i]);
					if ( i% 2 ==1){
						View ruler = new View(getApplicationContext()); ruler.setBackgroundColor(0xFF808080);
						linearLayout.addView(ruler,
						 new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
					}}
		      super.onPostExecute(result);
		   }
		   }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 inputStream = getResources().openRawResource(R.raw.info);
		
		setContentView(R.layout.activity_display_sections);
		// Show the Up button in the action bar.
		setupActionBar();
		Intent intent = getIntent();

		String[] c = intent.getStringArrayExtra(MainActivity.ARGS);
		subject = c[0];
		course = c[1];

		
	
		
		new getData().execute(subject,course);

		}
		


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_sections, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
