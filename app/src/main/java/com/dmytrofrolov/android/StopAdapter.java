package com.dmytrofrolov.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dmytrofrolov on 1/16/16.
 */
public class StopAdapter extends ArrayAdapter<StopItem> {
    private final Context context;
    private final ArrayList<StopItem> itemsArrayList;
    private boolean isShowButton;

    public StopAdapter(Context context, ArrayList<StopItem> itemsArrayList) {

        super(context, R.layout.stop_row, itemsArrayList);

        this.isShowButton = true;
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    public void setIsShowButton(boolean isShowButton){this.isShowButton=isShowButton;}

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.stop_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.label);
        TextView valueView = (TextView) rowView.findViewById(R.id.value);

        // 4. Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getDescription());

        Button addBtn = (Button) rowView.findViewById(R.id.save_stop);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                // SearchActivity::writeToFile(this, [itemsArrayList.get(position).getDescription()+itemsArrayList.get(position).getTitle()]);
                FileInputStream stream = null;
                String[] readBack = {};
                try {
                    stream = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/LvivRoutes.txt"));
                    ObjectInputStream din = new ObjectInputStream(stream);
                    try {
                        readBack = (String[]) din.readObject();
                    } catch (ClassNotFoundException e) {
                    }

                    stream.close();
                } catch (IOException e) {

                }

//                stringArrayList.add(0, itemsArrayList.get(position).getDescription()+itemsArrayList.get(position).getTitle());
//                readBack

                LinearLayout rl = (LinearLayout) v.getParent();
                TextView tv = (TextView) rl.findViewById(R.id.value);
                TextView tv2 = (TextView) rl.findViewById(R.id.label);
                String text = tv.getText().toString() + tv2.getText().toString();
                boolean isFound = false;
                int foundIndex = 0;

                ArrayList<String> stringArrayList = new ArrayList<String>();
                for (int i = 0; i < readBack.length; i++) {
                    stringArrayList.add(readBack[i]);
                    if (readBack[i].contains(text)) {
                        isFound = true;
                        foundIndex = i;
                    }
                }

                if (isFound == false) {
                    stringArrayList.add(0, text);
                    Toast.makeText(getContext(), "Додано успішно!", Toast.LENGTH_LONG).show();
                } else {
                    stringArrayList.remove(foundIndex);
                    Toast.makeText(getContext(), "Видалено успішно!", Toast.LENGTH_LONG).show();
                }

                FileOutputStream stream2 = null;
                try {
    /* you should declare private and final FILENAME_CITY */
                    stream2 = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/LvivRoutes.txt"));
                    ObjectOutputStream dout = new ObjectOutputStream(stream2);
                    dout.writeObject(stringArrayList.toArray(new String[stringArrayList.size()]));

                    dout.flush();
                    stream2.getFD().sync();
                    stream2.close();
                } catch (IOException e) {

                }
                Log.d("StarOnClick", "Clicked" + text);
            }
        });
        addBtn.setFocusable(false);

        if( isShowButton == false ){
//            addBtn.setEnabled(false);
//            addBtn.setVisibility(View.INVISIBLE);
            addBtn.setText("M");
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout rl = (LinearLayout) v.getParent();
                    TextView transportCode = (TextView) rl.findViewById(R.id.value);
                    TextView transportTitle = (TextView) rl.findViewById(R.id.label);
                    Intent appInfo = new Intent(v.getContext(), MapXYActivity.class);
                    appInfo.putExtra("transportCode", transportCode.getText());
                    appInfo.putExtra("transportTitle", transportTitle.getText());
                    context.startActivity(appInfo);
                }
            });
        }

        // 5. retrn rowView
        return rowView;
    }
}
