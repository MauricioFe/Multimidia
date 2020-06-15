package com.example.mutimidia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.mutimidia.Util.Util;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFotoFragment extends Fragment implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {
    File mCaminhoFoto;
    ImageView mImageViewFoto;
    CarregarImageTask task;
    int mLarguraImagem;
    int mAltImagem;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CameraFotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFotoFragment newInstance(String param1, String param2) {
        CameraFotoFragment fragment = new CameraFotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        String caminhoFoto = Util.getUltimaMidia(getActivity(), Util.MIDIA_FOTO);

        if (caminhoFoto != null) {
            mCaminhoFoto = new File(caminhoFoto);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_camera_foto, container, false);
        layout.findViewById(R.id.btnFoto).setOnClickListener(this);
        mImageViewFoto = layout.findViewById(R.id.imgFoto);
        layout.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener) this);
        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Util.REQUESTCODE_FOTO){
            carregarImagem();
        }
    }

    @Override
    public void onGlobalLayout() {

    }

    @Override
    public void onClick(View v) {

    }

    private void carregarImagem() {

    }

    private class CarregarImageTask extends AsyncTask<Void, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return null;
        }
    }
}
