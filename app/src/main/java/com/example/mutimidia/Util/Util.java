package com.example.mutimidia.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public abstract class Util {
    /*Essa é uma classe utilitária na qual foi definido algumas constantes e métodos que utilizaremos nas capturas
    * de imagens e vídeos. O método novaMidia gera um nome para a mídia com a data do aparelho. */

    public static final int MIDIA_FOTO = 0;
    public static final int MIDIA_VIDEO = 1;
    public static final int MEDIA_AUDIO = 2;

    public static final int REQUESTCODE_FOTO = 1;
    public static final int REQUESTCODE_VIDEO = 2;
    public static final int REQUESTCODE_AUDIO = 3;

    public static final String ULTIMA_FOTO = "ultima_foto";
    public static final String ULTIMO_VIDEO = "ultima_video";
    public static final String ULTIMO_AUDIO = "ultimo_audio";

    public static final String PREFERENCIA_MIDIA = "midia_perfs";
    /*Criação de um subdiretório*/
    public static final String PASTA_MIDIA = "Dominando Android";

    private static final String[] EXTENSOES = new String[]{".jpg", ".mp4", ".3gp"};
    private static final String[] CHAVES_PREF = new String[]{ULTIMA_FOTO, ULTIMO_VIDEO, ULTIMO_AUDIO};

    public static File novaMidia(int tipo) {
        String nomeMedia = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        File dirMidia = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), PASTA_MIDIA);
        if (!dirMidia.exists())
            dirMidia.mkdir();
        return new File(dirMidia, nomeMedia + EXTENSOES);
    }
/*O método SalvarUltimaMidia armazena em uma sharedPreferences o caminho do ultimo vídeo, da ultima foto e do ultimo audio salvo
* dependendo do parametro tipo. O sendBrodcast(intent) que é disparado com a ação ACTION_MEDIA_SCANNER_SCAN_FILE. Isso fará que
* o Android escaneie o sistema de arquivos e adicione essa nova media à galeria de mídia.*/
    public static void SalvarUltimaMidia(Context context, int tipo, String midia) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCIA_MIDIA, Context.MODE_PRIVATE);
        preferences.edit().putString(CHAVES_PREF[tipo], midia).commit();
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse(midia);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
/*O metodo getUltimaMidia retorna o caminho da ultima mídia e carrega informações que salvei no método SalvarUltimaMidia*/
    public static String getUltimaMidia(Context context, int tipo) {
        return context.getSharedPreferences(PREFERENCIA_MIDIA, Context.MODE_PRIVATE).getString(CHAVES_PREF[tipo], null);
    }
/*Esse método carrega a imagem e redmenciona ela para a área que ela vai ser exibida passando o arquivo que queremos carregar,
* a largura e a altura do imageView onde essa imagem será carregada. Instanciamos um objeto BitmapFactory.Options e setamos a
* propriedade inJustDecodeBounds para true indicando que apenas queremos ler o tamanho da imagem sem carrega-la realmente em
* memória. Assim quando chamamos o método decodeFile, o objeto bmOptions armazenará o tamanho real da imagem, e de pose dessa
* informação para o atributo inSAmplesSize do objeto bmOptions, setamos a propriedade inJustDecodeBounds para false. Um ultimo
* ajuste que fiz foi utilizar a propriedade inPreferredConfig definindo-a vom o valor RGB_565. Isso demanda menos memória, pois
* cada pixel da imagem é armazenado em 2 bytes, mas não tem transparência. Por fim, carregamos a imagem real redimensionada  que
* será retornada*/
    public static Bitmap carregarImagem(File imagem, int largura, int altura) {
        if (largura == 0 || altura == 0)
            return null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagem.getAbsolutePath(), bmOptions);

        int larguraFoto = bmOptions.outWidth;
        int alturaFoto = bmOptions.outHeight;

        int escala = Math.min(larguraFoto / largura, alturaFoto / altura);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = escala;
        bmOptions.inPurgeable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeFile(imagem.getAbsolutePath(), bmOptions);
        bitmap = rotacionar(bitmap, imagem.getAbsolutePath());
        return bitmap;
    }
/*A câmera do aparelho pode tirar fotos em portait ou landscape, por isso precisamos saber a orientação da foto; Utilizamos a
* classe ExifInterdace no método rotacionar, mas a rotação só efetivamente realizada com o método postRotate da classe matrix*/
    private static Bitmap rotacionar(Bitmap bitmap, String path) {

        try {
            ExifInterface ei  = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotacionar(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotacionar(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotacionar(bitmap, 270);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap rotacionar(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap bitmap = Bitmap.createBitmap(source, 0 , 0, source.getWidth(), source.getHeight(), matrix, true);
        return bitmap;
    }

}
