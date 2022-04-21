/*
    ASSUMPTIONS

    -   I have assumed that it is OK to increase the complexity of the code in order to reduce repetition.
        L02 "More advanced conditional and iterative methods used."
        For example, the four product buttons have similar functionality associated with them. On click, they each
        do a similar thing. And we need to display totals to each of those buttons. So I used a HashMap to support
        loops for those functions. The trade-off is that it takes a little more effort to understand the code.

    -   I have also assumed that L02 "An advanced approach has been taken" means that use of lambdas is OK (despite not being taught
        in the course

    -   Button colours are different from the mockup. I changed the theme colour to make the Action bar black (as in the mockup).
        This made the buttons black. Rather than fight what the theme was trying to achieve, I went with it.
        ("The client does not mind if it is not exactly as shown")
        In a real project I would discuss this with the designer or client.

        TODO check - separate buttons and product List
        TODO revise how restore on rotate is done
*/

package com.example.bit603_a1_part_a_johnmcpherson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.bit603_a1_part_a_johnmcpherson.Product.*;

public class MainActivity extends AppCompatActivity {

    // The following fields (including some widgets) are all used outside onCreate(). So declared (and sometimes initialised) here

    private final String TAG = "MainActivity";

    // these keys are used more than once, so use constants to avoid problems with mis-spelling
    private final String KEY_SALES_LIST = "salesList";
    private final String KEY_DISPLAY_TOTALS_FLAG = "displayTotalsFlag";



    // buttonToProductNme is used to:
    // - identify the sales total to update when the relevant button is pressed (main reason)
    // - reduce replication of code required to set the OnClickListeners for these buttons (a bonus)
    private final HashMap<Button, String> buttonToProductName = new HashMap<>();

    private final HashMap<String,Integer> salesTotals = new HashMap<>(); // Hash map to record the number of cupcakes sold
    private final ArrayList<String> salesList = new ArrayList<>(); // List to store the names of cupcakes in the order they are sold

    private Button buttonKiwi;
    private Button buttonTiki;
    private Button buttonBuzzyBee;
    private Button buttonGumboots;
    private TextView textViewLeaderMessage;

    private static boolean displayTotals = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise button variables for each product and the leader message
        buttonKiwi = findViewById(R.id.buttonKiwi);
        buttonTiki = findViewById(R.id.buttonTiki);
        buttonBuzzyBee = findViewById(R.id.buttonBuzzyBee);
        buttonGumboots = findViewById(R.id.buttonGumboots);
        // See comment in strings file (for how we initialise this message for no sales)
        textViewLeaderMessage = findViewById(R.id.textViewCurrentLeader);

        // initialise buttonToggleCounters
        Button buttonToggleCounters = findViewById(R.id.buttonToggleCounters);

        // initialise buttonToProductName hash map
        // use the Product enum to ensure that spelling is consistent throughout the app
        buttonToProductName.put(buttonKiwi, KIWI.getName());
        buttonToProductName.put(buttonTiki, TIKI.getName());
        buttonToProductName.put(buttonBuzzyBee, BUZZY_BEE.getName());
        buttonToProductName.put(buttonGumboots, GUMBOOTS.getName());

        // Add OnClicklisteners to do the sales updates
        // We created buttonToProduct to help us update the displayed totals,
        // and use it here to help us set the OnClickListener for each button
        for (HashMap.Entry<Button, String> buttonEntry : buttonToProductName.entrySet()) {
            Button productButton = buttonEntry.getKey();
            String productKey = buttonEntry.getValue();
            // use lambda to set the onClickListener (shorter syntax)
            productButton.setOnClickListener(v -> updateSalesRecordsAndLeaderMessage(productKey));
        }


        // allow the Toggle Counters button to make the display change
        // (using lambda)
        buttonToggleCounters.setOnClickListener(v -> {
            // toggle whether or not we should display counters
            displayTotals = !displayTotals;

            // update the product button displays (based on the new value of displayTotals)
            updateProductButtons();
        });

