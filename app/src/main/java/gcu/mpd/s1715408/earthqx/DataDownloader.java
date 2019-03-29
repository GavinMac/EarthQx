package gcu.mpd.s1715408.earthqx;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;

class DataDownloader implements Runnable {

    DatabaseHelper mDatabaseHelper;

    private String url = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    public MainEarthquakeList mainEarthquakeList;

    public DataDownloader(MainEarthquakeList earthquakeList){this.mainEarthquakeList = earthquakeList;}

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
            mainEarthquakeList.setMainEarthquakeList(allEarthquakes);

        } catch (IOException ae) {
            Log.e("MyTag", "ioexception");
            ae.printStackTrace();
        }
    }

//    //For database
//    public void AddDataToDb(List<Earthquake> eList){
//        for(Earthquake e : eList){
//            boolean insertData = mDatabaseHelper.addData(e);
//        }
//    }

}
