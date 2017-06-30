package sagib.edu.tickcheck;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    WebView webview;
    Show show;
    BootstrapButton btnAddToMyShows;
    BootstrapButton btnBack;
    TextView tvShowTitle;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web_view, container, false);
        webview = (WebView) v.findViewById(R.id.webview);
        btnAddToMyShows = (BootstrapButton) v.findViewById(R.id.btnAddToMyShows);
        btnBack = (BootstrapButton) v.findViewById(R.id.btnBack);
        tvShowTitle = (TextView) v.findViewById(R.id.tvShowTitle);
        show = getArguments().getParcelable("show");
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
                                    btnAddToMyShows.setText("ההופעה נוספה לרשימה!");
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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frame, new ShowsFragment()).commit();
            }
        });
        return v;
    }
}
