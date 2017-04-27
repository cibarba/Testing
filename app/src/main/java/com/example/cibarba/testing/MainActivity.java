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
import android.app.Application;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText input_email, input_password;
    TextView btnSignUp, btnForgotPassword;
    TextInputLayout input_layout_email, input_layout_password;

    RelativeLayout activity_main;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private LoginButton btnFacebook;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.login_btn_login);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        btnSignUp = (TextView) findViewById(R.id.login_btn_sign_up);
        btnForgotPassword = (TextView) findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        input_layout_email = (TextInputLayout) findViewById(R.id.login_input_email);
        input_layout_password = (TextInputLayout) findViewById(R.id.login_input_password);

        mCallbackManager = CallbackManager.Factory.create();

        btnFacebook = (LoginButton) findViewById(R.id.login_button);
        btnFacebook.setReadPermissions(Arrays.asList("email"));
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Snackbar snackbar = Snackbar.make(activity_main, "Se cancelo la operacion", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar snackbar = Snackbar.make(activity_main, "Hubo algun error en la operacion", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        input_email.addTextChangedListener(new MyTextWatcher(input_email));
        input_password.addTextChangedListener(new MyTextWatcher(input_password));

        btnSignUp.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // ...

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    goDashBoard();
                }
            }
        };
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, DashBoardActivity.class));
        }
    //se cierra el onCreate()
    }

    private void goDashBoard() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(activity_main, "Firebase error ", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }
        });
    }


    private void loginUser(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Snackbar snackbar = Snackbar.make(activity_main, "Authentication failed", Snackbar.LENGTH_LONG);
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
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

}