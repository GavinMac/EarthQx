////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener
{
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        resultCountTextView = findViewById(R.id.resultCountTextView);
        listViewDisplay = findViewById(R.id.listViewDisplay);

        filterSpinner = findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filters, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        currentDateSelection = LocalDate.parse(stringDate,dtf);
        Log.e("currentDateSelection", ""+currentDateSelection);

        dateFilteredEarthquakes = mDatabaseHelper.getListByDate(currentDateSelection);
        //Log.e("earthquakesByDate", ""+dateFilteredEarthquakes);

        UIWriter uiWriter = new UIWriter(this, mainHandler, dateFilteredEarthquakes,mMap,listViewDisplay, resultCountTextView, dateTextView, currentDateSelection);
        uiWriter.run();
    }

    //Use onMapReady to run the other functions to load markers
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadData(mMap);
    }

    public void loadData(GoogleMap gMap)
    {
        //Run network access on a separate thread;
        mDatabaseHelper = new DatabaseHelper(this);
        DataDownloader dataDownloader = new DataDownloader(this, mDatabaseHelper, mainHandler, mainEarthquakeList,listViewDisplay, gMap, resultCountTextView, dateTextView, currentDateSelection);
        new Thread(dataDownloader).start();
        mainEarthquakeList = mDatabaseHelper.getData();
        //Log.e("mainEarthquakeList", ""+mainEarthquakeList);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Log.e("position",""+position);
        toastMessage("Filtered " + text);

        List<Earthquake>listToInput;
        if(currentDateSelection != null){
            listToInput = dateFilteredEarthquakes;
        }
        else{
            listToInput = mainEarthquakeList;
        }

        List<Earthquake>listToDisplay = new ArrayList<>();

        switch (position){
            case 0 :
                listToDisplay = mDatabaseHelper.getData();
                dateFilteredEarthquakes.clear();
                currentDateSelection = null;
                break;

            case 1 :
                listToDisplay = mDatabaseHelper.getHighestMagnitude(listToInput);
                break;

            case 2 :
                listToDisplay = mDatabaseHelper.getDeepestQuake(listToInput);
                break;

            case 3 :
                listToDisplay = mDatabaseHelper.getFurthestCompassPoint(listToInput, "north");
                break;

            case 4:
                listToDisplay = mDatabaseHelper.getFurthestCompassPoint(listToInput, "south");
                break;

            case 5 :
                listToDisplay = mDatabaseHelper.getFurthestCompassPoint(listToInput, "east");
                break;

            case 6:
                listToDisplay = mDatabaseHelper.getFurthestCompassPoint(listToInput, "west");
                break;

            default :
                toastMessage("Unable to filter");
        }

        Log.e("listToDisplay",""+listToDisplay);

        UIWriter uiWriter = new UIWriter(this, mainHandler, listToDisplay, mMap, listViewDisplay, resultCountTextView, dateTextView, currentDateSelection);
        uiWriter.run();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}