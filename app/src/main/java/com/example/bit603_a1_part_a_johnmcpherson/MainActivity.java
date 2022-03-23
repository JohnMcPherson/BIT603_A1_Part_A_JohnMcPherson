/*
    ASSUMPTIONS
*/

package com.example.bit603_a1_part_a_johnmcpherson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    // We use these keys in more than one place. So stored as variables to reduce the chance of inconsistent spelling
    private final String KEY_KIWI = "Kiwi";
    private final String KEY_TIKI = "Tiki";
    private final String KEY_BUZZY_BEE = "Buzzy Bee";
    private final String KEY_GUMBOOTS = "Gumboots";

    private final HashMap<String,Integer> salesTallies = new HashMap<>();

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
                incrementSalesTally(KEY_KIWI);
            }
        });

        buttonTiki.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementSalesTally(KEY_TIKI);
            }
        });

        buttonBuzzyBee.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementSalesTally(KEY_BUZZY_BEE);
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
        // Ensure that the relevant salesVolume has been initialised
        // Done here to reduce repetition of code (DRY principle). This also protects future developers
        // from forgetting to do the initialisation if we add cakes later (leading to null pointer exceptions)
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

    // Save the salesTallies when the screen is rotated. Otherwise we lose them!
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        for (HashMap.Entry<String, Integer> salesTallyEntry: salesTallies.entrySet()) {
            outState.putInt(salesTallyEntry.getKey(), salesTallyEntry.getValue());
        }
    }

    // restore the tallies in the new orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String expectedTallies[] = {KEY_KIWI, KEY_TIKI, KEY_BUZZY_BEE, KEY_GUMBOOTS};
        for (String tallyKey: expectedTallies) {
            if (savedInstanceState.containsKey(tallyKey)) {
                salesTallies.put(tallyKey, savedInstanceState.getInt(tallyKey));

                // Demonstrate that the tally has been restored correctly
                Log.d(TAG, "Sales of " + tallyKey + " restored to " + salesTallies.get(tallyKey));
            }
        }
    }

}