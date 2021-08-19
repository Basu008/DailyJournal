package ui;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class JournalMethodsManager extends AppCompatActivity {

    public static void sendMessage(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey there, I just posted a new update about my day" +
                "on DailyJournal app, Check it out!");
    }

}
