package com.leo.h19305;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    RelativeLayout signup_btn;

    LottieAnimationView signup_btn_animation;

    FirebaseAuth mAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    Button callLogIn;

    ImageView image;
    TextView logoText, sloganText, signup_btn_txt;
    TextInputLayout register_email, register_password, register_first_name, register_last_name , register_confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance("https://project-2080856967274304582-default-rtdb.firebaseio.com/");


        signup_btn_txt = findViewById(R.id.register_btn_txt);
        signup_btn_animation = findViewById(R.id.register_btn_animation);
        //Hooks
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        register_first_name = findViewById(R.id.register_firstName);
        register_last_name = findViewById(R.id.register_lastName);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_confirm_password = findViewById(R.id.register_confirm_password);

        signup_btn = findViewById(R.id.register_btn);
        callLogIn = findViewById(R.id.login_screen);
        callLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(register_email, "username_tran");
                pairs[4] = new Pair<View, String>(register_password, "password_tran");
                pairs[5] = new Pair<View, String>(signup_btn, "button_tran");
                pairs[6] = new Pair<View, String>(callLogIn, "login_signup_tran");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });



        register_first_name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validateFirstNameO()){
                    register_first_name.setError(null);
                    register_first_name.setErrorEnabled(false);
                }
            }
        });
        register_last_name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validateLastNameO()){
                    register_last_name.setError(null);
                    register_last_name.setErrorEnabled(false);
                }
            }
        });
        register_email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validateEmailO()){
                    register_email.setError(null);
                    register_email.setErrorEnabled(false);
                }
            }
        });
        register_password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validatePasswordO()){
                    register_password.setError(null);
                    register_password.setErrorEnabled(false);
                }
            }
        });

        register_confirm_password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validateConfirmPasswordO()){
                    register_confirm_password.setError(null);
                    register_confirm_password.setErrorEnabled(false);
                }
            }
        });

    }

    private void registerUser() {
        String firstName = register_first_name.getEditText().getText().toString().trim();
        String lastName = register_last_name.getEditText().getText().toString().trim();
        String email = register_email.getEditText().getText().toString().trim();
        String password = register_password.getEditText().getText().toString().trim();
        String confirmPassword = register_confirm_password.getEditText().getText().toString().trim();

        if (!validateFirstName() | !validateLastName() | !validateEmail() | !validateConfirmPassword() | !validatePassword()){
            return;
        }

        signup_btn_txt.setVisibility(View.GONE);
        signup_btn_animation.setVisibility(View.VISIBLE);
        signup_btn_animation.playAnimation();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.e("reg","account created");
                            Users user = new Users(firstName,lastName,email,password);
                            rootNode.getReference("Users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            signup_btn_txt.setVisibility(View.VISIBLE);
                                            signup_btn_animation.setVisibility(View.GONE);
                                            signup_btn_animation.pauseAnimation();

                                            if (task.isSuccessful()){
                                                Log.e("reg","data created");
                                                startActivity(new Intent(RegisterActivity.this,BluetoothConnectionActivity.class));
                                            }
                                            else {
                                                Log.e("reg","data not created");
                                            }
                                        }
                                    });

                        }
                        else {
                            signup_btn_txt.setVisibility(View.VISIBLE);
                            signup_btn_animation.setVisibility(View.GONE);
                            signup_btn_animation.pauseAnimation();
                        }
                    }
                });
    }

    private Boolean validateFirstName(){
        String val = register_first_name.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            register_first_name.setError("First Name is required");
            return false;
        }
        else {
            register_first_name.setError(null);
            register_first_name.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateLastName(){
        String val = register_last_name.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            register_last_name.setError("Last Name is required");
            return false;
        }
        else {
            register_last_name.setError(null);
            register_last_name.setErrorEnabled(false);

            return true;
        }
    }
    private Boolean validateEmail(){
        String val = register_email.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            register_email.setError("Email is required");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()){
            register_email.setError("Please provide valid email");
            return false;
        }
        else {
            register_email.setError(null);
            register_email.setErrorEnabled(false);

            return true;
        }
    }
    private Boolean validatePassword(){
        String val = register_password.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            register_password.setError("Password is required");
            return false;
        }
        else if (val.length()<6){
            register_password.setError("Min password length should be 6 character!");
            return false;
        }
        else {
            register_password.setError(null);
            register_password.setErrorEnabled(false);

            return true;
        }
    }

    private Boolean validateConfirmPassword(){
        String val = register_confirm_password.getEditText().getText().toString().trim();
        String password = register_password.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            register_confirm_password.setError("Confirm Password is required");
            return false;
        }
        else if (!val.matches(password)){
            register_confirm_password.setError("Confirm password should match password!");
            return false;
        }
        else {
            register_confirm_password.setError(null);
            register_confirm_password.setErrorEnabled(false);

            return true;
        }
    }

    private Boolean validateFirstNameO(){
        String val = register_first_name.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }
    private Boolean validateLastNameO(){
        String val = register_last_name.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }
    private Boolean validateEmailO(){
        String val = register_email.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()){
            return false;
        }
        else {
            return true;
        }
    }
    private Boolean validatePasswordO(){
        String val = register_password.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            return false;
        }
        else if (val.length()<6){
            return false;
        }
        else {
            return true;
        }
    }

    private Boolean validateConfirmPasswordO(){
        String val = register_confirm_password.getEditText().getText().toString().trim();
        String password = register_password.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            return false;
        }
        else if (!val.matches(password)){
            return false;
        }
        else {
            return true;
        }
    }
}