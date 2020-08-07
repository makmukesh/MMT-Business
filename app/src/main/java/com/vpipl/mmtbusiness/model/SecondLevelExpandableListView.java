package com.vpipl.mmtbusiness.model;

import android.content.Context;
import android.view.View;

import com.vpipl.mmtbusiness.Utils.AnimatedExpandableListView;


public class SecondLevelExpandableListView extends AnimatedExpandableListView {
    public SecondLevelExpandableListView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(999999, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
