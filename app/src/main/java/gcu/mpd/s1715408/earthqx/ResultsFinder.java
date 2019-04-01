////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////
package gcu.mpd.s1715408.earthqx;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ResultsFinder implements Runnable{

    private Context mainContext;
    private Handler threadHandler = new Handler();
    private List<Earthquake> earthquakeList;
    private GoogleMap map;
    private ListView lstView;
    private LocalDate inputDate;

    public ResultsFinder(Context context, Handler handler, List<Earthquake> eArrayList, ListView listView, GoogleMap googleMap, LocalDate localDate) {
        this.mainContext = context;
        this.threadHandler = handler;
        this.earthquakeList = eArrayList;
        this.lstView =  listView;
        this.map = googleMap;
        this.inputDate = localDate;
    }

    @Override
    public void run() {
        UIWriter uiWriter = new UIWriter(mainContext, threadHandler, ResultsList(), map, lstView);
        uiWriter.run();
    }

    public List<Earthquake>ResultsList(){

        DatabaseHelper dbHelper = new DatabaseHelper(mainContext);

//        Multimap<LocalDate, Earthquake> earthquakeMultiMap =  ArrayListMultimap.create();
//
//        for(Earthquake e : earthquakeList){
//            LocalDate lDate = e.getEarthquakeDate();
//            earthquakeMultiMap.put(lDate, e);
//        }
//
//        Log.e("EarthquakeMultiMap", ""+earthquakeMultiMap);
//
//        for(LocalDate key : earthquakeMultiMap.keySet()){
//            if(key == inputDate){
//                returnList.addAll(earthquakeMultiMap.get(key));
//            }
//        }
//
//        Log.e("returnList", ""+returnList);
        return dbHelper.getData();
    }
}
