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
import android.os.Looper;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Main Activity - Sets up all components and listeners on the MainActivity
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private DatabaseHelper dbHelper;
    private TextView dateTextView;
    private TextView resultCountTextView;
    private ListView listViewDisplay;
    private Spinner filterSpinner;
    private GoogleMap mMap;
    private LocalDate currentDateSelection;
    private List<Earthquake> mainEarthquakeList = new ArrayList<>();
    private List<Earthquake> dateFilteredEarthquakes = new ArrayList<>();
    private Handler mainHandler = new Handler();
    private Bundle savedState;

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
        filterSpinner.setSelection(0);
        filterSpinner.setSelected(true);
        filterSpinner.setOnItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);

        //Updates the database every 30 seconds.
        //If there is an internet connection, it will drop the table if there is any data, then re-download the data.
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                if(isNetworkAvailable()){
                    dbHelper = new DatabaseHelper(MainActivity.this);
                    if(dbHelper.getData() != null){
                        dbHelper.DeleteTable();
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
        dbHelper = new DatabaseHelper(MainActivity.this);

        dateFilteredEarthquakes = dbHelper.getListByDate(currentDateSelection);
        if(dateFilteredEarthquakes.isEmpty()){
            toastMessage("No results found on " + currentDateSelection);
        }
        else{
            RunUIthread(dateFilteredEarthquakes);
        }
        //Log.e("earthquakesByDate", ""+dateFilteredEarthquakes);
    }

    //Use onMapReady to run the other functions to load markers
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RunDataDownloader();
        dbHelper = new DatabaseHelper(MainActivity.this);
        mainEarthquakeList = dbHelper.getData();
        //Log.e("mainEqList", ""+mainEarthquakeList.size());
        RunUIthread(mainEarthquakeList);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String text = parent.getItemAtPosition(position).toString();
            if(position > 0){
                toastMessage("Filtered " + text);
            }
            dbHelper = new DatabaseHelper(MainActivity.this);

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

            RunUIthread(listToDisplay);
            //Log.e("listToDisplay", "" + listToDisplay);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    /**
     * Runs the DataDownloader class and adds data to db
     */
    private void RunDataDownloader(){

        if(isNetworkAvailable()){
            DataDownloader dataDownloader = new DataDownloader(MainActivity.this, dbHelper, mainHandler);
            new Thread(dataDownloader).start();
        }
        else{
            toastMessage("No network available to download data");
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("bundle", savedState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getBundle("bundle");
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

    private void RunUIthread(List<Earthquake>earthquakeList){
        final EarthquakeListAdapter listAdapter = new EarthquakeListAdapter(this, R.layout.list_item, earthquakeList);
        final List<Earthquake> eList = earthquakeList;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                resultCountTextView.setText(Integer.toString(eList.size()));
                //Log.d("UI thread", "I am the UI thread");
                listViewDisplay.setAdapter(listAdapter);

                //set camera to UK
                LatLng ukLatLng = new LatLng(55.378052, -3.435973);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ukLatLng, 4.0f));
                mMap.clear();

                if(currentDateSelection == null){
                    dateTextView.setText(R.string.dateEditText);
                }

                for (Earthquake e : eList) {

                    ColourManager colourManager = new ColourManager(Double.parseDouble(e.getMagnitude()));

                    double latDouble = Double.parseDouble(e.getGeoLat());
                    double longDouble = Double.parseDouble(e.getGeoLong());
                    LatLng currentLatLng = new LatLng(latDouble, longDouble);
                    mMap.addMarker(new MarkerOptions().position(currentLatLng)
                            .title(e.minimalInfo())
                            .icon(BitmapDescriptorFactory.fromResource(colourManager.GetMarkerResourceIndex()))
                    );
                }
            }
        });
    }

}