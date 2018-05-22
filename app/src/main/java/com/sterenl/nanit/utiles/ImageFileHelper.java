package com.sterenl.nanit.utiles;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.sterenl.nanit.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;

public class ImageFileHelper {

    public static File getPrivateExternalCacheDir(Context context, String dir) {
        if (!isExternalStorageWritable())
            return null;
        final File rootCacheDir = context.getExternalCacheDir();
        if (rootCacheDir == null || !(rootCacheDir.canRead() && rootCacheDir.canWrite()))
            return null;
        final File directory = new File(rootCacheDir, dir);
        if (!directory.exists())
            directory.mkdir();
        final File hideMediaFile = new File(directory, ".nomedia");
        if (!hideMediaFile.exists()) {
            try {
                hideMediaFile.createNewFile();
            } catch (IOException e) {
            }
        }

        return directory;
    }


    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getRealPathFromURIForGallery(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return "file://" + uri.getPath();
    }

    public static void loadAvatar(Context context, final ImageView imgView, String avatarUrl) {
        Glide.get(imgView.getContext())
                .register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient.Builder().build()));
        Glide.with(imgView.getContext())
                .load(avatarUrl).bitmapTransform(new CropCircleTransformation(imgView.getContext()))
                .placeholder(ContextCompat.getDrawable(context, R.drawable.default_place_holder_blue))
                .into(imgView);
    }
}
