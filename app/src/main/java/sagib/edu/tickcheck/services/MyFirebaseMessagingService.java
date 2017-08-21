package sagib.edu.tickcheck.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import sagib.edu.tickcheck.MainActivity;
import sagib.edu.tickcheck.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void handleIntent(Intent intent) {
        String senderUID = intent.getExtras().getString("sender");
        String receiverUID = intent.getExtras().getString("receiver");
        String senderDisplayName = intent.getStringExtra("senderDisplayName");
        String message = intent.getStringExtra("message");
        String time = intent.getExtras().getString("time");
        String date = intent.getExtras().getString("date");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getUid().equals(receiverUID)) {
                Intent contentIntent = new Intent(this, MainActivity.class);
                PendingIntent pi =
                        PendingIntent.getActivity(this, 1, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setContentTitle("קיבלת הודעה מאת " + senderDisplayName)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(date + " " + time + "\n" + message))
                        .setSmallIcon(R.drawable.logolauncher)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pi);
                NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mgr.notify(1, builder.build());
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
