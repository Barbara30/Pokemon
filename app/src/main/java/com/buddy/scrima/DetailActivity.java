package com.buddy.scrima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class DetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name;
    TextView type;
    ListView statList;
    Bitmap bmp;
    String n;
    String strType;
    ArrayList<String> statsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String id = extras.getString("id");

        imageView = findViewById(R.id.img);
        name = findViewById(R.id.edt_name);
        type = findViewById(R.id.edt_type);
        loadImage(id);
    }

    private void loadImage(final String id) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pokeres.bastionbot.org/images/pokemon/"+ id +".png");
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } catch (ConnectException e) {
                    e.printStackTrace();
                    //runOnUiThread(alertConnectException);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    loadInfo(id);
                }
            }
        });
        t.start();
    }

    private void loadInfo(final String id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("https://pokeapi.co/api/v2/pokemon/"+ id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    String response = null;
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        InputStream stream = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder buffer = new StringBuilder();
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }
                        String finalJson = buffer.toString();
                        JSONObject parentObject = new JSONObject(finalJson);
                        //Set name
                        n= parentObject.getString("name");
                        //Set types
                        JSONArray parentArray = parentObject.getJSONArray("types");
                        strType = "";
                        for (int i = 0; i < parentArray.length(); i++) {
                            if(i!=0){
                                strType += "; ";
                            }
                            JSONObject object = parentArray.getJSONObject(i);
                            JSONObject o = object.getJSONObject("type");
                            strType += o.getString("name");
                        }

                        parentArray = parentObject.getJSONArray("stats");
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject object = parentArray.getJSONObject(i);
                            JSONObject o = object.getJSONObject("stat");
                            String s = o.getString("name") + ": " + object.getInt("base_stat");
                            statsArray.add(s);
                        }
                    } else {
                        //todo gestire eventuale errore
                        response = connection.getResponseMessage();
                    }
                } catch (ConnectException e) {
                    e.printStackTrace();
                    //runOnUiThread(alertConnectException);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                    try {
                        if (reader != null)
                            reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setInfo();
                }
            }
        });
        t.start();
    }

    private void setInfo(){
        setText(name, n);
    }



    private void setText(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bmp);
                text.setText(value);
                type.setText(strType);

                View linearLayout =  findViewById(R.id.infoStat);

                for (int i = 0; i < statsArray.size(); i++) {
                    TextView valueTV = new TextView(DetailActivity.this);
                    valueTV.setTextSize(20);
                    valueTV.setText(statsArray.get(i));
                    valueTV.setId(i);
                    valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

                    ((LinearLayout) linearLayout).addView(valueTV);
                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}