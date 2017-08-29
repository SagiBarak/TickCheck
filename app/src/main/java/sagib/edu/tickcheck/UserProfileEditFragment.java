package sagib.edu.tickcheck;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.aprilapps.easyphotopicker.EasyImage;

public class UserProfileEditFragment extends Fragment {


    private static final int RC_WRITE = 1;
    @BindView(R.id.etDisplayName)
    EditText etDisplayName;
    @BindView(R.id.tilDisplayName)
    TextInputLayout tilDisplayName;
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.btnChangePhoto)
    BootstrapButton btnChangePhoto;
    @BindView(R.id.btnKeepChanges)
    BootstrapButton btnKeepChanges;
    Unbinder unbinder;
    Uri path;
    SharedPreferences prefs;
    String stringPath;
    String fileName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri photoUrl;
    File file;
    boolean isLimited;
    boolean photoChanged = false;
    @BindView(R.id.sBand)
    Switch sBand;
    @BindView(R.id.tvTimeLimitTitle)
    TextView tvTimeLimitTitle;
    @BindView(R.id.tvTimeMinutes)
    TextView tvTimeMinutes;
    @BindView(R.id.btnMinus)
    ImageView btnMinus;
    @BindView(R.id.btnPlus)
    ImageView btnPlus;
    @BindView(R.id.btnRestore)
    TextView btnRestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile_edit, container, false);
        unbinder = ButterKnife.bind(this, v);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int minutes = intent.getIntExtra("minutes", 5);
                if (minutes > 60) {
                    tvTimeMinutes.setText("מותאם\nאישית");
                    btnMinus.setColorFilter(Color.GRAY);
                    btnPlus.setColorFilter(Color.GRAY);
                } else
                    tvTimeMinutes.setText(String.valueOf(minutes));
            }
        };
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getContext());
        IntentFilter custom = new IntentFilter("CustomTime");
        mgr.registerReceiver(receiver, custom);
        Picasso.with(getContext()).load(user.getPhotoUrl()).into(ivProfilePhoto);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            etDisplayName.setText(user.getDisplayName());
        prefs = getContext().getSharedPreferences("BandSwitchBoolean", Context.MODE_PRIVATE);
        isLimited = prefs.getBoolean("islimited", true);
        int limitMinutes = prefs.getInt("Minutes", 5);
        if (limitMinutes == 1) {
            btnMinus.setColorFilter(Color.rgb(15, 89, 228));
        }
        if (!isLimited) {
            tvTimeLimitTitle.setVisibility(View.GONE);
            tvTimeMinutes.setVisibility(View.GONE);
            btnMinus.setVisibility(View.GONE);
            btnPlus.setVisibility(View.GONE);
            btnRestore.setVisibility(View.GONE);
        } else {
            if (limitMinutes > 60) {
                tvTimeMinutes.setText("מותאם\nאישית");
                btnMinus.setColorFilter(Color.GRAY);
                btnPlus.setColorFilter(Color.GRAY);
            } else
                tvTimeMinutes.setText(String.valueOf(limitMinutes));
        }
        sBand.setChecked(isLimited);
        tvTimeMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTimeLimit customTimeLimit = new CustomTimeLimit();
                customTimeLimit.show(getChildFragmentManager(), "CustomTimeLimit");
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnChangePhoto)
    public void onBtnChangePhotoClicked() {
        if (!checkStoragePermission()) return;
        EasyImage.openChooserWithGallery(this, "בחירת תמונה", 0);


    }

    @OnClick(R.id.btnKeepChanges)
    public void onBtnKeepChangesClicked() {
        btnChangePhoto.setOnClickListener(null);
        btnChangePhoto.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
        btnChangePhoto.setText("מעדכן...");
        btnKeepChanges.setOnClickListener(null);
        btnKeepChanges.setVisibility(View.GONE);
        etDisplayName.setInputType(InputType.TYPE_NULL);
        String newDisplayName = etDisplayName.getText().toString();
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("displayName").setValue(newDisplayName);
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(newDisplayName);
        user.updateProfile(builder.build());
        if (photoChanged) {
            StorageReference storageReference = storage.getReference().child("ProfilePictures").child(fileName);
            UploadTask uploadTask = storageReference.putFile(Uri.parse(stringPath));
            final DatabaseReference child = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("profileImage");
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photoUrl = taskSnapshot.getDownloadUrl();
                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                    builder.setPhotoUri(photoUrl);
                    user.updateProfile(builder.build());
                    endTheChange(child);
                }
            });
        } else {
            btnChangePhoto.setOnClickListener(null);
            btnChangePhoto.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
            btnChangePhoto.setText("הנתונים השתנו!");
            btnKeepChanges.setOnClickListener(null);
            btnKeepChanges.setVisibility(View.GONE);
            etDisplayName.setInputType(InputType.TYPE_NULL);
        }
    }

    private void endTheChange(DatabaseReference child) {
        child.setValue(photoUrl.toString());
        btnChangePhoto.setOnClickListener(null);
        btnChangePhoto.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
        btnChangePhoto.setText("הנתונים השתנו!");
        btnKeepChanges.setOnClickListener(null);
        btnKeepChanges.setVisibility(View.GONE);
        etDisplayName.setInputType(InputType.TYPE_NULL);
        Intent intent = new Intent("UpdatePhoto");
        intent.putExtra("uri", photoUrl.toString());
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getContext());
        mgr.sendBroadcast(intent);
    }

    private boolean checkStoragePermission() {
        int resultcode = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean granted = resultcode == PackageManager.PERMISSION_GRANTED;
        if (!granted) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_WRITE);
        }
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_WRITE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openChooserWithGallery(this, "בחירת תמונה", 0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

                }

                @Override
                public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                    file = imageFiles.get(0);
                    Picasso.with(getContext()).load(file).into(ivProfilePhoto);
                    path = Uri.fromFile(file);
                    stringPath = path.toString();
                    fileName = file.getName();
                    photoChanged = true;
                }

                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {

                }
            });
        }
    }

    @OnCheckedChanged(R.id.sBand)
    public void onCheckSBandChanged() {
        boolean currentStatus = sBand.isChecked();
        prefs.edit().putBoolean("islimited", currentStatus).commit();
        if (!currentStatus) {
            tvTimeLimitTitle.setVisibility(View.GONE);
            tvTimeMinutes.setVisibility(View.GONE);
            btnMinus.setVisibility(View.GONE);
            btnPlus.setVisibility(View.GONE);
            btnRestore.setVisibility(View.GONE);
        } else {
            tvTimeLimitTitle.setVisibility(View.VISIBLE);
            tvTimeMinutes.setVisibility(View.VISIBLE);
            btnMinus.setVisibility(View.VISIBLE);
            btnPlus.setVisibility(View.VISIBLE);
            btnRestore.setVisibility(View.VISIBLE);
            int limitMinutes = prefs.getInt("Minutes", 5);
            if (limitMinutes > 60) {
                tvTimeMinutes.setText("מותאם\nאישית");
            } else {
                tvTimeMinutes.setText(String.valueOf(limitMinutes));
                int minutes = Integer.valueOf(tvTimeMinutes.getText().toString()) - 1;
                if (minutes - 2 < 0) {
                    btnMinus.setColorFilter(Color.GRAY);
                }
            }
        }
    }

    @OnClick(R.id.btnMinus)
    public void onBtnMinusClicked() {
        if (tvTimeMinutes.getText().equals("מותאם\nאישית")) {
            onBtnRestoreClicked();
            btnPlus.setColorFilter(Color.rgb(15, 89, 228));
        } else {
            int minutes = Integer.valueOf(tvTimeMinutes.getText().toString()) - 1;
            if (minutes > 0) {
                tvTimeMinutes.setText(String.valueOf(minutes));
                prefs.edit().putInt("Minutes", Integer.valueOf(tvTimeMinutes.getText().toString())).commit();
            }
            if (minutes - 2 < 0) {
                btnMinus.setColorFilter(Color.GRAY);
            }
        }
    }

    @OnClick(R.id.btnPlus)
    public void onBtnPlusClicked() {
        if (tvTimeMinutes.getText().equals("מותאם\nאישית")) {
            onBtnRestoreClicked();
            btnPlus.setColorFilter(Color.rgb(15, 89, 228));
        } else {
            int minutes = Integer.valueOf(tvTimeMinutes.getText().toString()) + 1;
            tvTimeMinutes.setText(String.valueOf(minutes));
            prefs.edit().putInt("Minutes", Integer.valueOf(tvTimeMinutes.getText().toString())).commit();
            if (minutes - 1 > 0) {
                btnMinus.setColorFilter(Color.rgb(15, 89, 228));
            }
        }
    }

    @OnClick(R.id.btnRestore)
    public void onBtnRestoreClicked() {
        tvTimeMinutes.setText(String.valueOf(1));
        prefs.edit().putInt("Minutes", Integer.valueOf(tvTimeMinutes.getText().toString())).commit();
        btnMinus.setColorFilter(Color.GRAY);
        btnPlus.setColorFilter(Color.rgb(15, 89, 228));
    }
}
