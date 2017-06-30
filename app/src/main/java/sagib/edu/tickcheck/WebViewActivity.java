package sagib.edu.tickcheck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebViewActivity extends AppCompatActivity {

    WebView webview;
    Show show;
    BootstrapButton btnAddToMyShows;
    TextView tvShowTitle;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        webview = (WebView) findViewById(R.id.webview);
        btnAddToMyShows = (BootstrapButton) findViewById(R.id.btnAddToMyShows);
        tvShowTitle = (TextView) findViewById(R.id.tvShowTitle);
        Intent intent = getIntent();
        show = intent.getParcelableExtra("show");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webview.loadUrl(request.getUrl().toString());
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        webview.loadUrl(show.getLink());
        tvShowTitle.setText(show.getPerformer() + " ב" + show.getArena() + "\nב" + show.getDayDateTime());
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddToMyShows.setOnClickListener(null);
                btnAddToMyShows.setBackgroundColor(Color.GRAY);
                btnAddToMyShows.setText("מעדכן...");
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MyShows").child(user.getUid()).push();
//                MyShow myShow = new MyShow(show.getPerformer(), show.getDayDateTime(), show.getArena(), show.getImage(), ref.getKey(), show.getDateTime());
//                ref.setValue(myShow).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        btnAddToMyShows.setOnClickListener(null);
//                        btnAddToMyShows.setBackgroundColor(Color.GRAY);
//                        btnAddToMyShows.setText("ההופעה נוספה ל״רשימת ההופעות שלי״!");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        btnAddToMyShows.setOnClickListener(null);
//                        btnAddToMyShows.setBackgroundColor(Color.RED);
//                        btnAddToMyShows.setText("הפעולה נכשלה, נסה שנית");
//                    }
//                });
                final String eventID = show.getLink().replace("https://tickets.zappa-club.co.il/loader.aspx/?target=hall.aspx?", "");
                final Context context = btnAddToMyShows.getContext();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyShowsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Toast.makeText(context, "ההופעה כבר קיימת ברשימה!", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseDatabase.getInstance().getReference("MyShowsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(eventID).setValue(eventID);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MyShows").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                            String date_s = show.getDateTime();
                            SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                            Date date = null;
                            try {
                                date = dt.parse(date_s);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
                            String finalDate = dt1.format(date);
                            MyShow myShow = new MyShow(show.getPerformer(), show.getDayDateTime(), show.getArena(), show.getImage(), ref.getKey(), finalDate, eventID);
                            ref.setValue(myShow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "ההופעה נוספה לרשימת ההופעות שלי", Toast.LENGTH_SHORT).show();
                                    btnAddToMyShows.setOnClickListener(null);
                                    btnAddToMyShows.setBackgroundColor(Color.GRAY);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "הפעולה נכשלה, נסה שנית", Toast.LENGTH_SHORT).show();
                                    btnAddToMyShows.setOnClickListener(null);
                                    btnAddToMyShows.setBackgroundColor(Color.RED);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        btnAddToMyShows.setOnClickListener(listener);
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
