package com.example.ex8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

class NewClient extends AppCompatActivity {
    Button signup;
    EditText add_name;

    @Override
    protected void onCreate(Bundle savedIS) {

        super.onCreate(savedIS);
        setContentView(R.layout.new_user);

        signup = findViewById(R.id.button_newuser);
        add_name = findViewById(R.id.insert_username);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(add_name.getText().toString().length() !=0 ) {
                    Intent intent = new Intent();
                    intent.putExtra("username", add_name.getText().toString());
                    setResult(1, intent);
                    finish();
                }
            }
        });
    }
}
