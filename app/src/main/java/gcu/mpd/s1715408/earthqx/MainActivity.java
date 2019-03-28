////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.app.DatePickerDialog;
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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private TextView dateTextView;
    private ListView listViewDisplay;
    private Button refreshButton;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private GoogleMap mMap;
    private LocalDate currentDateSelection;
    MainEarthquakeList mainEarthquakeList = new MainEarthquakeList();


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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/M/yyy");
        currentDateSelection = LocalDate.parse(stringDate,dtf);
        Log.d("currentDateSelection", ""+currentDateSelection);
    }

    //Use onMapReady to run the other functions to load markers
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadData(mMap);
    }

    public void loadData(GoogleMap gMap)
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
        //DownloadData downloader = new DownloadData();
        //new Thread(downloader).start();
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        List<Earthquake> allEarthquakes = null;
        private String url;
        public Task(String aurl)
        {
            url = aurl;
        }

        @Override
        public void run()
        {
            URL aurl;
            URLConnection yc;

            //Log.e("MyTag","in run");

            try
            {
                //Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();

                XMLPullParserHandler parser = new XMLPullParserHandler();

                allEarthquakes = parser.parse(yc.getInputStream());
                mainEarthquakeList.setMainEarthquakeList(allEarthquakes);

            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
                ae.printStackTrace();
            }

            final ArrayAdapter<Earthquake> adapter = new ArrayAdapter<Earthquake>(MainActivity.this, R.layout.list_item, allEarthquakes);

            MainActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    listViewDisplay.setAdapter(adapter);

                    //set camera to UK
                    LatLng ukLatLng = new LatLng(55.378, 3.436);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ukLatLng));

                    for(Earthquake e : allEarthquakes){
                        double latDouble = Double.parseDouble(e.getGeoLat());
                        double longDouble = Double.parseDouble(e.getGeoLong());
                        LatLng currentLatLng = new LatLng(latDouble,longDouble);
                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title(e.getTitle()));
                    }
                }
            });

        }

    }


//    public void applyDateFilter(){
//        List<Earthquake> earthquakeList = mainEarthquakeList.getMainEarthquakeList();
//        List<Earthquake> dateFilteredList = mainEarthquakeList.getDateFilteredList();
//
//                if( earthquakeList != null){
//                    for(Earthquake e : earthquakeList){
//
//                        //Get a string date from the earthquake object
//                        LocalDate eDate = e.getEarthquakeDate();
//
//                        Log.d("currentLocalDateSelect","" + currentDateSelection);
//                        Log.d("eDate", ""+eDate);
//
//                        if (eDate == currentDateSelection){
//                            dateFilteredList.add(e);
//                        }
//                    }
//                }
//    }



}