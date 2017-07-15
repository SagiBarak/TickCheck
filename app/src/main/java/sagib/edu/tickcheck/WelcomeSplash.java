package sagib.edu.tickcheck;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;

import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class WelcomeSplash extends AwesomeSplash {
    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(R.color.gradient);
        configSplash.setAnimCircularRevealDuration(0);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM);
        configSplash.setLogoSplash(R.drawable.biglogo);
        configSplash.setAnimLogoSplashDuration(1500);
        configSplash.setTitleSplash("");
        configSplash.setTitleTextSize(14F);
        configSplash.setTitleTextColor(R.color.colorPrimaryDark);
        configSplash.setAnimTitleDuration(2000);

    }

    @Override
    public void animationsFinished() {
        if (isNetworkConnected()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("לא נמצא חיבור אינטרנט זמין.\nמומלץ להתחבר לרשת אלחוטית.").setNegativeButton("יציאה", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            }).setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    animationsFinished();
                }
            }).setNeutralButton("הגדרות רשת", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivityForResult(intent, 10);
                }
            }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        animationsFinished();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
