package com.example.safejourney;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class test extends AppCompatActivity {
    private AlertDialog.Builder dialogbuilder;
    private AlertDialog dialog;
    private EditText contact1, contact2, contact3, contact4;
    private Button okay, cancel;
    DatabaseReference contactss;


    private Button capture, take_txt;
    private ImageView image;
    private TextView txt;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private  Button clickme;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        capture = findViewById(R.id.take_img);
        take_txt = findViewById(R.id.take_text);
        image = findViewById(R.id.img);
        txt = findViewById(R.id.text_display);
        clickme = (Button) findViewById(R.id.contactit);
        fAuth = FirebaseAuth.getInstance();
        contactss = FirebaseDatabase.getInstance().getReference("User");
        int f=0;


        dialogueBox();

        clickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogueBox();
            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        take_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
                txt.setText("");
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }


    private void detectText() {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayText(firebaseVisionText);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(test.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void displayText(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();
        if(blockList.size()==0){
            Toast.makeText(this, "no text found", Toast.LENGTH_LONG);
        }
        else{
            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()){
                String texts = block.getText();
                txt.setText(texts);
                Log.d("msg", texts);
            }
        }
    }

    public void logout_click(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), login.class));
    }

    public void dialogueBox(){
        dialogbuilder = new AlertDialog.Builder(this);
        final View contactpop = getLayoutInflater().inflate(R.layout.popup, null);
        contact1 =(EditText) contactpop.findViewById(R.id.contact1);
        contact2 =(EditText) contactpop.findViewById(R.id.contact2);
        contact3 =(EditText) contactpop.findViewById(R.id.contact3);
        contact4 =(EditText) contactpop.findViewById(R.id.contact4);
        okay = (Button) contactpop.findViewById(R.id.ok);
        cancel = (Button) contactpop.findViewById(R.id.cancel);

        dialogbuilder.setView(contactpop);
        dialog = dialogbuilder.create();
        dialog.show();

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("msg", String.valueOf(contact1));
                String ph1 = contact1.getText().toString();
                String ph2 = contact2.getText().toString();
                String ph3 = contact3.getText().toString();
                String ph4 = contact4.getText().toString();
                if(TextUtils.isEmpty(ph1)){
                    contact1.setError("cannot be empty");
                    return;
                }
                if(TextUtils.isEmpty(ph2)){
                    contact2.setError("cannot be empty");
                    return;
                }
                if(TextUtils.isEmpty(ph3)){
                    contact3.setError("cannot be empty");
                    return;
                }
                if(TextUtils.isEmpty(ph4)){
                    contact4.setError("cannot be empty");
                    return;
                }
                if (ph1.length()!=10){
                    contact1.setError("INVALID");
                    return;
                }
                if (ph2.length()!=10){
                    contact2.setError("INVALID");
                    return;
                }
                if (ph3.length()!=10){
                    contact3.setError("INVALID");
                    return;
                }
                if (ph4.length()!=10){
                    contact4.setError("INVALID");
                    return;
                }

                String Userid = fAuth.getCurrentUser().getUid();
                //String uid = contactss.push().getKey();
                //Map<String, Object> u = new HashMap<>();
                //u.put("phoneNumber 1", ph1);
                //u.put("phoneNumber 2", ph2);
                //u.put("phoneNumber 3", ph3);
                //u.put("phoneNumber 4", ph4);
                contactss.child(Userid).child("phoneNumber1").setValue(ph1);
                contactss.child(Userid).child("phoneNumber2").setValue(ph2);
                contactss.child(Userid).child("phoneNumber3").setValue(ph3);
                contactss.child(Userid).child("phoneNumber4").setValue(ph4);
                Toast.makeText(test.this, "your done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}