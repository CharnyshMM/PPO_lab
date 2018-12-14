package com.example.mikita.ppo_lab;

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

import com.example.mikita.ppo_lab.storage.AvatarRepository;
import com.example.mikita.ppo_lab.storage.UserDM;
import com.example.mikita.ppo_lab.storage.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.File;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ProfileFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button editButton;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView nameTextView;
    private TextView surnameTextView;
    private ImageView avatarView;

    private AvatarRepository.OnAvatarDownloadedListener onAvatarDownloadedListener;
    private UserRepository.OnUserDMUpdatedListener onUserDMUpdatedListener;

    //private OnProgressListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                e = e;
            }
        };
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


//    public interface OnProgressListener {
//        // TODO: Update argument type and name
//        void onProgressStarted();
//        void onProgressFinished();
//    }

}
