package com.calebmortensen.whatintheweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.*;

/**
 * Created by Admin on 12/12/2017.
 */

public class WeatherAdapter extends ArrayAdapter<Future> {

    //Extend scope
    Context context;
    int resource;
    java.util.List<Future> objects;

    //Constructor
    public WeatherAdapter(Context context, int resource, java.util.List<Future> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(resource,parent,false);

        TextView txtDescription = view.findViewById(R.id.textViewDescription);
        TextView txtDate = view.findViewById(R.id.textViewDate);


        Future future = objects.get(position);


        txtDescription.setText(future.getDescription());


        txtDate.setText(future.getDt());


        return view;
    }
}