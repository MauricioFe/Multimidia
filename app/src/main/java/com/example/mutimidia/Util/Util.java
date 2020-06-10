package com.example.mutimidia.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.util.Date;

public abstract class Util {
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

    public static void SalvarUltimaMidia(Context context, int tipo, String midia) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCIA_MIDIA, Context.MODE_PRIVATE);
        preferences.edit().putString(CHAVES_PREF[tipo], midia).commit();
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse(midia);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getUltimaMidia(Context context, int tipo) {
        return context.getSharedPreferences(PREFERENCIA_MIDIA, Context.MODE_PRIVATE).getString(CHAVES_PREF[tipo], null);
    }

}
