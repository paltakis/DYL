package paltakis.george.DYL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private MobileServiceTable<ToDoItem> mToDoTable;
    private ToDoItemAdapter mAdapter;
    private EditText mTextMinAerobicActivity;
    private ProgressBar mProgressBar;
    private int mIntSleepHours;
    private int mIntWineGlasses;
    private Date mDateDateToday;
    private Calendar mCalendar = Calendar.getInstance();
    private Spinner mSpinnerSleepHours;
    private Spinner mSpinnerWineGlasses;
    private DatePickerDialog.OnDateSetListener mListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String string_TodayDate = String.valueOf((monthOfYear+1) + "/" + dayOfMonth + "/" + year);
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            try {
                mDateDateToday = formatter.parse(string_TodayDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Button dateButton = (Button)findViewById(R.id.dateButton);
            dateButton.setText(string_TodayDate);

            // Pull data from Mobile Services, corresponding to date selected
            getItemFromTable();

            // Load the items from the Mobile Service
            refreshItemsFromTable();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            MobileServiceClient client = new MobileServiceClient(
                    "https://dyl.azure-mobile.net/",
                    "xwNheQrBchYJNYrRpFVbQUohiEmMwQ69",
                    this).withFilter(new ProgressFilter());

            // Get the Mobile Service Table instance to use
            mToDoTable = client.getTable(ToDoItem.class);
        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }

        // Create an adapter to bind the items with the view
        mAdapter = new ToDoItemAdapter(this, R.layout.row_list_to_do);
        ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
        listViewToDo.setAdapter(mAdapter);

        // Load the items from the Mobile Service
        refreshItemsFromTable();

        // Initialize the button showing today's date
        Button dateButton = (Button)findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, mListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Initialize the dropdown spinner showing hours of sleep
        mSpinnerSleepHours = (Spinner) findViewById(R.id.sleepSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterSleepHours = ArrayAdapter.createFromResource(this,
                R.array.sleep_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterSleepHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerSleepHours.setAdapter(adapterSleepHours);
        mSpinnerSleepHours.setOnItemSelectedListener(this);

        // Initialize the dropdown spinner showing number of wine glasses
        mSpinnerWineGlasses = (Spinner) findViewById(R.id.wineGlessesSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterWineGlasses = ArrayAdapter.createFromResource(this,
                R.array.wineglasses_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterWineGlasses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerWineGlasses.setAdapter(adapterWineGlasses);
        mSpinnerWineGlasses.setOnItemSelectedListener(this);

        // Initialize the text field for minutes of activity
        mTextMinAerobicActivity = (EditText) findViewById(R.id.textActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkItem(final ToDoItem item) {

        // Set the item as completed and update it in the table
        item.setComplete(true);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mToDoTable.update(item).get();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (item.isComplete()) {
                                mAdapter.remove(item);
                            }
                            refreshItemsFromTable();
                        }
                    });
                } catch (Exception exception) {
                    createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();
    }


    /**
     * Add a new item or update if it already exists in Mobile Services
     *
     * @param view
     *            The view that originated the call
     */
    public void addUpdateItem(View view) {

        // Create a new item
        final ToDoItem item = new ToDoItem();

        final int intMinAerobicActivity = Integer.valueOf(mTextMinAerobicActivity.getText().toString());
        item.setMinAerobicActivity(intMinAerobicActivity);
        item.setSleepHours(mIntSleepHours);
        item.setWineGlasses(mIntWineGlasses);
        item.setDateToday(mDateDateToday);
        item.setComplete(false);

        // Insert the new item
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String string_Year = (new SimpleDateFormat("yyyy")).format(mDateDateToday);
                    String string_Month = (new SimpleDateFormat("MM")).format(mDateDateToday);
                    String string_Day = (new SimpleDateFormat("dd")).format(mDateDateToday);

                    //Get the row corresponding to today's date from Mobile Services
                    final MobileServiceList<ToDoItem> result =
                            mToDoTable.where().year("datetoday").eq(string_Year).
                                    and().day("datetoday").eq(string_Day).
                                    and().month("datetoday").eq(string_Month).execute().get();

                    // If the date entered in the UI already exist in Mobile Services then update it
                    if (result.getTotalCount()==1) {
                        result.get(0).setMinAerobicActivity(intMinAerobicActivity);
                        result.get(0).setSleepHours(mIntSleepHours);
                        result.get(0).setWineGlasses(mIntWineGlasses);
                        result.get(0).setComplete(false);
                        mToDoTable.update(result.get(0)).get();
                        refreshItemsFromTable();
                    } else { // If it doesn't exist, add a new row to Mobile Services
                        mToDoTable.insert(item).get();
                        if (!item.isComplete()) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mAdapter.add(item);
                                }
                            });
                        }
                    }
                } catch (Exception exception) {
                    createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();

    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the adapter
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<ToDoItem> result = mToDoTable.where().field("complete").eq(false).execute().get();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (ToDoItem item : result) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (Exception exception) {
                    createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();

    }


    /**
     * Get a specific item from the Mobile Service Table
     */
    private void getItemFromTable() {

        // Get a specific item from the Mobile Services Table and refresh UI
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String string_Year = (new SimpleDateFormat("yyyy")).format(mDateDateToday);
                    String string_Month = (new SimpleDateFormat("MM")).format(mDateDateToday);
                    String string_Day = (new SimpleDateFormat("dd")).format(mDateDateToday);

                    //Get the row corresponding to today's date from Mobile Services
                    final MobileServiceList<ToDoItem> result =
                            mToDoTable.where().year("datetoday").eq(string_Year).
                                    and().day("datetoday").eq(string_Day).
                                    and().month("datetoday").eq(string_Month).execute().get();
                    //final MobileServiceList<ToDoItem> result = mToDoTable.where().field("todaydate").eq(mDateDateToday).execute().get();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mAdapter.clear();

                            // If the date entered in the UI dateToday field already exist in Mobile Services then update the rest of the UI with Mobilie Services values
                            if (result.getTotalCount() == 1) {
                                for (ToDoItem item : result) {
                                    mTextMinAerobicActivity.setText(Integer.toString(item.getMinAerobicActivity()));
                                    mSpinnerSleepHours.setSelection(item.getSleepHours());
                                    mSpinnerWineGlasses.setSelection(item.getWineGlasses());
                                }
                            } else { // If it doesn't exist, update UI with zero values
                                    mTextMinAerobicActivity.setText("0");
                                    mSpinnerSleepHours.setSelection(0);
                                    mSpinnerWineGlasses.setSelection(0);
                            }
                        }
                    });
                } catch (Exception exception) {
                    createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        createAndShowDialog(exception.toString(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(
                ServiceFilterRequest request, NextServiceFilterCallback next) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            SettableFuture<ServiceFilterResponse> result = SettableFuture.create();
            try {
                ServiceFilterResponse response = next.onNext(request).get();
                result.set(response);
            } catch (Exception exc) {
                result.setException(exc);
            }

            dismissProgressBar();
            return result;
        }

        private void dismissProgressBar() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                }
            });
        }
    }


    // Listener for the spinner showing hours of sleep
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView myText= (TextView) view;
        if (parent == mSpinnerSleepHours) {
            mIntSleepHours = Integer.parseInt((String) myText.getText());
        } else {
            mIntWineGlasses = Integer.parseInt((String) myText.getText());
        }

        Toast.makeText(this, "You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
