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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserRegistration extends AppCompatActivity {
    //Variables
    TextInputEditText user_fullname,user_confirmpassword,user_email,user_password,user_number;
    Button register_user;
    String email,name,password,confirmPassword,phone;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    TextView signInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        //Hooks
        signInTextView = findViewById(R.id.signIn_TextView);
        user_fullname = findViewById(R.id.login_name_editText);
        user_number = findViewById(R.id.login_number_editText);
        user_confirmpassword = findViewById(R.id.login_confirmpassword_editText);
        user_email = findViewById(R.id.login_email_editText);
        user_password = findViewById(R.id.login_password_editText);
        register_user = findViewById(R.id.register_user);

        //OnCLick listener
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),UserLogin.class));
            }
        });
    }

    public void Reg(View v){
        startActivity(new Intent(this,PanditLogin.class));

        //Toast.makeText(this, "Another app", Toast.LENGTH_SHORT).show()

    }

    private void updateOnlyName(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(user_fullname.getText().toString().trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //get current user id
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                    HashMap<String,String> hashMap = new HashMap<>();

                    hashMap.put("name",user_fullname.getText().toString().trim());
                    hashMap.put("email",user_email.getText().toString().trim());
                    hashMap.put("phone",user_number.getText().toString().trim());


                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserLogin.class));
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Failed to update profile" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerUser(View view){
        Log.i("We are inside  register" ,"Till Now working fine");
        email = user_email.getText().toString().trim();
        name = user_fullname.getText().toString().trim();
        phone = user_number.getText().toString().trim();
        password = user_password.getText().toString().trim();
        confirmPassword = user_confirmpassword.getText().toString().trim();

        if(email.equals("")){
            user_email.setError("Enter email");
        }else if(name.equals("")){
            user_fullname.setError("Enter name");
        }else if(phone.equals("")){
            user_number.setError("Enter phone number");
        }else if(password.equals("")){
            user_password.setError("Enter password");
        }else if(confirmPassword.equals("")){
            user_confirmpassword.setError("Enter confirm password");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            user_password.setError("Enter correct email");
        }else if(!password.equals(confirmPassword)){
            user_confirmpassword.setError("Password mismatch");
        }else if(phone.length() != 10){
            user_number.setError("Enter valid number");
        }else{

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        firebaseUser = firebaseAuth.getCurrentUser();
                        updateOnlyName();
                    }else{
                        Toast.makeText(getApplicationContext(), "SignUp Failed : " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}