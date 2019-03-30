package gcu.mpd.s1715408.earthqx;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;

class DataDownloader extends MainActivity implements Runnable {

    DatabaseHelper mDatabaseHelper;

    private String url = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    @Override
    public void run() {
        List<Earthquake> allEarthquakes = null;
        URL aurl;
        URLConnection yc;

        //Log.e("MyTag","in run");

        try {
            //Log.e("MyTag","in try");
            aurl = new URL(url);
            yc = aurl.openConnection();

            XMLPullParserHandler parser = new XMLPullParserHandler();

            allEarthquakes = parser.parse(yc.getInputStream());
            AddDataToDb(allEarthquakes);

        } catch (IOException ae) {
            Log.e("MyTag", "ioexception");
            ae.printStackTrace();
        }
    }

    //For database
    public void AddDataToDb(List<Earthquake> earthquakes){
        for(Earthquake e : earthquakes){
            boolean insertData = mDatabaseHelper.addData(e);

            if(insertData){
                toastMessage("Data inserted successfully");
            } else{
                toastMessage("Failed to insert data to database");
            }
        }

    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
