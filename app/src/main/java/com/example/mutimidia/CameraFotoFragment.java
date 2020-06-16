package com.example.mutimidia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

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
    int mAlturaImagem;

    public CameraFotoFragment() {
        // Required empty public constructor
    }
    public static CameraFotoFragment newInstance(String param1, String param2) {
        CameraFotoFragment fragment = new CameraFotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*No onCreate tentamos carregar a imagem caso já tenhamos carregado uma anteriormente.*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /*No onCreateView inicalizamos o layout do fragment, mas temos um detalhe interessante que é a chamada do método
     * addOnGlobalLayoutListener do objeto ViewTreeObserver obtido por meio da chamada layout.getViewTreeObserve.
     * Esse método é importante, pois, ao inicializarmos a view do fragment, ainda não sabemos a dimensão de cada componente
     * Entretanto nós precisamos ter essa informação para exibir a foto no ImageView, por isso utilizamos essa interface que só
     * contém o método onGlobalLayout que é chamado quando a imagem está com as dimensões definidas.*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_camera_foto, container, false);
        layout.findViewById(R.id.btnFoto).setOnClickListener(this);
        mImageViewFoto = layout.findViewById(R.id.imgFoto);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        return layout;
    }
/*Nesse método verificamos se o resultado está vindo é da requisição feita pela nossa tela. Fazemos isso checando se o requestcode
* é igual a REQUESTCODE_FOTO e se o usuário realmente tirou a foto checando se o resultCOde é igual a RESULTOK. Nesse caso, chamamos o método carregarImagem().
* Utilizamos o AsyncTask para evitar o travamento do app gerenciando novas threads*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Util.REQUESTCODE_FOTO) {
            carregarImagem();
        }
    }

    /*Nesse método, Carregamos a imagem e desregistramos a classe como listener das mundanças de layout, pois só precisamos
     * ser notificados desse evento uma vez.*/
    @Override
    public void onGlobalLayout() {
        getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        mLarguraImagem = mImageViewFoto.getWidth();
        mAlturaImagem = mImageViewFoto.getHeight();
        carregarImagem();
    }

/*No método onClick, iniciamos o fluxo de tirar uma foto, Primeiro verificamos se temos a permissão de escrever arquivos no
* cartão de memória. Em caso positivo, invocamos o método abrirCamera(). Nele invocamos a aplicação de câmera por meio da ação
* ACTION_IMAGE_CAPTURE e passamos o caminho do arquivo que será gerado no parâmetro EXTRA_OUTPUT. Sem ele, a imagem será salva
* com tamanho e qualidade inferior a que a camera realmente tirou e não será salva no sistema de arquivos
* Como foi utilizado o startActivityForResult, vamos tratar o resultado, ou seja a foto tirada, no método onActivityResult*/
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnFoto) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    private void abrirCamera() {
        mCaminhoFoto = Util.novaMidia(Util.MIDIA_FOTO);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Util.REQUESTCODE_FOTO);
    }
/*Executamos a task para abrir a imagem no imageView*/
    private void carregarImagem() {
        if (mCaminhoFoto != null && mCaminhoFoto.exists()) {
            if (task == null || task.getStatus() != AsyncTask.Status.RUNNING) {
                task = new CarregarImageTask();
                task.execute();
            }
        }
    }
/*Executamos o método carregarImagem da classe util a após execurar salvamos essa imagem e mostramos ela no ImageView*/
    private class CarregarImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return Util.carregarImagem(mCaminhoFoto, mLarguraImagem, mAlturaImagem);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                mImageViewFoto.setImageBitmap(bitmap);
                Util.SalvarUltimaMidia(getActivity(), Util.MIDIA_FOTO, mCaminhoFoto.getAbsolutePath());
            }
        }
    }
}

