package com.dmytrofrolov.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



/**
 * Created by dmytrofrolov on 1/16/16.
 */
public class SceduleAdapter extends ArrayAdapter<SceduleItem> {
    private final Context context;
    private final ArrayList<SceduleItem> itemsArrayList;

    public SceduleAdapter(Context context, ArrayList<SceduleItem> itemsArrayList) {

        super(context, R.layout.stop_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.scedule_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView waynumber = (TextView) rowView.findViewById(R.id.waynumber);
        TextView time = (TextView) rowView.findViewById(R.id.time);
        TextView waytitle = (TextView) rowView.findViewById(R.id.waytitle);
        TextView busnumber = (TextView) rowView.findViewById(R.id.busnumber);

        // 4. Set the text for textView
        waynumber.setText(itemsArrayList.get(position).getWaynumber());
        time.setText(itemsArrayList.get(position).getTime());
        waytitle.setText(itemsArrayList.get(position).getWaytitle());
        busnumber.setText(itemsArrayList.get(position).getBusnumber());

        // 5. retrn rowView
        return rowView;
    }
}
