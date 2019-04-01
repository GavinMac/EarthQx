////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener
{
    DatabaseHelper mDatabaseHelper;

    private TextView dateTextView;
    private ListView listViewDisplay;
    private Button refreshButton;
    private GoogleMap mMap;
    private LocalDate currentDateSelection;
    private List<Earthquake> mainEarthquakeList = new ArrayList<>();
    private Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);
        // Set up the raw links to the graphical components
        dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        listViewDisplay = findViewById(R.id.listViewDisplay);
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(mMap);
            }
        });
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

        List<Earthquake>earthquakes = mDatabaseHelper.getListByDate(currentDateSelection);
        Log.e("earthquakesByDate", ""+earthquakes);

        UIWriter uiWriter = new UIWriter(this, mainHandler, earthquakes,mMap,listViewDisplay);
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
        DataDownloader dataDownloader = new DataDownloader(this, mainHandler, mainEarthquakeList,listViewDisplay, gMap);
        new Thread(dataDownloader).start();
        mainEarthquakeList = mDatabaseHelper.getData();
        //Log.e("mainEarthquakeList", ""+mainEarthquakeList);
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}