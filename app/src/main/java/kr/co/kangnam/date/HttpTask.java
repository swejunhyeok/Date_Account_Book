package kr.co.kangnam.date;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aks56 on 2018-05-05.
 */

public class HttpTask extends AsyncTask<String, Void, String> {
    OnCompletionListener listener = null;

    public HttpTask(){
    }

    public HttpTask(OnCompletionListener listener){
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        System.out.println("url : " + strings[0]);
        URL Url = null;
        String line = null;
        try {
            Url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            line = reader.readLine();
            System.out.println("line : " + line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if(listener != null) listener.onComplete(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
