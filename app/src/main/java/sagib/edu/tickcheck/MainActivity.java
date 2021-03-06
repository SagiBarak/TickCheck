package sagib.edu.tickcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import sagib.edu.tickcheck.models.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser user;
    SharedPreferences prefs;
    NavigationView navigationView;
    SharedPreferences tokenprefs;
    String performer = "";
    boolean loadFUI = true;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String uri = intent.getStringExtra("uri");
            Picasso.with(MainActivity.this).load(Uri.parse(uri)).into(civProfileImage);
        }
    };
    LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this);

    private static final int RC_SIGN_IN = 0;

    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();
            if (user == null && loadFUI) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.
                                Builder(AuthUI.GOOGLE_PROVIDER).
                                setPermissions(
                                        Arrays.asList(Scopes.PROFILE, Scopes.EMAIL)).
                                build(),
                        new AuthUI.IdpConfig.
                                Builder(AuthUI.EMAIL_PROVIDER).
                                build(),
                        new AuthUI.IdpConfig.
                                Builder(AuthUI.FACEBOOK_PROVIDER).
                                build()
                );
                loadFUI = false;
                Intent intent = AuthUI.getInstance().
                        createSignInIntentBuilder().
                        setLogo(R.drawable.biglogo).
                        setAvailableProviders(providers).build();
                startActivityForResult(intent, RC_SIGN_IN);
            } else if (user == null){
                finish();
                System.exit(0);
            }
            else {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View header = navigationView.getHeaderView(0);
                user = mAuth.getCurrentUser();
                tvHeaderContentBar = (TextView) header.findViewById(R.id.tvHeaderContentBar);
                tvHeaderTitleBar = (TextView) header.findViewById(R.id.tvHeaderTitleBar);
                if (user != null) {
                    tvHeaderContentBar.setText(user.getEmail());
                    tvHeaderTitleBar.setText(user.getDisplayName());
                    if (!tvHeaderTitleBar.getText().toString().matches("^[a-zA-Z0-9.]+$"))
                        tvHeaderTitleBar.setGravity(GravityCompat.END);
                }
            }
        }
    };
    TextView tvHeaderContentBar;
    TextView tvHeaderTitleBar;
    CircularImageView civProfileImage;
    Toolbar toolbar;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            User user = new User(currentUser);
            if (currentUser != null) {
                if (currentUser.getPhotoUrl() == null) {
                    UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                    Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tickcheck-2bdf2.appspot.com/o/ProfilePictures%2Fdefault_profile.jpg?alt=media&token=72b274a4-8a84-446f-ade4-dfafb3c8c06c");
                    request.setPhotoUri(uri);
                    currentUser.updateProfile(request.build());
                    Picasso.with(this).load(uri).into(civProfileImage);
                } else {
                    Picasso.with(this).load(currentUser.getPhotoUrl()).into(civProfileImage);
                }
            }
            String token = FirebaseInstanceId.getInstance().getToken();
            SharedPreferences prefs = getSharedPreferences("id", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("token", token);
            editor.commit();
            user.setToken(token);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            ref.setValue(user);
            runMyShows();
        }
        if (requestCode != RC_SIGN_IN) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().getPhotoUrl() == null) {
                UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tickcheck-2bdf2.appspot.com/o/ProfilePictures%2Fdefault_profile.jpg?alt=media&token=72b274a4-8a84-446f-ade4-dfafb3c8c06c");
                request.setPhotoUri(uri);
                mAuth.getCurrentUser().updateProfile(request.build());
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = getSharedPreferences("DefaultPerformer", Context.MODE_PRIVATE);
        performer = prefs.getString("PerformerTitle", "שלמה ארצי");
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        user = mAuth.getCurrentUser();
        tokenprefs = getSharedPreferences("id", MODE_PRIVATE);
        tvHeaderContentBar = (TextView) header.findViewById(R.id.tvHeaderContentBar);
        tvHeaderTitleBar = (TextView) header.findViewById(R.id.tvHeaderTitleBar);
        civProfileImage = (CircularImageView) header.findViewById(R.id.civProfileImage);
        if (user != null) {
            tvHeaderContentBar.setText(user.getEmail());
            tvHeaderTitleBar.setText(user.getDisplayName());
            Picasso.with(this).load(user.getPhotoUrl()).into(civProfileImage);
            runMyShows();
        }
        View.OnClickListener goToProfileSettings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBackStack();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserProfileEditFragment(), "EditProfile").commit();
                toolbar.setTitle("עריכת משתמש");
                navigationView.setCheckedItem(R.id.nav_editprofile);
                drawer.closeDrawer(GravityCompat.START);
            }
        };
        civProfileImage.setOnClickListener(goToProfileSettings);
        tvHeaderTitleBar.setOnClickListener(goToProfileSettings);
        tvHeaderContentBar.setOnClickListener(goToProfileSettings);
        BroadcastReceiver getToken = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = intent.getStringExtra("token");
                User normalUser = new User(user);
                normalUser.setToken(token);
                FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).setValue(normalUser);
            }
        };
        IntentFilter intentFilter = new IntentFilter("SendToken");
        LocalBroadcastManager.getInstance(this).registerReceiver(getToken, intentFilter);
    }

    private void runMyShows() {
        String token = tokenprefs.getString("token", null);
        if (token != null) {
            User normalUser = new User(user);
            normalUser.setToken(token);
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).setValue(normalUser);
        }
        if (getFragmentManager().findFragmentById(R.id.frame) == null) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyShowsListFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_myshows);
            if (mAuth.getCurrentUser() != null) {
                if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                    Picasso.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(civProfileImage);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("האם ברצונך לצאת מהאפליקציה?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setCancelable(true).show();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_shows) {
            if (!toolbar.getTitle().toString().equals("רכישת כרטיסים")) {
                clearBackStack();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ShowsFragment(), "Shows").commit();
                toolbar.setTitle("רשימת הופעות של " + performer);
            } else {
                onBackPressed();
            }
        } else if (id == R.id.nav_showsbydate) {
            if (!toolbar.getTitle().toString().equals("רכישת כרטיסים")) {
                clearBackStack();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ShowsByDateFragment(), "ShowsByDate").commit();
                toolbar.setTitle("הופעות לפי תאריך");
            } else {
                onBackPressed();
            }
        } else if (id == R.id.nav_board) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new NewBoardFragment()).commit();
            toolbar.setTitle("לוח מכירת כרטיסים");
        } else if (id == R.id.nav_myshows) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyShowsListFragment()).commit();
            toolbar.setTitle("ההופעות שלי");
            toolbar.setTitle("ההופעות שלי");
        } else if (id == R.id.nav_privatechats) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new PrivateChatsListFragment()).commit();
            toolbar.setTitle("שיחות פרטיות");
        }  else if (id == R.id.nav_editprofile) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserProfileEditFragment(), "EditProfile").commit();
            toolbar.setTitle("עריכת משתמש");
        } else if (id == R.id.nav_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("אפליקציה זו פותחה כפרוייקט לימודי ואינה למטרות רווח.\nצריכת נתונים - כ-500~ ק״ב עבור 10 הופעות (ברשימת ההופעות עבור אמן או תאריך).");
            builder.setPositiveButton("חזרה", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else if (id == R.id.nav_signout) {
            clearBackStack();
            prefs.edit().clear().commit();
            tokenprefs.edit().clear().commit();
            getSharedPreferences("ShowsDate", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("BandSwitchBoolean", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("DefaultPerformer", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("SearchForPost", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("ShowsDate", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("BandSwitchBoolean", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("showslist", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("ShowIntro", MODE_PRIVATE).edit().clear().commit();
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tickcheck-2bdf2.appspot.com/o/ProfilePictures%2Fdefault_profile.jpg?alt=media&token=72b274a4-8a84-446f-ade4-dfafb3c8c06c");
            Picasso.with(this).load(uri).into(civProfileImage);
            mAuth.signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mgr.registerReceiver(mBroadcastReceiver, new IntentFilter("UpdatePhoto"));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        mgr.unregisterReceiver(mBroadcastReceiver);
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
}
