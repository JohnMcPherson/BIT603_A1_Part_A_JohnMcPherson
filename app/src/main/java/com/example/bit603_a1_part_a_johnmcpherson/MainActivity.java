package com.example.bit603_a1_part_a_johnmcpherson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    final HashMap<String,Integer> salesTallies = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create a button variable for each button
        Button buttonKiwi = findViewById(R.id.buttonKiwi);
        Button buttonTiki = findViewById(R.id.buttonTiki);
        Button buttonBuzzyBee = findViewById(R.id.buttonBuzzyBee);
        Button buttonGumboots = findViewById(R.id.buttonGumboots);

        // add click listeners to do the sales updates

        buttonKiwi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementSalesTally("Kiwi");
            }
        });

        buttonTiki.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementSalesTally("Tiki");
            }
        });

        buttonBuzzyBee.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementSalesTally("Buzzy Bee");
            }
        });

        buttonGumboots.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementSalesTally("Gumboots");
            }
        });

    }


    // use a common method for all tally increments. Reduces repetition (DRY principle)
    @SuppressWarnings("ConstantConditions")
    // ignoring the unboxing warning. We have ensured we have a value for currentTally
    private void incrementSalesTally(String recipient) {
        // Ensure that the relevant salesVolume has been initiallised
        // Done here to reduce repetition of code (DRY principle)
        // The tradeoff is that this test is executed for each increment. But the performance hit should be negligible
        if (!salesTallies.containsKey(recipient)) {
            salesTallies.put(recipient, 0);
        }

        // get the current tally and perform the increment
        Integer currentTally = salesTallies.get(recipient);
        salesTallies.put(recipient, currentTally + 1);

        // provide debug logging to test that increments are working correctly
        Log.d(TAG, "Sales of " + recipient + " increased to " + salesTallies.get(recipient));
    }
}