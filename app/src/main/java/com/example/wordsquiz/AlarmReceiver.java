package com.example.wordsquiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.util.Objects;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received");

        // Словарь для теста
        String[][] words = {
                {"cat", "кот"},
                {"dog", "собака"},
                {"house", "дом"},
                {"apple", "яблоко"},
                {"book", "книга"}
        };

        // Выбор случайного слова
        Random random = new Random();
        int index = random.nextInt(words.length);
        String englishWord = words[index][0];
        String correctTranslation = words[index][1];

        // Варианты ответов (включая правильный)
        String[] options = {"кот", "собака", "дом", "яблоко", "книга"};
        options[random.nextInt(options.length)] = correctTranslation; // Замена случайного элемента на правильный

        // Создание диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Переведите слово: " + englishWord)
                .setItems(options, (dialog, which) -> {
                    String selectedAnswer = options[which];
                    if (selectedAnswer.equals(correctTranslation)) {
                        showDialog(context, "Правильно!", "Ответ: " + selectedAnswer);
                    } else {
                        showDialog(context, "Неправильно", "Правильный ответ: " + correctTranslation);
                    }
                });

        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        Log.d(TAG, "Dialog shown with word: " + englishWord);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        Log.d(TAG, "Result dialog shown with message: " + message);
    }
}
