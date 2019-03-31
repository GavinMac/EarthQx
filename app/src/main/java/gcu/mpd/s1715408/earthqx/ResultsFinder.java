////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////
package gcu.mpd.s1715408.earthqx;

import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ResultsFinder {

    private List<Earthquake> earthquakeList;
    private LocalDate inputDate;

    public ResultsFinder(List<Earthquake> eList, LocalDate localDate) {
        this.earthquakeList = eList;
        this.inputDate = localDate;
    }

    public List<Earthquake>ResultsList(){

        //HashMap<LocalDate, ArrayList<Earthquake>>earthquakeHashMap = new HashMap<>();

        Multimap<LocalDate, Earthquake> earthquakeMultiMap =  ArrayListMultimap.create();

        ArrayList<Earthquake> returnList = null;

        for(Earthquake e : earthquakeList){
            LocalDate lDate = e.getEarthquakeDate();
            earthquakeMultiMap.put(lDate, e);
        }

        Log.e("EarthquakeMultiMap", ""+earthquakeMultiMap);

        for(LocalDate key : earthquakeMultiMap.keySet()){
            if(key == inputDate){
                returnList.addAll(earthquakeMultiMap.get(key));
            }
        }

        Log.e("returnList", ""+returnList);
        return returnList;
    }
}
