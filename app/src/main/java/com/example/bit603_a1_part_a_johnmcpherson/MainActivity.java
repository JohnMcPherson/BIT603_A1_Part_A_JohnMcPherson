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

    private static final String TAG = "MainActivity";

    // buttonToProductName is used:
    // - when we set OnClickListeners for the buttons (we need the product name)
    // - when we display the totals on the buttons. We need the list of buttons to be updated, and the name of the associated product
    //   (so we can get the total)
    private final HashMap<Button, String> buttonToProductName = new HashMap<>();

    // declare and initialise salesTotals. Static, allowing this field to survive rotation
    private static final HashMap<String,Integer> salesTotals = new HashMap<>(); // Hash map to record the totals for cupcakes sold
    static {
        // initialise salesTotals. Static, so it only gets done once
        for (Product product: Product.values()) {
            String productName = product.getName();
            salesTotals.put(productName, 0);
        }
    }

    // Static, allowing this field to survive rotation
    private static final ArrayList<String> salesList = new ArrayList<>(); // List to store the names of cupcakes in the order they are sold

    private Button buttonKiwi;
    private Button buttonTiki;
    private Button buttonBuzzyBee;
    private Button buttonGumboots;
    private TextView textViewLeaderMessage;

    // Static, allowing this field to survive rotation
    private static boolean displayTotals = false; // stores whether product buttons display names or totals

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
        // using buttonToProductName reduces code duplication. The code is the same for each of the 4 OnClickListeners
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
        salesTotals.put(product, currentTotal + 1); // safe, because currentTotal has previously been initialised and/or updated
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

    // called (or not), by updateProductButtons, dependant on the setting of displayTotalsFlag
    private void displayProductTotals() {
        // using the two hash maps allows us to build and debug code to update all the buttons in one set of code
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

    // called (or not), by updateProductButtons, dependant on the setting of displayTotalsFlag
    private void displayProductNames() {
        // the text used here has a different use from the product names in the Product class
        // these are display names, Product contains the internal names required to make the app work
        // So, we don't try to combine them
        buttonKiwi.setText(getString(R.string.kiwi));
        buttonTiki.setText(getString(R.string.tiki));
        buttonBuzzyBee.setText(getString(R.string.buzzy_bee));
        buttonGumboots.setText(getString(R.string.gumboots));
    }


    // restore the display in the new orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // test that the salesList is intact, after rotation
        Log.d(TAG, "All Sales (after device rotation): " + salesList);

        // update the display, based on saved data
        updateProductButtons();
        determineAndDisplayMostSold();
    }
}