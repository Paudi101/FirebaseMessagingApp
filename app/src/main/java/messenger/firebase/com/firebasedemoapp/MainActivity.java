package messenger.firebase.com.firebasedemoapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager pager;
    private SectionsPagerAdapter mSections;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        pager = (ViewPager) findViewById(R.id.main_pager);
        mSections = new SectionsPagerAdapter(getSupportFragmentManager(),this);
        pager.setAdapter(mSections);

        mToolbar = (Toolbar) findViewById(R.id.mainPageAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Firebase App");

        tabLayout = (TabLayout) findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(pager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            sendToStart();
        }
    }

    public void sendToStart(){
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public void updateUI(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
            if(item.getItemId() == R.id.main_logout){
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            }

            if(item.getItemId() == R.id.users){
               Intent settingIntent = new Intent(MainActivity.this,UsersActivity.class);
               startActivity(settingIntent);
            }
        return true;
    }
}
