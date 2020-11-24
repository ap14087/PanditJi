package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.amanpandey.panditji.R;
import com.google.android.material.tabs.TabLayout;

import fragments.MantraFragment;
import fragments.PanditFragment;
import fragments.SamagriFragment;

public class UserDashBoard extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);

        tabLayout = findViewById(R.id.tabMain);
        viewPager = findViewById(R.id.vpMain);

        setViewPager();
    }

    class Adapter extends FragmentPagerAdapter{

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    SamagriFragment samagriFragment = new SamagriFragment();
                    return samagriFragment;
                case 1:
                    MantraFragment mantraFragment = new MantraFragment();
                    return mantraFragment;
                case 2:
                    PanditFragment panditFragment = new PanditFragment();
                    return panditFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }
    }

    private void setViewPager(){
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_samagri));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_mantra));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_pandit));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Adapter adapter = new Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mnuProfile){
            startActivity(new Intent(getApplicationContext(), UserProfile.class));
        }

        return super.onOptionsItemSelected(item);
    }
}