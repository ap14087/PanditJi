package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amanpandey.panditji.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLogin extends AppCompatActivity {
    TextInputEditText user_email,user_password;
    String email,password;
    Button user_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //hooks
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        user_login = findViewById(R.id.user_login);
    }

    public void login_user(View v){

        email = user_email.getText().toString().trim();
        password = user_email.getText().toString().trim();

        if(email.equals("")){
            user_email.setError("Enter email");
        }else if(password.equals("")){
            user_password.setError("Enter Password");
        }else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        startActivity(new Intent(getApplicationContext(),UserDashBoard.class));
                        finish();

                    }else{
                        Log.d("Login Failed :",""+task.getException());
                        Toast.makeText(getApplicationContext(), "Login Failed" + task.getException()
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            Toast.makeText(this, "Redirecting to dashboard", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),UserDashBoard.class));
            finish();
        }
    }

}