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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class UpdateSongActivity extends AppCompatActivity {
    Button btn_saveSongToDB, btn_addMp3, btn_addImage, btn_addAlbums, btn_addSingers;
    TextInputLayout textInputLayout_songName, txtInputLayout_mp3FileName;
    EditText editText_songName, editText_mp3FileName;
    ImageView imageView_song;
    private Uri imageUri;
    private Uri mp3Uri;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    ProgressBar progressBar;
    StorageReference storageReferenceSongImg;
    StorageReference storageReferenceMp3;
    UploadTask uploadTaskImg;
    HashMap<String, Album> mListAlbumAdd;
    HashMap<String, Singer> mListSingerAdd;
    HashMap<String, Album> mListAlbumAddOld;
    HashMap<String, Singer> mListSingerAddOld;
    TextView tV_allAlbums, tV_allSingers;
    Song songInfo;
    String songId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewsong);
        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressBar = findViewById(R.id.progressBar);
        storageReferenceSongImg = FirebaseStorage.getInstance().getReference("song_img");
        storageReferenceMp3 = FirebaseStorage.getInstance().getReference("song_mp3");
        mListAlbumAdd = new HashMap<>();
        mListSingerAdd = new HashMap<>();
        mListAlbumAddOld = new HashMap<>();
        mListSingerAddOld = new HashMap<>();

        btn_saveSongToDB = findViewById(R.id.btn_saveSong);
        btn_addMp3 = findViewById(R.id.btn_addMusicResource);
        btn_addImage = findViewById(R.id.btn_addImage);
        btn_addAlbums = findViewById(R.id.btn_addAlbum);
        btn_addSingers = findViewById(R.id.btn_addSinger);

        txtInputLayout_mp3FileName = findViewById(R.id.layout_mp3FileName);
        textInputLayout_songName = findViewById(R.id.layout_songName);
        editText_songName = findViewById(R.id.editText_SongName);
        editText_mp3FileName = findViewById(R.id.editText_mp3FileName);
        imageView_song = findViewById(R.id.imgV_SongImg);
        tV_allSingers = findViewById(R.id.content_SingersRelate);
        tV_allAlbums = findViewById(R.id.content_AlbumsRelate);

        Intent intentGetFromAdminSongFragments = getIntent();
        songId = intentGetFromAdminSongFragments.getStringExtra("SongId");
        songInfo = (Song) intentGetFromAdminSongFragments.getSerializableExtra("SongInfo");

        btn_saveSongToDB.setText("Save change");
        btn_addMp3.setVisibility(View.GONE);
        btn_addImage.setText("Change image");
        btn_addAlbums.setText("Change albums");
        btn_addSingers.setText("Change singers");

        editText_songName.setText(songInfo.getName());
        editText_mp3FileName.setVisibility(View.GONE);
        txtInputLayout_mp3FileName.setVisibility(View.GONE);
        Glide.with(imageView_song.getContext())
                .load(songInfo.getImage())
                .placeholder(R.drawable.alec_album)
                .error(R.drawable.alec_album)
                .into(imageView_song);

        getAllAlbumsRelate();
        getAllSingersRelate();

        btn_addImage.setOnClickListener(view -> {
            chooseImage();
        });

        btn_addMp3.setOnClickListener(view -> {
            chooseMp3Source();
        });

        btn_saveSongToDB.setOnClickListener(view -> {
            if(!checkDataInputValid()) {
                Toast.makeText(this, "Song name is not valid", Toast.LENGTH_LONG).show();
                return;
            }
            saveImgToFireStore();
        });

        btn_addAlbums.setOnClickListener(view -> {
            Intent intent = new Intent(this, CheckListItemAlbumActivity.class);
            intent.putExtra("allAddAlbum", mListAlbumAdd);
            mListAlbumAddOld = (HashMap<String, Album>) mListAlbumAdd.clone();
            getAddAlbumActivityResultLauncher.launch(intent);
        });

        btn_addSingers.setOnClickListener(view -> {
            Intent intent = new Intent(this, CheckListItemSingerActivity.class);
            intent.putExtra("allAddSinger", mListSingerAdd);
            mListSingerAddOld = (HashMap<String, Singer>) mListSingerAdd.clone();
            getAddSingerActivityResultLauncher.launch(intent);
        });
    }

    private void setAllSingersRelate() {
        boolean isFirst = true;
        StringBuffer bufferStringSingerName = new StringBuffer();
        bufferStringSingerName.append("");
        for (Map.Entry<String, Singer> entry: mListSingerAdd.entrySet()) {
            if (isFirst) {
                isFirst = false;
                bufferStringSingerName.append(entry.getValue().getName());
            } else bufferStringSingerName.append(", " + entry.getValue().getName());
        }
        tV_allSingers.setText(bufferStringSingerName);
    }

    private void setAllAlbumsRelate() {
        boolean isFirst = true;
        StringBuffer bufferStringAlbumName = new StringBuffer();
        bufferStringAlbumName.append("");
        for (Map.Entry<String, Album> entry: mListAlbumAdd.entrySet()) {
            if (isFirst) {
                isFirst = false;
                bufferStringAlbumName.append(entry.getValue().getName());
            } else bufferStringAlbumName.append(", " + entry.getValue().getName());
        }
        tV_allAlbums.setText(bufferStringAlbumName);
    }

    public void getAllAlbumsRelate() {
        CollectionReference albumsRef = fStore.collection("albums");
        albumsRef.whereArrayContainsAny("relatedSongs", Arrays.asList(songId))
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mListAlbumAdd.put(document.getId(), document.toObject(Album.class));
                        setAllAlbumsRelate();
                    }
                } else {
                    Log.e("Firebase error", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getAllSingersRelate() {
        CollectionReference singersRef = fStore.collection("singers");
        singersRef.whereArrayContainsAny("relatedSongs", Arrays.asList(songId))
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mListSingerAdd.put(document.getId(), document.toObject(Singer.class));
                        setAllSingersRelate();
                    }
                } else {
                    Log.e("Firebase error", "Error getting documents: ", task.getException());
                }
            }
        });
    }


    ActivityResultLauncher getAddAlbumActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result != null && result.getData() != null) {
                        Intent data = result.getData();
                        mListAlbumAdd = (HashMap<String, Album>) data.getSerializableExtra("allAddAlbum");
                        setAllAlbumsRelate();
                    }
                }
            });

    ActivityResultLauncher getAddSingerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result != null && result.getData() != null) {
                        Intent data = result.getData();
                        mListSingerAdd = (HashMap<String, Singer>) data.getSerializableExtra("allAddSinger");
                        setAllSingersRelate();
                    }
                }
            });

    public boolean checkDataInputValid() {
        if (editText_songName.getText().equals("")) return false;
        return true;
    }

    private void saveImgToFireStore() {
        if(imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference referenceImg = storageReferenceSongImg.child(getFileName(imageUri) + "." + getExt(imageUri));
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
                        deleteOldImage();
                        updateSongToFireStore(downloadUri.toString());
                    }
                }
            });
        } else {
            updateSongToFireStore(songInfo.getImage());
        }
    }

    private void deleteOldImage() {
        StorageReference storageReference = storage.getReferenceFromUrl(songInfo.getImage());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("Success Storage delete: ", "Delete image successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Failure Storage delete: ", "Delete image fail");
            }
        });
    }

    private void updateSongToFireStore(String imageDownloadString) {
        Query query = fStore.collection("songs");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Map<String, Object> song = new HashMap<>();
                    song.put("name", editText_songName.getText().toString().trim());
                    song.put("image", imageDownloadString);
                    song.put("musicResource", songInfo.getMusicResource());
                    song.put("relatedSingers", saveSingersToSong());
                    fStore.collection("songs").document(songId)
                            .set(song)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(UpdateSongActivity.this, "Song saved", Toast.LENGTH_SHORT).show();
                                    updateSongToAlbums(songId);
                                    updateSongToSingers(songId);
                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateSongActivity.this, "Please check again", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Log.e("Count failed: ", task.getException().toString());
                }
            }
        });
    }

    private void updateSongToSingers(String songId) {
        for (HashMap.Entry<String, Singer> entry: mListSingerAdd.entrySet()) {
            boolean checkNotExistedKey = true;
            for (HashMap.Entry<String, Singer> entry2: mListSingerAddOld.entrySet()) {
                if (entry.getKey().equals(entry2.getKey())) checkNotExistedKey = false;
            }
            if (checkNotExistedKey) {
                DocumentReference AlbumReference = fStore.collection("singers").document(entry.getKey());
                AlbumReference.update("relatedSongs", FieldValue.arrayUnion(songId));
            }
        }

        for (HashMap.Entry<String, Singer> entry: mListSingerAddOld.entrySet()) {
            boolean checkExistedKey = true;
            for (HashMap.Entry<String, Singer> entry2: mListSingerAdd.entrySet()) {
                if (entry.getKey().equals(entry2.getKey())) checkExistedKey = false;
            }
            if (checkExistedKey) {
                DocumentReference SingerReference = fStore.collection("singers").document(entry.getKey());
                SingerReference.update("relatedSongs", FieldValue.arrayRemove(songId));
            }
        }

        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            onBackPressed();
        }
    }

    private ArrayList<String> saveSingersToSong() {
        ArrayList<String> allSingerId = new ArrayList<>();
        for (Map.Entry<String, Singer> entry: mListSingerAdd.entrySet()) {
            allSingerId.add(entry.getKey());
        }
        return allSingerId;
    }

    private void updateSongToAlbums(String songId) {
        for (HashMap.Entry<String, Album> entry: mListAlbumAdd.entrySet()) {
            boolean checkNotExistedKey = true;
            for (HashMap.Entry<String, Album> entry2: mListAlbumAddOld.entrySet()) {
                if (entry.getKey().equals(entry2.getKey())) checkNotExistedKey = false;
            }
            if (checkNotExistedKey) {
                DocumentReference AlbumReference = fStore.collection("albums").document(entry.getKey());
                AlbumReference.update("relatedSongs", FieldValue.arrayUnion(songId));
            }
        }

        for (HashMap.Entry<String, Album> entry: mListAlbumAddOld.entrySet()) {
            boolean checkExistedKey = true;
            for (HashMap.Entry<String, Album> entry2: mListAlbumAdd.entrySet()) {
                if (entry.getKey().equals(entry2.getKey())) checkExistedKey = false;
            }
            if (checkExistedKey) {
                DocumentReference AlbumReference = fStore.collection("albums").document(entry.getKey());
                AlbumReference.update("relatedSongs", FieldValue.arrayRemove(songId));
            }
        }

        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            onBackPressed();
        }
    }

    ActivityResultLauncher getMp3ActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result != null && result.getData() != null) {
                        Intent data = result.getData();
                        mp3Uri = data.getData();
                        editText_mp3FileName.setText(getFileName(mp3Uri));
                    }
                }
            });

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

    private void chooseMp3Source() {
        Intent intent = new Intent();
        intent.setType("audio/mpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getMp3ActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher getImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result != null && result.getData() != null) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        imageView_song.setImageURI(imageUri);
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