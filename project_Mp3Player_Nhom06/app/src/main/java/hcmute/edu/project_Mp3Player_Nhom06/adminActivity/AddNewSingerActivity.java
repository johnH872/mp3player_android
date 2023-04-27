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

public class AddNewSingerActivity extends AppCompatActivity {
    Button btn_saveSinger, btn_addImage, btn_addWallpaper;
    TextInputLayout textInputLayout_singerName;
    EditText editText_singerName;
    ImageView imageView_image, imageView_wallpaper;
    private Uri imageUri, wallpaperUri;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    StorageReference storageReferenceSingerImg, storageReferenceSingerWallpaper;
    UploadTask uploadTaskImg, uploadTaskWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_add_singers);
        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        storageReferenceSingerImg = FirebaseStorage.getInstance().getReference("singer_img/singer_profile_img");
        storageReferenceSingerWallpaper = FirebaseStorage.getInstance().getReference("singer_img/singer_wallpaper");

        btn_saveSinger = findViewById(R.id.btn_saveSinger);
        btn_addImage = findViewById(R.id.btn_addImage);
        btn_addWallpaper = findViewById(R.id.btn_addWallpaper);

        textInputLayout_singerName = findViewById(R.id.layout_singerName);
        editText_singerName = findViewById(R.id.editText_singerName);
        imageView_image = findViewById(R.id.imgV_SingerImg);
        imageView_wallpaper = findViewById(R.id.imgV_WallpaperImg);

        btn_addImage.setOnClickListener(view -> {
            chooseImage("Image");
        });

        btn_addWallpaper.setOnClickListener(view -> {
            chooseImage("Wallpaper");
        });

        btn_saveSinger.setOnClickListener(view -> {
            if(!checkDataInputValid()) {
                Toast.makeText(this, "Singer name is not valid", Toast.LENGTH_LONG).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            saveImgToStorage();
        });
    }

    public boolean checkDataInputValid() {
        if (editText_singerName.getText().equals("")) return false;
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
                        saveWallpaperToStorage(downloadUri.toString());
                    }
                }
            });
        } else {
            Toast.makeText(this, "Image is required", Toast.LENGTH_LONG).show();
        }
    }

    private void saveWallpaperToStorage(String imageDownloadUrl) {
        if(wallpaperUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference referenceWallpaper = storageReferenceSingerWallpaper.child(getFileName(wallpaperUri) + "." + getExt(wallpaperUri));
            uploadTaskWallpaper = referenceWallpaper.putFile(wallpaperUri);
            Task<Uri> urlTask = uploadTaskWallpaper.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return referenceWallpaper.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        SaveSingerToFireStore(imageDownloadUrl, downloadUri.toString());
                    }
                }
            });
        } else {
            Toast.makeText(this, "Wallpaper is required", Toast.LENGTH_LONG).show();
        }
    }

    private void SaveSingerToFireStore(String imageDownloadString, String wallpaperDownloadString) {
        Query query = fStore.collection("singers");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    String idGenerated = "SG"+(snapshot.getCount()+1);
                    Map<String, Object> singer = new HashMap<>();
                    singer.put("name", editText_singerName.getText().toString().trim());
                    singer.put("image", imageDownloadString);
                    singer.put("wallpaper", wallpaperDownloadString);
                    singer.put("relatedSongs", new ArrayList<>());
                    fStore.collection("singers").document(idGenerated)
                            .set(singer)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddNewSingerActivity.this, "Singer saved", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddNewSingerActivity.this, "Please check again", Toast.LENGTH_SHORT).show();
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

    ActivityResultLauncher getWallpaperActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result != null && result.getData() != null) {
                        Intent data = result.getData();
                        wallpaperUri = data.getData();
                        imageView_wallpaper.setImageURI(wallpaperUri);
                    }
                }
            });

    private void chooseImage(String type) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (type.equals("Image")) getImageActivityResultLauncher.launch(intent);
        if (type.equals("Wallpaper")) getWallpaperActivityResultLauncher.launch(intent);
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}