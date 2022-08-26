package com.example.chatbees;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
    TextView txt_signin, signUpBtn;
    CircleImageView profileImage;
    EditText regName, regEmail, regPassword, regCPassword;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
    ProgressDialog progressDialog;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        //firebase authentication instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        //find id
        txt_signin = findViewById(R.id.txt_signin);
        regName = findViewById(R.id.reg_name);
        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regCPassword = findViewById(R.id.reg_confirmPassword);
        signUpBtn = findViewById(R.id.signup_btn);
        profileImage = findViewById(R.id.profile_image);

        //sign up button handler
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String name = regName.getText().toString();
                String email = regEmail.getText().toString();
                String password = regPassword.getText().toString();
                String cPassword = regCPassword.getText().toString();


                //data validation
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)|| TextUtils.isEmpty(cPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Invalid Data", Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    regEmail.setError("Invalid Email");
                    Toast.makeText(RegistrationActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(cPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                }else{
                    //create user
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DatabaseReference databaseReference = database.getReference()
                                        .child("user").child(auth.getUid());
                                StorageReference storageReference = storage.getReference()
                                        .child("upload").child(auth.getUid());
                                if(imageUri != null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        Users users = new Users(auth.getUid(), name, email,imageURI);
                                                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                                                }else{
                                                                    Toast.makeText(RegistrationActivity.this, "Error in Registration", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/chatbees-7c4bf.appspot.com/o/profile_image.png?alt=media&token=3aeaaf6a-5456-456f-9a98-aa79d561a136";
                                    Users users = new Users(auth.getUid(), name, email,imageURI);
                                    databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                            }else{
                                                Toast.makeText(RegistrationActivity.this, "Error in Registration", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        //profile image click listner
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 13);
            }
        });
        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==13){
            if(data != null){
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);
            }
        }
    }
}