////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////
package gcu.mpd.s1715408.earthqx;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalDate;
import java.util.List;

/**
 * Run on the UI thread, sets all text, adapters and markers needed
 */
public class UIWriter implements Runnable {

    private List<Earthquake> earthquakeList;
    private GoogleMap map;
    private ListView listView;
    private Context mainContext;
    private Handler threadHandler;
    private TextView resultCountTxtView;
    private TextView dateTextView;
    private LocalDate currentDateSelection;

    public UIWriter(Context context, Handler handler, List<Earthquake> eList, GoogleMap gMap, ListView lstView, TextView resultsTextView, TextView dateTextView, LocalDate currDateSelection) {
        this.mainContext = context;
        this.threadHandler = handler;
        this.earthquakeList = eList;
        this.map = gMap;
        this.listView = lstView;
        this.resultCountTxtView = resultsTextView;
        this.dateTextView = dateTextView;
        this.currentDateSelection = currDateSelection;
    }

    @Override
    public void run() {

        final EarthquakeListAdapter listAdapter = new EarthquakeListAdapter(mainContext, R.layout.list_item, earthquakeList);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                resultCountTxtView.setText(Integer.toString(earthquakeList.size()));
                //Log.d("UI thread", "I am the UI thread");
                listView.setAdapter(listAdapter);

                //set camera to UK
                LatLng ukLatLng = new LatLng(55.378052, -3.435973);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ukLatLng, 4.0f));
                //map.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
                map.clear();

                if(currentDateSelection == null){
                    dateTextView.setText(R.string.dateEditText);
                }

                for (Earthquake e : earthquakeList) {

                    ColourManager colourManager = new ColourManager(Double.parseDouble(e.getMagnitude()));

                    double latDouble = Double.parseDouble(e.getGeoLat());
                    double longDouble = Double.parseDouble(e.getGeoLong());
                    LatLng currentLatLng = new LatLng(latDouble, longDouble);
                    map.addMarker(new MarkerOptions().position(currentLatLng)
                            .title(e.minimalInfo())
                            .icon(BitmapDescriptorFactory.fromResource(colourManager.GetMarkerResourceIndex()))
                    );
                }
            }
        });
    }
}
