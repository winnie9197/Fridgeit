package com.example.winniehcy.fridgeit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
* Form UI to request user item input.
*
*/
public class NewEntryActivity extends AppCompatActivity {
    SQLiteDatabase writable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_entry);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Entry");

        //Category Spinner
        Spinner fCSpinner = (Spinner) findViewById(R.id.food_category_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fCSpinner.setAdapter(adapter);

        fCSpinner.setSelection(0);

        fCSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                adapter.getItemAtPosition(position);

                String state = "onItemSelected" + String.valueOf(position);
            }

            public void onNothingSelected (AdapterView<?> adapter) {

            };
        });

        EditText nameField = (EditText) findViewById(R.id.food_name);
        EditText quantityField = (EditText) findViewById(R.id.food_quantity);

        //set focus change listener
        nameField.setOnFocusChangeListener(new MandatoryFieldOnFocusChangeListener(1));
        quantityField.setOnFocusChangeListener(new MandatoryFieldOnFocusChangeListener(3));
    }

    //after pressing save button
    public void saveEntry(View view) {
        //save entry...

        //check if required fields non empty
        EditText nameField = (EditText) findViewById(R.id.food_name);
        EditText quantityField = (EditText) findViewById(R.id.food_quantity);

        MandatoryFieldOnFocusChangeListener nameFocusListener = ((MandatoryFieldOnFocusChangeListener) nameField.getOnFocusChangeListener());
        MandatoryFieldOnFocusChangeListener quantityFocusListener = ((MandatoryFieldOnFocusChangeListener) quantityField.getOnFocusChangeListener());

        boolean nameEntered = nameFocusListener.hasContent(nameField);
        boolean quantityEntered = quantityFocusListener.hasContent(quantityField);

        if (!nameEntered || !quantityEntered) {
            //alert missing fields
            List missingFields = new ArrayList<String>();
            if (!nameEntered) {
                missingFields.add(nameFocusListener.getColumnName());
                nameFocusListener.setFieldError(nameField);
            }
            if (!quantityEntered) {
                missingFields.add(quantityFocusListener.getColumnName());
                quantityFocusListener.setFieldError(quantityField);
            }
            Context context = getApplicationContext();
            Toast fieldsRequiredToast = Toast.makeText(context, missingFields + "  required", Toast.LENGTH_SHORT);
            fieldsRequiredToast.show();
        } else {
            //get entry info
            String item = getTextVal(view, R.id.food_name);
            String category = getSpinnerVal(view, R.id.food_category_spinner);
            int quantity = Integer.valueOf(getTextVal(view, R.id.food_quantity));
            String unit = getTextVal(view, R.id.food_unit);
            Calendar cal = Calendar.getInstance();
            String timestamp = cal.getTime().toString();

            //handle empty food unit
            if (unit == "") {
                unit = " ";
            }

            //access writable db
            writable = mDbHelper.getWritableDatabase();

            //put values
            ContentValues values = new ContentValues();
            values.put(FoodEntryContract.FoodEntry.COLUMN_NAME_ITEM, item);
            values.put(FoodEntryContract.FoodEntry.COLUMN_NAME_CATEGORY, category);
            values.put(FoodEntryContract.FoodEntry.COLUMN_NAME_QUANTITY, quantity);
            values.put(FoodEntryContract.FoodEntry.COLUMN_NAME_UNIT, unit);
            values.put(FoodEntryContract.FoodEntry.COLUMN_NAME_TIME_LOGGED, timestamp);

            //check existing entry to save data
            Cursor cursor = accessEntries(item);
            int count = cursor.getCount();
            if (count == 0) {
                //save data
                writable.insert(FoodEntryContract.FoodEntry.TABLE_NAME, null, values);
            } else {
                //update data
                String[] name = {item};
                writable.update(FoodEntryContract.FoodEntry.TABLE_NAME, values, FoodEntryContract.FoodEntry.COLUMN_NAME_ITEM + " = ?", name);
            }

            //kill activity
            cursor.close();
            finish();
        }
    }


    //get value from EditText
    public String getTextVal(View view, int ViewId) {
        EditText targetView = (EditText) findViewById(ViewId);
        String val = targetView.getText().toString();

        return val;
    }

    //get value from Spinner
    public String getSpinnerVal(View view, int ViewId) {
        Spinner spinner = (Spinner) findViewById(ViewId);
        String val = spinner.getSelectedItem().toString();
        return val;
    }

    FoodEntryContract.DbHelper mDbHelper = new FoodEntryContract.DbHelper(this);

    public Cursor accessEntries(String selectionArg) {
        String[] cols = {
                FoodEntryContract.FoodEntry._ID,
                FoodEntryContract.FoodEntry.COLUMN_NAME_ITEM,
                FoodEntryContract.FoodEntry.COLUMN_NAME_CATEGORY,
                FoodEntryContract.FoodEntry.COLUMN_NAME_QUANTITY,
                FoodEntryContract.FoodEntry.COLUMN_NAME_UNIT,
                FoodEntryContract.FoodEntry.COLUMN_NAME_TIME_LOGGED
        };

        String selection = FoodEntryContract.FoodEntry.COLUMN_NAME_ITEM + " = ?";
        String[] selectionArgs = {selectionArg};

        Cursor cursor =  writable.query(FoodEntryContract.FoodEntry.TABLE_NAME,
                cols,
                selection,
                selectionArgs,
                null,
                null,
                null);

        return cursor;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_create_new_entry, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_delete_new_entry:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
