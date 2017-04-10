package com.csgroup.eventsched;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by prossik on 26.02.16.
 */
public class CustomSpinnerAdapter extends ArrayAdapter {

    private List<String> objects;
    private Context context;

    public CustomSpinnerAdapter(Context context, int resourceId,
                                List<String> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label=(TextView)row.findViewById(R.id.spItem);
        label.setText(objects.get(position));
        label.setTextColor(context.getResources().getColor(R.color.common_google_signin_btn_text_dark_focused));


        return row;
    }

}
