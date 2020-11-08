package com.example.safejourney;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class portal extends AppCompatActivity {

    private AlertDialog.Builder dialogbuilder;
    private AlertDialog dialog;
    private EditText contact1, contact2, contact3, contact4;
    private Button okay, cancel;

    private Button capture, take_txt;
    private ImageView image;
    private TextView txt;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private FirebaseAuth pauth;
    //private Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pauth = FirebaseAuth.getInstance();
        capture = findViewById(R.id.take_img);
        take_txt = findViewById(R.id.take_text);
        image = findViewById(R.id.img);
        txt = findViewById(R.id.text_display);



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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cuser = pauth.getCurrentUser();
        if(cuser == null)
        {
            startActivity(new Intent(getApplicationContext(), login.class));
            finish();
        }

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
                Toast.makeText(portal.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG);
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
        contact1 =(EditText) findViewById(R.id.contact1);
        contact2 =(EditText) findViewById(R.id.contact2);
        contact3 =(EditText) findViewById(R.id.contact3);
        contact4 =(EditText) findViewById(R.id.contact4);
        okay = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        dialogbuilder.setView(contactpop);
        dialog = dialogbuilder.create();
        dialog.show();

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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