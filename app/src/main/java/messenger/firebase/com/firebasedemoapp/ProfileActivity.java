package messenger.firebase.com.firebasedemoapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView displayName;
    private Button sendBtn;
    private Integer current_state;
    private DatabaseReference mUserDatabase,friendReqDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        displayName = (TextView) findViewById(R.id.user_name);
        sendBtn = (Button) findViewById(R.id.requestBtn);
        final String userKey = getIntent().getStringExtra("user_id");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Friends
        current_state = 0;

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userKey);
        friendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                displayName.setText(display_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBtn.setEnabled(false);
                if(current_state == 0){
                    //Not friends
                    friendReqDatabase.child(currentUser.getUid()).child(userKey).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendReqDatabase.child(userKey).child(currentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendBtn.setEnabled(true);
                                        current_state = 1;
                                        sendBtn.setText("Cancel Friend Request");
                                        Toast.makeText(ProfileActivity.this,"Request Sent ",Toast.LENGTH_LONG);
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this,"Request Failed to send ",Toast.LENGTH_LONG);
                            }
                        }
                    });
                } else {
                    //Delete request from our DB area
                    friendReqDatabase.child(currentUser.getUid()).child(userKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Delete request from our their DB area
                            friendReqDatabase.child(userKey).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendBtn.setEnabled(true);
                                    current_state = 0;
                                    sendBtn.setText("Send Request");
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
