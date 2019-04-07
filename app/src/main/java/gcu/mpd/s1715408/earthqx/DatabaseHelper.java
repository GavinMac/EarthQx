////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Sets up the database and all CRUD methods for it.
 * A database was used in this project to meet the requirement of being able to index objects in a data structure
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "earthquakes";
    private static final String ID_COL = "ID";
    private static final String TITLE_COL = "title";
    private static final String DESC_COL = "description";
    private static final String LOC_COL = "location";
    private static final String PUB_DATE = "pubDate";
    private static final String OR_DATE_COL = "originDate";
    private static final String LAT_COL = "geoLat";
    private static final String LONG_COL = "geoLong";
    private static final String DEPTH_COL = "depth";
    private static final String MAG_COL = "magnitude";
    private static final String CAT_COL = "category";
    private static final String LINK_COL = "link";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + "("+ ID_COL + " TEXT PRIMARY KEY UNIQUE, "
                + TITLE_COL + " TEXT, "
                + DESC_COL + " TEXT, "
                + LOC_COL + " TEXT, "
                + PUB_DATE + " TEXT, "
                + OR_DATE_COL + " TEXT, "
                + LAT_COL + " REAL, "
                + LONG_COL + " REAL, "
                + DEPTH_COL + " TEXT, "
                + MAG_COL + " REAL, "
                + CAT_COL + " TEXT, "
                + LINK_COL + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }

    /**
     * Adds data to the database based on an earthquake that is passed in
     * @param e Earthquake object
     * @return boolean true or false if success or failure inserting data
     */
    public boolean addData(Earthquake e) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COL, GenerateId(e));
        contentValues.put(TITLE_COL, e.getTitle());
        contentValues.put(DESC_COL, e.getDescription());
        contentValues.put(LOC_COL, e.getLocation());
        contentValues.put(PUB_DATE, e.getEarthquakeDate().toString());
        contentValues.put(OR_DATE_COL, e.getOriginDate());
        contentValues.put(LAT_COL, Float.parseFloat(e.getGeoLat()));
        contentValues.put(LONG_COL, Float.parseFloat(e.getGeoLong()));
        contentValues.put(DEPTH_COL, e.getDepth());
        contentValues.put(MAG_COL, Float.parseFloat(e.getMagnitude()));
        contentValues.put(CAT_COL, e.getCategory());
        contentValues.put(LINK_COL, e.getLink());

        try{
            long result = db.insert(TABLE_NAME, null, contentValues);

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch(SQLiteConstraintException ex){
            Log.e("SQLiteException", ""+ex);
            return false;
        }

    }

    /**
     * Gets all rows from the SQLite database from a query then sends to QueryReturnList to build a list of earthquake objects
     * @return List of earthquakes built from QueryReturnList()
     */
    public List<Earthquake> getData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return QueryReturnList(data);
    }

    /**
     * Gets all rows that match an input date from the SQLite database
     * @return List of earthquakes built from QueryReturnList()
     */
    public List<Earthquake> getListByDate(LocalDate dateInput) {

        Log.e("dateInput",""+dateInput);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PUB_DATE + " = " + "'" + dateInput + "'", null);

        //Log.e("Cursor", "" + data.getCount());

        return QueryReturnList(data);
    }

    /**
     * Gets the deepest earthquake in a given earthquake list from the SQLite database
     * @return List of earthquakes built from QueryReturnList()
     */
    public List<Earthquake> getDeepestQuake(List<Earthquake> earthquakeList) {

        List<Integer> depths = new ArrayList<>();
        int earthquakeIndex = -1;

        for (Earthquake e : earthquakeList) {
            String depthString = e.getDepth().replaceAll("[^0-9]", "");
            depths.add(Integer.parseInt(depthString));
        }
        int max = Collections.max(depths);

        for(int i = 0; i < earthquakeList.size(); i++){
            String depthString = earthquakeList.get(i).getDepth().replaceAll("[^0-9]", "");
            double eDepth = Double.parseDouble(depthString);
            if(eDepth == max){
                earthquakeIndex = i;
            }
        }
        String earthquakeId = earthquakeList.get(earthquakeIndex).getId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COL + " = " + earthquakeId, null);

        return QueryReturnList(data);
    }

    /**
     * Gets the largest magnitude earthquake in a given earthquake list from the SQLite database
     * @return List of earthquakes built from QueryReturnList()
     */
    public List<Earthquake> getHighestMagnitude(List<Earthquake> earthquakeList) {

        List<Double> mags = new ArrayList<>();
        int earthquakeIndex = -1;

        for (Earthquake e : earthquakeList) {
            String magString = e.getMagnitude();
            mags.add(Double.parseDouble(magString));
        }
        double max = Collections.max(mags);

        for(int i = 0; i < earthquakeList.size(); i++){
            double eMag = Double.parseDouble(earthquakeList.get(i).getMagnitude());
            if(eMag == max){
                 earthquakeIndex = i;
            }
        }
        String earthquakeId = earthquakeList.get(earthquakeIndex).getId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COL + " = " + earthquakeId,null);

        return QueryReturnList(data);
    }

    /**
     * Gets the most northerly/southerly/easterly/westerly earthquake by Lat/Long
     * in a given earthquake list from the SQLite database
     * @return List of earthquakes built from QueryReturnList()
     */
    public List<Earthquake>getFurthestCompassPoint(List<Earthquake> earthquakeList, String compassPoint){

        List<Double>coordinates = new ArrayList<>();
        //String queryModifier = "";
        String earthquakeId ="";

        for(Earthquake e : earthquakeList){
            if(compassPoint.equals("north") || compassPoint.equals("south")){
                coordinates.add(Double.parseDouble(e.getGeoLat()));
            }
            else if(compassPoint.equals("east" )|| compassPoint.equals("west")){
                coordinates.add(Double.parseDouble(e.getGeoLong()));
            }
        }

        double max = Collections.max(coordinates);
        double min = Collections.min(coordinates);

        for(Earthquake e : earthquakeList){
            double lat = Double.parseDouble(e.getGeoLat());
            double lon = Double.parseDouble(e.getGeoLong());

            switch(compassPoint){
                case "north" :
                    if(lat == max){
                    earthquakeId = e.getId();
                    }
                    break;

                case "south" :
                    if(lat == min){
                        earthquakeId = e.getId();
                    }
                    break;

                case "east" :
                    if(lon == max){
                        earthquakeId = e.getId();
                    }
                    break;

                case "west" :
                    if(lon == min){
                        earthquakeId = e.getId();
                    }
                    break;
            }

        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COL + " = '" + earthquakeId + "'",null);

        return QueryReturnList(data);
    }

    /**
     * Builds a list of earthquake objects from an input Cursor parameter
     * @param data Cursor with SQL query results
     * @return List of Earthquake objects
     */
    private List<Earthquake> QueryReturnList(Cursor data) {

        List<Earthquake> returnList = new ArrayList<>();

        int idIndex = data.getColumnIndex(ID_COL),
                titleIndex = data.getColumnIndex(TITLE_COL),
                descIndex = data.getColumnIndex(DESC_COL),
                locIndex = data.getColumnIndex(LOC_COL),
                pubIndex = data.getColumnIndex(PUB_DATE),
                orDateIndex = data.getColumnIndex(OR_DATE_COL),
                latIndex = data.getColumnIndex(LAT_COL),
                longIndex = data.getColumnIndex(LONG_COL),
                depthIndex = data.getColumnIndex(DEPTH_COL),
                magIndex = data.getColumnIndex(MAG_COL),
                catIndex = data.getColumnIndex(CAT_COL),
                linkIndex = data.getColumnIndex(LINK_COL);

        //Log.e("data at first pointer", ""+data.isFirst());
        //Log.e("data pointer position", ""+data.getPosition());

        data.moveToFirst();
        while (!data.isAfterLast()) {
            Earthquake e = new Earthquake(
                    data.getString(idIndex),
                    data.getString(titleIndex),
                    data.getString(descIndex),
                    data.getString(locIndex),
                    data.getString(orDateIndex),
                    data.getString(magIndex),
                    data.getString(depthIndex),
                    data.getString(linkIndex),
                    data.getString(pubIndex),
                    data.getString(catIndex),
                    data.getString(latIndex),
                    data.getString(longIndex)
            );
            returnList.add(e);
            data.moveToNext();
        }
        data.close();
        return returnList;
    }

    /**
     * Deletes everything from the table (unused)
     */
    public void DeleteTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        //db.execSQL("DELETE FROM " + TABLE_NAME + ";");

    }


    /**
     * Generates an ID for an earthquake to be inserted into the database
     * @param earthquake Earthquake object
     * @return String returnId - a generated ID from the Latitude/Longitude and timestamp of an earthquake
     */
    private String GenerateId(Earthquake earthquake){
        String returnId;
        String timeStamp = earthquake.getEarthquakeDate().toString().replaceAll("[^0-9]", "");
        String latLong = (earthquake.getGeoLat() + earthquake.getGeoLong()).replaceAll("[^0-9]", "");
        returnId = timeStamp + latLong;

        return returnId;
    }


}
