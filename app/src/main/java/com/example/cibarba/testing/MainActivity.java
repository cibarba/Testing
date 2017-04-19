package com.example.cibarba.testing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText input_email, input_password;
    TextView btnSignUp, btnForgotPassword;
    TextInputLayout input_layout_email, input_layout_password;

    RelativeLayout activity_main;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.login_btn_login);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        btnSignUp = (TextView) findViewById(R.id.login_btn_sign_up);
        btnForgotPassword = (TextView) findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        input_layout_email = (TextInputLayout) findViewById(R.id.login_input_email);
        input_layout_password = (TextInputLayout) findViewById(R.id.login_input_password);

        input_email.addTextChangedListener(new MyTextWatcher(input_email));
        input_password.addTextChangedListener(new MyTextWatcher(input_password));

        btnSignUp.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // ...
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, DashBoardActivity.class));
        }

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.login_btn_forgot_password){
            startActivity(new Intent(MainActivity.this , ForgotPassword.class));
            finish();
        }

        else if (v.getId() == R.id.login_btn_sign_up){
            startActivity(new Intent(MainActivity.this , SignUp.class));
            finish();
        }

        else if (v.getId() == R.id.login_btn_login){
            loginUser(input_email.getText().toString(), input_password.getText().toString());
        }

    }


    private void loginUser(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Snackbar snackbar = Snackbar.make(activity_main, "Authenthication failed", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        if (password.length() < 6){
                            Snackbar snackBar = Snackbar.make(activity_main,"Password length must be over 6", Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        }
                    }
                    else{
                        startActivity(new Intent(MainActivity.this, DashBoardActivity.class));
                    }
                }
            });
    }

    private void submitForm(){
        if (!validateEmail()){
            return;
        }

        if (!validatePass()){
            return;
        }
        Toast.makeText(getApplicationContext(), "Thank you", Toast.LENGTH_SHORT);
    }

    //Validate Email boolean
    private boolean validateEmail() {
        String email = input_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            input_layout_email.setError(getString(R.string.err_msg_email));
            requestFocus(input_email);
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }
        return true;

    }
    //Validate Password boolean
    private boolean validatePass() {
        if (input_password.getText().toString().trim().isEmpty()) {
            input_layout_password.setError(getString(R.string.err_msg_password));
            requestFocus(input_password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }
        return true;
    }

    //Validate Email Atritbute
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View v;

        public MyTextWatcher(View v) {
            this.v = v;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

        }

        public void afterTextChanged(Editable editable){
            switch (v.getId()){
                case R.id.login_email:
                    validateEmail();
                    break;
                case R.id.login_password:
                    validatePass();
                    break;
            }

        }

    }

}



