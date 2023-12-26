package com.example.examenfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth Auth;
    TextView txtRegisterM;
    EditText editEmailM, editPassM;
    Button buttonLoginM, buttonMenuM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();

        txtRegisterM = findViewById(R.id.txtRegisterMain);
        editEmailM = findViewById(R.id.editEmailMain);
        editPassM = findViewById(R.id.editPassMain);
        buttonLoginM = findViewById(R.id.buttonLoginMain);
        buttonMenuM = findViewById(R.id.buttonMenuMain);

        buttonLoginM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmailM.getText().toString();
                String pass = editPassM.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        Auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(MainActivity.this, "Bienvenido Usuario.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Ingrese su Contraseña", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        txtRegisterM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        buttonMenuM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });

    }
}
