package gcu.mpd.s1715408.earthqx;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.logging.LogRecord;

public class UIWriter implements Runnable {

    private List<Earthquake> earthquakeList;
    private GoogleMap map;
    private ListView listView;
    private Context mainContext;
    private Handler threadHandler;

    public UIWriter(Context context, Handler handler, List<Earthquake> eList, GoogleMap gMap, ListView lstView) {
        this.mainContext = context;
        this.threadHandler = handler;
        this.earthquakeList = eList;
        this.map = gMap;
        this.listView = lstView;
    }


    @Override
    public void run() {

        final EarthquakeListAdapter listAdapter = new EarthquakeListAdapter(mainContext, R.layout.list_item, earthquakeList);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("UI thread", "I am the UI thread");
                listView.setAdapter(listAdapter);
                //Log.e("adapter: ", "count: " + listAdapter.getCount());

                //set camera to UK
                LatLng ukLatLng = new LatLng(55.378052, -3.435973);
                map.moveCamera(CameraUpdateFactory.newLatLng(ukLatLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
                map.clear();

                for (Earthquake e : earthquakeList) {
                    double latDouble = Double.parseDouble(e.getGeoLat());
                    double longDouble = Double.parseDouble(e.getGeoLong());
                    LatLng currentLatLng = new LatLng(latDouble, longDouble);
                    map.addMarker(new MarkerOptions().position(currentLatLng).title(e.minimalInfo()));
                }
            }
        });






    }
}
