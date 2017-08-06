package sagib.edu.tickcheck;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MyShowOptionsFragment extends BottomSheetDialogFragment {
    MyShow mShow;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvPerformer)
    TextView tvPerformer;
    @BindView(R.id.tvArena)
    TextView tvArena;
    @BindView(R.id.tvDayDateTime)
    TextView tvDayDateTime;
    @BindView(R.id.btnNavigate)
    BootstrapButton btnNavigate;
    @BindView(R.id.btnAddToCalendar)
    BootstrapButton btnAddToCalendar;
    @BindView(R.id.btnRemoveFromList)
    BootstrapButton btnRemoveFromList;
    String uri;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_show_options, container, false);
        mShow = getArguments().getParcelable("myShow");
        unbinder = ButterKnife.bind(this, v);
        Picasso.with(getContext()).load(mShow.getImage()).into(ivImage);
        tvPerformer.setText(mShow.getPerformer());
        tvArena.setText(mShow.getArena());
        tvDayDateTime.setText(mShow.getDateTime());
        uri = "";
        getLocationOfArena(mShow);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Date date = LocalDate.parse(mShow.getDate(), formatter).toDate();
        if (date.before(LocalDate.now().toDate())) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            ivImage.setColorFilter(filter);
            btnNavigate.setVisibility(View.GONE);
            btnAddToCalendar.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnNavigate)
    public void onBtnNavigateClicked() {
        getContext().startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
        dismiss();
    }

    @OnClick(R.id.btnAddToCalendar)
    public void onBtnAddToCalendarClicked() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        String time = mShow.getDateTime().substring(mShow.getDateTime().length() - 5, mShow.getDateTime().length());
        Date date = LocalDateTime.parse(mShow.getDate() + " " + time, formatter).toDate();
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Calendar endDateCal = Calendar.getInstance();
        endDateCal.setTime(date);
        endDateCal.add(Calendar.HOUR_OF_DAY, 3);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, mShow.getPerformer())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, mShow.getArena())
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateCal.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateCal.getTimeInMillis());
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnRemoveFromList)
    public void onBtnRemoveFromListClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("הסרה מהרשימה").setMessage("האם ברצונך למחוק את ההופעה מרשימת ״ההופעות שלי״?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference("MyShows").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mShow.getMyShowUID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseDatabase.getInstance().getReference("MyShowsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mShow.getEventID()).removeValue();
                        Toast.makeText(getContext(), "ההופעה נמחקה מהרשימה!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
                dialog.dismiss();
                Intent intent = new Intent("ItemRemoved");
                intent.putExtra("model", mShow);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
        }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dismiss();
            }
        }).show();

    }

    private void getLocationOfArena(MyShow mShow) {
        uri = "geo:?q=" + mShow.getArena();
    }
}
