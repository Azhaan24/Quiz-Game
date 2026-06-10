package com.project.quizgame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizPage extends AppCompatActivity {

    TextView time;
    TextView correctAnswers;
    TextView wrongAnswers;
    TextView question;
    TextView optionA;
    TextView optionB;
    TextView optionC;
    TextView optionD;
    ImageView finishGame;
    ImageView nextQuestion;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference().child("Questions");

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    DatabaseReference databaseReferenceSec = database.getReference();

    String quizQuestion,quizAnswerA,quizAnswerB,quizAnswerC,quizAnswerD,quizAnswerCorrect;
    int questionCount;
    int questionNumber=1;
    String userAnswer;
    int userCorrect=0;
    int userWrong=0;

    CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 60000;
    Boolean timerContinue;
    long timeLeft = TOTAL_TIME;
    Boolean optionSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        time=findViewById(R.id.textViewTime);
        correctAnswers=findViewById(R.id.textViewCorrectAnswer);
        wrongAnswers=findViewById(R.id.textViewWrongAnswer);
        question=findViewById(R.id.textViewQuestion);
        optionA=findViewById(R.id.textViewA);
        optionB=findViewById(R.id.textViewB);
        optionC=findViewById(R.id.textViewC);
        optionD=findViewById(R.id.textViewD);
        finishGame=findViewById(R.id.imageViewFinishGameButton);
        nextQuestion=findViewById(R.id.imageViewNextQuestionButton);

        game();

        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optionSelected){
                    resetTimer();
                    game();
                    optionSelected=false;
                }
                else {
                    Toast.makeText(QuizPage.this, "Choose an Option", Toast.LENGTH_SHORT).show();
                }
            }
        });

        finishGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendScore();

                Intent i = new Intent(QuizPage.this,ResultPage.class);
                startActivity(i);
                finish();
            }
        });

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                optionA.setClickable(false);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
                optionSelected=true;

                userAnswer="a";
                if(quizAnswerCorrect.equals(userAnswer)){
                    optionA.setTextColor(Color.GREEN);
                    userCorrect++;
                    correctAnswers.setText(""+userCorrect);
                }
                else{
                    optionA.setTextColor(Color.RED);
                    userWrong++;
                    wrongAnswers.setText(""+userWrong);
                    findAnswer();
                }
            }
        });
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                optionA.setClickable(false);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
                optionSelected=true;

                userAnswer="b";
                if(quizAnswerCorrect.equals(userAnswer)){
                    optionB.setTextColor(Color.GREEN);
                    userCorrect++;
                    correctAnswers.setText(""+userCorrect);
                }
                else{
                    optionB.setTextColor(Color.RED);
                    userWrong++;
                    wrongAnswers.setText(""+userWrong);
                    findAnswer();
                }
            }
        });
        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                optionA.setClickable(false);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
                optionSelected=true;

                userAnswer="c";
                if(quizAnswerCorrect.equals(userAnswer)){
                    optionC.setTextColor(Color.GREEN);
                    userCorrect++;
                    correctAnswers.setText(""+userCorrect);
                }
                else{
                    optionC.setTextColor(Color.RED);
                    userWrong++;
                    wrongAnswers.setText(""+userWrong);
                    findAnswer();
                }
            }
        });
        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                optionA.setClickable(false);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
                optionSelected=true;

                userAnswer="d";
                if(quizAnswerCorrect.equals(userAnswer)){
                    optionD.setTextColor(Color.GREEN);
                    userCorrect++;
                    correctAnswers.setText(""+userCorrect);
                }
                else{
                    optionD.setTextColor(Color.RED);
                    userWrong++;
                    wrongAnswers.setText(""+userWrong);
                    findAnswer();
                }
            }
        });
    }

    public void game(){
        optionA.setClickable(true);
        optionB.setClickable(true);
        optionC.setClickable(true);
        optionD.setClickable(true);
        nextQuestion.setVisibility(View.VISIBLE);

        startTimer();

        optionA.setTextColor(Color.WHITE);
        optionB.setTextColor(Color.WHITE);
        optionC.setTextColor(Color.WHITE);
        optionD.setTextColor(Color.WHITE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionCount =(int) dataSnapshot.getChildrenCount();

                quizQuestion=dataSnapshot.child(String.valueOf(questionNumber)).child("q").getValue().toString();
                quizAnswerA=dataSnapshot.child(String.valueOf(questionNumber)).child("a").getValue().toString();
                quizAnswerB=dataSnapshot.child(String.valueOf(questionNumber)).child("b").getValue().toString();
                quizAnswerC=dataSnapshot.child(String.valueOf(questionNumber)).child("c").getValue().toString();
                quizAnswerD=dataSnapshot.child(String.valueOf(questionNumber)).child("d").getValue().toString();
                quizAnswerCorrect=dataSnapshot.child(String.valueOf(questionNumber)).child("answer").getValue().toString();

                question.setText(quizQuestion);
                optionA.setText(quizAnswerA);
                optionB.setText(quizAnswerB);
                optionC.setText(quizAnswerC);
                optionD.setText(quizAnswerD);

                if(questionNumber<questionCount){
                    questionNumber++;
                }
                else{
                    Toast.makeText(QuizPage.this,"You Answered All the Questions",Toast.LENGTH_SHORT).show();
                    nextQuestion.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(QuizPage.this,"There is an Error!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void findAnswer(){
        if(quizAnswerCorrect.equals("a")){
            optionA.setTextColor(Color.GREEN);
        }
        else if(quizAnswerCorrect.equals("b")){
            optionB.setTextColor(Color.GREEN);
        }
        else if(quizAnswerCorrect.equals("c")){
            optionC.setTextColor(Color.GREEN);
        }
        else if(quizAnswerCorrect.equals("d")){
            optionD.setTextColor(Color.GREEN);
        }
    }
    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onFinish() {
                timerContinue=false;
                pauseTimer();
                question.setText("Time's Up!");
                findAnswer();
                optionA.setClickable(false);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
                optionSelected=true;
            }

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updatedCountDownText();
            }
        }.start();
        timerContinue=true;
    }
    public void resetTimer(){
        timeLeft=TOTAL_TIME;
        updatedCountDownText();
    }
    public void updatedCountDownText(){
        int seconds = (int) (timeLeft/1000)%60;
        time.setText(""+seconds);
    }
    public void pauseTimer(){
        countDownTimer.cancel();
        timerContinue=false;
    }

    public void sendScore(){
        String userUID = user.getUid();
        databaseReferenceSec.child("Scores").child(userUID).child("Correct").setValue(userCorrect);
        databaseReferenceSec.child("Scores").child(userUID).child("Wrong").setValue(userWrong);
    }
}