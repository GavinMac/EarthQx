////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * Main Activity - Sets up all components and listeners on the MainActivity
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    DatabaseHelper mDatabaseHelper;
    private TextView dateTextView;
    private TextView resultCountTextView;
    private ListView listViewDisplay;
    private Spinner filterSpinner;
    private GoogleMap mMap;
    private LocalDate currentDateSelection;
    private List<Earthquake> mainEarthquakeList = new ArrayList<>();
    private List<Earthquake> dateFilteredEarthquakes = new ArrayList<>();
    private Handler mainHandler = new Handler();
    private boolean userIsInteracting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_range, 0, 0, 0);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        resultCountTextView = findViewById(R.id.resultCountTextView);
        listViewDisplay = findViewById(R.id.listViewDisplay);
        listViewDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EarthquakeInfoActivity.class);
                Earthquake earthquakeObj = (Earthquake) listViewDisplay.getItemAtPosition(position);
                intent.putExtra("earthquake", earthquakeObj);
                startActivity(intent);
            }
        });

        filterSpinner = findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filters, R.layout.filter_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
//        filterSpinner.setSelected(false);
//        filterSpinner.setSelection(0, true);
        filterSpinner.setOnItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Updates the database every 30 seconds.
        //If there is an internet connection, it will drop the table if there is any data, then re-download the data.
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                if(isNetworkAvailable()){
                    mDatabaseHelper = new DatabaseHelper(MainActivity.this);
                    if(mDatabaseHelper.getData() != null){
                        mDatabaseHelper.DeleteTable();
                    }
                    RunDataDownloader();
                    toastMessage("Data Updated");
                } else {
                    toastMessage("No network available to download data");
                }
            }
        };
        timer.schedule(timerTask, 30000, 30000);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String stringDate = (dayOfMonth + "/" + (month + 1) + "/" + year);
        dateTextView.setText(stringDate);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy");
        currentDateSelection = LocalDate.parse(stringDate, dtf);
        Log.e("currentDateSelection", "" + currentDateSelection);

        dateFilteredEarthquakes = mDatabaseHelper.getListByDate(currentDateSelection);
        //Log.e("earthquakesByDate", ""+dateFilteredEarthquakes);

        UIWriter uiWriter = new UIWriter(this, mainHandler, dateFilteredEarthquakes, mMap, listViewDisplay, resultCountTextView, dateTextView, currentDateSelection);
        uiWriter.run();
    }

    //Use onMapReady to run the other functions to load markers
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDatabaseHelper = new DatabaseHelper(MainActivity.this);
        RunDataDownloader();
        mainEarthquakeList = mDatabaseHelper.getData();
        UIWriter uiWriter = new UIWriter(this, mainHandler, mainEarthquakeList, mMap, listViewDisplay, resultCountTextView, dateTextView, currentDateSelection);
        uiWriter.run();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String text = parent.getItemAtPosition(position).toString();
            toastMessage("Filtered " + text);
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);

            List<Earthquake> listToInput;
            if (currentDateSelection != null) {
                listToInput = dateFilteredEarthquakes;
            } else {
                listToInput = mainEarthquakeList;
            }

            List<Earthquake> listToDisplay = new ArrayList<>();

            switch (position) {
                case 0:
                    filterSpinner.getAdapter().getDropDownView(position, view, parent).setBackgroundResource(R.drawable.ic_menu);
                    dateFilteredEarthquakes.clear();
                    currentDateSelection = null;
                    listToDisplay = dbHelper.getData();
                    break;

                case 1:
                    listToDisplay = dbHelper.getHighestMagnitude(listToInput);
                    filterSpinner.getAdapter().getDropDownView(position, view, parent).setBackgroundResource(R.drawable.ic_magnitude);
                    break;

                case 2:
                    listToDisplay = dbHelper.getDeepestQuake(listToInput);
                    filterSpinner.getAdapter().getDropDownView(position, view, parent).setBackgroundResource(R.drawable.ic_deepness);
                    break;

                case 3:
                    listToDisplay = dbHelper.getFurthestCompassPoint(listToInput, "north");
                    filterSpinner.getAdapter().getDropDownView(position, view, parent).setBackgroundResource(R.drawable.ic_compass_north);
                    break;

                case 4:
                    listToDisplay = dbHelper.getFurthestCompassPoint(listToInput, "south");
                    filterSpinner.getAdapter().getDropDownView(position, view, parent).setBackgroundResource(R.drawable.ic_compass_south);
                    break;

                case 5:
                    listToDisplay = dbHelper.getFurthestCompassPoint(listToInput, "east");
                    filterSpinner.getAdapter().getDropDownView(position, view, parent).setBackgroundResource(R.drawable.ic_compass_east);
                    break;

                case 6:
                    listToDisplay = dbHelper.getFurthestCompassPoint(listToInput, "west");
                    filterSpinner.getAdapter().getDropDownView(position, view, parent).setBackgroundResource(R.drawable.ic_compass_west);
                    break;

                default:
                    toastMessage("Unable to filter");
            }

            //Log.e("listToDisplay", "" + listToDisplay);

            UIWriter uiWriter = new UIWriter(this, mainHandler, listToDisplay, mMap, listViewDisplay, resultCountTextView, dateTextView, currentDateSelection);
            uiWriter.run();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Checks if network is available
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Runs the data downloader
     */
    private void RunDataDownloader(){
        DataDownloader dataDownloader = new DataDownloader(MainActivity.this, mDatabaseHelper, mainHandler);
        new Thread(dataDownloader).start();
    }

    //Creates and displays a toast message
    private void toastMessage(String message) {
        final String msg = message;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

}