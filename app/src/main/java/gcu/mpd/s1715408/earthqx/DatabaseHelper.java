package gcu.mpd.s1715408.earthqx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "earthquakes";
    //private static final String ID_COL = "ID";
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
        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Earthquake e){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
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

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public List<Earthquake> getData(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        List<Earthquake> eArray = QueryReturnList(data);
        return eArray;
    }

    public List<Earthquake>getListByDate(LocalDate dateInput){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PUB_DATE + " like " + "'"+dateInput + "'", null);

        Log.e("Cursor", ""+data.getColumnCount());
        Log.e("QueryReturnList", ""+QueryReturnList(data));

        return QueryReturnList(data);
    }


    public List<Earthquake>getDeepestQuake(List<Earthquake> earthquakeList){

        List<Integer> depths = new ArrayList<>();
        for (Earthquake e : earthquakeList){
            depths.add(Integer.parseInt(e.getDepth()));
        }

        int max = Collections.max(depths);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + DEPTH_COL + " = " + max, null);

        return QueryReturnList(data);
    }


    private List<Earthquake>QueryReturnList(Cursor data){

        List<Earthquake>returnList = new ArrayList<>();

        int titleIndex = data.getColumnIndex(TITLE_COL),
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

        while(data.moveToNext()){
            Earthquake e = new Earthquake(
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
        }
        return returnList;
    }


}
