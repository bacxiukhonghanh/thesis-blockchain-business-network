package app.studio.android.com.android_participant_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewParcelUnitListViewAdapter extends BaseAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public ViewParcelUnitListViewAdapter(ArrayList<String> list, Context context) {
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
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_parcel_unit_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.textView);
        listItemText.setText(list.get(position));

        return view;
    }
}
