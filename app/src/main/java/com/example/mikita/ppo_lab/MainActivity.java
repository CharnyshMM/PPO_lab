package com.example.mikita.ppo_lab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements OnProgressListener,
        NewsFragment.OnFragmentInteractionListener, FavoritesFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnAvatarImageClickListener, AboutFragment.OnFragmentInteractionListener,
        LoginFragment.OnLoginFragmentSignInClickListener
{


    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    private static final int RC_PICK_IMAGE_REQUEST = 1234;

    private FirebaseAuth mAuth;
    private NavController navController;
    private BottomNavigationView navigationView;
    private FirebaseUser firebaseUser;

    //private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //progressBar = findViewById(R.id.main__topProgressBar);
        navController = Navigation.findNavController(this, R.id.my_nav_hos_f);
        navigationView = (BottomNavigationView) findViewById(R.id.main__bottom_navigation_view);
        NavigationUI.setupWithNavController(navigationView, navController);

        AvatarRepository.getInstance().addOnProgressListener(this);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            NavOptions.Builder navBuilder = new NavOptions.Builder();
            NavOptions navOptions = navBuilder.setClearTask(true).build();
            navController.navigate(R.id.profileFragment, null, navOptions);
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
                  navController.navigate(R.id.aboutFragment);
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
                navController.navigate(R.id.profileFragment, null, navOptions);
                navigationView.setVisibility(View.VISIBLE);
            } else {

                navigationView.setVisibility(View.GONE);
                navController.navigate(R.id.loginFragment);

                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "shit, why did you pressed back???", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "shit, NO NETWORK ERROR.", Toast.LENGTH_LONG);
                    return;
                }

                Toast.makeText(this, "unknown error", Toast.LENGTH_LONG);


                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAvatarImageClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                RC_PICK_IMAGE_REQUEST);
    }

    @Override
    public void onLoginFragmentSignInClick() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    @Override
    public void onProgressStarted() {
//        progressBar.setVisibility(View.VISIBLE);
//        progressBar.bringToFront();
//        progressBar.setIndeterminate(true);
//        progressBar.animate();
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressEnded() {
//        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Loading finished", Toast.LENGTH_SHORT).show();
    }
}

