package com.example.mikita.ppo_lab;

import android.app.Dialog;
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

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mikita.ppo_lab.storage.AvatarRepository;
import com.example.mikita.ppo_lab.storage.UserDM;
import com.example.mikita.ppo_lab.storage.UserRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    private static final int RC_PICK_IMAGE_REQUEST = 1234;
    private static final int RC_TAKE_PHOTO_REQUEST = 4321;

    private Button saveButton;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private ImageView avatarView;

    private UserDM userDM;

    private Uri selectedAvatarUri = null;
    private Bitmap takenAvatarBitmap = null;

    //private OnAvatarImageClickListener mListener;

    private AvatarRepository.OnAvatarDownloadedListener onAvatarDownloadedListener;

    public EditProfileFragment() {
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
        outState.putParcelable("takenAvatarBitmap", takenAvatarBitmap);
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

//            setFileAsAvatar(AvatarRepository.getInstance().getAvatarFile());
        } else {
            nameEditText.setText(savedInstanceState.getString("name"));
            surnameEditText.setText(savedInstanceState.getString("surname"));
            emailEditText.setText(savedInstanceState.getString("email"));
            phoneEditText.setText(savedInstanceState.getString("phone"));
            String avatarUri = savedInstanceState.getString("selectedAvatarUri");
            takenAvatarBitmap = savedInstanceState.getParcelable("takenAvatarBitmap");
            if (avatarUri != null) {
                selectedAvatarUri = Uri.parse(avatarUri);
            }
        }
        if (selectedAvatarUri != null) {
            avatarView.setImageURI(selectedAvatarUri);
        }else if (takenAvatarBitmap != null) {
            avatarView.setImageBitmap(takenAvatarBitmap);
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
                Toast.makeText(getContext(), "Error downloading", Toast.LENGTH_SHORT).show();
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
                } else if (takenAvatarBitmap != null) {
                    AvatarRepository.getInstance().setAvatar(takenAvatarBitmap);
                }

                Navigation.findNavController(view).navigate(R.id.profileFragment);
            }
        });

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarPhotoSourceSelectionDialog();
            }
        });
     }

    private void setFileAsAvatar(File file) {
        if (file == null)
            return;
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        avatarView.setImageBitmap(myBitmap);
    }

    private void showAvatarPhotoSourceSelectionDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.pick_photo_sourse_selection_dialog);
        dialog.setTitle(R.string.set_avatar_photo);
        Button makePhotoButton = dialog.findViewById(R.id.pickPhotoSourceSelectionDialog_takePhoto_button);
        makePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, RC_TAKE_PHOTO_REQUEST);
                dialog.hide();
            }
        });
        Button chooseExistingButton = dialog.findViewById(R.id.pickPhotoSourceSelectionDialog_chooseExisting_button);
        chooseExistingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), RC_PICK_IMAGE_REQUEST);
                dialog.hide();
            }
        });
        dialog.show();
    }



    @Override
    public void onDetach() {
        super.onDetach();
        AvatarRepository.getInstance().removeOnAvatarDownloadedListener(onAvatarDownloadedListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    selectedAvatarUri = data.getData();
                    avatarView.setImageURI(selectedAvatarUri);
                    takenAvatarBitmap = null;
                }
                break;
            case RC_TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    avatarView.setImageBitmap(imageBitmap);
                    takenAvatarBitmap = imageBitmap;
                    selectedAvatarUri = null;
                }
        }
    }
}
