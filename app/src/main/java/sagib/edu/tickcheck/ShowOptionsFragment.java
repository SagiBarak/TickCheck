package sagib.edu.tickcheck;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ShowOptionsFragment extends BottomSheetDialogFragment {

    Show show;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvPerformer)
    TextView tvPerformer;
    @BindView(R.id.tvArena)
    TextView tvArena;
    @BindView(R.id.tvDayDateTime)
    TextView tvDayDateTime;
    @BindView(R.id.btnTicketsStatus)
    BootstrapButton btnTicketsStatus;
    @BindView(R.id.btnAddToMyShows)
    BootstrapButton btnAddToMyShows;
    @BindView(R.id.btnBuyTickets)
    BootstrapButton btnBuyTickets;
    @BindView(R.id.btnShowZones)
    BootstrapButton btnShowZones;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_options, container, false);
        show = getArguments().getParcelable("show");
        unbinder = ButterKnife.bind(this, v);
        Picasso.with(getContext()).load(show.getImage()).into(ivImage);
        tvPerformer.setText(show.getPerformer());
        tvArena.setText(show.getArena());
        tvDayDateTime.setText(show.getDayDateTime());
        if (!show.isTicketsAvailable()) {
            btnTicketsStatus.setText("אין כרטיסים זמינים");
            btnTicketsStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            btnBuyTickets.setVisibility(View.GONE);
        }
        final String eventID = show.getLink().replace("https://tickets.zappa-club.co.il/loader.aspx/?target=hall.aspx?", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyShowsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    btnAddToMyShows.setText("ההופעה קיימת ברשימת ההופעות שלי");
                    btnAddToMyShows.setOnClickListener(null);
                    btnAddToMyShows.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnAddToMyShows)
    public void onBtnAddToMyShowsClicked() {
        final String eventID = show.getLink().replace("https://tickets.zappa-club.co.il/loader.aspx/?target=hall.aspx?", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyShowsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    dismiss();
                    Toast.makeText(getContext(), "ההופעה כבר קיימת ברשימה!", Toast.LENGTH_SHORT).show();
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
                            dismiss();
                            Toast.makeText(getContext(), "ההופעה נוספה לרשימת ההופעות שלי", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismiss();
                            Toast.makeText(getContext(), "הפעולה נכשלה, נסה שנית", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @OnClick(R.id.btnBuyTickets)
    public void onBtnBuyTicketsClicked() {
        Bundle args = new Bundle();
        args.putParcelable("show", show);
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(args);
        FragmentManager fm = getParentFragment().getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        getParentFragment().getFragmentManager().beginTransaction().replace(R.id.frame, webViewFragment).addToBackStack("Buy").commit();
        dismiss();
    }

    @OnClick(R.id.btnShowZones)
    public void onBtnShowZonesClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("איזורים ב" + show.getArena()).
                setMessage(" " + show.getZones().
                        toString().replace("[", "").replace("]", "").replace(",", "")).
                setCancelable(true).
                setNegativeButton("חזרה", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (show.getFreeFromShow() > 0) {
            builder.setPositiveButton("לרכישה", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBtnBuyTicketsClicked();
                }
            });
        }
        builder.show();
    }
}
