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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String from_lang;
    private String to_lang;
    private PyObject scraper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // PYTHON SET UP --------------
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();

        // FROM SPINNER -----------------
        Spinner from_spinner = findViewById(R.id.from_spinner);
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
               from_lang = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),
                "From Spinner : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {
           }
        });

        // TO SPINNER -----------------
        Spinner to_spinner = findViewById(R.id.to_spinner);
        // Apply the adapter to the spinner
        to_spinner.setAdapter(adapter);
        // Default to language - Vietnamese
        to_spinner.setSelection(11);
        // Spinner selection listener
        to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to_lang = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),
                        "To Spinner : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SEARCH QUERY -------------------
        EditText query = findViewById(R.id.searchQuery);
        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(v.getContext(),
                            "Query : " + query.getText(), Toast.LENGTH_SHORT).show();
                    hideSoftKeyboard(MainActivity.this);

                    // TATOEBA SCRAPER ---------------------------
                    PyObject module = py.getModule("scraper");
                    scraper = module.callAttr("TatoebaScraper",
                            String.valueOf(query.getText()), from_lang, to_lang);
                    Map<PyObject, PyObject> result = scraper.callAttr("get_sentence").asMap();

                    // UPDATE UI -------------------------
                    TextView sentence = findViewById(R.id.sentence);
                    TextView translations = findViewById(R.id.translations);
                    TextView source_url = findViewById(R.id.source_url);
                    TextView track_result = findViewById(R.id.track_result);

                    sentence.setText(result.get("sentence").toString());
                    translations.setText(result.get("translations").toString());
                    source_url.setText(result.get("url").toString());
                    track_result.setText(result.get("id").toString() + " / " + result.get("total").toString());

                    handled = true;
                }
                return handled;
            }
        });

        Button page_left = findViewById(R.id.page_left);
        page_left.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        // TODO - Check empty dictionary / end of dictionary
        // TODO - Paging buttons
    }

    public static void update_sentence() {

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
