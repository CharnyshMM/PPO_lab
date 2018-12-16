package com.example.mikita.ppo_lab;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikita.ppo_lab.storage.AvatarRepository;
import com.example.mikita.ppo_lab.storage.OnProgressListener;
import com.example.mikita.ppo_lab.storage.UserDM;
import com.example.mikita.ppo_lab.storage.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.File;


public class ProfileFragment extends Fragment implements OnProgressListener {

    private Button editButton;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView nameTextView;
    private TextView surnameTextView;
    private ImageView avatarView;
    private ProgressDialog progressDialog;

    private AvatarRepository.OnAvatarDownloadedListener onAvatarDownloadedListener;
    private UserRepository.OnUserDMUpdatedListener onUserDMUpdatedListener;

    //private OnProgressListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        editButton = view.findViewById(R.id.profile__edit_button);
        nameTextView = view.findViewById(R.id.profile__name_textView);
        surnameTextView = view.findViewById(R.id.profile__surname_textView);
        emailTextView = view.findViewById(R.id.profile__email_textView);
        phoneTextView = view.findViewById(R.id.profile__phone_textView);
        avatarView = view.findViewById(R.id.profile__avatarView);

        editButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_editProfileFragment));

        UserRepository rep = UserRepository.getInstance();
        setUI(rep.getUser());
        setFileAsAvatar(AvatarRepository.getInstance().getAvatarFile());

        onUserDMUpdatedListener = new UserRepository.OnUserDMUpdatedListener() {
            @Override
            public void OnUserUpdated(UserDM user) {
                setUI(user);
            }
        };
        rep.addOnUserDMUpdatedListener(onUserDMUpdatedListener);

        onAvatarDownloadedListener = new AvatarRepository.OnAvatarDownloadedListener() {
            @Override
            public void onAvatarDownloaded(File avatar) {
                setFileAsAvatar(avatar);
            }

            @Override
            public void onAvatarDownloadFailure(Exception e) {
                Toast.makeText(getContext(), "Error downloading avatar", Toast.LENGTH_SHORT).show();
            }
        };
        AvatarRepository.getInstance().addOnProgressListener(this);
        AvatarRepository.getInstance().addOnAvatarDownloadedListener(onAvatarDownloadedListener);
    }

    private void setUI(UserDM user) {
        if (user == null) {
            return;
        }
        nameTextView.setText(user.getName());
        surnameTextView.setText(user.getSurname());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnProgressListener) {
//            mListener = (OnProgressListener) context;
//        }
//        else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnProgressListener");
//        }
    }

    private void setFileAsAvatar(File file) {
        if (file == null)
            return;
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        avatarView.setImageBitmap(myBitmap);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
        AvatarRepository.getInstance().removeOnAvatarDownloadedListener(onAvatarDownloadedListener);
        UserRepository.getInstance().removeOnUserDMUpdatedListener(onUserDMUpdatedListener);
    }


    @Override
    public void onProgressStarted() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
        }
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onProgressEnded() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

}
