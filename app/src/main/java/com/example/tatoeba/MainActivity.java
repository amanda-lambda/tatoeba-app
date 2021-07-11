package com.example.tatoeba;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FROM SPINNER -----------------
        Spinner from_spinner = (Spinner) findViewById(R.id.from_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        from_spinner.setAdapter(adapter);
        // Default from language - English
        from_spinner.setSelection(10);
        // Spinner selection listener
        from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(),
                "From Spinner : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {
           }
        });

        // TO SPINNER -----------------
        Spinner to_spinner = (Spinner) findViewById(R.id.to_spinner);
        // Apply the adapter to the spinner
        to_spinner.setAdapter(adapter);
        // Default to language - Vietnamese
        to_spinner.setSelection(11);
        // Spinner selection listener
        to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(),
                        "To Spinner : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SEARCH QUERY -------------------
        EditText query = (EditText) findViewById(R.id.searchQuery);
        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(v.getContext(),
                            "Query : " + query.getText(), Toast.LENGTH_SHORT).show();
                    hideSoftKeyboard(MainActivity.this);
                    handled = true;
                }
                return handled;
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

}

public class AndroidPythonCallActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // in case you have created a separate IntentBuilders.java file
        // Intent intent = IntentBuilders.buildStartInTerminalIntent(new File("/sdcard/sl4a/scripts/say_time.py"));

        // else use this one
        Intent intent = buildStartInTerminalIntent(new File("/sdcard/sl4a/scripts/hello_world.py"));

        Log.d("SL4A Launcher", "The intent is " + intent.toString());
        startActivity(intent);

    } // onCreate

    /**
     * Builds an intent that launches a script in a terminal.
     *
     * @param script
     *            the script to launch
     * @return the intent that will launch the script
     */
    public static Intent buildStartInTerminalIntent(File script) {
        final ComponentName componentName = Constants.SL4A_SERVICE_LAUNCHER_COMPONENT_NAME;
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setAction(Constants.ACTION_LAUNCH_FOREGROUND_SCRIPT);
        intent.putExtra(Constants.EXTRA_SCRIPT_PATH, script.getAbsolutePath());
        return intent;
    } // buildStartInTerminalIntent
}

