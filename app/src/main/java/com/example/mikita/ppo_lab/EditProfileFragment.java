package com.example.mikita.ppo_lab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mikita.ppo_lab.storage.AvatarRepository;
import com.example.mikita.ppo_lab.storage.UserDM;
import com.example.mikita.ppo_lab.storage.UserRepository;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfileFragment.OnAvatarImageClickListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int RC_PICK_IMAGE_REQUEST = 1234;

    private Button saveButton;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private ImageView avatarView;

    private UserDM userDM;

    private Uri selectedAvatarUri;

    private OnAvatarImageClickListener mListener;

    private AvatarRepository.OnAvatarDownloadedListener onAvatarDownloadedListener;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", nameEditText.getText().toString());
        outState.putString("surname", surnameEditText.getText().toString());
        outState.putString("email", emailEditText.getText().toString());
        outState.putString("phone", phoneEditText.getText().toString());
        outState.putString("selectedAvatarUri", selectedAvatarUri == null? null : selectedAvatarUri.toString());
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton = (Button) view.findViewById(R.id.editProfile__save_button);
        emailEditText = (EditText) view.findViewById(R.id.editProfile__email_textView);
        nameEditText = (EditText) view.findViewById(R.id.editProfile__name_editText);
        surnameEditText = (EditText) view.findViewById(R.id.editProfile__surname_editText);
        phoneEditText = (EditText) view.findViewById(R.id.editProfile__phone_textView);
        avatarView = (ImageView) view.findViewById(R.id.editProfile__avatarView);

        userDM = UserRepository.getInstance().getUser();
        if (userDM == null) {
            userDM = new UserDM();
        }

        selectedAvatarUri = null;

        if (savedInstanceState == null) {
            nameEditText.setText(userDM.getName());
            surnameEditText.setText(userDM.getSurname());
            emailEditText.setText(userDM.getEmail());
            phoneEditText.setText(userDM.getPhone());

            setFileAsAvatar(AvatarRepository.getInstance().getAvatarFile());
        } else {
            nameEditText.setText(savedInstanceState.getString("name"));
            surnameEditText.setText(savedInstanceState.getString("surname"));
            emailEditText.setText(savedInstanceState.getString("email"));
            phoneEditText.setText(savedInstanceState.getString("phone"));
            String avatarUri = savedInstanceState.getString("selectedAvatarUri");
            if (avatarUri != null) {
                selectedAvatarUri = Uri.parse(avatarUri);
            }
        }
        if (selectedAvatarUri != null) {
            avatarView.setImageURI(selectedAvatarUri);
        } else {
            setFileAsAvatar(AvatarRepository.getInstance().getAvatarFile());
        }
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



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDM userDM = new UserDM();
                userDM.setName(nameEditText.getText().toString());
                userDM.setSurname(surnameEditText.getText().toString());
                userDM.setEmail(emailEditText.getText().toString());
                userDM.setPhone(phoneEditText.getText().toString());
                UserRepository.getInstance().setUser(userDM);

                if (selectedAvatarUri != null) {
                    AvatarRepository.getInstance().setAvatar(selectedAvatarUri);
                }

                Navigation.findNavController(view).navigate(R.id.profileFragment);
            }
        });

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE_REQUEST);
            }
        });
     }

    private void setFileAsAvatar(File file) {
        if (file == null)
            return;
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        avatarView.setImageBitmap(myBitmap);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAvatarImageClick();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAvatarImageClickListener) {
            mListener = (OnAvatarImageClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        AvatarRepository.getInstance().removeOnAvatarDownloadedListener(onAvatarDownloadedListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedAvatarUri = data.getData();
            avatarView.setImageURI(selectedAvatarUri);
        }
    }

    public interface OnAvatarImageClickListener {
        void onAvatarImageClick();
    }
}
