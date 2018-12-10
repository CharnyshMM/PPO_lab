package com.example.mikita.ppo_lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener, FavoritesFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnAvatarImageClickListener, AboutFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener
{


    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    private static final int RC_PICK_IMAGE_REQUEST = 1234;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private NavController navController;
    private BottomNavigationView navigationView;
    private FirebaseUser firebaseUser;
    private UserDM userDM;

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

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            navController.navigate(R.id.profileFragment);
            mDatabase = FirebaseDatabase.getInstance().getReference();
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
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                navController.navigate(R.id.profileFragment);
            } else {

                navigationView.setVisibility(View.GONE);
//              navController.navigate(R.id.loginFragment);

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
//        } else if (requestCode == RC_PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
//            AvatarRepository.getInstance().setAvatar(uri);
//        }
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
}

