package com.example.safejourney;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    EditText email, passwd;
    Button log;
    TextView regpg;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.Email_log);
        passwd = findViewById(R.id.Password_log);
        log = (Button)findViewById(R.id.login_button);
        regpg = (TextView)findViewById(R.id.register_butt);
        fAuth = FirebaseAuth.getInstance();

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String e = email.getText().toString().trim();
                String pd = passwd.getText().toString().trim();

                if(TextUtils.isEmpty(e)) {
                    email.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(pd)) {
                    passwd.setError("Password  is required");
                    return;
                }



                if(pd.length() < 7){
                    passwd.setError("Password must be atleast 8 characters");
                    return;
                }



                fAuth.signInWithEmailAndPassword(e, pd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), test.class));
                        }
                        else {
                            Toast.makeText(login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });
        regpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}