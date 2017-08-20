package sagib.edu.tickcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(IntroFragment.newInstance(1));
        addSlide(IntroFragment.newInstance(2));
        addSlide(IntroFragment.newInstance(3));
        addSlide(IntroFragment.newInstance(4));

//        setBarColor(Color.parseColor("#3F51B5"));
        showSeparator(false);
        setNextArrowColor(Color.WHITE);
        setIndicatorColor(Color.WHITE, Color.LTGRAY);
        setDoneText("סיום");
        setColorDoneText(Color.WHITE);

        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences prefs = getSharedPreferences("ShowIntro", MODE_PRIVATE);
        prefs.edit().putBoolean("isDone", true).commit();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
