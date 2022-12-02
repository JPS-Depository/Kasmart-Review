package com.jps.kasmart_review;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class DetailPictureFragment extends Fragment {
    int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_picture, container, false);
        Bundle bundle = this.getArguments();
        id = bundle.getInt("id");
        ImageView detailImage = view.findViewById(R.id.detail_picture);
        String url = "https://www.kasmart-review.com.dpmd-bengkalis.com/image/dpmd/screenshot_id_"+ id +".jpg";

        Log.d("debug", url);
        Picasso.get().load(url).fit().centerInside().networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(detailImage);

        return view;
    }
}