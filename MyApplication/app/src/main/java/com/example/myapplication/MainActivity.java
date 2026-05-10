                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          package com.example.myapplication;

import android.Manifest;
import android.content.Context; //מאפשר גישה למשאבי המכשיר (קבצים, שירותים, הגדרות).
import android.content.pm.PackageManager;
import android.os.Bundle;// משמש להעברת ושמירת מידע בין מסכים באפליקציה.
import android.telephony.PhoneNumberUtils; // פונקציות שעוזרות לבדוק ולעבד מספרי טלפון.
import android.telephony.SmsManager;//מאפשר לשלוח הודעות SMS דרך האפליקציה
import android.util.Log;//משמש להדפיס הודעות דיבוג למסך Logcat
import android.view.View;//מייצג כל רכיב בתצוגה (כפתור, טקסט ועוד)
import android.widget.Button;//שימוש בכפתור במסך
import android.widget.EditText;//שימוש בתיבת קלט שבה המשתמש יכול לכתוב

import androidx.activity.EdgeToEdge;//מאפשר לממשק לעבוד במסך מלא
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;//מחלקת Activity בסיסית שאיתה בונים מסך באפליקציה
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;//עוזר לקבל מידע על שוליים/מסגרת של המסך
import androidx.core.view.ViewCompat;//כלים נוספים לניהול תצוגה (Views)
import androidx.core.view.WindowInsetsCompat;//עוזר להתאים את העיצוב לשוליים של מסכים שונים

import java.io.BufferedReader;//קורא טקסט מקבצים בצורה יעילה ומהירה
import java.io.FileInputStream;//פותח קובץ לקריאה
import java.io.FileOutputStream;//פותח קובץ לכתיבה
import java.io.IOException;//טיפול בשגיאות שקורות בזמן קריאת/כתיבת קבצים
import java.io.InputStreamReader;//ממיר נתוני קובץ לטקסט לקריאה

import com.google.firebase.database.DataSnapshot;//מייצג נתונים שהגיעו מהפיירבייס
import com.google.firebase.database.DatabaseError;//שגיאה שהתקבלה מפיירבייס
import com.google.firebase.database.DatabaseReference;//מצביע למיקום מסוים בתוך מסד הנתונים
import com.google.firebase.database.FirebaseDatabase;//החיבור עצמו למסד הנתונים בענן
import com.google.firebase.database.ValueEventListener;//מאזין לשינויים בדאטה — מופעל כשנתונים משתנים

public class MainActivity extends AppCompatActivity {

    public static final String EMERGENCY_BRACE_DB_NAME = "db.txt";
    private DatabaseReference databaseReference;
    private boolean appInitiated = false;

