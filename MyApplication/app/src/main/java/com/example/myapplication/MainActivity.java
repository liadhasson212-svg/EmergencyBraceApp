package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button btnSaveContacts = null;
        Button btnMada = null;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSaveContacts = this.findViewById(R.id.saveContacts);
        btnSaveContacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Saving contacs for emergency ", Toast.LENGTH_SHORT).show();
                /*String phoneNumber = "1221"; // Replace with your desired phone number
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(dialIntent);
                */

            }
        });
    }

    /**
     * This is the function which the esp32 will call via BT/Wifi to send SMS to emergency contacts
     */
    public void manFellDown(){
        //Fetch from DB the contacts and the messgae which the user defined and send SMS
        readFromDB()
        String phoneNumber1 = "0543540000";
        String phoneNumber2 = "0552282628";
        String msg = "Help !!!\n" +
                "            , I fell down please call Mada now and come to my house\" ";
        String dbWrite = phoneNumber1+","+phoneNumber2+","+msg;

        sendSMS(phoneNumber1,msg);
        sendSMS(phoneNumber2,msg);
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