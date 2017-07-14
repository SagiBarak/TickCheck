package sagib.edu.tickcheck;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.aprilapps.easyphotopicker.EasyImage;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileEditFragment extends Fragment {


    private static final int RC_WRITE = 1;
    @BindView(R.id.tvDisplayNameTitle)
    TextView tvDisplayNameTitle;
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
    String stringPath;
    String fileName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri photoUrl;
    File file;
    boolean photoChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile_edit, container, false);
        unbinder = ButterKnife.bind(this, v);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        etDisplayName.setText(user.getDisplayName());
        Picasso.with(getContext()).load(user.getPhotoUrl()).into(ivProfilePhoto);
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
                    // Handle unsuccessful uploads
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
    }

    private boolean checkStoragePermission() {
        int resultcode = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean granted = resultcode == PackageManager.PERMISSION_GRANTED;
        if (!granted) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_WRITE);
        }
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_WRITE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            onBtnChangePhotoClicked();
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
}
