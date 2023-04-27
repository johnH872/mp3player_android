package hcmute.edu.project_Mp3Player_Nhom06.adminActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hcmute.edu.project_Mp3Player_Nhom06.LoginActivity;
import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.fragments.AdminAlbumFragment;
import hcmute.edu.project_Mp3Player_Nhom06.fragments.AdminSingerFragment;
import hcmute.edu.project_Mp3Player_Nhom06.fragments.AdminSongsFragment;

public class AdminActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth auth;
    private BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
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
                .replace(R.id.frameLayout, AdminSongsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit();
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab_songs:
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, AdminSongsFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(null) // name can be null
                                .commit();
                        return true;
                    case R.id.tab_singers:
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, AdminSingerFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(null) // name can be null
                                .commit();
                        return true;
                    case R.id.tab_albums:
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, AdminAlbumFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(null) // name can be null
                                .commit();
                        return true;
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