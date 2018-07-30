package com.example.winniehcy.fridgeit;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Listener for quantity dialog in MainListDisplay activity.
 *
 * Created by winniehcy on 16-10-22.
 */
public class FoodListOnClickListener implements View.OnClickListener {
    SQLiteDatabase writable;
    String[] cols;
    CursorAdapter adapter;

    ImageView deleteButton;

    Cursor updated_entry_cursor;

    public FoodListOnClickListener(ImageView deleteButton, CursorAdapter adapter, SQLiteDatabase writable, String[] cols) {
        this.adapter = adapter;
        this.deleteButton = deleteButton;
        this.writable = writable;
        this.cols = cols;
    }

    @Override
    public void onClick(View view) {

        View row = (View) deleteButton.getTag();
        TextView idTV = (TextView) row.findViewById(R.id.food_list_item_id);
        String rowID = idTV.getText().toString();

        //delete entry
        writable.delete(FoodEntryContract.FoodEntry.TABLE_NAME, FoodEntryContract.FoodEntry._ID + " = " + rowID, null);

        MainListDisplay activity = (MainListDisplay) view.getContext();

        TableEmptyChecker tableEmptyChecker = new TableEmptyChecker(writable);
        boolean tableEmpty = tableEmptyChecker.getTableEmptyBool();

        //if table is empty
        if (tableEmpty) {

            activity.switchLayoutToEmpty();
        } else {
            activity.switchLayoutToList();

            //refresh cursor
            updated_entry_cursor = writable.query(FoodEntryContract.FoodEntry.TABLE_NAME, cols, null, null, null, null, null);
            updated_entry_cursor.moveToFirst();
            adapter.swapCursor(updated_entry_cursor);
        }
    }

}
