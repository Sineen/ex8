package com.example.ex8;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private static final String USERNAME = "username";
    final String LOG_TAG = "CS";
    SharedPreferences sp;
    SharedPreferences.Editor sp_editor;
    ClientInfo user_info;
    Server server;

    TextView prettyNameTxt;
    EditText prettyEdit;
    Button prettyButton;
    ImageView imageV;
    ProgressBar pbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_info = new ClientInfo();
        server = new Server(user_info, MainActivity.this);
        //TODO duprecated???
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getViews();
        getUserName();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            getUserName();
        }

        String temp_username = data.getStringExtra(USERNAME);
        if ((temp_username == null) || (temp_username.length() < 1)) {
            getUserName();
        } else {
            user_info.setUsername(temp_username) ;
        }

        sp_editor = sp.edit();
        sp_editor.putString(USERNAME, user_info.getUsername()).apply();
        registerNewUsername();
    }

    private void getViews() {
        prettyNameTxt = findViewById(R.id.pretty_name);
        prettyEdit = findViewById(R.id.pretty_edit);
        prettyButton = findViewById(R.id.pretty_button);
        imageV = findViewById(R.id.imageView);
        pbar = findViewById(R.id.progressBar);
    }

    void getImage() {
        try {
            Glide.with(getApplicationContext()).load(user_info.getImageURL()).into(imageV);
            Log.d(LOG_TAG, "getImageURL" + user_info.getImageURL());
        } catch (Exception exception) {
            Log.d(LOG_TAG, "getImageERROR" + exception.getMessage());
        }
    }

    void existedUserCycle() {
        pbar.setVisibility(View.INVISIBLE);
        if ((user_info.getPretty_name() != null) && (user_info.getPretty_name().length() > 0)) {
            prettyNameTxt.setText(user_info.getPretty_name());
        }

        prettyEdit.setVisibility(View.VISIBLE);
        prettyButton.setVisibility(View.VISIBLE);
        prettyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbar.setVisibility(View.VISIBLE);
                server.setClientPrettyName(prettyEdit.getText().toString());
            }
        });
        getImage();
    }

    void prettyNameCallback() {
        if (!user_info.getPretty_name().isEmpty()) {
            prettyNameTxt.setText(user_info.getPretty_name());
            getImage();
//            pbar.setVisibility(View.GONE);
            pbar.setVisibility(View.INVISIBLE);
        }
    }

    void registerNewUsername() {
        prettyNameTxt.setText(user_info.getUsername());
        prettyEdit.setVisibility(View.INVISIBLE);
        prettyButton.setVisibility(View.INVISIBLE);
        server.getToken();
    }

    void updateClientInfo() {
        server.getClientDetails();
    }

    void getUserName() {
        user_info.setUsername( sp.getString(USERNAME, null));
        if ((user_info.getUsername() == null) || (user_info.getUsername().length() < 1)) {
            Intent intent = new Intent(MainActivity.this, NewClient.class);
            startActivityForResult(intent, 0);
        } else {
            registerNewUsername();
        }
    }
}
