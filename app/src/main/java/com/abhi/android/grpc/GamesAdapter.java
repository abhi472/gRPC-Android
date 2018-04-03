package com.abhi.android.grpc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.gamezop.interview.Game;

/**
 * Created by abhishek on 2/4/18.
 */

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {


    private List<Game> games;
    private Context context;

    public GamesAdapter(List<Game> games, Context context) {
        this.games = games;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

Game game = games.get(position);
holder.setup(game);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game game1 = games.get(holder.getAdapterPosition());
                Intent intent = new Intent(context, GamesActivity.class);
                intent.putExtra("gameName", game1.getName());
                intent.putExtra("url", game1.getZipUrl());
                intent.putExtra("id", game1.getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        CustomImageView img;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.card)
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setup(Game game) {
            Picasso
                    .get()
                    .load(game.getCover())
                    .fit()
                    .into(img);
            name.setText(game.getName());


        }
    }
}
