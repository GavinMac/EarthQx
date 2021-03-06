////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Downloads the XML from the URL and runs an XMLPullParserHandler to insert all data into the database
 */
class DataDownloader implements Runnable {

    public List<Earthquake> allEarthquakes;
    private String url = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private Context mainContext;
    private DatabaseHelper dbHelper;
    private Handler threadHandler;

    public DataDownloader(Context context, DatabaseHelper databaseHelper, Handler handler) {
        this.mainContext = context;
        this.dbHelper = databaseHelper;
        this.threadHandler = handler;
    }

    public List<Earthquake>GetEarthquakeList(){
        return allEarthquakes;
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
            Log.e("allEarthquakes",""+allEarthquakes.size());
            AddDataToDb(allEarthquakes);

        } catch (IOException ae) {
            Log.e("IO Exception", "ioexception");
            ae.printStackTrace();
        }
    }

    /**
     * Adds each parsed earthquake from a list into the database
     * @param earthquakes List of Earthquake objects
     */
    public void AddDataToDb(List<Earthquake> earthquakes){
        dbHelper = new DatabaseHelper(mainContext);
        for(Earthquake e : earthquakes){
            boolean insertData = dbHelper.addData(e);
            if(insertData){
                toastMessage("Adding Earthquakes...");
            }
        }
    }

    //Displays a toast message
    private void toastMessage(String message){

        Handler handler = new Handler(Looper.getMainLooper());
        final String messageString = message;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainContext, messageString, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
