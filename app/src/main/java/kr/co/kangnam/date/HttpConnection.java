package kr.co.kangnam.date;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aks56 on 2018-05-09.
 */

public class HttpConnection {
    OnCompletionListener listener = null;

    public HttpConnection(){
    }

    public HttpConnection(OnCompletionListener listener){
        this.listener = listener;
    }

    public void execute(String line){
        doInBackground(line);
    }

    public void doInBackground(String strings){
        System.out.println("url : " + strings);
        URL Url = null;
        String line = null;
        try {
            Url = new URL(strings);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            line = reader.readLine();
            System.out.println("line : " + line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onPostExecute(line);
    }

    protected void onPostExecute(String s) {
        try {
            if(listener != null) listener.onComplete(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}