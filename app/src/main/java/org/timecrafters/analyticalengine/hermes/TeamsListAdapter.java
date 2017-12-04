package org.timecrafters.analyticalengine.hermes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.athena.TeamsListCreatorActivity;

import java.util.ArrayList;

/**
 * Created by cyber on 12/4/2017.
 * Thanks to the author of this Answer: https://stackoverflow.com/a/23021960
 */

public class TeamsListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;



    public TeamsListAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    public void remove(int i) {
        list.remove(i);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.teams_list_listview_layout, null);
        }

        final TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        Button deleteButton = (Button)view.findViewById(R.id.delete);

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                list.remove(position);
                listItemText.startAnimation(((TeamsListCreatorActivity)context).animation);
                v.startAnimation(((TeamsListCreatorActivity)context).animation);
                ((TeamsListCreatorActivity)context).selectedRow = position;
                remove(position);
//                notifyDataSetChanged();
            }
        });

        return view;
    }
}
