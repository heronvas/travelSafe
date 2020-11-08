package com.example.safejourney;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText name, email, phone, passwd, cnpasswd;
    Button reg;
    TextView loginpg;
    FirebaseAuth fAuth;
    DatabaseReference user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.FullName);

        email = findViewById(R.id.Email);
        phone = findViewById(R.id.phone);
        passwd = (EditText)findViewById(R.id.Password);
        cnpasswd = (EditText)findViewById(R.id.ConfirmPassword);
        reg = findViewById(R.id.register_button);
        loginpg = findViewById(R.id.login_text);
        fAuth = FirebaseAuth.getInstance();
        user = FirebaseDatabase.getInstance().getReference("User");

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String n = name.getText().toString().trim();

                final String ph = phone.getText().toString().trim();
                String e = email.getText().toString().trim();
                String pd = passwd.getText().toString();
                String cpd = cnpasswd.getText().toString();
                int f = 0;
                if(TextUtils.isEmpty(e)) {
                    email.setError("Email is required");
                    f = 1;
                }

                if(TextUtils.isEmpty(pd)) {
                    passwd.setError("Password  is required");
                    f = 1;
                }

                if(TextUtils.isEmpty(n)) {
                    name.setError("Name is required");
                    f = 1;
                }



                if(pd.length() < 7){
                    passwd.setError("Password must be atleast 8 characters");
                    f = 1;
                }

                if(!cpd.equals(pd)){
                    cnpasswd.setError("Password and Confirm Password must be same");
                    f = 1;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
                    email.setError("Invalid Email");
                    f = 1;
                }

                if (f == 1) {
                    return;
                }
                fAuth.createUserWithEmailAndPassword(e, pd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            String Userid = fAuth.getCurrentUser().getUid();
                            //String uid = user.push().getKey();
                            startActivity(new Intent(getApplicationContext(), login.class));
                            Map<String, Object> u = new HashMap<>();
                            //u.put("Userid", Userid);
                            u.put("Full Name", n);

                            u.put("Contact", ph);
                            user.child(Userid).setValue(u);
                            Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
        loginpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });

    }
}