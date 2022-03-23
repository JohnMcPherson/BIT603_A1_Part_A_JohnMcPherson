/*
    ASSUMPTIONS1111111
*/

package com.example.bit603_a1_part_a_johnmcpherson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    // We use these keys in more than one place. So stored as variables to reduce the chance of inconsistent spelling
    private final String KEY_KIWI = "Kiwi";
    private final String KEY_TIKI = "Tiki";
    private final String KEY_BUZZY_BEE = "Buzzy Bee";
    private final String KEY_GUMBOOTS = "Gumboots";

    private final HashMap<String,Integer> salesTotals = new HashMap<>();
    private final ArrayList<String> salesRegister = new ArrayList<>();

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
                updateSalesRecords(KEY_KIWI);
            }
        });

        buttonTiki.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateSalesRecords(KEY_TIKI);
            }
        });

        buttonBuzzyBee.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateSalesRecords(KEY_BUZZY_BEE);
            }
        });

        buttonGumboots.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateSalesRecords("Gumboots");
            }
        });

    }


    // use a common method for all salesTotal increments. Reduces repetition (DRY principle)
    @SuppressWarnings("ConstantConditions")
    // ignoring the unboxing warning. We have ensured we have a value for the current salesTotal
    private void updateSalesRecords(String product) {
        updateSalesTotal(product);

        updateSalesRegister(product);
    }

    private void updateSalesTotal(String product) {
        // Ensure that the relevant salesVolume has been initialised
        // Done here to reduce repetition of code (DRY principle). This also protects future developers
        // from forgetting to do the initialisation if we add cakes later (leading to null pointer exceptions)
        // The tradeoff is that this test is executed for each increment. But the performance hit should be negligible
        if (!salesTotals.containsKey(product)) {
            salesTotals.put(product, 0);
        }

        // get the current sales total (for this product) and increase it by one
        Integer currentTotal = salesTotals.get(product);
        salesTotals.put(product, currentTotal + 1);

        // provide debug logging to test that increments are working correctly
        Log.d(TAG, "Sales of " + product + " increased to " + salesTotals.get(product));
    }

    private void updateSalesRegister(String product) {
        // add product to the sales register
        salesRegister.add(product);

        // demonstrate that the sales register contains all sales to now
        Log.d(TAG, "All sales: " + salesRegister.toString());
    }

    // Save the salesTotals and salesRegister when the screen is rotated. Otherwise we lose them!
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        saveSalesTotals(outState);
        saveSalesRegister(outState);
    }

    // save the sales totals for later retrieval
    private void saveSalesTotals(@NonNull Bundle outState) {
        for (HashMap.Entry<String, Integer> salesTotalEntry: salesTotals.entrySet()) {
            outState.putInt(salesTotalEntry.getKey(), salesTotalEntry.getValue());
        }
    }

    // save the sales register for later retrieval
    private void saveSalesRegister(Bundle outState) {
        outState.putStringArrayList("salesRegister", salesRegister);
    }

    // restore the salesTotals and salesRegister in the new orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreSalesTotals(savedInstanceState);
        restoreSalesRegister(savedInstanceState);
    }

    // restore the sales totals
    private void restoreSalesTotals(Bundle savedInstanceState) {
        String[] productTypes = {KEY_KIWI, KEY_TIKI, KEY_BUZZY_BEE, KEY_GUMBOOTS};
        for (String totalKey: productTypes) {
            if (savedInstanceState.containsKey(totalKey)) { // check that this product total was saved
                //load the relevant sales total with the saved total
                salesTotals.put(totalKey, savedInstanceState.getInt(totalKey));

                // Demonstrate that the total has been restored correctly
                Log.d(TAG, "Sales of " + totalKey + " restored to " + salesTotals.get(totalKey));
            }
        }
    }

    private void restoreSalesRegister(Bundle savedInstanceState) {
        // load the salesRegister with salesRegister entries that we saved to the savedInstanceState
        salesRegister.addAll(savedInstanceState.getStringArrayList("salesRegister"));

        // demonstrate that salesRegister is restored
        Log.d(TAG, "Restored salesRegister to: " + salesRegister.toString());
    }
}