package messenger.firebase.com.firebasedemoapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private TextView name;
    private DatabaseReference mRootRef;
    private FirebaseAuth auth;
    private String user_id;
    private EditText messageArea;
    private Button send, add;
    private RecyclerView chatView;
    private String currentUserId;
    private RecyclerView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = (TextView) findViewById(R.id.user_name);
        messageArea = (EditText) findViewById(R.id.messageTxt);
        send = (Button) findViewById(R.id.sendBtn);
        add = (Button) findViewById(R.id.addBtn);
        messageList = (RecyclerView) findViewById(R.id.chatView);

        user_id = getIntent().getStringExtra("user_id");
        name.setText(user_id);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mRootRef.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chatUserName = dataSnapshot.child("name").getValue().toString();
                name.setText(chatUserName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Add chat reference to the database
        mRootRef.child("Chat").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_id)){
                    Map chatAddMap = new HashMap<>();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap<>();
                    chatUserMap.put("Chat/"+ currentUserId+"/"+user_id,chatAddMap);
                    chatUserMap.put("Chat/"+ user_id+"/"+currentUserId,chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("CHAT LOG",databaseError.getDetails());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void SendMessage(){
        String msg = messageArea.getText().toString();
        if(!TextUtils.isEmpty(msg)){
            String current_user_ref = "messages/"+currentUserId+"/"+user_id;
            String chat_user_ref = "messages/"+user_id+"/"+currentUserId;

            DatabaseReference userMessagePush = mRootRef.child("messages").child(currentUserId).child(user_id).push();
            String pushId = userMessagePush.getKey();

            Map msgMap = new HashMap<>();
            msgMap.put("message",msg);
            msgMap.put("seen",false);
            msgMap.put("type","text");
            msgMap.put("time",ServerValue.TIMESTAMP);

            Map msgUserMap = new HashMap<>();
            msgUserMap.put(current_user_ref+"/"+pushId, msgMap);
            msgUserMap.put(chat_user_ref+"/"+pushId, msgMap);

            mRootRef.updateChildren(msgUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
        }
    }
}