        // initialise salesTotals
        for (Map.Entry<Button, String> buttonToProductNameEntry: buttonToProductName.entrySet()) {
            String productName = buttonToProductNameEntry.getValue();
            salesTotals.put(productName, 0);
        }
    }


    // There is a common pattern for the sale of each product.
    // Collected here to avoid repeating ourselves (DRY principle)
    private void updateSalesRecordsAndLeaderMessage(String product) {
        updateSalesTotal(product); // update the sales total for the relevant product

        updateProductButtons(); // (the method will decide whether totals or names are required

        updateSalesListAndWriteToLog(product); //record the sale of the product and write to the log

        determineAndDisplayMostSold(); // update the Current Leader(s)
    }

     // use a common method for all salesTotal increments. Reduces repetition (DRY principle)
    @SuppressWarnings("ConstantConditions") // we can suppress the unboxing warning. We have previously initialised (or updated) each salesTotal
    private void updateSalesTotal(String product) {

        // get the current sales total (for this product) and increase it by one
        Integer currentTotal = salesTotals.get(product);
        salesTotals.put(product, currentTotal + 1); // safe, because currentTotal has previously been initialised or updated
    }

    // use a common method for all product sales. Reduces repetition (DRY principle)
    private void updateSalesListAndWriteToLog(String product) {
        // add cupcake sale to the list of cupcakes sold
        salesList.add(product);

        // write to the log whenever a sale is made (as requested)
        Log.d(TAG, "All sales: " + salesList);
    }

    // separate method to determine the most sold cupcake(s)
    // includes display of the result
    private void determineAndDisplayMostSold() {
        HashMap<String, Integer> leadingTotals = new HashMap<>();

        // Load leadingTotals with one or more product/total sales HashMap Entries. Multiple entries means joint leaders.
        Integer leadingValue = 0;
        for (HashMap.Entry<String, Integer> salesTotalToTest: salesTotals.entrySet()) {
            if (leadingTotals.isEmpty() || salesTotalToTest.getValue() > leadingValue) {
                // we have a new clear leader
                // - by virtue of this being the first total tested
                // - OR because this score is greater than the last highest
                // so we make sure our new entry is the only one by clearing leadingTotals
                // clear() is redundant if leadingTotals is already empty. But it allows a clean set of conditional logic.
                leadingTotals.clear();
                leadingTotals.put(salesTotalToTest.getKey(), salesTotalToTest.getValue());
                leadingValue = salesTotalToTest.getValue();
            } else if (salesTotalToTest.getValue().equals(leadingValue)) {
                // we have joint leaders. This will work even if all products have zero sales
                leadingTotals.put(salesTotalToTest.getKey(), salesTotalToTest.getValue());
            }
        }
        // Using iterator because it provides a clean way to:
        // - get the first leader, no matter what;
        // - determine if there are more leaders, and act accordingly.
        // (Adding the commas is the tricky bit, we need one less than there are leaders)
        Iterator<Map.Entry<String, Integer>> entrySetIterator = leadingTotals.entrySet().iterator();

        String leaderListString = "";
        // entrySetIterator should always haveNext(). But, it is safer to put in a check now
        // than to risk a future change of logic (above) that allows entrySetIterator.hasNext() to be false (and cause an exception)
        if (entrySetIterator.hasNext()) {
            leaderListString = entrySetIterator.next().getKey();
        }

        String leaderHeaderString;
        // we have identified a leader. Test to see if there are more
        if (entrySetIterator.hasNext()) { // We have more than one leader, so our header text is set up accordingly
            leaderHeaderString = getString(R.string.multiple_leaders_header); // use plural
        } else { // there is only one leader, so we use a singular header
            leaderHeaderString = getString(R.string.current_leader_header);
        }

        // add extra leader(s) (if we have a tie)
        // (we could have put this loop inside the above if statement, and saved a redundant test. But reduced nesting is easier to follow)
        while (entrySetIterator.hasNext()) {
            // the comma reinforces that "Buzzy Bee" is a single product (not two products)
            // Not using StringBuilder (despite warning from AndroidStudio). The minimal performance gain seems to be outweighed by the extra complexity
            leaderListString = leaderListString + ", " + entrySetIterator.next().getKey();
        }

        // put the complete string together
        String leaderDisplayString = leaderHeaderString + ": " + leaderListString;

        // display the message
        textViewLeaderMessage.setText(leaderDisplayString);
    }

    // this functionality is used more than once. So extracted into a separate method
    private void updateProductButtons() {
        // update the "product buttons", depending on current value of displayTotals
        if (displayTotals) {
            displayProductTotals();
        } else {
            displayProductNames();
        }
    }

    private void displayProductTotals() {
        // using the two hashmaps allows us to build and debug code to update all the buttons in one set of code
        // slightly more complex; but we reduce repetition

        // loop through all the "product" buttons
        for (HashMap.Entry<Button, String> buttonEntry: buttonToProductName.entrySet()) {
            // get the product string for the button
            Button button = buttonEntry.getKey();
            String productName = buttonEntry.getValue();
            // use the product string to find the sales total (that is stored in the salesTotals hash map)
            Integer salesTotal = salesTotals.get(productName);
            // and display it on the relevant button
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

    // Save the salesTotals, salesRegister and displayTotalsFlag when the screen is rotated. Otherwise we lose them!
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        saveSalesTotals(outState);
        saveSalesList(outState);
        saveDisplayTotalsFlag(outState);
    }

    // save the sales totals for later retrieval
    private void saveSalesTotals(@NonNull Bundle outState) {
        // loop through the sales totals hash map
        for (HashMap.Entry<String, Integer> salesTotalEntry: salesTotals.entrySet()) {
            // get the product and the total
            String product = salesTotalEntry.getKey();
            Integer total = salesTotalEntry.getValue();
            // using the product (name) as the key, store the total in outState
            outState.putInt(product, total);
        }
    }

    // save the sales register for later retrieval
    private void saveSalesList(Bundle outState) {
        // store the complete salesList ArrayList for later retrieval
        outState.putStringArrayList(KEY_SALES_LIST, salesList);
    }

    // save the flag that determines whether to display totals or product names
    private void saveDisplayTotalsFlag(Bundle outState) {
        outState.putBoolean(KEY_DISPLAY_TOTALS_FLAG, displayTotals);
    }

    // restore the salesTotals and salesRegister in the new orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // restore the saved data
        restoreSalesTotals(savedInstanceState);
        restoreSalesList(savedInstanceState);

        // update the display, based on the restored data
        updateProductButtons();
        determineAndDisplayMostSold();
    }

    // restore the sales totals
    private void restoreSalesTotals(Bundle savedInstanceState) {
        // loop through our Product (enum) values which tells us which product totals we expect to have been saved
        for (Product product: Product.values()) {
            String productName = product.getName();
            if (savedInstanceState.containsKey(productName)) { // check that this product total was saved. It should have been!
                // get the saved total for this product
                Integer salesTotal = savedInstanceState.getInt(productName);
                //and load back into the salesTotals list
                salesTotals.put(productName, salesTotal);
            }
        }

        // tested by checking buttons display the same totals before and after rotation
    }

    private void restoreSalesList(Bundle savedInstanceState) {
        // load the salesRegister with salesRegister entries that we saved to the savedInstanceState
        salesList.addAll(savedInstanceState.getStringArrayList(KEY_SALES_LIST));

        // confirm that salesRegister is restored
        Log.d(TAG, "Restored salesList to: " + salesList);
    }
}