package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.amanpandey.panditji.R;

public class SplashScreen extends AppCompatActivity {

    ImageView backgroundImage;
    TextView welcome,tag_line,sub_line;
    private static int SPLASH_TIMER = 5000;
    SharedPreferences onBoardingScreen;

    //Animation
    Animation sideAnim,topAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Hooks
        backgroundImage = findViewById(R.id.splash);
        welcome = findViewById(R.id.welcome);
        tag_line = findViewById(R.id.tagline);
        sub_line = findViewById(R.id.sub_line);

        //Animations
        sideAnim = AnimationUtils.loadAnimation(this,R.anim.side_anim);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);

        //set Animation on elements
        backgroundImage.setAnimation(sideAnim);
        welcome.setAnimation(topAnim);
        tag_line.setAnimation(topAnim);
        sub_line.setAnimation(topAnim);

        new Handler().postDelayed(() -> {

            onBoardingScreen = getSharedPreferences( "onBoardingScreen",MODE_PRIVATE);

            boolean isFirstTime = onBoardingScreen.getBoolean("firstTime",true);

            if(isFirstTime){
                SharedPreferences.Editor editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime",false);
                editor.commit();
                startActivity(new Intent(SplashScreen.this,Onboarding.class));
                finish();
            }
            else {
                startActivity(new Intent(SplashScreen.this, UserRegistration.class));
                finish();
            }

        },SPLASH_TIMER);
    }
}