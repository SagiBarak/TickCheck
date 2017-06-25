package sagib.edu.tickcheck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MyShows").child(user.getUid());
                ref.push().setValue(show).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        btnAddToMyShows.setOnClickListener(null);
                        btnAddToMyShows.setBackgroundColor(Color.GRAY);
                        btnAddToMyShows.setText("ההופעה נוספה ל״רשימת ההופעות שלי״!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnAddToMyShows.setOnClickListener(null);
                        btnAddToMyShows.setBackgroundColor(Color.RED);
                        btnAddToMyShows.setText("הפעולה נכשלה, נסה שנית");
                    }
                });
            }
        };
        btnAddToMyShows.setOnClickListener(listener);
    }
        @Override
        public void onBackPressed () {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                super.onBackPressed();
            }
        }

    }
