package com.example.chatbees;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String receiverImage, receiverUid, receiverName;
    CircleImageView profileImage;
    TextView receiverChatName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String senderImage;
    public static String rImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        receiverName = getIntent().getStringExtra("name");
        receiverImage = getIntent().getStringExtra("receiverImage");
        receiverUid = getIntent().getStringExtra("uid");

        profileImage = findViewById(R.id.chatProfileImage);
        receiverChatName = findViewById(R.id.receiver_name);

        Picasso.get().load(receiverImage).into(profileImage);
        receiverChatName.setText(""+receiverName);

        //get sender image
        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImage=snapshot.child("imageUri").getValue().toString();
                rImage = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}