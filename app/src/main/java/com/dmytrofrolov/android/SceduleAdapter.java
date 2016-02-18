package com.dmytrofrolov.android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
        TextView state = (TextView) rowView.findViewById(R.id.state);
        TextView transportCode = (TextView) rowView.findViewById(R.id.transportCode);


        // 4. Set the text for textView
        waynumber.setText(itemsArrayList.get(position).getWaynumber());
        time.setText(itemsArrayList.get(position).getTime());
        waytitle.setText(itemsArrayList.get(position).getWaytitle());
        busnumber.setText(itemsArrayList.get(position).getBusnumber());
        transportCode.setText(itemsArrayList.get(position).getRouteCode());

        if(itemsArrayList.get(position).getState()=="0") {
            state.setText("Імовірно не на маршруті!");
        }else{
            state.setText("");
        }

        Button addBtn = (Button) rowView.findViewById(R.id.show_map);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout rl = (LinearLayout) v.getParent();
                TextView transportCode = (TextView) rl.findViewById(R.id.transportCode);
                TextView transportTitle = (TextView) rl.findViewById(R.id.waytitle);
                TextView busnumber = (TextView) rl.findViewById(R.id.busnumber);
                Intent appInfo = new Intent(v.getContext(), MapXYActivity.class);
                appInfo.putExtra("transportCode", transportCode.getText());
                appInfo.putExtra("transportTitle", transportTitle.getText());
                appInfo.putExtra("transportId", busnumber.getText());
                context.startActivity(appInfo);
            }
        });

        // 5. retrn rowView
        return rowView;
    }
}
