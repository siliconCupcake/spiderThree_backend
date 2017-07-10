package com.curve.nandhakishore.spiderthreeback;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.curve.nandhakishore.spiderthreeback.Models.Forecast.ForecastWeather;
import com.curve.nandhakishore.spiderthreeback.Models.Forecast.List;
import com.curve.nandhakishore.spiderthreeback.Models.Today.Clouds;
import com.curve.nandhakishore.spiderthreeback.Models.Today.CurrentWeather;
import com.curve.nandhakishore.spiderthreeback.Models.Today.Main;
import com.curve.nandhakishore.spiderthreeback.Models.Today.Sys;
import com.curve.nandhakishore.spiderthreeback.Models.Today.Weather;
import com.curve.nandhakishore.spiderthreeback.Models.Today.Wind;
import java.util.ArrayList;

public class databaseManage {

    private static final String DB_NAME = "SPIDER_THREE_BACK";
    private static final int DB_VERSION = 1;
    private static final String WEATHER_NAME = "WEATHER";
    private static final String FORECAST_NAME = "FORECAST";
    private static final String W_CID = "CITY_ID";
    private static final String W_CITY = "CITY";
    private static final String W_ID = "ID";
    private static final String W_COUNTRY = "COUNTRY";
    private static final String W_ICON = "ICON";
    private static final String W_MAIN = "MAIN";
    private static final String W_DESC = "DESCRIPTION";
    private static final String W_TEMP = "TEMP";
    private static final String W_MIN = "MIN";
    private static final String W_DATE = "DATE";
    private static final String W_TIME = "TIME";
    private static final String W_MAX = "MAX";
    private static final String W_PRESSURE = "PRESSURE";
    private static final String W_HUMIDITY = "HUMIDITY";
    private static final String W_WIND = "WIND";
    private static final String W_CLOUDS = "CLOUDS";
    private String[] allColumns_W = {W_CID, W_CITY, W_ICON, W_MAIN, W_DESC, W_TEMP, W_MIN, W_MAX, W_PRESSURE, W_HUMIDITY, W_WIND, W_CLOUDS, W_TIME, W_COUNTRY};
    private String[] allColumns_F = {W_CID, W_CITY, W_DATE, W_ICON, W_MAIN, W_DESC, W_TEMP, W_MIN, W_MAX, W_PRESSURE, W_HUMIDITY, W_WIND, W_CLOUDS};

    private static final String CREATE_DB_W = "CREATE TABLE " + WEATHER_NAME + "( " + W_CID + " INTEGER PRIMARY KEY, " + W_CITY + " TEXT, "
            + W_ICON + " TEXT, " + W_MAIN + " TEXT, " + W_DESC + " TEXT, " + W_TEMP + " DOUBLE PRECISION, " + W_MIN + " DOUBLE PRECISION, "
            + W_MAX + " DOUBLE PRECISION, " + W_PRESSURE + " DOUBLE PRECISION, " + W_HUMIDITY + " INTEGER, " + W_WIND + " DOUBLE PRECISION, "
            + W_CLOUDS + " INTEGER, " + W_TIME + " INTEGER, " + W_COUNTRY + " TEXT);";

    private static final String CREATE_DB_F = "CREATE TABLE " + FORECAST_NAME + "( " + W_CID + " INTEGER, " + W_CITY + " TEXT, "
            + W_DATE + " INTEGER, " + W_ICON + " TEXT, " + W_MAIN + " TEXT, " + W_DESC + " TEXT, "
            + W_TEMP + " DOUBLE PRECISION(3), " + W_MIN + " DOUBLE PRECISION, " + W_MAX + " DOUBLE PRECISION, "
            + W_PRESSURE + " DOUBLE PRECISION, " + W_HUMIDITY + " INTEGER, " + W_WIND + " DOUBLE PRECISION, "
            + W_CLOUDS + " INTEGER, " + W_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, FOREIGN KEY(" + W_CITY + ") REFERENCES "
            + WEATHER_NAME + "(" + W_CITY + "));";

    private dbHelper myHelper;
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    public databaseManage (Context c) {
        myContext = c;
    }

    public class dbHelper extends SQLiteOpenHelper {

        public dbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB_W);
            sqLiteDatabase.execSQL(CREATE_DB_F);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WEATHER_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public databaseManage open() {
        myHelper = new dbHelper(myContext);
        myDatabase = myHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myHelper.close();
    }

    public long addWeather (CurrentWeather c) {
        ContentValues cv = new ContentValues();
        cv.put(W_CID, c.getId());
        cv.put(W_CITY, c.getName());
        cv.put(W_ICON, c.getWeather().get(0).getIcon());
        cv.put(W_MAIN, c.getWeather().get(0).getMain());
        cv.put(W_DESC, c.getWeather().get(0).getDescription());
        cv.put(W_TEMP, c.getMain().getTemp());
        cv.put(W_MIN, c.getMain().getTempMin());
        cv.put(W_MAX, c.getMain().getTempMax());
        cv.put(W_PRESSURE, c.getMain().getPressure());
        cv.put(W_HUMIDITY, c.getMain().getHumidity());
        cv.put(W_WIND, c.getWind().getSpeed());
        cv.put(W_CLOUDS, c.getClouds().getAll());
        cv.put(W_TIME, c.getDt());
        cv.put(W_COUNTRY, c.getSys().getCountry());
        return myDatabase.insert(WEATHER_NAME, null, cv);
    }

