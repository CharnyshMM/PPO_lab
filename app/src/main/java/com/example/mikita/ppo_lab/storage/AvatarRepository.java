package com.example.mikita.ppo_lab.storage;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class AvatarRepository {
    private static final String TAG = "AvatarRepository";
    private final String avatarFilename = "avatar";
    private final String avatarFileExtention = ".jpg";

    private static AvatarRepository instance;

    private StorageReference storageRef;

    private Uri downloadUrl;
    private File avatarFile;

    private ArrayList<OnAvatarUploadedListener> onAvatarUploadedListeners;
    private ArrayList<OnAvatarDownloadedListener> onAvatarDownloadedListeners;
    private ArrayList<OnProgressListener> progressListeners;

    private AvatarRepository(){
        storageRef = FirebaseStorage.getInstance().getReference();
        onAvatarDownloadedListeners = new ArrayList<>();
        onAvatarUploadedListeners = new ArrayList<>();
        progressListeners = new ArrayList<>();
        avatarFile = null;
        downloadUrl = null;
    }

    public static AvatarRepository getInstance()
    {
        if (instance == null){
            instance = new AvatarRepository();
            instance.downloadAvatar();
        }
        return instance;
    }

    public void setAvatar(Uri file) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            notifyOnAvatarDownloadedListenersAboutFailure(new FirebaseAuthException("not authorized", "user is not authorized"));
            return;
        }
        String uid = user.getUid();
        final StorageReference fileRef = storageRef.child(uid+"/"+avatarFilename+avatarFileExtention);
        notifyOnProgressListeners(true);
        fileRef.putFile(file).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                notifyOnProgressListeners(false);
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult();
                    downloadAvatar();

                } else {
                    // Handle failures
                }
            }
        });
    }

    public void setAvatar(Bitmap bitmap) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            notifyOnAvatarDownloadedListenersAboutFailure(new FirebaseAuthException("not authorized", "user is not authorized"));
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String uid = user.getUid();
        final StorageReference fileRef = storageRef.child(uid+"/"+avatarFilename+avatarFileExtention);
        notifyOnProgressListeners(true);
        fileRef.putBytes(data).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                notifyOnProgressListeners(false);
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult();
                    downloadAvatar();

                } else {
                    // Handle failures
                }
            }
        });
    }

    public File getAvatarFile() {
        return avatarFile;
    }

    public void downloadAvatar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            notifyOnAvatarDownloadedListenersAboutFailure(new FirebaseAuthException("not authorized", "user is not authorized"));
            return;
        }
        String uid = user.getUid();
        final StorageReference fileRef = storageRef.child( uid+"/"+avatarFilename+avatarFileExtention);
        try {
            final File localFile = File.createTempFile(avatarFilename, "jpg");
            notifyOnProgressListeners(true);
            fileRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            avatarFile = localFile;
                            notifyOnAvatarDownloadedListenersAboutSuccess(avatarFile);
                            notifyOnProgressListeners(false);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    notifyOnAvatarDownloadedListenersAboutFailure(exception);
                    notifyOnProgressListeners(false);
                }
            });
        } catch (IOException e) {

        }
    }

    public void addOnAvatarUploadedListener(OnAvatarUploadedListener listener) {
        if (!onAvatarUploadedListeners.contains(listener)) {
            onAvatarUploadedListeners.add(listener);
        }
    }

    public void removeOnAvatarUploadedListener(OnAvatarUploadedListener listener){
        onAvatarUploadedListeners.remove(listener);
    }

    public void addOnAvatarDownloadedListener(OnAvatarDownloadedListener listener) {
        if (!onAvatarDownloadedListeners.contains(listener)) {
            onAvatarDownloadedListeners.add(listener);
        }
    }

    public void removeOnAvatarDownloadedListener(OnAvatarDownloadedListener listener) {
        onAvatarDownloadedListeners.remove(listener);
    }

    public void notifyOnAvatarUploadedListenersAboutSuccess() {
        for (OnAvatarUploadedListener listener:onAvatarUploadedListeners) {
            listener.onAvatarUploaded();
        }
    }

    public void notifyOnAvatarDownloadedListenersAboutSuccess(File avatar) {
        for (OnAvatarDownloadedListener listener:onAvatarDownloadedListeners) {
            listener.onAvatarDownloaded(avatar);
        }
    }

    public void notifyOnAvatarUploadedListenersAboutFailure(Exception e) {
        for (OnAvatarUploadedListener listener:onAvatarUploadedListeners) {
            listener.onAvatarUploadFailure(e);
        }
    }

    public void notifyOnAvatarDownloadedListenersAboutFailure(Exception e) {
        for (OnAvatarDownloadedListener listener:onAvatarDownloadedListeners) {
            listener.onAvatarDownloadFailure(e);
        }
    }

    public void addOnProgressListener(OnProgressListener listener) {
        if (!progressListeners.contains(listener)) {
            progressListeners.add(listener);
        }
    }

    public void removeOnProgressListener(OnProgressListener listener) {
        progressListeners.remove(listener);
    }

    protected void notifyOnProgressListeners(boolean start) {
        for (OnProgressListener listener:progressListeners) {
            if (start) {
                listener.onProgressStarted();
            } else {
                listener.onProgressEnded();
            }
        }
    }

    public interface OnAvatarUploadedListener {
        void onAvatarUploaded();
        void onAvatarUploadFailure(Exception e);
    }

    public interface OnAvatarDownloadedListener {
        void onAvatarDownloaded(File avatar);
        void onAvatarDownloadFailure(Exception e);
    }
}
