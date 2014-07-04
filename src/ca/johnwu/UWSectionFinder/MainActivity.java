package ca.johnwu.UWSectionFinder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public static final String SUBJECT = null;
	public static final String COURSE = null;
	public static final String ARGS = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    public void sendMessage (View view){
    	EditText subjectText = (EditText)findViewById(R.id.subjectID);
    	String subject = subjectText.getText().toString();
    	EditText courseText = (EditText)findViewById(R.id.courseID);
    	String course = courseText.getText().toString();
    	Intent intent = new Intent(this, DisplaySections.class);
    	System.out.println(subject);
    	System.out.println(course);
    	String[] args = new String[]{subject, course};
    	intent.putExtra(ARGS, args);
    	startActivity(intent);
    }

}
