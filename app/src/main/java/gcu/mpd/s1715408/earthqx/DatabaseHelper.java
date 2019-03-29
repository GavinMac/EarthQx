package gcu.mpd.s1715408.earthqx;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "earthquakes";
    //private static final String ID_COL = "ID";
    private static final String LOC_COL = "location";
    private static final String PUB_DATE = "publishDate";
    private static final String OR_DATE_COL = "originDate";
    private static final String LATLON_COL = "LatLong";
    private static final String DEPTH_COL = "depth";
    private static final String MAG_COL = "magnitude";
    private static final String CAT_COL = "category";
    private static final String LINK_COL = "link";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LOC_COL + " TEXT, "
                + PUB_DATE + " TEXT, "
                + OR_DATE_COL + "TEXT, "
                + LATLON_COL + " TEXT, "
                + DEPTH_COL + " TEXT, "
                + MAG_COL + " TEXT, "
                + CAT_COL + " TEXT, "
                + LINK_COL + " TEXT);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

//    public boolean addData(String column, Earthquake item){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(column, item);
//
//        long result = db.insert(TABLE_NAME, null, contentValues);
//
//        if(result == -1){
//            return false;
//        }else {
//            return true;
//        }
//    }


}
