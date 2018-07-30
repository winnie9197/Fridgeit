package com.example.winniehcy.fridgeit;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Listener for mandatory fields in NewEntryActivity.
 *
 * Created by winniehcy on 16-10-23.
 */
public class MandatoryFieldOnFocusChangeListener implements View.OnFocusChangeListener {
    private int columnIndex;
//    private boolean nonEmpty;

    public MandatoryFieldOnFocusChangeListener(int columnIndex) {
        this.columnIndex = columnIndex;
//        this.nonEmpty = false;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        EditText field = (EditText) view;
        if (!hasFocus) {
            if (!hasContent(field)) {
                setFieldError(field);
            } else {
                revokeFieldError(field);
            }
        }
    }

    public boolean hasContent(EditText editText) {
        String content = editText.getText().toString().trim();

        if (content.matches("")) {
            return false;
        }
        return true;
    }

    public String getColumnName() {
        return FoodEntryContract.FoodEntry.ENTRY_COLUMNS[columnIndex];
    }

    public void setFieldError(EditText field) {
        field.setError(getColumnName() + " cannot be empty");
    }

    public void revokeFieldError(EditText field){
        field.setError(null);
    }
}

