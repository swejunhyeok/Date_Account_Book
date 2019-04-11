package kr.co.kangnam.date;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private long lastTimeBackPressed;
    // 연/월 텍스트뷰
    private TextView tvDate;
    ViewHolder holder;
    // 그리드뷰 어댑터
    private GridAdapter gridAdapter;

    //일 저장 할 리스트
    private ArrayList<String> dayList;

    // 그리드뷰
    private GridView gridView;

    // 캘린더 변수
    private Calendar mCal;

    SimpleDateFormat curYearFormat;
    SimpleDateFormat curMonthFormat;
    SimpleDateFormat curDayFormat;

    String phoneNum;
    String id, sex;

    IpAddress ipAddress;

    String nowMonth;
    String Year, Month;

    ArrayList<Integer> SpendMoney = new ArrayList<>();
    ArrayList<Integer> SaveMoney = new ArrayList<>();
    ArrayList<Message> messages = new ArrayList<>();

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView leftarrow = (ImageView)findViewById(R.id.leftarrow);
        ImageView rightarrow = (ImageView)findViewById(R.id.rightarrow);

        ipAddress = new IpAddress();

        phoneNum = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            phoneNum = telephonyManager.getLine1Number();
            if (phoneNum.startsWith("+82")) {
                phoneNum = phoneNum.replace("+82", "0");
            }
        }

        tvDate = (TextView)findViewById(R.id.tv_date);

        // 오늘날짜를 세팅
        //long now = System.currentTimeMillis();
        final Date date = new Date();
        //연, 월, 일을 따로 저장
        curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        nowMonth = curMonthFormat.format(date);

        Year = curYearFormat.format(date);
        Month = curMonthFormat.format(date);

        //현재 날짜 텍스뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));

        //girdview 요일 표시
        dayList = new ArrayList<String>();
        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단mCal.set(Year,Month,Day);
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);


        for (int k = 0; k < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
            SaveMoney.add(0);
            SpendMoney.add(0);
        }
        gridView = (GridView)findViewById(R.id.gridview);
        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);

        final ImageView message = (ImageView) findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("content://sms/inbox");
                String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
                Cursor cur = getContentResolver().query(uri, projection, "address=16449999", null, "date desc");
                if (cur.moveToFirst())
                {
                    int index_Address = cur.getColumnIndex("address");
                    int index_Person = cur.getColumnIndex("person");
                    int index_Body = cur.getColumnIndex("body");
                    int index_Date = cur.getColumnIndex("date");
                    int index_Type = cur.getColumnIndex("type");
                    do
                    {
                        String strAddress = cur.getString(index_Address);
                        int intPerson = cur.getInt(index_Person);
                        String strbody = cur.getString(index_Body);
                        String MsgMoney = strbody.substring(0, strbody.indexOf("잔액") - 1);
                        String MsgOther = MsgMoney.substring(0, MsgMoney.lastIndexOf(System.getProperty("line.separator")) - 1);
                        MsgOther = MsgOther.substring(MsgOther.lastIndexOf(System.getProperty("line.separator")) + 1, MsgOther.length());
                        if(MsgOther.indexOf("입금") != -1)
                            MsgOther = "1";
                        else
                            MsgOther = "0";
                        MsgMoney = MsgMoney.substring(MsgMoney.lastIndexOf(System.getProperty("line.separator")) + 1, MsgMoney.length());
                        MsgMoney = MsgMoney.replaceAll(",","");

                        System.out.println("other -> " + MsgOther);
                        System.out.println("money -> " + Integer.parseInt(MsgMoney));

                        long longDate = cur.getLong(index_Date);
                        Date today = new Date(longDate);
                        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.KOREA);
                        String formatted = format.format(today);

                        System.out.println("date -> " + curYearFormat.format(today) + curMonthFormat.format(today) + curDayFormat.format(today));
                        int int_Type = cur.getInt(index_Type);

                        messages.add(new Message(MsgOther, MsgMoney, curYearFormat.format(today), curMonthFormat.format(today), curDayFormat.format(today)));
                    }while (cur.moveToNext());
                    if (!cur.isClosed())
                    {
                        cur.close();
                        cur = null;
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                new HttpTask(new OnCompletionListener() {
                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getString("msg").equals("ok")) {
                                sex = null;
                                id = jObj.getString("id");
                                if (jObj.getString("user1").equals(phoneNum))
                                    sex = jObj.getString("user1sex");
                                if (jObj.getString("user2").equals(phoneNum))
                                    sex = jObj.getString("user2sex");
                                for (index = 0; index < messages.size(); index++) {
                                    new HttpTask(new OnCompletionListener() {
                                        @Override
                                        public void onComplete(String result2) {
                                            try {
                                                JSONObject jObj2 = new JSONObject(result2);
                                                if (jObj2.getString("msg").equals("ok")) {

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=inputCalendar&id=" + id + "&sex=" + sex + "&other=" + messages.get(index).getOther() +
                                            "&money=" + messages.get(index).getMoney() + "&category=RR&content=RRR&year=" + messages.get(index).getYear() + "&month=" + messages.get(index).getMonth() + "&date=" + messages.get(index).getDate());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);
            }
        });

        new HttpTask(new OnCompletionListener() {
            @Override
            public void onComplete(String result) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    if (jObj.getString("msg").equals("ok")) {
                        id = jObj.getString("id");
                    }
                    new HttpTask(new OnCompletionListener() {
                        @Override
                        public void onComplete(String result2) {
                            try {
                                JSONObject jObj2 = new JSONObject(result2);
                                JSONArray msg = jObj2.getJSONArray("msg");
                                for (int i = 0; i < msg.length(); i++) {
                                    if (msg.getJSONObject(i).getString("other").equals("0"))
                                        SpendMoney.set(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1, SpendMoney.get(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1) + Integer.parseInt(msg.getJSONObject(i).getString("money")));
                                    if (msg.getJSONObject(i).getString("other").equals("1"))
                                        SaveMoney.set(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1, SaveMoney.get(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1) + Integer.parseInt(msg.getJSONObject(i).getString("money")));
                                }
                                gridAdapter.notifyDataSetInvalidated();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            int totalspend = 0, totalsave = 0;
                            for(int i = 0; i < SpendMoney.size(); i++){
                                totalspend += SpendMoney.get(i);
                                totalsave += SaveMoney.get(i);
                            }
                            MyView myView = (MyView)findViewById(R.id.myview);
                            if(totalsave+totalspend != 0)
                                myView.ratio = (float)(totalspend) / (totalsave + totalspend);
                            else
                                myView.ratio = 0.0f;
                            myView.invalidate();
                        }
                    }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=monthReturn&id=" + id + "&year=" + Year + "&month=" + Integer.parseInt(Month));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);

        leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setMonth(date.getMonth() - 1);
                Year = curYearFormat.format(date);
                Month = curMonthFormat.format(date);

                dayList.clear();
                dayList = new ArrayList<String>();
                dayList.add("일");
                dayList.add("월");
                dayList.add("화");
                dayList.add("수");
                dayList.add("목");
                dayList.add("금");
                dayList.add("토");

                tvDate.setText(curYearFormat.format(date) + "/" + Integer.parseInt(curMonthFormat.format(date)));

                mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
                int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                //1일 - 요일 매칭 시키기 위해 공백 add
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);

                SaveMoney.clear();
                SpendMoney.clear();
                for (int k = 0; k < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
                    SaveMoney.add(0);
                    SpendMoney.add(0);
                }
                new HttpTask(new OnCompletionListener() {
                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getString("msg").equals("ok")) {
                                id = jObj.getString("id");
                            }
                            new HttpTask(new OnCompletionListener() {
                                @Override
                                public void onComplete(String result2) {
                                    try {
                                        JSONObject jObj2 = new JSONObject(result2);
                                        JSONArray msg = jObj2.getJSONArray("msg");
                                        for (int i = 0; i < msg.length(); i++) {
                                            if (msg.getJSONObject(i).getString("other").equals("0"))
                                                SpendMoney.set(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1, SpendMoney.get(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1) + Integer.parseInt(msg.getJSONObject(i).getString("money")));
                                            if (msg.getJSONObject(i).getString("other").equals("1"))
                                                SaveMoney.set(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1, SaveMoney.get(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1) + Integer.parseInt(msg.getJSONObject(i).getString("money")));
                                        }
                                        gridAdapter.notifyDataSetInvalidated();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    int totalspend = 0, totalsave = 0;
                                    for(int i = 0; i < SpendMoney.size(); i++){
                                        totalspend += SpendMoney.get(i);
                                        totalsave += SaveMoney.get(i);
                                    }
                                    MyView myView = (MyView)findViewById(R.id.myview);
                                    if(totalsave+totalspend != 0)
                                        myView.ratio = (float)(totalspend) / (totalsave + totalspend);
                                    else
                                        myView.ratio = 0.0f;
                                    myView.invalidate();
                                }
                            }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=monthReturn&id=" + id + "&year=" + Year + "&month=" + Integer.parseInt(Month));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);
            }
        });

        rightarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setMonth(date.getMonth() + 1);

                Year = curYearFormat.format(date);
                Month = curMonthFormat.format(date);
                dayList.clear();
                dayList = new ArrayList<String>();
                dayList.add("일");
                dayList.add("월");
                dayList.add("화");
                dayList.add("수");
                dayList.add("목");
                dayList.add("금");
                dayList.add("토");

                tvDate.setText(curYearFormat.format(date) + "/" + (Integer.parseInt(curMonthFormat.format(date))));

                mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
                int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                //1일 - 요일 매칭 시키기 위해 공백 add
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);
                SaveMoney.clear();
                SpendMoney.clear();
                for (int k = 0; k < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
                    SaveMoney.add(0);
                    SpendMoney.add(0);
                }
                new HttpTask(new OnCompletionListener() {
                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getString("msg").equals("ok")) {
                                id = jObj.getString("id");
                            }
                            new HttpTask(new OnCompletionListener() {
                                @Override
                                public void onComplete(String result2) {
                                    try {
                                        JSONObject jObj2 = new JSONObject(result2);
                                        JSONArray msg = jObj2.getJSONArray("msg");
                                        for (int i = 0; i < msg.length(); i++) {
                                            if (msg.getJSONObject(i).getString("other").equals("0"))
                                                SpendMoney.set(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1, SpendMoney.get(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1) + Integer.parseInt(msg.getJSONObject(i).getString("money")));
                                            if (msg.getJSONObject(i).getString("other").equals("1"))
                                                SaveMoney.set(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1, SaveMoney.get(Integer.parseInt(msg.getJSONObject(i).getString("date")) - 1) + Integer.parseInt(msg.getJSONObject(i).getString("money")));
                                        }
                                        gridAdapter.notifyDataSetInvalidated();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    int totalspend = 0, totalsave = 0;
                                    for(int i = 0; i < SpendMoney.size(); i++){
                                        totalspend += SpendMoney.get(i);
                                        totalsave += SaveMoney.get(i);
                                    }
                                    MyView myView = (MyView)findViewById(R.id.myview);
                                    if(totalsave+totalspend != 0)
                                        myView.ratio = (float)(totalspend) / (totalsave + totalspend);
                                    else
                                        myView.ratio = 0.0f;
                                    myView.invalidate();
                                }
                            }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=monthReturn&id=" + id + "&year=" + Year + "&month=" + Integer.parseInt(Month));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute("http://" + ipAddress.getIp() + ":8080/MainPage.jsp?method=idReturn&user=" + phoneNum);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!"월".equals(dayList.get(i)) && !"".equals(dayList.get(i)) && !"화".equals(dayList.get(i)) && !"수".equals(dayList.get(i)) && !"목".equals(dayList.get(i))
                        && !"금".equals(dayList.get(i)) && !"토".equals(dayList.get(i)) && !"일".equals(dayList.get(i))) {
                    Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                    intent.putExtra("Year", String.valueOf(date.getYear()));
                    intent.putExtra("Month", String.valueOf(date.getMonth()));
                    intent.putExtra("Date", dayList.get(i));
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    }
                }
            });
        }

        // 해당 월에 표시할 일 수 구함
        private void setCalendarDate(int month) {
            mCal.set(Calendar.MONTH, month - 1);
            for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                dayList.add("" + (i + 1));
            }
        }

        // 그리드뷰 어댑터
        private class GridAdapter extends BaseAdapter {
            public ArrayList<Integer> spend;
            public ArrayList<Integer> save;
            private final List<String> list;
            private final LayoutInflater inflater;
            /**
             * 생성자
             *
             * @param context
             * @param list
             */
            public GridAdapter(Context context, List<String> list) {
                this.list = list;
                this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            @Override
            public int getCount() {
                return list.size();
            }
            @Override
            public String getItem(int position) {
                return list.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                holder = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                    holder = new ViewHolder();
                    holder.tvspendGridView = (TextView)convertView.findViewById(R.id.tv_spend_gridview);
                    holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_item_gridview);
                    holder.tvearnGridView = (TextView)convertView.findViewById(R.id.tv_earn_gridview);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder)convertView.getTag();
                }
                holder.tvItemGridView.setText("" + getItem(position));
                //해당 날짜 텍스트 컬러,배경 변경
                mCal = Calendar.getInstance();

                if(!"월".equals(getItem(position)) && !"".equals(getItem(position)) && !"화".equals(getItem(position)) && !"수".equals(getItem(position)) && !"목".equals(getItem(position))
                        && !"금".equals(getItem(position)) && !"토".equals(getItem(position)) && !"일".equals(getItem(position))) {
                    if(SpendMoney.get(Integer.parseInt(getItem(position)) - 1) != 0)
                        holder.tvspendGridView.setText(String.valueOf(SpendMoney.get(Integer.parseInt(getItem(position)) - 1)));
                    if(SaveMoney.get(Integer.parseInt(getItem(position)) - 1) != 0)
                        holder.tvearnGridView.setText(String.valueOf(SaveMoney.get(Integer.parseInt(getItem(position)) - 1)));
                }
                //오늘 day 가져옴
                Integer today = mCal.get(Calendar.DAY_OF_MONTH);
                String sToday = String.valueOf(today);
                if(nowMonth.equals(Month))
                    if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                        holder.tvItemGridView.setTextColor(Color.BLACK);
                    }
                if("일".equals(getItem(position))){
                    holder.tvItemGridView.setTextColor(Color.RED);
                }
                if("토".equals(getItem(position))){
                    holder.tvItemGridView.setTextColor(Color.BLUE);
                }
                return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
        TextView tvspendGridView;
        TextView tvearnGridView;
    }

    public void onBackPressed(){
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}
