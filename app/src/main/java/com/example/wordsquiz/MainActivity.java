package com.example.wordsquiz;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wordsquiz.WordQuizDatabaseHelper;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText editTextQuestion;
    private EditText editTextAnswer;
    private Button buttonAddQuestion;
    private WordQuizDatabaseHelper dbHelper;
    private int wrongAnswerCount = 0;
    private CountDownTimer countDownTimer;
    private boolean isFrozen = false;
    private ActivityResultLauncher<Intent> overlayPermissionLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new WordQuizDatabaseHelper(this);

        editTextQuestion = findViewById(R.id.editTextQuestion);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonAddQuestion = findViewById(R.id.buttonAddQuestion);

        buttonAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestion();
            }
        });

        overlayPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Settings.canDrawOverlays(this)) {
                        Log.d(TAG, "Overlay permission granted after request");
                        setRecurringAlarm(this);
                    } else {
                        Log.d(TAG, "Overlay permission denied after request");
                        Toast.makeText(this, "Разрешение на отображение поверх других приложений необходимо для работы приложения", Toast.LENGTH_LONG).show();
                    }
                }
        );

        if (Settings.canDrawOverlays(this)) {
            Log.d(TAG, "Overlay permission granted");
            setRecurringAlarm(this);
        } else {
            Log.d(TAG, "Requesting overlay permission");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            overlayPermissionLauncher.launch(intent);
        }
    }

    private void addQuestion() {
        String question = editTextQuestion.getText().toString().trim();
        String answer = editTextAnswer.getText().toString().trim();

        if (question.isEmpty() || answer.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите вопрос и ответ", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addQuestion(question, answer);
        if (result != -1) {
            Toast.makeText(this, "Вопрос успешно добавлен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при добавлении вопроса", Toast.LENGTH_SHORT).show();
        }

        editTextQuestion.getText().clear();
        editTextAnswer.getText().clear();
    }

    private void setRecurringAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1); // Первое уведомление через 3 минуты

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long interval = 60 * 1000; // Интервал в миллисекундах для 3 минут
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval, pendingIntent); // Повторение каждые 3 минуты
            Log.d(TAG, "Alarm set for every 3 minutes");
        } else {
            Log.e(TAG, "AlarmManager is null");
        }
    }

    private void freezeDevice() {
        if (!isFrozen) {
            isFrozen = true;
            countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Здесь ничего не нужно делать
                }

                @Override
                public void onFinish() {
                    isFrozen = false;
                    wrongAnswerCount = 0;
                }
            };
            countDownTimer.start();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFrozen) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
