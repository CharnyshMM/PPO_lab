package com.example.mikita.ppo_lab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener, FavoritesFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavController navController = Navigation.findNavController(this, R.id.my_nav_hos_f);
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.main__bottom_navigation_view);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
