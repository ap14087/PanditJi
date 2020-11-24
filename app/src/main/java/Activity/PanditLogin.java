package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class PanditLogin extends AppCompatActivity {
    TextInputEditText pandit_email,pandit_password;
    String email,password;
    Button pandit_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandit_login);

        pandit_email = findViewById(R.id.pandit_email);
        pandit_password = findViewById(R.id.pandit_password);
        pandit_login = findViewById(R.id.pandit_login);
    }

    public void LogintoRegister(View view){
        startActivity(new Intent(getApplicationContext(),PanditRegistartion.class));
    }

    public void login_pandit(View v){

        email = pandit_email.getText().toString().trim();
        password = pandit_password.getText().toString().trim();

        if(email.equals("")){
            pandit_email.setError("Enter email");
        }else if(password.equals("")){
            pandit_password.setError("Enter Password");
        }else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        startActivity(new Intent(getApplicationContext(),PanditDashboard.class));
                        finish();

                    }else{
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
            startActivity(new Intent(getApplicationContext(),PanditDashboard.class));
            finish();
        }
    }
}