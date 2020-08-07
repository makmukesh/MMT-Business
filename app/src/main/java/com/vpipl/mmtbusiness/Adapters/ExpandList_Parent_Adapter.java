package com.vpipl.mmtbusiness.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vpipl.mmtbusiness.Home_New;
import com.vpipl.mmtbusiness.ProductListGrid_Activity;
import com.vpipl.mmtbusiness.R;
import com.vpipl.mmtbusiness.Utils.AnimatedExpandableListView;
import com.vpipl.mmtbusiness.Utils.AppUtils;
import com.vpipl.mmtbusiness.model.ExpandList;
import com.vpipl.mmtbusiness.model.SecondLevelExpandableListView;

import java.util.List;

public class ExpandList_Parent_Adapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    String TAG = "ExpandList_Parent_Adapter";
    List<ExpandList> expandList;
    String comesFrom = "";
    private Context context;
    private LayoutInflater inflater;

    public ExpandList_Parent_Adapter(Context context, List<ExpandList> expandList, String comesFrom) {
        this.context = context;
        this.expandList = expandList;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.comesFrom = comesFrom;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandList.get(groupPosition).getExpandList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int mainGroupPosition, final int mainchildPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (AppUtils.showLogs)
            Log.v("TAG", "mainGroupPosition : " + mainGroupPosition + " childPosition : " + mainchildPosition);

        ExpandList expandChildList = (ExpandList) getChild(mainGroupPosition, mainchildPosition);
        final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(context);
        secondLevelELV.setAdapter(new ExpandList_Child_Adapter(context, expandChildList, comesFrom));
        secondLevelELV.setGroupIndicator(null);

        secondLevelELV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (expandList.get(mainGroupPosition).getExpandList() != null && expandList.get(mainGroupPosition).getExpandList().size() > 0) {
                    if (expandList.get(mainGroupPosition).getExpandList().get(mainchildPosition).getExpandList() != null && expandList.get(mainGroupPosition).getExpandList().get(mainchildPosition).getExpandList().size() == 0) {
                        moveToGroup(mainGroupPosition, mainchildPosition);
                    }
                }

                return false;
            }
        });

        secondLevelELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                moveToGroupChild(mainGroupPosition, mainchildPosition, childPosition, expandList.get(mainGroupPosition).getExpandList().get(mainchildPosition).getExpandList().get(childPosition).getName());

                if (AppUtils.showLogs)
                    Log.v("TAG", "groupPosition : " + groupPosition + " childPosition : " + childPosition);
                return false;
            }
        });

        return secondLevelELV;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return expandList.get(groupPosition).getExpandList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return expandList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return expandList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandList expandList1 = (ExpandList) getGroup(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expand_row_first, null);
        }

        RelativeLayout layout_first = (RelativeLayout) convertView.findViewById(R.id.layout_first);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView icon_right = (TextView) convertView.findViewById(R.id.icon_right);

        if (expandList.get(groupPosition).getExpandList().size() == 0) {
            icon_right.setText("");
        } else {
            if (isExpanded) {
                icon_right.setText("-");
            } else {
                icon_right.setText("+");
            }
        }

        title.setText(expandList1.getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void moveToGroup(int mainGroupPosition, int groupPosition) {
        try {
            // this case is used for both comes user like navigation and product sxpand page because it
            // only major to move both on product list page. but only check that it will shop page.

            if (!expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getId().equals(""))
            {
                Home_New.drawer.closeDrawer(Home_New.navigationView);
                Intent intent = new Intent(context, ProductListGrid_Activity.class);
                intent.putExtra("Type", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getType());
                intent.putExtra("categoryID", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getId());
                context.startActivity(intent);
                // (Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                AppUtils.showExceptionDialog(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveToGroupChild(int mainGroupPosition, int groupPosition, int groupChildPosition, String pageName) {
        try {
            if (!expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getId().equals("")) {
                Home_New.drawer.closeDrawer(Home_New.navigationView);
                Intent intent = new Intent(context, ProductListGrid_Activity.class);
                intent.putExtra("Type", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getType());
                intent.putExtra("categoryID", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getId());
                context.startActivity(intent);
                // ((Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                AppUtils.showExceptionDialog(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}