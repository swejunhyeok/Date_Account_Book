package kr.co.kangnam.date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aks56 on 2018-05-07.
 */

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<MyItem> items = new ArrayList<>();
    private Context context;

    public ListViewAdapter(ArrayList<MyItem> m, Context context){
        items = m;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i) ;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView sex = (TextView) convertView.findViewById(R.id.sex);
        TextView phone = (TextView) convertView.findViewById(R.id.phone);

        String korSex = null;
        if(items.get(position).getSex().equals("M"))
            korSex = "남";
        else if (items.get(position).getSex().equals("W"))
            korSex = "여";

        sex.setText("성별 : " + korSex);
        phone.setText("번호 : " + items.get(position).getNum());
        return convertView;
    }
}
