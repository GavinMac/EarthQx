package gcu.mpd.s1715408.earthqx;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class UIThreadWriter extends MainActivity implements Runnable {

    @Override
    public void run(final List<Earthquake> earthquakes, GoogleMap mMap) {

        final EarthquakeListAdapter listAdapter = new EarthquakeListAdapter(this, R.layout.list_item, earthquakes);

        Log.d("UI thread", "I am the UI thread");
        listViewDisplay.setAdapter(listAdapter);
        Log.e("adapter: ", "count: " + listAdapter.getCount());

        //set camera to UK
        LatLng ukLatLng = new LatLng(55.378, 3.436);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ukLatLng));

        for (Earthquake e : earthquakes) {
            double latDouble = Double.parseDouble(e.getGeoLat());
            double longDouble = Double.parseDouble(e.getGeoLong());
            LatLng currentLatLng = new LatLng(latDouble, longDouble);
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title(e.minimalInfo()));
        }
    }
}
