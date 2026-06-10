package com.project.quizgame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText email;
    Button buttoncont;
    ProgressBar progressBar;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email=findViewById(R.id.editTextForgotPasswordEmail);
        buttoncont=findViewById(R.id.buttonContinue);
        progressBar=findViewById(R.id.progressBarForgotPassword);

        buttoncont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                if(userEmail.isEmpty()){
                    Toast.makeText(ForgotPassword.this, "Enter your Email", Toast.LENGTH_LONG).show();
                }
                else{
                    resetPassword(userEmail);
                }
            }
        });
    }

    public void resetPassword(String userEmail){
        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this,"We sent you an Email to reset your password",Toast.LENGTH_LONG).show();
                    buttoncont.setClickable(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                }
                else {
                    Toast.makeText(ForgotPassword.this,"There is a problem! Please try again later",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}