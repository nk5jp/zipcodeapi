package jp.nk5.zipcodeapi;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class ZipcodeApiAsyncTask extends AsyncTask<Integer, Void, String> {

    private ZipcodeApiListener listener;

    ZipcodeApiAsyncTask(ZipcodeApiListener listener)
    {
        this.listener = listener;
    }

    protected void onPostExecute()
    {
        listener.lockUI();
    }

    protected String doInBackground(Integer... param)
    {
        HttpURLConnection connection = null;
        String urlFormat = "http://zipcloud.ibsnet.co.jp/api/search?zipcode=%d";
        StringBuilder builder = new StringBuilder();
        String returnString = "";

        // 接続の確立
        try
        {
            URL url = new URL(String.format(Locale.JAPAN, urlFormat, param[0]));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Accept-Language", "jp");
            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                //普通はエラーを書きだす
            }
        }

        try
        {
            JSONObject jsonObject = new JSONObject(builder.toString());

            JSONArray jsonArray = jsonObject.getJSONArray("results");

            JSONObject result = jsonArray.getJSONObject(0);
            returnString = result.getString("address1") + result.getString("address2") + result.getString("address3");


        } catch (Exception e) {
            return null;
        }

        return returnString;
    }


    protected void onPostExecute(String returnString)
    {
        listener.unlockUI();
        listener.updateUI(returnString);
    }

    protected void onCancelled()
    {
        listener.unlockUI();
    }

}
