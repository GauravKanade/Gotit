package com.felisys.gotit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.felisys.gotit.R;
import com.felisys.gotit.entity.Target;

import java.util.List;

/**
 * Created by gauravkanade on 1/26/17.
 */

public class TargetExpandableListViewAdapter extends BaseExpandableListAdapter {
    Context context;
    Target[] targetList;

    public TargetExpandableListViewAdapter(Context context, Target[] targetList) {
        this.context = context;
        this.targetList = targetList;
    }

    @Override
    public int getGroupCount() {
        return targetList.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return targetList[groupPosition].getSubjects().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return targetList[groupPosition].getTargetId();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return targetList[groupPosition].getSubjects().get(childPosition).getSubjectName();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_view_layout, null);
        }
        ((TextView) convertView.findViewById(R.id.tvTargetName)).setText(targetList[groupPosition].getTargetId());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_view_child_layout, null);
        }
        ((TextView) convertView.findViewById(R.id.tvSubjectName)).setText(targetList[groupPosition].getSubjects().get(childPosition).getSubjectName());
        ((TextView) convertView.findViewById(R.id.tvTimeInMinutes)).setText(targetList[groupPosition].getSubjects().get(childPosition).getTimeInMinutes() + " mins");
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
