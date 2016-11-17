package com.ekasilab.facebooklivefeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ekasilab.facebooklivefeed.data.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserProfile extends BaseActivity implements View.OnClickListener {

    private static final int RC_TAKE_PICTURE = 101;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseAuth auth;
    private Profile profile;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private EditText eUserFullName;
    private Button btnNext;
    private FirebaseAuth.AuthStateListener authListener;
    private Uri file;
    private RoundImageView displayPicture;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 5) {
                btnNext.setEnabled(true);
            } else {
                btnNext.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();

        displayPicture = (RoundImageView) findViewById(R.id.display_picture);
        eUserFullName = (EditText) findViewById(R.id.username);
        btnNext = (Button) findViewById(R.id.next);

        eUserFullName.addTextChangedListener(textWatcher);

        btnNext.setOnClickListener(this);
        displayPicture.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                if (file != null) {
                    storageRef = mStorage.getReferenceFromUrl("gs://facebooklivefeed.appspot.com");
                    StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
                    uploadTask = riversRef.putFile(file);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            setProfileInformation(eUserFullName.getText().toString().trim(), downloadUrl.getPath());
                        }
                    });
                } else {

                }
                break;
            case R.id.display_picture:
                showDisplayPicturePopUpMenu();
                break;
        }
    }

    public void setProfileInformation(String name, String profile_picture) {
        profile = new Profile(name, profile_picture);
        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();
            saveUserInformation(getUid(), profile);
        }
    }

    private void saveUserInformation(String userId, Profile profile) {
        mDatabase.child("users").child(userId).child("profile").setValue(profile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try {
                    file = data.getData();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),
                            "An Error Occured", Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
        } else {
            if (resultCode == RESULT_OK) {
                try {
                    file = data.getData();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),
                            "An Error Occured", Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
        }
    }

    public void pickPhoto() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 0);
    }

    private void launchCamera() {
        Log.d("Launched Camera", "launchCamera");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    public void showDisplayPicturePopUpMenu() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(UserProfile.this, displayPicture);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.profile_picture_options_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getGroupId()) {
                    case R.id.choose_picture:
                        pickPhoto();
                        break;
                    case R.id.take_picture:
                        launchCamera();
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

}
