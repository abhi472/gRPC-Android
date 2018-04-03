package com.abhi.android.grpc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class GamesActivity extends AppCompatActivity {
    Bundle bundle;
    Disposable disposable;
    @BindView(R.id.web)
    WebView mWebView;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        ButterKnife.bind(this);
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        String name = bundle.getString("gameName");
        String zipUrl = bundle.getString("url");
        id = bundle.getString("id");
        ProgressDialog dialog = new ProgressDialog(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(new MyOwnWebViewClient());


        if (!isFileExisting()) {
            dialog.setMessage("downloading required files");
            dialog.show();
            disposable = providesNetworkService()
                    .zipDownload(zipUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(this::downloadString)
                    .flatMap(this::zipfile)
                    .subscribe(
                            s -> {
                                dialog.cancel();
                                File file = new File(getExternalFilesDir(null),
                                        id + "/index.html");
                                mWebView.loadUrl("file:///" + file);
                                mWebView.setWebViewClient(new MyOwnWebViewClient());
                            },
                            throwable -> {
                                dialog.cancel();
                            })

            ;
        } else {

            File file = new File(getExternalFilesDir(null), id + "/index.html");
            mWebView.loadUrl("file:///" + file);
            mWebView.setWebViewClient(new MyOwnWebViewClient());
        }


    }


    private boolean isFileExisting() {
        File zipFile = new File(getExternalFilesDir(null), id + ".zip");
        return zipFile.exists();
    }

    private Observable<String> zipfile(String path) {
        return Observable.fromCallable(() -> unpackZip(path));
    }


    private String unpackZip(String path) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename = "";
            is = new FileInputStream(path);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            String filePath = path.
                    substring(0, path.lastIndexOf(File.separator));

            while ((ze = zis.getNextEntry()) != null) {
                // zapis do souboru
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(filePath + "/" + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(filePath + "/" + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
            return filePath + "/" + filename + "/index.html";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    private Observable<String> downloadString(ResponseBody file) {

        return Observable.fromCallable(() -> writeResponseBodyToDisk(file));
    }

    private String writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File zipFile = new File(getExternalFilesDir(null), id + ".zip");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(zipFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();

                return zipFile.getAbsolutePath();
            } catch (IOException e) {
                return "";
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return "";
        }
    }

    Retrofit provideCall() {


        return new Retrofit.Builder()
                .baseUrl("https://192.168.0.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    RetrofitApiNodes providesNetworkService() {
        return provideCall().create(RetrofitApiNodes.class);
    }

    @Override
    protected void onDestroy() {

        if(disposable != null)
            disposable.dispose();
        super.onDestroy();
    }
}
