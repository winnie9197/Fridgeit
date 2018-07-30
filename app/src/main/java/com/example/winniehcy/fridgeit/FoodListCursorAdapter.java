package com.example.winniehcy.fridgeit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

/**
 * CursorAdapter for listview in MainListDisplay activity.
 *
 * Created by winniehcy on 16-10-16.
 */
public class FoodListCursorAdapter extends CursorAdapter implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private transient Context context;
    private transient Cursor cursor;

    public FoodListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.food_row_layout, parent, false);
        //row.setTag(row.findViewWithTag(R.id.list_delete_entry_button));
        ImageView deleteButton = (ImageView) row.findViewById(R.id.list_delete_entry_button);
        deleteButton.setTag(row);
        setUpDeleteButton(row);

        return row;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //fill each row with data
        TextView nameTV = (TextView) view.findViewById(R.id.food_list_item_name);
        TextView amountTV = (TextView) view.findViewById(R.id.food_list_item_amount);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntryContract.FoodEntry.COLUMN_NAME_ITEM));

        String quantity = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(FoodEntryContract.FoodEntry.COLUMN_NAME_QUANTITY)));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntryContract.FoodEntry.COLUMN_NAME_UNIT));
        String amount = quantity + " " + unit;

        //temp
//        String time = String.valueOf(FoodEntryContract.FoodEntry.COLUMN_NAME_TIME_LOGGED);
//        Log.d("time", time);

        nameTV.setText(name);
        amountTV.setText(amount);

        //id
        String id = String.valueOf(cursor.getLong(0));
        TextView idTV = (TextView) view.findViewById(R.id.food_list_item_id);
        idTV.setVisibility(View.GONE);
        idTV.setText(id);
    }

    private void setUpDeleteButton(View row) {

        Context activity = row.getContext();
        FoodEntryContract.DbHelper mDbHelper = ((MainListDisplay) activity).getDbHelper();
        SQLiteDatabase writable = mDbHelper.getWritableDatabase();

        String [] cols = FoodEntryContract.FoodEntry.ENTRY_COLUMNS;

        ImageView deleteButton = (ImageView) row.findViewById(R.id.list_delete_entry_button);

        //delete button onClickListener
        FoodListOnClickListener listener = new FoodListOnClickListener(deleteButton, this, writable, cols);
        deleteButton.setOnClickListener(listener);
    }


}

