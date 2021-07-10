package com.example.tatoeba;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

        // TO SPINNER -----------------
        Spinner to_spinner = (Spinner) findViewById(R.id.to_spinner);
        // Apply the adapter to the spinner
        to_spinner.setAdapter(adapter);
    }

}