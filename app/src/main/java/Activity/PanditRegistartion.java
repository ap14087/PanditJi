package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amanpandey.panditji.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PanditRegistartion extends AppCompatActivity {

    //Variables
    TextInputEditText pandit_fullname,pandit_confirmpassword,pandit_email,pandit_password;
    Button register_pandit;
    String email,name,password,confirmPassword;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandit_registartion);

        //Hooks
        pandit_fullname = findViewById(R.id.pandit_fullname);
        pandit_confirmpassword = findViewById(R.id.pandit_confirmpassword);
        pandit_email = findViewById(R.id.pandit_email);
        pandit_password = findViewById(R.id.pandit_password);
        register_pandit = findViewById(R.id.pandit_registration);




    }

    private void updateOnlyName(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(pandit_fullname.getText().toString().trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //get current user id
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Pandits");

                    HashMap<String,String> hashMap = new HashMap<>();

                    hashMap.put("name",pandit_fullname.getText().toString().trim());
                    hashMap.put("email",pandit_email.getText().toString().trim());
                    hashMap.put("number","");
                    hashMap.put("photo","");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), PanditLogin.class));
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerPandit(View view){
        Log.i("We are inside  register" ,"Till Now working fine");
        email = pandit_email.getText().toString().trim();
        name = pandit_fullname.getText().toString().trim();
        password = pandit_password.getText().toString().trim();
        confirmPassword = pandit_confirmpassword.getText().toString().trim();

        if(email.equals("")){
            pandit_email.setError("Enter email");
        }else if(name.equals("")){
            pandit_fullname.setError("Enter name");
        }else if(password.equals("")){
            pandit_password.setError("Enter password");
        }else if(confirmPassword.equals("")){
            pandit_confirmpassword.setError("Enter confirm password");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            pandit_email.setError("Enter correct email");
        }else if(!password.equals(confirmPassword)){
            pandit_confirmpassword.setError("Password mismatch");
        }else{
            Log.i("We are inside else","Till now working fine");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.i(" we are insde oncomplet","Till now working fine");
                    if(task.isSuccessful()){
                        Log.i("We are inside task","Till now working fine");
                        firebaseUser = firebaseAuth.getCurrentUser();
                            updateOnlyName();
                    }else{
                        Log.d("Task Failed", "SignUp Failed : " + task.getException());
                        Toast.makeText(getApplicationContext(), "SignUp Failed : " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
