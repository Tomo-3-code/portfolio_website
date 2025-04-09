package com.example.latestissueviewer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "book_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String bookTitle = intent.getStringExtra("title"); // 通知に表示する書籍タイトル
        String releaseDateString = intent.getStringExtra("releaseDate"); // 書籍の発売日（例: "2025-05-01"）

        // 発売日をDate型に変換
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = null;
        try {
            releaseDate = dateFormat.parse(releaseDateString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 現在の日付を取得
        Date currentDate = new Date();

        // 発売日までの残り日数を計算
        long diffInMillis = releaseDate.getTime() - currentDate.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        // 通知メッセージに残り日数を追加
        String notificationText = bookTitle + " が発売予定です！";
        if (releaseDate != null && diffInDays > 0) {
            notificationText = bookTitle + " が発売まで " + diffInDays + " 日です！";
        }

        // 通知チャネルの作成 (API 26以降)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "新刊情報通知", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // 通知を作成
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("新刊発売日通知")
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.ic_notification) // 通知アイコン
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // 通知を表示
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
