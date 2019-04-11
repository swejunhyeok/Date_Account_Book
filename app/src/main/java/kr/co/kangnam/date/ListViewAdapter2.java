package kr.co.kangnam.date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aks56 on 2018-05-08.
 */

public class ListViewAdapter2 extends BaseAdapter {
    private ArrayList<MyItem2> items = new ArrayList<>();
    private Context context;

    public ListViewAdapter2(ArrayList<MyItem2> m, Context context){
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
            convertView = inflater.inflate(R.layout.list_item2, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        TextView textView = (TextView) convertView.findViewById(R.id.text);

        ImageView sexImage = (ImageView) convertView.findViewById(R.id.sex);
        TextView moneyText = (TextView) convertView.findViewById(R.id.money);

        TextView num = (TextView)convertView.findViewById(R.id.num);
        num.setText(String.valueOf(items.get(position).getNum()));

        if(items.get(position).getOther() == 1) {
            imageView.setImageResource(R.drawable.savemoney);
            textView.setText("수입");
        }
        if(items.get(position).getSex().equals("W")){
            sexImage.setImageResource(R.drawable.woman);
        }

        moneyText.setText(String.valueOf(items.get(position).getMoney()));
        return convertView;
    }
}
