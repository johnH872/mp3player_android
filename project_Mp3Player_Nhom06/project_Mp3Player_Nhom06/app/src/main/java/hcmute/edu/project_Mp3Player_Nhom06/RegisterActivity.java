package hcmute.edu.project_Mp3Player_Nhom06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hcmute.edu.project_Mp3Player_Nhom06.adminActivity.AdminActivity;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextBirthday, editTextName;
    TextInputLayout emailLayout, passwordLayout, nameLayout, birthdayLayout;
    Button btnRegister;
    FirebaseAuth mAuth;
    FirebaseFirestore mFireStore;
    String userID;
    ProgressBar progressBar;
    TextView loginNow;
    private static final String EMAIL_ERROR = "Invalid email address";
    private static final String PASSWORD_ERROR = "8 Characters or longer. At least one number and character";
    private static final String NAME_ERROR = "Invalid name. Between 4-20 characters.";
    private static final String EMAIL_EXISTED_ERROR = "Email existed";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            checkUserAccessLevel(mAuth.getCurrentUser().getUid());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        nameLayout = findViewById(R.id.name_layout);
        birthdayLayout = findViewById(R.id.birth_layout);
        editTextEmail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        editTextBirthday = findViewById(R.id.input_birthDay);
        editTextName = findViewById(R.id.input_name);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        loginNow = findViewById(R.id.loginNow);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                myCalendar.set(Calendar.YEAR, y);
                myCalendar.set(Calendar.MONTH, m);
                myCalendar.set(Calendar.DAY_OF_MONTH, d);
                editTextBirthday.setText(DATE_FORMAT.format(myCalendar.getTime()));
            }
        };

        editTextBirthday.setOnClickListener(view -> {
            new DatePickerDialog(this, date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        loginNow.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
                passwordLayout.setError(null);
                nameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
                passwordLayout.setError(null);
                nameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
                passwordLayout.setError(null);
                nameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnRegister.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);
            String email, password, name, day;
            email = editTextEmail.getText().toString().trim();
            password = editTextPassword.getText().toString().trim();
            name = editTextName.getText().toString().trim();
            day = editTextBirthday.getText().toString();
            if (!isValidEmail(email)) {
                emailLayout.setError(EMAIL_ERROR);
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (!isValidPassword(password)) {
                passwordLayout.setError(PASSWORD_ERROR);
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (!isValidName(name)) {
                nameLayout.setError(NAME_ERROR);
                progressBar.setVisibility(View.GONE);
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = mFireStore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                try {
                                    user.put("birth", DATE_FORMAT.parse(day));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                user.put("favoriteSongs", new ArrayList<>());
                                user.put("isAdmin", "0");
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "Account created.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user
                                Toast.makeText(RegisterActivity.this, EMAIL_EXISTED_ERROR, Toast.LENGTH_LONG).show();
                                emailLayout.setError(EMAIL_EXISTED_ERROR);
                            }
                        }
                    });
        });
    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = mFireStore.collection("users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin").equals("0")) {
                    // Sign in success, update UI with the signed-in user's information
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                if (documentSnapshot.getString("isAdmin").equals("1")) {
                    // Sign in success, update UI with the signed-in user's information
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean isValidName(String name) {
        Pattern pattern;
        Matcher matcher;
        final String NAME_PATTERN = "^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);

        return matcher.matches();
    }


    private boolean isValidEmail(String email) {
        return (TextUtils.isEmpty(email) || Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}