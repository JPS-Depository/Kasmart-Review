package com.jps.kasmart_review;

public class Laporan {
    private  int mId;
    private String mJudul, mDeskripsi, mTanggal, mPhoto, mCreatedBy, mJenis;
    public Laporan(int id, String judul, String deskripsi, String tanggal, String createdBy, String photo, String jenis){
        mId = id;
        mJudul = judul;
        mDeskripsi = deskripsi;
        mTanggal = tanggal;
        mCreatedBy = createdBy;
        mPhoto = photo;
        mJenis = jenis;
    }

    public int getId(){ return mId;}
    public String getJudul(){return  mJudul;}
    public String getDeskripsi(){return  mDeskripsi;}
    public String getTanggal(){return mTanggal;}
    public String getCreatedBy(){return mCreatedBy;}
    public String getPhoto(){return mPhoto;}
    public String getJenis(){return mJenis;}
}
