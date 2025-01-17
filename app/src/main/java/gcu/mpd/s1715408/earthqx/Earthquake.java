////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////
package gcu.mpd.s1715408.earthqx;

import android.util.Log;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Earthquake POCO class
 */
public class Earthquake implements Serializable {

    private String id;
    private String title;
    private String description;
    private String location;
    private String originDate;
    private String magnitude;
    private String depth;
    private String link;
    private String pubDate;
    private String category;
    private String geoLat;
    private String geoLong;
    public LocalDate earthquakeDate;

    public Earthquake(){
    }

    public Earthquake(String id, String title, String description, String location, String originDate, String magnitude, String depth, String link, String pubDate, String category, String geoLat, String geoLong) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.originDate = originDate;
        this.magnitude = magnitude;
        this.depth = depth;
        this.link = link;
        this.pubDate = pubDate;
        this.category = category;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOriginDate() {
        return originDate;
    }

    public void setOriginDate(String originDate) {
        this.originDate = originDate;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGeoLat() { return geoLat; }

    public void setGeoLat(String geoLat) {
        this.geoLat = geoLat;
    }

    public String getGeoLong() { return geoLong; }

    public void setGeoLong(String geoLong) {
        this.geoLong = geoLong;
    }

    public LocalDate getEarthquakeDate(){ return earthquakeDate; }

    public void setEarthquakeDate(LocalDate earthquakeDate){this.earthquakeDate = earthquakeDate;}


    @Override
    public String toString(){
        return location
                + "\n" + magnitude
                +"\n" + depth
                + "\n" + pubDate
                +"\n" + originDate
                + "\n" + category
                + "\n"+ geoLat + ", " + geoLong
                +"\n";
    }

    public String minimalInfo(){
        return location + "\nM: " + magnitude;
    }

}
