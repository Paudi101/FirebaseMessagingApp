package messenger.firebase.com.firebasedemoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button regBtn,loginBtn;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        regBtn = (Button) findViewById(R.id.start_reg_btn);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        mToolbar = (Toolbar) findViewById(R.id.mainPageAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Firebase App");

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent registerIntent = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
