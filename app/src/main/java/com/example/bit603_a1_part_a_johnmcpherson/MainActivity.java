/*
    ASSUMPTIONS

    -   I have assumed that it is OK to increase the complexity of the code in order to reduce repetition.
        For example, the four product buttons have similar functionality associated with them. On click, they each
        do a similar thing. And we need to display totals to each of those buttons. So I used a HashMap to support
        loops for those functions. The trade-off is that it takes a little more effort to understand the code.
*/

package com.example.bit603_a1_part_a_johnmcpherson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    // We use these keys in more than one place. So stored as variables to reduce the chance of inconsistent spelling
    private final String KEY_KIWI = "Kiwi";
    private final String KEY_TIKI = "Tiki";
    private final String KEY_BUZZY_BEE = "Buzzy Bee";
    private final String KEY_GUMBOOTS = "Gumboots";
    // productTypes is only used in restoreSalesTotals. But declared and initialised here because any additions/removals to the
    // keys above should be reflected in a change to the product list below

    // We could have used a productTypes HashMap or enum (to keep the keys and the productTypes locked together). I decided against that because
    // (unlike some other places in the code) it seemed to make the code unnecessarily complex and harder to understand
    private final String[] productTypes = {KEY_KIWI, KEY_TIKI, KEY_BUZZY_BEE, KEY_GUMBOOTS}; // java style declaration - as suggested by AndroidStudio

    // buttonToProduct is used to:
    // - identify the sales total to update each button
    // - make the onClickListeners, associated with each button, slightly more generic (a small bonus)
    private final HashMap<Button, String> buttonToProduct = new HashMap<>();

    private final HashMap<String,Integer> salesTotals = new HashMap<>(); // to record a sales total for each product
    private final ArrayList<String> salesRegister = new ArrayList<>(); // to record each sale

    // declare these widgets outside onCreate; so they can be accessed by functions that update them
    Button buttonKiwi;
    Button buttonTiki;
    Button buttonBuzzyBee;
    Button buttonGumboots;
    TextView textViewLeaderMessage;

    private boolean displayTotals = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inialise button variables for each product and the leader message
        buttonKiwi = findViewById(R.id.buttonKiwi);
        buttonTiki = findViewById(R.id.buttonTiki);
        buttonBuzzyBee = findViewById(R.id.buttonBuzzyBee);
        buttonGumboots = findViewById(R.id.buttonGumboots);
        // See comment in strings file (for how we initialise this message for no sales)
        textViewLeaderMessage = findViewById(R.id.textViewCurrentLeader);

        Button buttonToggleCounters = findViewById(R.id.buttonToggleCounters);

        //initialise buttonToProduct
        buttonToProduct.put(buttonKiwi, KEY_KIWI);
        buttonToProduct.put(buttonTiki, KEY_TIKI);
        buttonToProduct.put(buttonBuzzyBee, KEY_BUZZY_BEE);
        buttonToProduct.put(buttonGumboots, KEY_GUMBOOTS);

        // add OnClicklisteners to do the sales updates
        // we created buttonToProduct to help us update the displayed totals
        // But since we have that map, we can use it here to help us setOnClickLister for each button

        for (HashMap.Entry<Button, String> buttonEntry : buttonToProduct.entrySet()) {
            Button productButton = buttonEntry.getKey();
            String productKey = buttonEntry.getValue();
            productButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    updateSalesRecordsAndLeaderMessage(productKey);
                }
            });
        }

        buttonToggleCounters.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // toggle whether or not we should display counters
                displayTotals = !displayTotals;

                // update the product button displays (based on the new value of displayTotals)
                updateProductButtons();
            }
        });

        // initialise salesTotals
        for (String productName: productTypes) {
            salesTotals.put(productName, 0);
        }
    }


    // There is a common pattern for the sale of each product.
    // Collected here to avoid repeating ourselves (DRY principle)
    private void updateSalesRecordsAndLeaderMessage(String product) {
        updateSalesTotal(product);

        updateProductButtons();

        updateSalesRegister(product);

        updateLeaderMessage();
    }

     // use a common method for all salesTotal increments. Reduces repetition (DRY principle)
    @SuppressWarnings("ConstantConditions") // ignoring the unboxing warning. We have initialised (or updated) each salesTotal
    private void updateSalesTotal(String product) {

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

    private void updateLeaderMessage() {
        HashMap<String, Integer> leadingTotals = new HashMap<>();
        Integer leadingValue = 0;
        for (HashMap.Entry<String, Integer> salesTotalToTest: salesTotals.entrySet()) {
            if (leadingTotals.isEmpty() || salesTotalToTest.getValue() > leadingValue) {
                // we have a new clear leader
                // - by virtue of this being the first total tested
                // - OR because this score is greater than the last highest
                // so we make sure our new entry is the only one
                leadingTotals.clear();
                leadingTotals.put(salesTotalToTest.getKey(), salesTotalToTest.getValue());
                leadingValue = salesTotalToTest.getValue();
            } else if (salesTotalToTest.getValue().equals(leadingValue)) {
                leadingTotals.put(salesTotalToTest.getKey(), salesTotalToTest.getValue());
            }
        }
        // Using iterator because it provides a clean way to:
        // - get the first leader, no matter what;
        // - determine if there are more leaders, and act accordingly.
        // (Adding the commas is the tricky bit, we need one less than there are leaders)
        Iterator<Map.Entry<String, Integer>> entrySetIterator = leadingTotals.entrySet().iterator();

        String leaderListString = "";
        // entrySetIterator should always haveNext(). But, it is simpler and safer to put in a check now
        // than to risk a change of logic (above) that allows entrySetIterator.hasNext() to be false (and cause an exception)
        if (entrySetIterator.hasNext()) {
            leaderListString = entrySetIterator.next().getKey();
        }

        String leaderHeaderString;
        // we have identified a leader. Test to see if there are more
        if (entrySetIterator.hasNext()) { // We have more than one leader, so our header text is set up accordingly
            leaderHeaderString = getString(R.string.current_leaders_header); // use plural
        } else {
            leaderHeaderString = getString(R.string.current_leader_header);
        }

        // add extra leader(s) (if we have a tie)
        // (we could have put this loop inside the above if statement, and saved a redundant test. But reduced nesting is easier to follow)
        while (entrySetIterator.hasNext()) {
            // the comma reinforces that "Buzzy Bee" is a single product (not two products)
            leaderListString = leaderListString + ", " + entrySetIterator.next().getKey();
        }

        // put the complete string together
        String leaderDisplayString = leaderHeaderString + ": " + leaderListString;

        // display the message
        textViewLeaderMessage.setText(leaderDisplayString);
    }

    private void updateProductButtons() {
        if (displayTotals) {
            displayProductTotals();
        } else {
            displayProductNames();
        }
    }

    private void displayProductTotals() {
        // using the two hashmaps allows us to build and debug code to update all the buttons in one set of code
        // slightly more complex; but we avoid repeating the same code pattern 4 times
        for (HashMap.Entry<Button, String> buttonEntry: buttonToProduct.entrySet()) {
            Button button = buttonEntry.getKey();
            String productName = buttonEntry.getValue();
            Integer salesTotal = salesTotals.get(productName);
            button.setText(String.valueOf(salesTotal));
        }
    }

    private void displayProductNames() {
        // HashMap NOT used here because it's simpler to directly code the "map" between the button and the string value to be displayed
        buttonKiwi.setText(getString(R.string.kiwi));
        buttonTiki.setText(getString(R.string.tiki));
        buttonBuzzyBee.setText(getString(R.string.buzzy_bee));
        buttonGumboots.setText(getString(R.string.gumboots));
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