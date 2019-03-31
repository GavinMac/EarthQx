package gcu.mpd.s1715408.earthqx;

import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultsFinder {


    private List<Earthquake> earthquakeList;
    private LocalDate inputDate;

    public ResultsFinder(ArrayList<Earthquake> eList, LocalDate localDate) {
        this.earthquakeList = eList;
        this.inputDate = localDate;

    }

    public List<Earthquake>ResultsList(){

        HashMap<LocalDate, ArrayList<Earthquake>>earthquakeHashMap = new HashMap<>();


        ArrayList<Earthquake>returnList = null;

        for(Earthquake e : earthquakeList){
            LocalDate lDate = e.getEarthquakeDate();
            earthquakeHashMap.put(lDate, new ArrayList<Earthquake>());
        }
        Log.e("EarthquakeHashMap", ""+earthquakeHashMap);

        returnList = earthquakeHashMap.get(inputDate);

        return returnList;
    }
}
