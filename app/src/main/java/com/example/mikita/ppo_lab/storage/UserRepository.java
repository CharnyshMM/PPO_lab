package com.example.mikita.ppo_lab.storage;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class UserRepository implements ValueEventListener {
    private static final String TAG = "UserRepository";

    private static UserRepository instance = null;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private UserDM user;

    private ArrayList<OnUserDMUpdatedListener> userDMUpdatedListeners;
    private ArrayList<OnProgressListener> progressListeners;

    private UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.setPersistenceEnabled(true);
        databaseReference = db.getReference();

        firebaseUser = firebaseAuth.getCurrentUser();
        user = new UserDM();
        databaseReference.child("users").child(firebaseUser.getUid()).addValueEventListener(this);
        userDMUpdatedListeners = new ArrayList<OnUserDMUpdatedListener>();
        progressListeners = new ArrayList<>();
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public UserDM getUser() {
        return user;
    }

    public void setUser(UserDM user){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.e(TAG, "user is not authorized when saving profile !!!!!!!!!!!!!");
        }
        databaseReference.child("users").child(firebaseUser.getUid()).setValue(user);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(UserDM.class);
        notifyUserDMUpdated();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        databaseError = databaseError;
    }

    public interface OnUserDMUpdatedListener {
        void OnUserUpdated(UserDM user);
    }

    private void notifyUserDMUpdated() {
        for (OnUserDMUpdatedListener listener:userDMUpdatedListeners) {
            listener.OnUserUpdated(this.user);
        }
    }

    public void addOnUserDMUpdatedListener(OnUserDMUpdatedListener listener){
        if (!userDMUpdatedListeners.contains(listener)) {
            userDMUpdatedListeners.add(listener);
        }
    }

    public void removeOnUserDMUpdatedListener(OnUserDMUpdatedListener listener) {
        userDMUpdatedListeners.remove(listener);
    }

    public void addOnProgressListener(OnProgressListener listener) {
        if (!progressListeners.contains(listener)) {
            progressListeners.add(listener);
        }
    }

    public void removeOnProgressListener(OnProgressListener listener) {
        progressListeners.remove(listener);
    }
}
