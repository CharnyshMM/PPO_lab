package com.example.mikita.ppo_lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.mikita.ppo_lab.storage.AvatarRepository;
import com.example.mikita.ppo_lab.storage.OnProgressListener;
import com.example.mikita.ppo_lab.storage.UserDM;
import com.example.mikita.ppo_lab.storage.UserRepository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnLoginFragmentSignInClickListener
{


    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    private static final int RC_PICK_IMAGE_REQUEST = 1234;

    private FirebaseAuth mAuth;
    private NavController navController;
    private BottomNavigationView navigationView;
    private FirebaseUser firebaseUser;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.my_nav_hos_f);
        navigationView = (BottomNavigationView) findViewById(R.id.main__bottom_navigation_view);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (navController.getCurrentDestination().getId() == R.id.editProfileFragment) {
                    askAndNavigateToFragment(menuItem.getItemId());
                } else {
                    navController.navigate(menuItem.getItemId());
                }

                return false;
            }
        });

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            NavOptions.Builder navBuilder = new NavOptions.Builder();
            NavOptions navOptions = navBuilder.setClearTask(true).build();
            if(navController.getCurrentDestination().getId() == R.id.loginFragment) {
                navController.navigate(R.id.profileFragment, null, navOptions);
            }
        } else {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_menu__action_about:
                if (navController.getCurrentDestination().getId() == R.id.editProfileFragment) {
                    askAndNavigateToFragment(R.id.aboutFragment);
                } else {
                    navController.navigate(R.id.aboutFragment);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                NavOptions.Builder navBuilder = new NavOptions.Builder();
                NavOptions navOptions = navBuilder.setClearTask(true).build();
                if (response.isNewUser()) {
                    navController.navigate(R.id.editProfileFragment, null, navOptions );
                } else {
                    if(navController.getCurrentDestination().getId() == R.id.loginFragment) {
                        navController.navigate(R.id.profileFragment, null, navOptions);
                    }
                }
                navigationView.setVisibility(View.VISIBLE);
            } else {

                navigationView.setVisibility(View.GONE);
                navController.navigate(R.id.loginFragment);

                if (response == null) {
                    Toast.makeText(this, R.string.why_did_you_pressed_back, Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG);
                    return;
                }

                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG);


                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void askAndNavigateToFragment(final int fragmentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.youre_about_to_loose_changes)
                .setPositiveButton(R.string.leave, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        navController.navigate(fragmentId);
                    }
                })
                .setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() == R.id.editProfileFragment) {
            askAndNavigateToFragment(R.id.profileFragment);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoginFragmentSignInClick() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
    }


}

