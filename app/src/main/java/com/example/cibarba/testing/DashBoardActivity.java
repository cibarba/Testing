package com.example.cibarba.testing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtWelcome;
    private EditText input_new_password;
    private Button btnChangePass, btnLogOut;
    private RelativeLayout activity_dashboard;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        //View
        txtWelcome = (TextView) findViewById(R.id.dashboard_welcome);
        input_new_password = (EditText) findViewById(R.id.dashboard_newpassword);
        btnChangePass = (Button) findViewById(R.id.dashboard_btn_change_pass);
        btnLogOut = (Button) findViewById(R.id.dashboard_btn_logout);
        activity_dashboard = (RelativeLayout) findViewById(R.id.activity_dashboard);

        btnChangePass.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);

        //Init Firebase
        mAuth = FirebaseAuth.getInstance();

        //Session check
        if(mAuth.getCurrentUser() != null){
            txtWelcome.setText("Welcome, " + mAuth.getCurrentUser().getEmail());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.dashboard_btn_change_pass){
            changePassword(input_new_password.getText().toString());
        }

        else if(v.getId() == R.id.dashboard_btn_logout){
            logoutUser();
        }

    }

    private void logoutUser() {
        mAuth.signOut();
        if (mAuth.getCurrentUser() == null){
            startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
            finish();
        }
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar snackbar = Snackbar.make(activity_dashboard, "Password changed",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });
    }
}
