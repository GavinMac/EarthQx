package gcu.mpd.s1715408.earthqx;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

class DataDownloader implements Runnable {

    public List<Earthquake> allEarthquakes;
    private ListView listViewDisplay;
    private GoogleMap mMap;
    private String url = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private Context mainContext;
    private Handler threadHandler;

    public DataDownloader(Context context, Handler handler, List<Earthquake> eArrayList, ListView listView, GoogleMap googleMap) {
        this.mainContext = context;
        this.threadHandler = handler;
        this.allEarthquakes = eArrayList;
        this.listViewDisplay = listView;
        this.mMap = googleMap;
    }


    //Download XML from link and call the XML parser
    @Override
    public void run() {

        URL aurl;
        URLConnection yc;
        //Log.e("MyTag","in run");

        try {
            //Log.e("MyTag","in try");
            aurl = new URL(url);
            yc = aurl.openConnection();

            XMLPullParserHandler parser = new XMLPullParserHandler();

            allEarthquakes = parser.parse(yc.getInputStream());
            //Log.e("allEarthquakes",""+allEarthquakes);
            //AddDataToDb(allEarthquakes);

        } catch (IOException ae) {
            Log.e("MyTag", "ioexception");
            ae.printStackTrace();
        }

        UIWriter uiWriter = new UIWriter(mainContext, threadHandler, allEarthquakes, mMap, listViewDisplay);
        uiWriter.run();
    }

    //For database
//    public void AddDataToDb(List<Earthquake> earthquakes){
//        for(Earthquake e : earthquakes){
//            boolean insertData = mDatabaseHelper.addData(e);
//
//            if(insertData){
//                toastMessage("Data inserted successfully");
//            } else{
//                toastMessage("Failed to insert data to database");
//            }
//        }
//    }

    private void toastMessage(String message){
        Toast.makeText(mainContext, message, Toast.LENGTH_SHORT).show();
    }

}
