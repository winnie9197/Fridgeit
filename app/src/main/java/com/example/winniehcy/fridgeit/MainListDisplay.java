package com.example.winniehcy.fridgeit;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainListDisplay extends AppCompatActivity {

    //private List<String> listValues;
    FoodEntryContract.DbHelper mDbHelper = new FoodEntryContract.DbHelper(this);

    ArrayList<String> colsArrayList = new ArrayList<>();
    String[] cols;

    SQLiteDatabase writable;
    FoodListCursorAdapter mAdapter;
    ListView listView;
    Activity activity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_display);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //clear existing table
//        writable.execSQL("delete from "+ FoodEntryContract.FoodEntry.TABLE_NAME);

        //----Data Entries to List----
        //fill cols
        colsArrayList.add(FoodEntryContract.FoodEntry._ID);
        colsArrayList.add(FoodEntryContract.FoodEntry.COLUMN_NAME_ITEM);
        colsArrayList.add(FoodEntryContract.FoodEntry.COLUMN_NAME_CATEGORY);
        colsArrayList.add(FoodEntryContract.FoodEntry.COLUMN_NAME_QUANTITY);
        colsArrayList.add(FoodEntryContract.FoodEntry.COLUMN_NAME_UNIT);
        colsArrayList.add(FoodEntryContract.FoodEntry.COLUMN_NAME_TIME_LOGGED);
        //make a String array version of cols
        cols = colsArrayList.toArray(new String[0]);
        //check if list empty
        writable = mDbHelper.getWritableDatabase();
        TableEmptyChecker tableEmptyChecker = new TableEmptyChecker(writable);
        boolean tableEmpty = tableEmptyChecker.getTableEmptyBool();

        initializeListView();

        //if table is empty
        if (tableEmpty) {
            switchLayoutToEmpty();
        } else {
            switchLayoutToList();
        }
    }

    public void initializeListView() {
        Cursor initialItems = writable.query(FoodEntryContract.FoodEntry.TABLE_NAME, cols, null, null, null, null, null);

        //initiate the list adapter
        FoodListCursorAdapter adapter = new FoodListCursorAdapter(this, initialItems);
        listView = (ListView) findViewById(R.id.foods_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog alertDialog = createFoodQuantityChangeDialog(position);
                alertDialog.show();
            }
        });

        //make accessible
        mAdapter = (FoodListCursorAdapter) listView.getAdapter();
        activity = this;
    }

    public void switchLayoutToEmpty() {
        RelativeLayout listLayout = (RelativeLayout)findViewById(R.id.list_layout);
        RelativeLayout emptyLayout = (RelativeLayout)findViewById(R.id.empty_layout);

        // Enable empty and disable list layout
        listLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    public void switchLayoutToList() {
        RelativeLayout listLayout = (RelativeLayout)findViewById(R.id.list_layout);
        RelativeLayout emptyLayout = (RelativeLayout)findViewById(R.id.empty_layout);

        // Enable list & disable empty layout
        listLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    //Dialog
    public AlertDialog createFoodQuantityChangeDialog(final int position) {

        //dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainListDisplay.this);

        //dialog content
        Cursor existing_entry_cursor = (Cursor) listView.getItemAtPosition(position);
        String name = existing_entry_cursor.getString(existing_entry_cursor.getColumnIndexOrThrow(FoodEntryContract.FoodEntry.COLUMN_NAME_ITEM));
        String title = getString(R.string.dialog_change_quantity_title);
        builder.setTitle( title + " (" + name + ")" );

        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.content_change_quantity_dialog, null);

        //set NumberPicker properties
        final NumberPicker quantityPicker = (NumberPicker) view.findViewById(R.id.dialog_food_quantity_picker);
        quantityPicker.setMinValue(0);
        quantityPicker.setMaxValue(100);
        quantityPicker.setWrapSelectorWheel(true);

        //set unit hint
        final EditText unitSetter = (EditText) view.findViewById(R.id.dialog_food_unit);
        String unit = "unit";//check here
        unitSetter.setHint(unit);

        //set checkbox listener
        final CheckBox use_all_checkbox = (CheckBox) view.findViewById(R.id.use_up_food_checkbox);

        use_all_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    quantityPicker.setEnabled(false);
                    unitSetter.setEnabled(false);
                } else {
                    quantityPicker.setEnabled(true);
                    unitSetter.setEnabled(true);
                }
            }
        });

        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                //instantiate db and position cursor
                Cursor existing_entry_cursor = (Cursor) listView.getItemAtPosition(position);

                //get current row id
                long rowID = existing_entry_cursor.getLong(0);

                Cursor updated_entry_cursor;

                //see if checkbox is checked

                if (use_all_checkbox.isChecked()) {
                    //delete entry
                    writable.delete(FoodEntryContract.FoodEntry.TABLE_NAME, FoodEntryContract.FoodEntry._ID + " = " + String.valueOf(rowID), null);

                    TableEmptyChecker tableEmptyChecker = new TableEmptyChecker(writable);
                    boolean tableEmpty = tableEmptyChecker.getTableEmptyBool();

                    //if table is empty
                    if (tableEmptyChecker.getTableEmptyBool()) {
                        switchLayoutToEmpty();

                    } else {
                        switchLayoutToList();

                        //refresh cursor
                        updated_entry_cursor = writable.query(FoodEntryContract.FoodEntry.TABLE_NAME, cols, null, null, null, null, null);
                        mAdapter.swapCursor(updated_entry_cursor);
                    }

                } else {
                    //update entry

                    //get new quantity
                    int new_quantity = quantityPicker.getValue();
                    String new_unit = unitSetter.getText().toString();

                    //arguments to change (quantity & unit)
                    ContentValues val = new ContentValues();
                    val.put(FoodEntryContract.FoodEntry.COLUMN_NAME_QUANTITY, new_quantity);
                    val.put(FoodEntryContract.FoodEntry.COLUMN_NAME_UNIT, new_unit);

                    //update amount
                    writable.update(FoodEntryContract.FoodEntry.TABLE_NAME, val, FoodEntryContract.FoodEntry._ID + " = " + String.valueOf(rowID), null);

                    //refresh cursor
                    updated_entry_cursor = writable.query(FoodEntryContract.FoodEntry.TABLE_NAME, cols, null, null, null, null, null);
                    updated_entry_cursor.moveToFirst();
                    mAdapter.swapCursor(updated_entry_cursor);

                }

                existing_entry_cursor.close();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //cancel dialog
                dialog.cancel();
            }
        });
        return builder.create();
    }

    //Menu inflater
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main_list_display, menu);

        return true;

    }

    //Menu selection
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_food_entry:
                startNewEntryActivity();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TableEmptyChecker tableEmptyChecker = new TableEmptyChecker(writable);
        boolean tableEmpty = tableEmptyChecker.getTableEmptyBool();
        if (!tableEmpty) {
            switchLayoutToList();
            Cursor updated_entry_cursor = writable.query(FoodEntryContract.FoodEntry.TABLE_NAME, cols, null, null, null, null, null);
            updated_entry_cursor.moveToFirst();
            mAdapter.swapCursor(updated_entry_cursor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        writable.close();
    }

    public FoodEntryContract.DbHelper getDbHelper() {
        return mDbHelper;
    }

    //call NewEntryActivity
    public void startNewEntryActivity() {
        Intent intent = new Intent(this, NewEntryActivity.class);
        startActivity(intent);
    }

}
