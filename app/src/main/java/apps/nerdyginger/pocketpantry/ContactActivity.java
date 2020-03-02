package apps.nerdyginger.pocketpantry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;


public class ContactActivity extends AppCompatActivity {
    private boolean messageSuccess;
    private TextInputEditText messageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final Button submitBtn = findViewById(R.id.contactSubmitBtn);
        submitBtn.setEnabled(false);
        messageBox = findViewById(R.id.contactMessage);
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (messageBox.getText().toString().equals("")) {
                    submitBtn.setEnabled(false);
                } else {
                    submitBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(messageBox.getText().toString());
                if (messageSuccess) {
                    submitBtn.setEnabled(false);
                }
            }
        });
    }

    protected void sendEmail(String message) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            if (keyStore != null) {
                //do some stuff
                Log.e("DEBUG_DEBUG", "This is an important message!");
                generator.generateKeyPair();
            }

        } catch (Exception e) {
            Log.e("MAIL_ERROR", e.toString());
            messageSuccess = false;
            return;
        }

        BackgroundMail.newBuilder(this)
                .withUsername("gingerthenerd@gmail.com")
                .withPassword("Ju5tSh1n3*")
                .withMailTo("gingerthenerd@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Pocket Pantry: COMMENT")
                .withBody(message)
                .withSendingMessage("Sending...")
                .withOnSuccessCallback(new BackgroundMail.OnSendingCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                        messageBox.setText("");
                        messageSuccess = true;
                    }

                    @Override
                    public void onFail(Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to send email", Toast.LENGTH_SHORT).show();
                        Log.e("MAIL_ERROR", e.toString());
                        messageSuccess = false;
                    }
                })
                .send();
    }
}
