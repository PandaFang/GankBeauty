package com.github.pandafang.gankbeauty;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.pandafang.gankbeauty.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";

    public static final String EXTRA_URL = "url";

    private ImageView iv;

    private boolean isReady;

    Bitmap bitmap;

    private String mUrl;
    boolean isGrand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        iv = (ImageView) findViewById(R.id.iv);

        isReady = false;
        String url = getIntent().getStringExtra(EXTRA_URL);
        mUrl = url;

         loadImage(url);

        // 这种方式保存的图片不是原图会变大
//        downBitmap(url);



        iv.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {

                checkNeedPermission();

                return true;
            }
        });
    }


    public void loadImage(String url) {
        Glide.with(this).load(url).placeholder(R.drawable.image_loading)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        isReady = false;
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d(TAG, "onResourceReady: mode： " + model);
                        isReady = true;

                        // 如果return true; 则 into(iv) 不起作用， 要手动设置图片
                        // iv.setImageDrawable(resource);

                        return false;
                    }
                })
                .into(iv);
    }


    public void checkNeedPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final int REQUEST_EXTERNAL_STORAGE = 1;
            final String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
            else {
                download(mUrl);
            }

        }
        else {
            download(mUrl);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            saveImage();
            download(mUrl);
        }

        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // We don't have permission so prompt the user
                    new AlertDialog.Builder(this)
                            .setTitle("标题")
                            .setMessage("需要存储权限，前往设置中打开")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri packageURI = Uri.parse("package:" + getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivity(intent);
                                }
                            })
                            .show();

                }
            }
            Toast.makeText(this, "storage permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    public void download(final String url) {

        new AsyncTask<Void, Integer, File>() {

            @Override
            protected File doInBackground(Void... params) {
                File file = null;
                try {
                    FutureTarget<File>  future = Glide
                            .with(ImageActivity.this)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    file = future.get();

                    Log.i(TAG, "doInBackground: " + file.getName());

                    // 首先保存图片
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();//注意小米手机必须这样获得public绝对路径

                    File appDir = new File(pictureFolder ,"GankBeauty");
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File destFile = new File(appDir, fileName);

                    FileUtil.copy(file, destFile);

                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(destFile.getPath()))));




                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                return file;
            }

            @Override
            protected void onPostExecute(File file) {

                Toast.makeText(ImageActivity.this, "saved in Pictures/GankBeauty", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute();
    }




    public void downBitmap(String url) {
        Glide.with(this).load(url).asBitmap().placeholder(R.drawable.image_loading)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {

                        Log.i(TAG, "onResourceReady: thread is " + Thread.currentThread().getName());
                        isReady = true;
                        bitmap = resource;
                        iv.setImageBitmap(resource);
                        saveImage();

                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

    }

    public void saveImage() {

        if (!isReady)
            return;


        // 首先保存图片
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();//注意小米手机必须这样获得public绝对路径

        File appDir = new File(file ,"GankBeauty");
        boolean createed = false;
        if (!appDir.exists()) {
            createed =  appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File currentFile = new File(appDir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // 最后通知图库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(currentFile.getPath()))));



    }


}
