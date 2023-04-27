package hcmute.edu.project_Mp3Player_Nhom06.adminActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.project_Mp3Player_Nhom06.R;

public class AddNewAlbumActivity extends AppCompatActivity {
    Button btn_saveAlbum, btn_addImage;
    TextInputLayout textInputLayout_albumName, textInputLayout_albumDescription;
    EditText editText_albumName, editText_albumDescription;
    ImageView imageView_image;
    private Uri imageUri;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    StorageReference storageReferenceSingerImg;
    UploadTask uploadTaskImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_add_albums);
        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        storageReferenceSingerImg = FirebaseStorage.getInstance().getReference("album_img");

        btn_saveAlbum = findViewById(R.id.btn_saveAlbum);
        btn_addImage = findViewById(R.id.btn_addImage);

        textInputLayout_albumName = findViewById(R.id.layout_albumName);
        textInputLayout_albumDescription = findViewById(R.id.layout_albumDescription);
        editText_albumName = findViewById(R.id.editText_albumName);
        editText_albumDescription = findViewById(R.id.editText_albumDescription);
        imageView_image = findViewById(R.id.imgV_albumImg);

        btn_addImage.setOnClickListener(view -> {
            chooseImage();
        });

        btn_saveAlbum.setOnClickListener(view -> {
            if(!checkDataInputValid()) {
                Toast.makeText(this, "Album name or description is not valid", Toast.LENGTH_LONG).show();
                return;
            }
            saveImgToStorage();
        });
    }

    public boolean checkDataInputValid() {
        if (editText_albumName.getText().equals("") || editText_albumDescription.getText().equals("")) return false;
        return true;
    }

    private void saveImgToStorage() {
        if(imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference referenceImg = storageReferenceSingerImg.child(getFileName(imageUri) + "." + getExt(imageUri));
            uploadTaskImg = referenceImg.putFile(imageUri);
            Task<Uri> urlTask = uploadTaskImg.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return referenceImg.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        SaveAlbumToFireStore(downloadUri.toString());
                    }
                }
            });
        } else {
            Toast.makeText(this, "Image is required", Toast.LENGTH_LONG).show();
        }
    }

    private void SaveAlbumToFireStore(String imageDownloadString) {
        Query query = fStore.collection("albums");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    String idGenerated = "ALB"+(snapshot.getCount()+1);
                    Map<String, Object> singer = new HashMap<>();
                    singer.put("name", editText_albumName.getText().toString().trim());
                    singer.put("image", imageDownloadString);
                    singer.put("description", editText_albumDescription.getText().toString().trim());
                    singer.put("relatedSongs", new ArrayList<>());
                    fStore.collection("albums").document(idGenerated)
                            .set(singer)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddNewAlbumActivity.this, "Album saved", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddNewAlbumActivity.this, "Please check again", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Log.e("Count failed: ", task.getException().toString());
                }
            }
        });
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut == -1) {
                result = result.substring(cut+1);
            }
        }
        return  result;
    }

    ActivityResultLauncher getImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result != null && result.getData() != null) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        imageView_image.setImageURI(imageUri);
                    }
                }
            });

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImageActivityResultLauncher.launch(intent);
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}