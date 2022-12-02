package com.jps.kasmart_review;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LaporankuFragment extends Fragment {
    String id;
    RecyclerView recyclerView;
    ArrayList<Laporan> Laporan;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    HashMap<String, String> user;
    SwipeRefreshLayout swipeRefreshLayout;
    String baseUrl = "http://www.kasmart-review.com.dpmd-bengkalis.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Daftar Laporan");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laporanku, container, false);
        Laporan = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this.getContext());
        recyclerView = view.findViewById(R.id.laporan_card);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        sessionManager = new SessionManager(getContext());
        sessionManager.checkLogin();
        user = sessionManager.getUserDetail();
        id = user.get(sessionManager.ID);

        ParseJSON();
        return view;
    }

    private void ParseJSON() {
        String url = baseUrl + "/get_laporanku.php";
        Log.d("url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length() ; i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        if(String.valueOf(data.getInt("user_id")).equals(id)){
                            int id = data.getInt("id");
                            String judul = data.getString("judul");
                            String deskripsi = data.getString("deskripsi");
                            String createdAt = data.getString("createdAt");
                            String image = data.getString("image");
                            String jenis = data.getString("jenis");
                            String createdBy = data.getString("fullname");
                            String imgUrl = baseUrl+"/"+image;
                            Laporan.add(new Laporan(id,judul,deskripsi,createdAt,createdBy,imgUrl,jenis));
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    LaporankuAdapter laporankuAdapter = new LaporankuAdapter(getContext(), Laporan, swipeRefreshLayout);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(laporankuAdapter);
                    laporankuAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "onResponse: error");
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }
}