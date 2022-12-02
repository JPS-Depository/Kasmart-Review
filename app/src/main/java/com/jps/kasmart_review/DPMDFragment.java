package com.jps.kasmart_review;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DPMDFragment extends Fragment {

    EditText inputJudul, inputDeskripsi;
    Button inputGambar, inputLaporan;
    TextView namaFile;
    Bitmap photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Kritik dan Saran DPMD");
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d_p_m_d, container, false);

        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();

        inputJudul = view.findViewById(R.id.input_judul_laporan);
        inputDeskripsi = view.findViewById(R.id.deskripsi_laporan);

        inputGambar = view.findViewById(R.id.button_input_gambar);
        inputLaporan = view.findViewById(R.id.button_input_laporan);

        namaFile = view.findViewById(R.id.file_gambar);

        inputLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storedJudul = inputJudul.getText().toString().trim();
                String storedDeskripsi = inputDeskripsi.getText().toString().trim();
                String storedCreatedBy = user.get(sessionManager.ID);
                insertItem(storedJudul,storedDeskripsi,storedCreatedBy);

                Toast.makeText(getActivity(),"Laporan telah di masukkan",Toast.LENGTH_SHORT).show();
            }
        });

        inputGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(getActivity())
                        .galleryOnly()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        if(uri != null){
            try {
                photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri );
                if(uri != null){
                    File f = new File(String.valueOf(uri));
                    String name = f.getName();
                    namaFile.setText(name);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            namaFile.setText("Pilih Gambar");
        }

    }

    private void insertItem(String storedJudul, String storedDeskripsi, String storedCreatedBy) {
        String url = "https://www.kasmart-review.com.dpmd-bengkalis.com/DPMDInsert.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response + " " + url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", ""+error);
            }
        }){
            protected HashMap<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                String image = getStringImage(photo);
                map.put("id",storedCreatedBy);
                map.put("judul",storedJudul);
                map.put("deskripsi",storedDeskripsi);
                map.put("img",image);
                return map;
            }
        };
        requestQueue.add(stringRequest);
        replaceFragment(new LaporankuFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.disallowAddToBackStack().commit();
    }

    private String getStringImage(Bitmap photo) {
        if(photo != null){
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,100,ba);
            byte[] imageByte =  ba.toByteArray();
            String encode = android.util.Base64.encodeToString(imageByte,android.util.Base64.DEFAULT);
            return encode;
        }else {
            return "";
        }
    }
}