    public long addForecast (List c, String city, int cid){
        ContentValues cv = new ContentValues();
        cv.put(W_CID, cid);
        cv.put(W_CITY, city);
        cv.put(W_DATE, c.getDt());
        cv.put(W_ICON, c.getWeather().get(0).getIcon());
        cv.put(W_MAIN, c.getWeather().get(0).getMain());
        cv.put(W_DESC, c.getWeather().get(0).getDescription());
        cv.put(W_TEMP, c.getMain().getTemp());
        cv.put(W_MIN, c.getMain().getTempMin());
        cv.put(W_MAX, c.getMain().getTempMax());
        cv.put(W_PRESSURE, c.getMain().getPressure());
        cv.put(W_HUMIDITY, c.getMain().getHumidity());
        cv.put(W_WIND, c.getWind().getSpeed());
        cv.put(W_CLOUDS, c.getClouds().getAll());
        return myDatabase.insert(FORECAST_NAME, null, cv);
    }

    public CurrentWeather getWeather(String city) {
        CurrentWeather item = new CurrentWeather();
        String where = W_CITY + " = ?";
        String[] args = new String[]{city};
        Cursor c = myDatabase.query(WEATHER_NAME, allColumns_W, where, args, null, null, null);
        if (c.getCount() <= 0)
            return null;
        else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                item = convertData_W(c);
            }
            c.close();
            return item;
        }
    }

    public ArrayList<List> getForecast(int cid) {
        ArrayList<List> item = new ArrayList<>();
        String where = W_CID + " = ?";
        String[] args = new String[]{String.valueOf(cid)};
        Cursor c = myDatabase.query(FORECAST_NAME, allColumns_F, where, args, null, null, null);
        if (c.getCount() <= 0)
            return null;
        else {
            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
                item.add(convertData_F(c));
        }
        c.close();
        return item;

    }

    public void removeWeather(CurrentWeather c){
        String whereClause = W_CID + " = ?";
        String[] args = new String[] {String.valueOf(c.getId())};
        myDatabase.delete(WEATHER_NAME, whereClause ,args);
    }

    public void removeForecast(ForecastWeather c) {
        String whereClause = W_CID + " = ?";
        String[] args = new String[] {String.valueOf(c.getCity().getId())};
        myDatabase.delete(FORECAST_NAME, whereClause, args);
    }

    private CurrentWeather convertData_W(Cursor cursor) {
        CurrentWeather c = new CurrentWeather();

        c.setName(cursor.getString(1));

        Weather weather = new Weather();
        java.util.List<Weather> list = new ArrayList<>();
        weather.setIcon(cursor.getString(2));
        weather.setMain(cursor.getString(3));
        weather.setDescription(cursor.getString(4));
        list.add(weather);
        c.setWeather(list);

        Main main = new Main();
        main.setTemp(cursor.getDouble(5));
        main.setTempMin(cursor.getDouble(6));
        main.setTempMax(cursor.getDouble(7));
        main.setPressure(cursor.getDouble(8));
        main.setHumidity(cursor.getInt(9));
        c.setMain(main);

        Wind wind = new Wind();
        wind.setSpeed(cursor.getDouble(10));
        c.setWind(wind);

        Clouds clouds = new Clouds();
        clouds.setAll(cursor.getInt(11));
        c.setClouds(clouds);

        c.setDt(cursor.getInt(12));

        Sys sys = new Sys();
        sys.setCountry(cursor.getString(13));
        sys.setId(cursor.getInt(0));
        c.setSys(sys);

        return c;
    }

    private List convertData_F(Cursor cursor) {
        List c = new List();

        c.setDt(cursor.getInt(2));

        com.curve.nandhakishore.spiderthreeback.Models.Forecast.Weather weather = new com.curve.nandhakishore.spiderthreeback.Models.Forecast.Weather();
        java.util.List<com.curve.nandhakishore.spiderthreeback.Models.Forecast.Weather> list = new ArrayList<>();
        weather.setIcon(cursor.getString(3));
        weather.setMain(cursor.getString(4));
        weather.setDescription(cursor.getString(5));
        list.add(weather);
        c.setWeather(list);

        com.curve.nandhakishore.spiderthreeback.Models.Forecast.Main main = new com.curve.nandhakishore.spiderthreeback.Models.Forecast.Main();
        main.setTemp(cursor.getDouble(6));
        main.setTempMin(cursor.getDouble(7));
        main.setTempMax(cursor.getDouble(8));
        main.setPressure(cursor.getDouble(9));
        main.setHumidity(cursor.getInt(10));
        c.setMain(main);

        com.curve.nandhakishore.spiderthreeback.Models.Forecast.Wind wind = new com.curve.nandhakishore.spiderthreeback.Models.Forecast.Wind();
        wind.setSpeed(cursor.getDouble(11));
        c.setWind(wind);

        com.curve.nandhakishore.spiderthreeback.Models.Forecast.Clouds clouds = new com.curve.nandhakishore.spiderthreeback.Models.Forecast.Clouds();
        clouds.setAll(cursor.getInt(12));
        c.setClouds(clouds);

        return c;
    }
}