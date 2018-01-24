package biz.eastservices.tumpang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

public class DriverLoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
            startActivity(intent);
            finish();
        } else {
            // not signed in
            startActivityForResult(

                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .setAvailableProviders(
                                    Collections.singletonList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()

                                    )
                            )
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                current_user_db.setValue(true);

                Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);

                startActivity(intent);
                finish();

                return;
            }
        } else {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Sign in failed

            if (response == null) {
                // User pressed back button
                //showSnackbar(R.string.sign_in_cancelled);
                return;

            }
            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(DriverLoginActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(DriverLoginActivity.this, "UNKNOWN ERROR", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(DriverLoginActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();

    }

}
