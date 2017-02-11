package com.felisys.gotit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.felisys.gotit.R;

import java.util.List;

/**
 * Created by gauravkanade on 1/24/17.
 */

public class MainMenuListAdapter extends ArrayAdapter<String> {
    Context context;
    Activity activity;
    List<String> menuContentList;

    public MainMenuListAdapter(Context context, Activity activity, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.activity = activity;
        this.menuContentList = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.main_menu_custom_layout, null, true);
        ((TextView) rowView.findViewById(R.id.tvMenuTitle)).setText(menuContentList.get(position));
        return rowView;
    }
}