                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          public class MainActivity extends AppCompatActivity {

    public static final String EMERGENCY_BRACE_DB_NAME = "db.txt";
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button btnSaveContacts = null;
        Button triggerFall = null;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSaveContacts = this.findViewById(R.id.saveContacts);
        triggerFall = this.findViewById(R.id.triggerFall);

        final EditText emegencyContactNum1 = this.findViewById(R.id.emergencyNum1);
        final EditText emegencyContactNum2 = this.findViewById(R.id.emergencyNum2);
        final EditText emergencyMsg = this.findViewById(R.id.large_edit_text);

        EditText finalEmegencyContactNum = emegencyContactNum1;
        EditText finalEmergencyMsg = emergencyMsg;

        btnSaveContacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //Toast.makeText(getApplicationContext(), "Saving contacs for emergency ", Toast.LENGTH_SHORT).show();
                saveContactInDB(finalEmegencyContactNum.getText().toString(),emegencyContactNum2.getText().toString(), finalEmergencyMsg.getText().toString());

                /*String phoneNumber = "1221"; // Replace with your desired phone number
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(dialIntent);
                */
            }
        });

        triggerFall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manFellDown();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("message").setValue("Hello, World!");

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                //Log.d("Emeregency brace", "got message from server Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    /**
     * This function will save in a text file the emergency numbers which the user choose in the main screen
     * @param emergencyNum1
     * @param emergencyNum2
     * @param emergencyMsg
     */
    private void saveContactInDB(String emergencyNum1, String emergencyNum2, String emergencyMsg) {
        try {
           PhoneNumberUtils.formatNumber(emergencyNum1);
           PhoneNumberUtils.formatNumber(emergencyNum2);

            //For example : 0543540000#0552282628#Help its saba Yaaakov I fell down
            String saveText = new StringBuffer()
                    .append(emergencyNum1).append("#")
                    .append(emergencyNum2).append("#")
                    .append(emergencyMsg).toString();

            FileOutputStream fos = this.openFileOutput(EMERGENCY_BRACE_DB_NAME, Context.MODE_APPEND);
            fos.write((saveText + "\n").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is the function which the Firebase server will call via Server -Client to send SMS to emergency contacts
     */
    public void manFellDown(){
        //Fetch from DB the contacts and the messgae which the user defined and send SMS
        DbEmergencyData dbEmergencyData = readFromDB();
        sendSMS(dbEmergencyData.emergencyContacNum1,dbEmergencyData.emergencyMsg);
        sendSMS(dbEmergencyData.emergencyContacNum2,dbEmergencyData.emergencyMsg);
    }

    /**
     * This Fucntion read the emergency contacts numbers when the app needs to send emergency SMS
     * @return
     */
    private DbEmergencyData readFromDB() {
        StringBuilder builder = new StringBuilder();

        try {
            //open and read from DB file the line of emergency contcact and msg
            FileInputStream fis = this.openFileInput(EMERGENCY_BRACE_DB_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //For example : 0543540000#0552282628#Help its saba Yaaakov I fell down
        String[] parts = builder.toString().split("#");
        if(parts != null && parts.length >=3) {
            DbEmergencyData dbEmergencyData = new DbEmergencyData(parts[0], parts[1], parts[2]);
            return dbEmergencyData;
        }

        return null;
    }

    private void sendSMS(String phoneNumber, String msg){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            // Optional: Add a Toast or log message to confirm sending
            // Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Handle potential exceptions (e.g., no SMS capabilities, permission denied)
            // Toast.makeText(getApplicationContext(), "SMS failed to send.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}