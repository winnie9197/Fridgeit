package com.example.winniehcy.fridgeit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * A Contract for SQLite table food_stored that stores food items.
 *
 * Created by winniehcy on 16-10-15.
 */
public final class FoodEntryContract {
    private FoodEntryContract() {}

    //define table contents
    public static class FoodEntry implements BaseColumns{
        public static final String TABLE_NAME = "food_stored";
        public static final String COLUMN_NAME_ITEM = "item";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_UNIT = "unit";
        public static final String COLUMN_NAME_TIME_LOGGED = "time_logged";
        public static final String[] ENTRY_COLUMNS = {
                _ID,
                COLUMN_NAME_ITEM,
                COLUMN_NAME_CATEGORY,
                COLUMN_NAME_QUANTITY,
                COLUMN_NAME_UNIT,
                COLUMN_NAME_TIME_LOGGED
        };

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String SQL_CREATE_FOOD_TABLE = "CREATE TABLE " + FoodEntry.TABLE_NAME + " (" + FoodEntry._ID + " INTEGER PRIMARY KEY," +
            FoodEntry.COLUMN_NAME_ITEM + TEXT_TYPE + "," +
            FoodEntry.COLUMN_NAME_CATEGORY + TEXT_TYPE + "," +
            FoodEntry.COLUMN_NAME_QUANTITY + INT_TYPE + "," +
            FoodEntry.COLUMN_NAME_UNIT + TEXT_TYPE + "," +
            FoodEntry.COLUMN_NAME_TIME_LOGGED + TEXT_TYPE + ")";

    private static final String SQL_DELETE_FOOD_TABLE =
            "DROP TABLE IF EXISTS " + FoodEntry.TABLE_NAME;

    public static class DbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FoodStored.db";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FoodEntryContract.SQL_CREATE_FOOD_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(FoodEntryContract.SQL_DELETE_FOOD_TABLE);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
