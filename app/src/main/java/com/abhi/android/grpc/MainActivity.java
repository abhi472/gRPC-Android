package com.abhi.android.grpc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.gameList)
    RecyclerView gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GrpcTask task = new GrpcTask(this);
        task.execute("");
    }

    private static class GrpcTask extends AsyncTask<String, Void, List<Game>> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        public GrpcTask(Activity activity) {

            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<Game> s) {


        }

        @Override
        protected List<Game> doInBackground(String... strings) {
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
                Log.d("check", "doInBackground: " + channel);
                return null;
            }
        }
    }
}
