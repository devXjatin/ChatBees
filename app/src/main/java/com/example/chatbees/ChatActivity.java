package com.example.chatbees;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String receiverImage, receiverUid, receiverName, senderUid;
    CircleImageView profileImage;
    TextView receiverChatName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String senderImage;
    public static String rImage;

    CardView sendBtn;
    EditText sendMessage;

    String senderRoom, receiverRoom;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesArrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        receiverName = getIntent().getStringExtra("name");
        receiverImage = getIntent().getStringExtra("receiverImage");
        receiverUid = getIntent().getStringExtra("uid");

        messageAdapter = findViewById(R.id.messageAdapter);

        profileImage = findViewById(R.id.chatProfileImage);
        receiverChatName = findViewById(R.id.receiver_name);

        Picasso.get().load(receiverImage).into(profileImage);
        receiverChatName.setText("" + receiverName);

        senderUid = firebaseAuth.getUid();

        senderRoom = senderUid+receiverUid;
        receiverRoom = receiverUid+senderUid;

        sendBtn = findViewById(R.id.send_btn);
        sendMessage = findViewById(R.id.send_message);

        //get sender image
        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImage = snapshot.child("imageUri").getValue().toString();
                rImage = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //chat refernce
        DatabaseReference chatReference = database.getReference().child("chats")
                .child(senderRoom).child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //send button listner
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = sendMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please Enter valid message", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage.setText("");
                Date date = new Date();
                Messages messages = new Messages(message, senderUid, date.getTime());

                database.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push().
                        setValue(messages)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference()
                                .child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push()
                                .setValue(messages)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                    }
                });
            }
        });

    }
}