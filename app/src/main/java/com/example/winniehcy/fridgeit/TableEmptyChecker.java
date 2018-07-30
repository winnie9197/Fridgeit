package com.example.winniehcy.fridgeit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by winniehcy on 16-10-24.
 */
public class TableEmptyChecker {
    SQLiteDatabase writable;

    String table = FoodEntryContract.FoodEntry.TABLE_NAME;
//    String tbExistsQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
//    private boolean tableExists;
    String tbEmptyQuery = "SELECT count(*) FROM " + table;
    private boolean tableEmpty;

    public TableEmptyChecker(SQLiteDatabase writable) {
        this.writable = writable;
        tableEmpty = checkTableEmpty();
    }

    public boolean getTableEmptyBool() {
        return tableEmpty;
    }

    public boolean checkTableEmpty() {
        Cursor cursor = writable.rawQuery(tbEmptyQuery, null);

        //if table exists
        if (cursor != null) {
            cursor.moveToFirst();

            //if table empty
            if (cursor.getInt(0) == 0) {
                return true;
            }
            return false;
        }
        return true;
    }
}
