package com.buddy.scrima;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment {

    final List<Pokemon> list = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPokemon(view);

            }
        });
    }
    private List<Pokemon> loadPokemon(final View view) {
        //vado a leggere la lista di pokemon
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("https://pokeapi.co/api/v2/pokemon/?offset=0&limit=1000");
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
                        JSONArray parentArray = parentObject.getJSONArray("results");
                        for (int i = 0; i < parentArray.length(); i++) {
                            Pokemon item = new Gson().fromJson(parentArray.get(i).toString(), Pokemon.class);
                            list.add(item);
                        }
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
                    next(view);
                }
            }
        });
        t.start();
        return list;
    }

    private void next(View view){

        Log.d("LIST", list.get(0).name);
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) list);
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }
}