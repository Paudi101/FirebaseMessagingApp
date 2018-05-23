package messenger.firebase.com.firebasedemoapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Message> moviesList;
    private FirebaseAuth auth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTxt, year, genre;
        private ConstraintLayout container;

        public MyViewHolder(View view) {
            super(view);
            auth = FirebaseAuth.getInstance();
            container = (ConstraintLayout) view.findViewById(R.id.container);
            messageTxt = (TextView) view.findViewById(R.id.message);
            genre = (TextView) view.findViewById(R.id.timestamp);
        }
    }


    public MessageAdapter(List<Message> messages) {
        this.moviesList = messages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String currentUserId = auth.getCurrentUser().getUid();
        Message movie = moviesList.get(position);
        String fromId = movie.getFrom();

        if(fromId.equals(currentUserId)){
            holder.container.setBackgroundColor(Color.WHITE);
            holder.messageTxt.setTextColor(Color.BLACK);
            holder.genre.setTextColor(Color.BLACK);
        }

        holder.messageTxt.setText(movie.getMessage());
        holder.genre.setText(movie.getTime().toString());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}