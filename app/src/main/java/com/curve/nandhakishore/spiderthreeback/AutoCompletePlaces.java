package com.curve.nandhakishore.spiderthreeback;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatAutoCompleteTextView;

import java.util.HashMap;

/**
 * Created by Nandha Kishore on 09-07-2017.
 */

public class AutoCompletePlaces extends AppCompatAutoCompleteTextView {

    public AutoCompletePlaces (Context context, AttributeSet attrs) {
        super  (context, attrs);
    }

    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        HashMap<String, String > hm = (HashMap<String , String>) selectedItem;
        return hm.get("name");
    }
}
