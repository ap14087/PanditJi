package fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.amanpandey.panditji.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import Activity.PanditLogin;

import static android.app.Activity.RESULT_OK;

public class PanditProfileFragment extends Fragment {
    private TextInputEditText etEmail,etName,etNumber;
    private String email,name;
    private ImageView ivProfile;
    private Button btnSave,btnLogout;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference fileStorage;
    private Uri localFileUri,serverFileUri;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pandit_profile, container, false);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!




    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialise your views
        localFileUri = null ;
        serverFileUri = null;
        etEmail = view.findViewById(R.id.pandit_email);
        etName = view.findViewById(R.id.pandit_name);
        etNumber = view.findViewById(R.id.pandit_number);
        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.bntLogout);
        ivProfile = view.findViewById(R.id.ivProfile);
        fileStorage = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            etName.setText(firebaseUser.getDisplayName());
            etEmail.setText(firebaseUser.getEmail());
            etNumber.setText("");
            serverFileUri = firebaseUser.getPhotoUrl();

            if (serverFileUri != null) {
                //Glide library to lode image from firebase storage to imageView
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .into(ivProfile);
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().trim().equals("")) {
                    etName.setError("Enter name");
                } else {
                    if (localFileUri != null) {
                        updateNameAndPhoto();
                    } else {
                        updateOnlyName();
                    }
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity().getApplicationContext(), PanditLogin.class));
                getActivity().finish();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Log.d("We are inside ", "click listener");
                if (serverFileUri == null) {
                    Log.d("We are inside ", "if statement");
                    PanditProfileFragment.this.pickImage();
                } else {
                    PopupMenu popupMenu = new PopupMenu(getActivity(),ivProfile);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_picture, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            if (id == R.id.mnuChangePic) {
                                pickImage();
                            } else if (id == R.id.mnuRemovePic) {
                                removePhoto();
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            }
        });





    }


    private void pickImage() {
        Log.d("We are inside ","pick image method");
        //Checking if the user has given permission to access external storage.
        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("We are inside ","pick image if statement");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);

        }
        else
        {   Log.d("We are inside ","pick image else statement");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);

        }
    }

    //to handle select activity result


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        if(requestCode == 101){

                localFileUri = intent.getData();
                ivProfile.setImageURI(localFileUri);

        }
    }



    //To handle permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("We are inside ","onRequestPermissionsResult");
        if(requestCode == 102){
            Log.d("We are inside ","requestCode == 102");
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("We are inside ","grantResults[0] == PackageManager.PERMISSION_GRANTED");
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "Access permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removePhoto(){

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .setPhotoUri(null)
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //get current user id
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Pandits");

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("name",etName.getText().toString().trim());
                    hashMap.put("email",etEmail.getText().toString().trim());
                    hashMap.put("number",etNumber.getText().toString().trim());
                    hashMap.put("photo","");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity().getApplicationContext(), "Photo removed successfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void updateNameAndPhoto(){
        String strFileName = firebaseUser.getUid() + ".jpg";

        final StorageReference fileRef = fileStorage.child("images/"+ strFileName);

        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //get current user id
                                        String userID = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pandits");

                                        HashMap<String,String> hashMap = new HashMap<>();
                                        hashMap.put("name",etName.getText().toString().trim());
                                        hashMap.put("email",etEmail.getText().toString().trim());
                                        hashMap.put("number",etNumber.getText().toString().trim());
                                        hashMap.put("photo",serverFileUri.getPath());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                getActivity().finish();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    // if user not selected profile photo
    private void updateOnlyName(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //get current user id
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Pandits");

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("name",etName.getText().toString().trim());
                    hashMap.put("email",etEmail.getText().toString().trim());
                    hashMap.put("number",etNumber.getText().toString().trim());
                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getActivity().finish();
                        }
                    });
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}