package com.abhi.android.grpc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.abhi.android.grpc.adapters.GamesAdapter;
import com.abhi.android.grpc.R;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.gamezop.interview.Game;
import co.gamezop.interview.GamesRequest;
import co.gamezop.interview.GamesResponse;
import co.gamezop.interview.InfoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.gameList)
    RecyclerView gameList;

    Observable<List<Game>> gameObservable;
    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        gameObservable = Observable
                .fromCallable(this::getGames)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposable = gameObservable.subscribe(games -> {
            GamesAdapter adapter = new GamesAdapter(games, MainActivity.this);
            GridLayoutManager manager = new GridLayoutManager(MainActivity.this,
                    2);
            gameList.setLayoutManager(manager);
            gameList.setAdapter(adapter);
        }, throwable -> {

        });
    }

    @Override
    protected void onDestroy() {

        if(disposable != null)
            disposable.dispose();

        super.onDestroy();
    }

    private List<Game> getGames() {
         final WeakReference<Activity> activityReference;
         ManagedChannel channel;

        try {
            channel = OkHttpChannelBuilder
                    .forAddress("ben.gamezop.io", Integer.parseInt("50051"))
                    .usePlaintext(true)
                    .build();
            InfoServiceGrpc.InfoServiceBlockingStub stub = InfoServiceGrpc
                    .newBlockingStub(channel);

            GamesRequest request = GamesRequest.newBuilder().build();
            GamesResponse response = stub.getGames(request);
            return response.getGamesList();

        } catch (Exception e) {
            return null;
        }
    }

}
