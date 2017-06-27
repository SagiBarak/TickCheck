package sagib.edu.tickcheck;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser user;

    private static final int RC_SIGN_IN = 1;

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
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyShowsListFragment()).commit();
                }
            }
        }
    };
    TextView tvHeaderContentBar;
    TextView tvHeaderTitleBar;
    Toolbar toolbar;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                //0. create the user
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                //save the user.
                User user = new User(currentUser);
                // 1. ref the table.
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                // 2. push()... setValue.
                ref.setValue(user);

            }
            //TODO: else if (idpResponse != null && idpResponse.getError)
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MainFragment()).commit();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        user = mAuth.getCurrentUser();
        tvHeaderContentBar = (TextView) header.findViewById(R.id.tvHeaderContentBar);
        tvHeaderTitleBar = (TextView) header.findViewById(R.id.tvHeaderTitleBar);
        if (user != null) {
            tvHeaderContentBar.setText(user.getEmail());
            tvHeaderTitleBar.setText(user.getDisplayName());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
            }).setCancelable(false).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_refresh).setVisible(false);
        String tag = getSupportFragmentManager().findFragmentById(R.id.frame).getTag();
        if (tag != null) {
            menu.findItem(R.id.action_refresh).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            mAuth.signOut();
            return true;
        }

        if (id == R.id.action_refresh) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ShowsFragment()).commit();
        }

        if (id == R.id.action_about) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("אודות");
            dialog.setMessage("אפליקציה זו נוצרה ע״י שגיא ברק, עבור הקהל הרחב של שלמה ארצי.\nמכיוון שכרטיס להופעה הפך להיות נדיר (עקב הביקוש הרב), החלטתי להקל על תהליך הבדיקה של זמינות הכרטיסים ולאחד את הפעולה הזו ביחד עם לוח מכירת כרטיסים.\n שימו לב!\nמומלץ להשתמש באפליקציה באמצעות חיבור WiFi על מנת לחסוך בחבילת הגלישה.\n תהנו!");
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shows) {
            this.invalidateOptionsMenu();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ShowsFragment(), "Shows").commit();
            this.invalidateOptionsMenu();

        } else if (id == R.id.nav_board) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new BoardFragment()).commit();
            this.invalidateOptionsMenu();

        } else if (id == R.id.nav_myshows) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyShowsListFragment()).commit();
            this.invalidateOptionsMenu();

        } else if (id == R.id.nav_signout) {
            this.invalidateOptionsMenu();
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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
