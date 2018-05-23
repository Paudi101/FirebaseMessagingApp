package messenger.firebase.com.firebasedemoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FriendsFragment extends Fragment {

    private RecyclerView recycler;
    private DatabaseReference friendDatabase,usersDB;
    private View mainView;
    private FirebaseAuth auth;
    private String currentUser;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.friends_fragment, container, false);
        recycler = (RecyclerView) mainView.findViewById(R.id.friends_cycler);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser().getUid();

        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUser);
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return mainView;

    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .limitToLast(50);

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UsersViewHolder holder, int position, final Friends model) {
                // Bind the Chat object to the ChatHolder
                // ...
                final String user_id = getRef(position).getKey();
                usersDB.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        holder.setDate(userName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                        chatIntent.putExtra("user_id",user_id);
                        startActivity(chatIntent);
                    }
                });

            }
        };
        adapter.startListening();
        recycler.setAdapter(adapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String name) {
            System.out.println("LOOK HERE :" + name);
            TextView nameView = (TextView) mView.findViewById(R.id.name_txt);
            nameView.setText(name);
        }
    }


}
