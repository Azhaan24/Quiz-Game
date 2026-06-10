package com.project.quizgame;

import static com.project.quizgame.R.id.progressBarLogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginPage extends AppCompatActivity {

    EditText email;
    EditText password;
    Button signIn;
    SignInButton signInGoogle;
    TextView signUp;
    TextView forgotPassword;
    ProgressBar progressBarSignIn;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GoogleSignInClient googleSignInClient;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerActivityForGoogleSignIn();

        email=findViewById(R.id.editTextLoginEmail);
        password=findViewById(R.id.editTextLoginPassword);
        signIn=findViewById(R.id.buttonLoginSignIn);
        signInGoogle=findViewById(R.id.buttonLoginGoogleSignIn);
        signUp=findViewById(R.id.textViewLoginSignUp);
        forgotPassword=findViewById(R.id.textViewLoginForgotPassword);
        progressBarSignIn=findViewById(R.id.progressBarLogin);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn.setClickable(false);
                signInGoogle.setClickable(false);

                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                if(userEmail.isEmpty()||userPassword.isEmpty()){
                    Toast.makeText(LoginPage.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                    signIn.setClickable(true);
                    signInGoogle.setClickable(true);
                }
                else{
                    signInWithFirebase(userEmail,userPassword);
                }
            }
        });

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn.setClickable(false);
                signInGoogle.setClickable(false);

                signInWithGoogle();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(i);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPage.this, ForgotPassword.class);
                startActivity(i);
            }
        });
    }

    public void signInWithGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("381277859230-v7ls4l9oumfsjo3u5kamjotae2vm703o.apps.googleusercontent.com").requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
        signin();
    }

    public void signin(){
        Intent signinIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signinIntent);
    }
    public void registerActivityForGoogleSignIn(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data = result.getData();

                if(resultCode==RESULT_OK && data!=null){
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    firebaseSignInWithGoogle(task);
                }
            }
        });
    }
    private void firebaseSignInWithGoogle(Task<GoogleSignInAccount> task){
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseGoogleAccount(account);
        } catch (ApiException e) {
            //e.printStackTrace();
            signIn.setClickable(true);
            signInGoogle.setClickable(true);
            Toast.makeText(LoginPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    private void firebaseGoogleAccount(GoogleSignInAccount account){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginPage.this,"Signed In Successfully!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginPage.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    signIn.setClickable(true);
                    signInGoogle.setClickable(true);
                    Toast.makeText(LoginPage.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    Log.e("GoogleAuth","Firebase authentication failed",task.getException());
                }
            }
        });
    }

    public void signInWithFirebase(String userEmail,String userPassword){
        progressBarSignIn.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBarSignIn.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginPage.this, "SignIn Successful!", Toast.LENGTH_SHORT).show();
                    signIn.setClickable(true);
                    signInGoogle.setClickable(true);
                    Intent i = new Intent(LoginPage.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(LoginPage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Google Auth","Firebase Auth Failed",task.getException());
                    progressBarSignIn.setVisibility(View.INVISIBLE);
                    signIn.setClickable(true);
                    signInGoogle.setClickable(true);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            Intent i = new Intent(LoginPage.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}