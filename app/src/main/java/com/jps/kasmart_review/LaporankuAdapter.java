package com.jps.kasmart_review;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class LaporankuAdapter extends RecyclerView.Adapter<LaporankuAdapter.LaporankuViewHolder> {
    private Context mContext;
    private ArrayList<Laporan> mLaporan;
    public SwipeRefreshLayout swipeRefreshLayout;
    SessionManager sessionManager;
    HashMap<String, String> user;
    ImageView screenshoot;

    public LaporankuAdapter(Context context, ArrayList<Laporan> laporan, SwipeRefreshLayout swipeRefreshLayout){
        this.mContext = context;
        this.mLaporan = laporan;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @NonNull
    @Override
    public LaporankuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.laporan_card, parent, false);
        LaporankuAdapter.LaporankuViewHolder laporankuViewHolder = new LaporankuAdapter.LaporankuViewHolder(v);

        sessionManager = new SessionManager(mContext);
        sessionManager.checkLogin();
        user = sessionManager.getUserDetail();
        return laporankuViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LaporankuViewHolder holder,int position){
        Laporan currentItem = mLaporan.get(position);

        String judul = currentItem.getJudul();
        String deskripsi = currentItem.getDeskripsi();
        String createdAt = currentItem.getTanggal();
        String jenis = currentItem.getJenis();
        String createdBy = currentItem.getCreatedBy();
        String imgUrl = currentItem.getPhoto();

        Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_image_24).fit().centerInside().networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(screenshoot);

        holder.mCreatedBy.setText(createdBy);
        holder.mCreatedAt.setText(createdAt);
        holder.mJudul.setText(judul);
        holder.mDeskripsi.setText(deskripsi);
        holder.mJenis.setText(jenis);

        screenshoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = (FragmentActivity) (mContext);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = new DetailPictureFragment();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                Bundle bundle = new Bundle();

                final int id = currentItem.getId();
                bundle.putInt("id",id);
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null).commit();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLaporan.size();
    }

    public class LaporankuViewHolder extends  RecyclerView.ViewHolder{
        public TextView mJudul, mDeskripsi, mCreatedAt, mCreatedBy, mJenis;
        public LaporankuViewHolder(View itemView){
            super(itemView);

            screenshoot = itemView.findViewById(R.id.screenshot_laporan);
            mCreatedAt = itemView.findViewById(R.id.tanggal_laporan);
            mJudul = itemView.findViewById(R.id.judul_laporan);
            mDeskripsi = itemView.findViewById(R.id.deskripsi);
            mCreatedBy = itemView.findViewById(R.id.created_by);
            mJenis = itemView.findViewById(R.id.jenis);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                    FragmentActivity activity = (FragmentActivity) (mContext);
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = new LaporankuFragment();
                    fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction.replace(R.id.fragment_container,fragment);
                    fragmentTransaction.commit();
                    notifyDataSetChanged();
                }
            });

        }
    }
}
