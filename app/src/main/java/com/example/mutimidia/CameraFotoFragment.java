package com.example.mutimidia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFotoFragment extends Fragment implements View.OnClickListener {
    private File arquivoFoto = null;
    ImageView imageViewFoto;

    public CameraFotoFragment() {
        // Required empty public constructor
    }

    public static CameraFotoFragment newInstance(String param1, String param2) {
        CameraFotoFragment fragment = new CameraFotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_camera_foto, container, false);
        layout.findViewById(R.id.btnFoto).setOnClickListener(this);
        imageViewFoto = layout.findViewById(R.id.imgFoto);
        return layout;
    }

    /*Nesse método verificamos se o resultado está vindo é da requisição feita pela nossa tela. Fazemos isso checando se o requestcode
     * é igual a REQUESTCODE_FOTO e se o usuário realmente tirou a foto checando se o resultCOde é igual a RESULTOK. Nesse caso, chamamos o método carregarImagem().
     * Utilizamos o AsyncTask para evitar o travamento do app gerenciando novas threads*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(arquivoFoto)));
            Bitmap bitmap = BitmapFactory.decodeFile(arquivoFoto.getAbsolutePath());
            imageViewFoto.setImageBitmap(bitmap);
        }
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
                tirarFoto();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    private File criarArquivo() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pasta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagem = new File(pasta.getPath() + File.separator + "JPGE_" + timeStamp + ".jpg");
        return imagem;
    }


    private void tirarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            arquivoFoto = criarArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (arquivoFoto != null) {
            Uri photoUri = FileProvider.getUriForFile(getContext(),
                    getContext().getApplicationContext().getPackageName() + ".provider", arquivoFoto);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, 0);
        }

    }

}