    @Override//מציין שאנחנו משתמשים בגרסה משלנו לפונקציה שקיימת במחלקה אחרת
    protected void onCreate(Bundle savedInstanceState) {//הפונקציה שנקראת כשהמסך נוצר. כאן בונים את ה־UI ומחברים את הכפתורים
        Button btnSaveContacts = null;//הכרזה על משתנה לכפתור שמאוחר יותר נגדיר מה הוא
        Button triggerFall = null;//עוד כפתור שנשתמש בו בהמשך

        super.onCreate(savedInstanceState);//קורא לגרסה המקורית של onCreate מהמחלקה שממנה ירשנו
        EdgeToEdge.enable(this);//מפעיל מצב מסך־מלא יפה וחדש
        setContentView(R.layout.activity_main);//טוען את קובץ ה־XML שמציג את מסך האפליקציה
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {//מתאים את המסך לשוליים של הטלפון (מצלמה, ניווט.. )
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());//מתאים את המסך לשוליים של הטלפון (מצלמה, ניווט וכו')
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);//מתאים את המסך לשוליים של הטלפון (מצלמה, ניווט וכו')
            return insets;//מתאים את המסך לשוליים של הטלפון (מצלמה, ניווט וכו')
        });

        btnSaveContacts = this.findViewById(R.id.saveContacts);//מחבר את הכפתור שמירת אנשי קשר מהעיצוב לקוד.
        triggerFall = this.findViewById(R.id.triggerFall);//מחבר כפתור נוסף למסך (כנראה הדמיית נפילה)

        final EditText emegencyContactNum1 = this.findViewById(R.id.emergencyNum1);//מחבר את שדה מספר חירום 1
        final EditText emegencyContactNum2 = this.findViewById(R.id.emergencyNum2);//מחבר את שדה מספר חירום 2
        final EditText emergencyMsg = this.findViewById(R.id.large_edit_text);//מחבר את שדה הודעת החירום שהמשתמש כותב

        EditText finalEmegencyContactNum = emegencyContactNum1;//יוצר עותק "final" כי צריך את זה בתוך לחיצה על כפתור
        EditText finalEmergencyMsg = emergencyMsg;//כנ"ל – לגרסה לשימוש בתוך onClick

        btnSaveContacts.setOnClickListener(new View.OnClickListener() {//אומר מה יקרה כשהכפתור יילחץ
            public void onClick(View v)//הפעולה שמתבצעת בלחיצה
            {//
                //Toast.makeText(getApplicationContext(), "Saving contacs for emergency ", Toast.LENGTH_SHORT).show();//
                saveContactInDB(finalEmegencyContactNum.getText().toString(),emegencyContactNum2.getText().toString(), finalEmergencyMsg.getText().toString());//קורא לפונקציה ששומרת את מספר טלפון 1ו2 ושומרת את הודעת החירום
                appInitiated = true;
                /*String phoneNumber = "1221"; // Replace with your desired phone number//
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);//
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));//
                startActivity(dialIntent);//
                *///
            }//
        });//

        triggerFall.setOnClickListener(new View.OnClickListener() {//מאזין לאירוע לחיצה על כפתור triggerFall
            @Override//מציין שהפונקציה הזו מחליפה פונקציה קיימת (onClick)
            public void onClick(View view) {//כאשר נלחץ הכפתור
                manFellDown();//קורא לפונקציה שמדמה מצב חירום
            }//סגירת פונקציית onClick
        });//סגירת מאזין הלחיצה
    }

    protected void onPostCreate(@Nullable Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        Log.i("Emeregency brace","App initiated. register to Firebase !!");
        databaseReference = FirebaseDatabase.getInstance().getReference();//מקבל הפניה למסד הנתונים של Firebase
        //databaseReference.child("message").setValue("Hello, World!");//יוצר או מעדכן את השדה "message" בערך "Hello, World!"

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {//מאזין לשינויים במסד הנתונים בזמן אמת
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//נקרא בכל פעם שהערך ב-"message" משתנה
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //Map value = dataSnapshot.getValue(HashMap.class);//מקבל את הערך החדש כטקסט
                if(appInitiated) {
                    Log.d("Emeregency brace", "got message from server Value is:" + dataSnapshot.toString());
                    manFellDown();//קורא שוב לפונקציית החירום כדי לשלוח SMS
                }else{
                    Log.d("Emeregency brace", "emergency contacts were not saved yet");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {//קרא במקרה של כשל בקריאה מהמסד
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }//סגירת onCancelled
        });//סגירת ה-ValueEventListener
    }

    /**
     * This function will save in a text file the emergency numbers which the user choose in the main screen
     * @param emergencyNum1
     * @param emergencyNum2
     * @param emergencyMsg
     */
    private void saveContactInDB(String emergencyNum1, String emergencyNum2, String emergencyMsg) {//פונקציה לשמירת מספרי חירום והודעה בקובץ פנימי
        try {//מתחיל טיפול בשגיאות בזמן כתיבה לקובץ
           PhoneNumberUtils.formatNumber(emergencyNum1);//מעצב את המספר הראשון בפורמט סטנדרטי
           PhoneNumberUtils.formatNumber(emergencyNum2);//מעצב את המספר השני בפורמט סטנדרטי

            //For example : 0543540000#0552282628#Help its saba Yaaakov I fell down
            String saveText = new StringBuffer()//מחבר את המספרים וההודעה למחרוזת אחת עם מפריד "#"
                    .append(emergencyNum1).append("#")//מחבר את המספרים וההודעה למחרוזת אחת עם מפריד "#"
                    .append(emergencyNum2).append("#")//מחבר את המספרים וההודעה למחרוזת אחת עם מפריד "#"
                    .append(emergencyMsg).toString();//מחבר את המספרים וההודעה למחרוזת אחת עם מפריד "#"

            FileOutputStream fos = this.openFileOutput(EMERGENCY_BRACE_DB_NAME, Context.MODE_APPEND);//פותח קובץ פנימי של האפליקציה לכתיבה
            fos.write((saveText + "\n").getBytes());//שומר את המחרוזת בקובץ ומוסיף שורה חדשה
            fos.close();//סוגר את הקובץ לאחר כתיבה
            Log.d("EmergencyBrace", "Emergency contact were saved in DB ");
        } catch (IOException e) {//תופס שגיאה אפשרית בזמן כתיבה
            e.printStackTrace();//מדפיס את השגיאה ללוג
        }//סיום ה-try-catch
    }//סיום הפונקציה saveContactInDB

    /**
     * This is the function which the Firebase server will call via Server -Client to send SMS to emergency contacts
     */
    public void manFellDown(){//פונקציה המדמה מצב חירום לשליחת SMS
        //Fetch from DB the contacts and the messgae which the user defined and send SMS
        DbEmergencyData dbEmergencyData = readFromDB();//קורא את מספרי החירום וההודעה מהקובץ
        sendSMS(dbEmergencyData.emergencyContacNum1,dbEmergencyData.emergencyMsg);//שולח SMS למספר החירום הראשון
        sendSMS(dbEmergencyData.emergencyContacNum2,dbEmergencyData.emergencyMsg);//שולח SMS למספר החירום השני
    }//סיום הפונקציה manFellDown

    /**
     * This Fucntion read the emergency contacts numbers when the app needs to send emergency SMS
     * @return
     */
    private DbEmergencyData readFromDB() {//פונקציה שקוראת נתוני חירום מהקובץ ומחזירה אובייקט.
        StringBuilder builder = new StringBuilder();//יוצר משתנה לאגירת הטקסט שנקרא מהקובץ

        try {//התחלת בלוק לטיפול בשגיאות
            //open and read from DB file the line of emergency contcact and msg
            FileInputStream fis = this.openFileInput(EMERGENCY_BRACE_DB_NAME);//פותח את הקובץ לקריאה
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));//יוצר קורא שורות מהקובץ

            String line;//משתנה לשמירת כל שורה שנקראת
            while ((line = reader.readLine()) != null) {//לולאה שקוראת שורה-שורה עד סוף הקובץ
                builder.append(line).append("\n");//מוסיף כל שורה למשתנה builder
            }//סיום הלולאה

            reader.close();//סוגר את הקורא
            fis.close();//סוגר את הקובץ
        } catch (IOException e) {//תופס שגיאות אפשריות בקריאה
            e.printStackTrace();//מדפיס את השגיאה ללוג
        }//סיום try-catch

        //For example : 0543540000#0552282628#Help its saba Yaaakov I fell down
        String[] parts = builder.toString().split("#");//מפצל את הטקסט לפי הסימן "#"
        if(parts != null && parts.length >=3) {//בודק שיש מספיק נתונים לפחות 3 חלקים
            DbEmergencyData dbEmergencyData = new DbEmergencyData(parts[0], parts[1], parts[2]);//יוצר אובייקט עם שני מספרים והודעה
            return dbEmergencyData;//מחזיר את הנתונים שנקראו
        }//סיום תנאי

        return null;//אם אין נתונים תקינים – מחזיר null
    }//סיום הפונקציה

    private void sendSMS(String phoneNumber, String msg){//פונקציה לשליחת SMS למספר מסוים
        Log.d("Emeregency brace", "Sending SMS to"+ phoneNumber);
        try {//התחלת בלוק לטיפול בשגיאות
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();//מקבל את מנהל ה-SMS של המכשיר
                smsManager.sendTextMessage(phoneNumber, null, msg, null, null);//שולח הודעת SMS למספר עם הטקסט
            }
        } catch (Exception e) {//תופס שגיאות למשל אין הרשאה או אין אפשרות לשלוח SMS
            // Handle potential exceptions (e.g., no SMS capabilities, permission denied)
            // Toast.makeText(getApplicationContext(), "SMS failed to send.", Toast.LENGTH_LONG).show();
            e.printStackTrace();//מדפיס את השגיאה ללוג
        }//סיום try-catch
    }//סיום הפונקציה
}//