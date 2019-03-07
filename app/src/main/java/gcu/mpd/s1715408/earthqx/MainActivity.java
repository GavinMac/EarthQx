////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback//implements OnClickListener
{
    private TextView dateTextView;
    //private ListView listViewDisplay;
    //private Spinner staticSpinner = (Spinner) findViewById(R.id.dropDownSpinner);
    private Button refreshButton;
    private String url1="";
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private GoogleMap mMap;

    private int mDay, mMonth, mYear;

    List<Earthquake> dateFilteredList = null;

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
                setDateSelection();
            }
        });
        //listViewDisplay = (ListView)findViewById(R.id.listViewDisplay);
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
                MainEarthquakeList mainEarthquakeList = new MainEarthquakeList();
                allEarthquakes = parser.parse(yc.getInputStream());
                mainEarthquakeList.setMainEarthquakeList(allEarthquakes);

                //Log.d("earthquakes: ",allEarthquakes.toString());

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
                    //rawDataDisplay.setText(result);
                    //listViewDisplay.setAdapter(adapter);

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


    public void applyDateFilter(){
                if(allEarthquakes != null){
                    for(Earthquake e : allEarthquakes){

                        //Get a string date from the earthquake object
                        LocalDate eDate = getEarthquakeDate(e);
                        LocalDate currentLocalDateSelection = currentDateSelection.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        Log.d("currentLocalDateSelection",""+currentLocalDateSelection);
                        Log.d("eDate", ""+eDate);

                        if (eDate == currentLocalDateSelection){
                            dateFilteredList.add(e);
                        }
                    }
                }
    }

    public LocalDate getEarthquakeDate(Earthquake eq){

        String rawData = eq.getPubDate();
        //Log.d("rawData", rawData);
        Date rawDataAsDate = new Date();

        try{
            rawDataAsDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").parse(rawData);
        }
        catch(ParseException e){
            Log.e("Date Converting Error", "Can't convert string date to earthquake returnDate");
        }

        LocalDate returnDate = rawDataAsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //Log.d("returnDate",": " + returnDate);
        return returnDate;
    }


}