package hcmute.edu.project_Mp3Player_Nhom06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hcmute.edu.project_Mp3Player_Nhom06.fragments.HomepageFragment;
import hcmute.edu.project_Mp3Player_Nhom06.fragments.PersonalpageFragment;

public class MainActivity extends AppCompatActivity{
    FirebaseUser user;
    FirebaseAuth auth;
    private BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //Kiểm tra đã đăng nhập chưa, nếu chưa thì đăng xuất
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        bottomNavigation = findViewById(R.id.bottom_nav);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, HomepageFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit();
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab_home:

                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, HomepageFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(null) // name can be null
                                .commit();
                        return true;
                    case R.id.tab_personal:
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, PersonalpageFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(null) // name can be null
                                .commit();
                        return true;
                    case R.id.tab_isPlayingMusic:
                        //handlePlayNav(this);
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}