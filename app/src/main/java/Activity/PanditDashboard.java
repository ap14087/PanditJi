package Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.amanpandey.panditji.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import fragments.PanditDashboardFragment;
import fragments.PanditManageFragment;
import fragments.PanditProfileFragment;

public class PanditDashboard extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandit_dashboard);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_dashboard,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PanditDashboardFragment()).commit();
        bottomMenu();
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.bottom_nav_dashboard:
                        fragment = new PanditDashboardFragment();
                        break;
                    case R.id.bottom_nav_manage:
                        fragment = new PanditManageFragment();
                        break;
                    case R.id.bottom_nav_profile:
                        fragment = new PanditProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
    }
}