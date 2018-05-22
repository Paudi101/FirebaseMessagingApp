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

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private TextView displayName;
    private Button sendBtn;
    private Integer current_state;
    private DatabaseReference mUserDatabase, friendReqDatabase, friendDatabase;
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
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                displayName.setText(display_name);

                //Friend List - Request Feature
                friendReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userKey)) {
                            String requestType = dataSnapshot.child(userKey).child("request_type").getValue().toString();
                            if (requestType.equals("received")) {
                                current_state = 2;
                                sendBtn.setText("Accept Request");
                            } else if (requestType.equals("sent")) {
                                current_state = 1;
                                sendBtn.setText("Cancel Friend Request");
                            }

                        } else {
                            //Check if this user is already a friend
                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userKey)) {
                                        current_state = 3;
                                        sendBtn.setText("Unfriend");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBtn.setEnabled(false);
                if (current_state == 0) {
                    //Send Request State
                    friendReqDatabase.child(currentUser.getUid()).child(userKey).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendReqDatabase.child(userKey).child(currentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendBtn.setEnabled(true);
                                        current_state = 1;
                                        sendBtn.setText("Cancel Friend Request");
                                        Toast.makeText(ProfileActivity.this, "Request Sent ", Toast.LENGTH_LONG);
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Request Failed to send ", Toast.LENGTH_LONG);
                            }
                        }
                    });
                } else if (current_state == 1) {
                    //Cancel Request state
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
                } else if (current_state == 2) {
                    //Accept Request state
                    final String currenDate = DateFormat.getDateTimeInstance().format(new Date());
                    friendReqDatabase.child(currentUser.getUid()).child(userKey).setValue(currenDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Add  relation to db

                            friendDatabase.child(userKey).child(currentUser.getUid()).setValue(currenDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //TODO: move this its own helper mehtod (avoid duplication)
                                    //Remove request from both users request pile
                                    friendReqDatabase.child(currentUser.getUid()).child(userKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Delete request from our their DB area
                                            friendReqDatabase.child(userKey).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendBtn.setEnabled(true);
                                                    current_state = 3;
                                                    sendBtn.setText("Unfriend");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });

                    friendReqDatabase.child(userKey).child(currentUser.getUid()).setValue(currenDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Add  relation to db
                            friendDatabase.child(currentUser.getUid()).child(userKey).setValue(currenDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //TODO: move this its own helper mehtod (avoid duplication)
                                    //Remove request from both users request pile
                                    System.out.println("NOW BOTH FRIENDS KNOW IT");
                                }
                            });
                        }
                    });

                } else if (current_state == 3) {
                    //Already friends
                    friendDatabase.child(currentUser.getUid()).child(userKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Delete request from our their DB area
                            friendDatabase.child(userKey).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
