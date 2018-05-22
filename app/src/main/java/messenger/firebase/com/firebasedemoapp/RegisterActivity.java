package messenger.firebase.com.firebasedemoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity{

    private Button registerBtn;
    private TextInputLayout usernameTxt, emailTxt, passwordTxt;
    private Toolbar mToolbar;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        registerBtn = (Button) findViewById(R.id.createBtn);
        usernameTxt = (TextInputLayout) findViewById(R.id.txtInputName);
        emailTxt = (TextInputLayout) findViewById(R.id.txtInputEmail);
        passwordTxt = (TextInputLayout) findViewById(R.id.txtInputPassword);


        mToolbar = (Toolbar) findViewById(R.id.registerAppbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Firebase App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = usernameTxt.getEditText().getText().toString();
                String email = emailTxt.getEditText().getText().toString();
                String password = usernameTxt.getEditText().getText().toString();

                if(!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    mProgress.setTitle("Registering User");
                    mProgress.setMessage("Please wait while we create your account");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    registerUser(displayName,email,password);
                }
            }
        });
    }

    public void registerUser(String name, String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    mProgress.hide();
                    Toast.makeText(RegisterActivity.this,"Error Logging in", Toast.LENGTH_LONG);
                }
            }
        });
    }

}
