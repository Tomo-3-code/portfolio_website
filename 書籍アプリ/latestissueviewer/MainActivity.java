package com.example.latestissueviewer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;


public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonFavorites; // お気に入り表示ボタン

    private ListView listViewBooks;
    private CustomAdapter adapter; // カスタムアダプター
    private ArrayList<HashMap<String, String>> bookList; // タイトルと画像URLを保持するリスト

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this); // Realm初期化
        setContentView(R.layout.activity_main);


        RealmMigration migration = new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                RealmSchema schema = realm.getSchema();

                if (oldVersion == 0) {
                    schema.create("FavoriteBook")
                            .addField("id", String.class)
                            .addField("title", String.class)
                            .addField("imageUrl", String.class)
                            .addField("releaseDate", String.class);
                    oldVersion++;
                }
                if (oldVersion == 1) {
                    schema.get("FavoriteBook")
                            .addField("newField", String.class);
                    oldVersion++; // インクリメントが必須
                }
            }
        };

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(2) // スキーマバージョン
                .deleteRealmIfMigrationNeeded() // 古いデータベースを削除
                .migration(migration) // マイグレーションを適用
                .build();

        Realm.setDefaultConfiguration(config);

        // Viewの初期化
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonFavorites = findViewById(R.id.buttonFavorites);
        listViewBooks = findViewById(R.id.listViewBooks);
        bookList = new ArrayList<>();
        adapter = new CustomAdapter(this, bookList);
        listViewBooks.setAdapter(adapter);

        // ボタンのリスナー設定
        buttonSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString();
            searchBooks(query);
        });

        buttonFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        listViewBooks.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> selectedBook = bookList.get(position);
            showBookDetails(selectedBook.get("title"), selectedBook.get("imageUrl"));
        });

        // 通知権限リクエストを追加
        requestNotificationPermission();

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonFavorites = findViewById(R.id.buttonFavorites); // お気に入りボタン

        listViewBooks = findViewById(R.id.listViewBooks);

        bookList = new ArrayList<>();
        adapter = new CustomAdapter(this, bookList); // カスタムアダプターを初期化

        listViewBooks.setAdapter(adapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearch.getText().toString();
                searchBooks(query);
            }
        });

        listViewBooks.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> selectedBook = bookList.get(position);
            showBookDetails(selectedBook.get("title"), selectedBook.get("imageUrl")); // 画像URLも渡す
        });
        // 「お気に入り表示」ボタンの画面遷移処理
        buttonFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent); // FavoritesActivity へ遷移
        });


    }

    private void scheduleNotification(String bookTitle, String releaseDate, Context context, int daysBefore) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDateParsed = dateFormat.parse(releaseDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(releaseDateParsed);
            calendar.add(Calendar.DAY_OF_YEAR, -daysBefore); // 発売日から指定日数前

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("title", bookTitle); // 通知に表示する本のタイトル
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); // 指定日に通知をスケジュール
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//書籍詳細
    private void showBookDetails(String bookTitle, String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_book_details, null);
        builder.setView(dialogView);

        TextView titleView = dialogView.findViewById(R.id.textViewTitle);
        ImageView imageView = dialogView.findViewById(R.id.imageViewBook);
        TextView releaseDateView = dialogView.findViewById(R.id.textViewReleaseDate); // 発売日表示用
        Button buttonFavorite = dialogView.findViewById(R.id.buttonFavorite);

        titleView.setText(bookTitle);
        Picasso.get().load(imageUrl).into(imageView);

        // 楽天APIから書籍情報を取得して発売日を表示
        new Thread(() -> {
            try {
                String apiUrl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId=1035945938062560415&title=" + bookTitle;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray items = jsonResponse.getJSONArray("Items");
                if (items.length() > 0) {
                    JSONObject item = items.getJSONObject(0).getJSONObject("Item");
                    String releaseDate = item.getString("salesDate"); // 発売日を取得
                    runOnUiThread(() -> {
                        releaseDateView.setText("発売日: " + releaseDate); // 発売日を表示
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Realmからお気に入りの書籍を取得
        Realm realm = Realm.getDefaultInstance();
        FavoriteBook existingFavorite = realm.where(FavoriteBook.class)
                .equalTo("title", bookTitle)
                .findFirst();

        if (existingFavorite != null) {
            // お気に入りがすでに存在する場合、お気に入りボタンを非表示
            buttonFavorite.setVisibility(View.GONE);
        } else {
            // お気に入りに追加するためのボタン
            buttonFavorite.setText("お気に入りに追加");
            buttonFavorite.setVisibility(View.VISIBLE); // ボタンを表示
            buttonFavorite.setOnClickListener(addFavoriteListener(bookTitle, imageUrl, buttonFavorite));
        }

        builder.setPositiveButton("閉じる", null);
        builder.show();
    }
//お気に入り追加リスト
    private View.OnClickListener addFavoriteListener(String bookTitle, String imageUrl, Button buttonFavorite) {
        return v -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(r -> {
                FavoriteBook favoriteBook = r.createObject(FavoriteBook.class, java.util.UUID.randomUUID().toString());
                favoriteBook.setTitle(bookTitle);
                favoriteBook.setImageUrl(imageUrl);

                // 楽天APIから発売日を取得して設定する部分
                String releaseDate = "2023-05-01";  // 仮のデータ、実際にはAPIから取得した値に変更
                favoriteBook.setReleaseDate(releaseDate);  // 発売日を設定

                // 通知をスケジュール (例えば3日前に通知)
                scheduleNotification(bookTitle, releaseDate, v.getContext(), 3);

            }, () -> {
                new AlertDialog.Builder(buttonFavorite.getContext())
                        .setMessage("お気に入りに登録しました。")
                        .setPositiveButton("OK", null)
                        .show();

                buttonFavorite.setText("お気に入りを解除する");
                buttonFavorite.setOnClickListener(v2 -> {
                    realm.executeTransactionAsync(r -> {
                        FavoriteBook bookToDelete = r.where(FavoriteBook.class)
                                .equalTo("title", bookTitle)
                                .findFirst();
                        if (bookToDelete != null) {
                            bookToDelete.deleteFromRealm();
                        }
                    }, () -> {
                        new AlertDialog.Builder(buttonFavorite.getContext())
                                .setMessage("お気に入りを解除しました。")
                                .setPositiveButton("OK", null)
                                .show();
                        buttonFavorite.setText("お気に入りに追加");
                        buttonFavorite.setOnClickListener(addFavoriteListener(bookTitle, imageUrl, buttonFavorite));
                    }, error -> {
                        error.printStackTrace();
                        new AlertDialog.Builder(buttonFavorite.getContext())
                                .setMessage("解除に失敗しました。もう一度お試しください。")
                                .setPositiveButton("OK", null)
                                .show();
                    });
                });

            }, error -> {
                error.printStackTrace();
                new AlertDialog.Builder(buttonFavorite.getContext())
                        .setMessage("登録に失敗しました。もう一度お試しください。")
                        .setPositiveButton("OK", null)
                        .show();
            });
        };
    }

//検索リスト
    private void searchBooks(String query) {
        new Thread(() -> {
            try {
                String apiUrl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId=1035945938062560415&title=" + query;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray items = jsonResponse.getJSONArray("Items");

                bookList.clear();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i).getJSONObject("Item");
                    String bookTitle = item.getString("title");//タイトルを取得
                    String imageUrl = item.getString("largeImageUrl"); // 画像URLを取得
                    String releaseDate = item.getString("salesDate"); // 発売日を取得

                    HashMap<String, String> book = new HashMap<>();
                    book.put("title", bookTitle);
                    book.put("imageUrl", imageUrl);
                    book.put("releaseDate", releaseDate); // 発売日を追加
                    bookList.add(book);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Button getButtonFavorites() {
        return buttonFavorites;
    }

    public void setButtonFavorites(Button buttonFavorites) {
        this.buttonFavorites = buttonFavorites;
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // 親クラスのメソッドを呼び出す

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 権限が許可された場合
            } else {
                // 権限が拒否された場合
            }
        }
    }


}


