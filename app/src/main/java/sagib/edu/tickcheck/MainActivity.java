package sagib.edu.tickcheck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser user;
    SharedPreferences prefs;
    SharedPreferences tokenprefs;
    String performer = "";

    private static final int RC_SIGN_IN = 0;

    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();
            if (user == null) {
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
                Intent intent = AuthUI.getInstance().
                        createSignInIntentBuilder().
                        setLogo(R.drawable.biglogo).
                        setAvailableProviders(providers).build();
                startActivityForResult(intent, RC_SIGN_IN);
            } else {
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
                }
            }
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
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference boardRef = FirebaseDatabase.getInstance().getReference("Board");
        boardRef.keepSynced(true);
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference myshowsRef = FirebaseDatabase.getInstance().getReference("MyShowsList").child(mAuth.getCurrentUser().getUid());
            myshowsRef.keepSynced(true);
            DatabaseReference privatechatsListRef = FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(mAuth.getCurrentUser().getUid());
            privatechatsListRef.keepSynced(true);
        }
        MobileAds.initialize(this, "ca-app-pub-7962012481002515~8641009187");
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
            String token = tokenprefs.getString("token", null);
            if (token != null) {
                User normalUser = new User(user);
                normalUser.setToken(token);
                FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).setValue(normalUser);
                FirebaseMessaging.getInstance().subscribeToTopic("topic");
            }
        }
    }

    private void runMyShows() {
        if (getFragmentManager().findFragmentById(R.id.frame) == null) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyShowsListFragment()).commit();
            toolbar.setTitle("ההופעות שלי");
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
            builder.setTitle("יציאה מהאפליקציה").setMessage("האם ברצונך לצאת מהאפליקציה?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            mAuth.signOut();
            return true;
        }
        if (id == R.id.action_default) {
            DefaultPerformerFragment defaultPerformerFragment = new DefaultPerformerFragment();
            defaultPerformerFragment.show(getSupportFragmentManager(), "Choose");
            return true;
        }

        if (id == R.id.action_about) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("אודות");
            dialog.setMessage("אפליקציה זו נוצרה במקור עבור הקהל הרחב של שלמה ארצי.\nמכיוון שכרטיס להופעה הפך להיות נדיר (עקב הביקוש הרב), החלטתי להקל על תהליך הבדיקה של זמינות הכרטיסים ולאחד את הפעולה הזו ביחד עם לוח מכירת כרטיסים.\nבשלב השני, נוספה האפשרות לבדיקת כרטיסים עבור אמנים נוספים.\nשימו לב!\nמומלץ להשתמש באפליקציה באמצעות חיבור WiFi על מנת לחסוך בחבילת הגלישה.\n תהנו!");
            dialog.setCancelable(false);
            dialog.setNeutralButton("חזרה", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
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
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new BoardFragment()).commit();
            toolbar.setTitle("פורום מכירת כרטיסים");
        } else if (id == R.id.nav_myshows) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyShowsListFragment()).commit();
            toolbar.setTitle("ההופעות שלי");
        } else if (id == R.id.nav_privatechats) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new PrivateChatsListFragment()).commit();
            toolbar.setTitle("שיחות פרטיות");
        } else if (id == R.id.nav_chooseperformer) {
            clearBackStack();
            DefaultPerformerFragment defaultPerformerFragment = new DefaultPerformerFragment();
            defaultPerformerFragment.show(getSupportFragmentManager(), "Choose");
        } else if (id == R.id.nav_editprofile) {
            clearBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserProfileEditFragment(), "EditProfile").commit();
            toolbar.setTitle("עריכת משתמש");
        } else if (id == R.id.nav_signout) {
            clearBackStack();
            mAuth.signOut();
            Intent intent = new Intent(this, WelcomeSplash.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
}
