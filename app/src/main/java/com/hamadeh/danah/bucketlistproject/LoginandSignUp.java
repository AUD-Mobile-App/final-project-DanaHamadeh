package com.hamadeh.danah.bucketlistproject;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginandSignUp extends AppCompatActivity {

    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;
    private EditText txtEmail, txtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginsignup);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPass = (EditText) findViewById(R.id.txtPass);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        //change color of action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50E3A4")));
        if(firebaseAuth.getCurrentUser() != null) {
            loggedIn();
        }
    }

    //starts the list activity
    private  void loggedIn(){
        startActivity(new Intent(LoginandSignUp.this,List.class));
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = txtEmail.getText().toString();
        String password = txtPass.getText().toString();

        //If username field is empty
        //show error and "field required"
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("This field is Required");
            valid = false;}
            else {
            txtEmail.setError(null); }

        //If password field is empty
        //show error and "field required"
        if (TextUtils.isEmpty(password)) {
            txtPass.setError("This field is Required");
            valid = false;}
            else {
            txtPass.setError(null);}
        return valid;   }



        //when user presses signup button
    public void signUp(View view){

        if (!validateForm()) {
            return;}

        //get email and password written by user
        String email = txtEmail.getText().toString();
        String pass = txtPass.getText().toString();

        //save to firebase
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //if creation was successful then proceeds to list activity
                        if (task.isSuccessful()) {
                            loggedIn();}
                        else {
                            //else show signing up failed
                            Toast.makeText(LoginandSignUp.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();}
                    }
                });
    }

    public void login(View view){
        //get email and pass entered by user
        String email = txtEmail.getText().toString();
        String pass = txtPass.getText().toString();

        if (!validateForm()) {
            return;}

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Check if username and password are in the firebase
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //if exists then continue to list activity
                            Toast.makeText(LoginandSignUp.this, "Successful: " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            loggedIn();}
                        else {
                            Toast.makeText(LoginandSignUp.this, "Invalid Username or Password ",
                                    Toast.LENGTH_SHORT).show();}
                    }
                });
    }
}





