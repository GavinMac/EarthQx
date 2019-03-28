package gcu.mpd.s1715408.earthqx;

import java.util.List;

public class MainEarthquakeList {

    private List<Earthquake> allEarthquakes;
    private List<Earthquake> dateFilteredList;

    public void setMainEarthquakeList(List<Earthquake> mainEarthquakeList) {
        allEarthquakes = mainEarthquakeList;
    }

    public List<Earthquake> getMainEarthquakeList() {
        return allEarthquakes;
    }


    public List<Earthquake> getDateFilteredList() {
        return dateFilteredList;
    }

    public void setDateFilteredList(List<Earthquake> dateFilteredList) {
        this.dateFilteredList = dateFilteredList;
    }










}
