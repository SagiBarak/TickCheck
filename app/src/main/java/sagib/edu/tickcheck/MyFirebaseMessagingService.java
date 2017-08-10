package sagib.edu.tickcheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sagib on 09/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void handleIntent(Intent intent) {
        Log.d("SagiB", "Handle");
        //get the payload from your notification
//        sender = message.senderUID,
//                receiver = message.receiverUID,
//                senderDisplayName = message.senderDisplayName,
//                message = message.message,
//                time = message.time,
//                date = message.date
        String senderUID = intent.getExtras().getString("sender");
        String receiverUID = intent.getExtras().getString("receiver");
        String senderDisplayName = intent.getExtras().getString("senderDisplayName");
        String message = intent.getExtras().getString("message");
        String time = intent.getExtras().getString("time");
        String date = intent.getExtras().getString("date");

        //
        //super if the app is in the background:
        //send a push notification "DEFAULT" title and icon


        //if the app is in the foreground:
        //send the push to onMessageReceived
        Intent contentIntent = new Intent(this, MainActivity.class);
        PendingIntent pi =
                PendingIntent.getActivity(this, 1, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("קיבלת הודעה חדשה!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("הודעה מ: " + senderDisplayName + ":\n" + message))
                .setSmallIcon(R.drawable.logolauncher)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pi);
        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mgr.notify(1, builder.build());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("SagiB", "Received");
        super.onMessageReceived(remoteMessage);
    }
}
