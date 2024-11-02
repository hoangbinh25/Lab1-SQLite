package com.example.lab1_sqlite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText editText_user, editText_pass;
    private DatabaseReference mDatabase;
    String test = "day la du lieu moi";
    String test2 = "day la du lieu moi";

    public static class User {

        public String username;
        public String password;

        public User() {
        }

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public void btn_onclick(View view){
        writeNewUser("user3","123");
    }
        public void writeNewUser(String name, String password) {
            User user = new User(name, password);
            mDatabase.child("users").push().setValue(user);
        }

    public void btn_login(View view) {
        editText_user = findViewById(R.id.edt_user1);
        editText_pass = findViewById(R.id.edt_pass1);
        String user_name = editText_user.getText().toString().trim();
        String pass_word = editText_pass.getText().toString().trim();
        if (user_name.isEmpty() || pass_word.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please Enter Your Phone Number Or Password", Toast.LENGTH_SHORT).show();
        } else {
            mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean userFound = false;
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        if (user_name.equals(username)) {
                            userFound = true;
                            String getpassword = userSnapshot.child("password").getValue(String.class);
                            if (getpassword != null && getpassword.equals(pass_word)) {
                                Toast.makeText(LoginActivity.this, "Successfully login!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                    if (!userFound) {
                        Toast.makeText(LoginActivity.this, "Wrong name!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }
}