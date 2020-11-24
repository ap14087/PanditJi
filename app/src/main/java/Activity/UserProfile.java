package Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
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

public class UserProfile extends AppCompatActivity {

    private TextInputEditText etEmail,etName;
    private String email,name;
    private ImageView ivProfile;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference fileStorage;
    private Uri localFileUri,serverFileUri;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        ivProfile = findViewById(R.id.ivProfile);
        fileStorage = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            etName.setText(firebaseUser.getDisplayName());
            etEmail.setText(firebaseUser.getEmail());
            serverFileUri = firebaseUser.getPhotoUrl();

            if(serverFileUri != null){
                //Glide library to lode image from firebase storage to imageView
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .into(ivProfile);
            }
        }
    }

    public void changeImage(View v){

        if(serverFileUri == null){
            pickImage();
        }
        else
        {
            PopupMenu popupMenu = new PopupMenu(this,v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_picture,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    if(id == R.id.mnuChangePic){
                        pickImage();
                    }else if(id ==R.id.mnuRemovePic){
                        removePhoto();
                    }
                    return false;
                }
            });
            popupMenu.show();
        }

    }

    private void pickImage() {

        //Checking if the user has given permission to access external storage.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);

        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);

        }
    }

    //to handle selectavtvity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if(resultCode == RESULT_OK){
                localFileUri = data.getData();
                ivProfile.setImageURI(localFileUri);
            }
        }
    }

    //To handle permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 102){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else {
                Toast.makeText(this, "Access permission is required", Toast.LENGTH_SHORT).show();
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
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("photo","");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Photo removed successfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void btnLogoutClick(View view){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(),UserLogin.class));
        finish();
    }

    public void btnSaveClick(View v){
        if(etName.getText().toString().trim().equals("")){
            etName.setError("Enter name");
        }
        else
        {
            if(localFileUri != null){
                updateNameAndPhoto();
            }else{
                updateOnlyName();
            }
        }
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
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                                        HashMap<String,String> hashMap = new HashMap<>();
                                        hashMap.put("name",etName.getText().toString().trim());
                                        hashMap.put("name",serverFileUri.getPath());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                finish();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
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
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("name",etName.getText().toString().trim());

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void btnChangePasswordClick(View view){
        startActivity(new Intent(getApplicationContext(), UserChangePassword.class));
    }

}