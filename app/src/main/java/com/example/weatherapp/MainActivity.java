package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;
    private double lat = 0d;
    private double lon = 0d;
    private String key = "d264df4a07ac0fa3b57448ef10d8b164";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_field = findViewById(R.id.user_field);
        main_btn= findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);

        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this,R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {
                     String city = user_field.getText().toString();
                     String url = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + key;
                     new GetURLData().execute(url);
                }
            }
        });



    }
    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = null;
                if (lat != 0 && lon != 0)
                    url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + key);
                else
                    url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");
                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }



                try {
                    if (reader != null)
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            return  null;

        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                try {
                    String str = "";
                    for (char c:result.toCharArray()) {
                        if (c == '[' || c == ']')
                            continue;
                        str += c;
                    }
                    JSONObject jsonObject = new JSONObject(str);
                    if (lat != 0 && lon != 0) {
                        result_info.setText("Широта: " + lat +
                                "\nДолгота: " + lon +
                                "\nТемпература: " + ((int)(jsonObject.getJSONObject("main").getDouble("temp")-273)));
                        lat = 0;
                        lon = 0;
                    }else{
                        lat = jsonObject.getDouble("lat");
                        lon = jsonObject.getDouble("lon");
                        new GetURLData().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
}