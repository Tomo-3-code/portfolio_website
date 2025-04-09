package com.example.latestissueviewer;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class FavoritesActivity extends AppCompatActivity {
    private ListView listViewFavorites;
    private CustomAdapter adapter;
    private ArrayList<HashMap<String, String>> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Realmの初期化
        Realm.init(this);

        listViewFavorites = findViewById(R.id.listViewFavorites);
        favoritesList = new ArrayList<>();
        adapter = new CustomAdapter(this, favoritesList);
        listViewFavorites.setAdapter(adapter);



        loadFavorites(); // データをロード
        // 書籍の詳細表示と削除
        listViewFavorites.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> selectedBook = favoritesList.get(position);
            showBookDetailsDialog(selectedBook, position);
        });


    }

    private void loadFavorites() {
        Realm realm = Realm.getDefaultInstance();
        favoritesList.clear(); // 再読み込み時の重複防止
        try {
            RealmResults<FavoriteBook> favorites = realm.where(FavoriteBook.class).findAll();
            for (FavoriteBook book : favorites) {
                HashMap<String, String> favorite = new HashMap<>();
                favorite.put("id", book.getId());  // ID
                favorite.put("title", book.getTitle());
                favorite.put("imageUrl", book.getImageUrl());
                favorite.put("releaseDate", book.getReleaseDate());  // 発売日を追加
                favoritesList.add(favorite);
            }
        } finally {
            realm.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showBookDetailsDialog(HashMap<String, String> book, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_book_details, null);
        builder.setView(dialogView);

        TextView titleView = dialogView.findViewById(R.id.textViewTitle);
        TextView releaseDateView = dialogView.findViewById(R.id.textViewReleaseDate);  // 発売日用のTextViewを追加
        ImageView imageView = dialogView.findViewById(R.id.imageViewBook);

        titleView.setText(book.get("title"));
        releaseDateView.setText("発売日: " + book.get("releaseDate"));  // 発売日をセット
        Picasso.get().load(book.get("imageUrl")).into(imageView);


        builder.setPositiveButton("閉じる", null);
        builder.setNegativeButton("お気に入りから外す。", (dialog, which) -> {
            deleteFavorite(book.get("id"), position);  // IDを使って削除
        });

        builder.show();
    }

    private void deleteFavorite(String id, int position) {
        new Thread(() -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(r -> {
                FavoriteBook bookToDelete = r.where(FavoriteBook.class).equalTo("id", id).findFirst();
                if (bookToDelete != null) {
                    bookToDelete.deleteFromRealm();
                }
            });
            realm.close();

            runOnUiThread(() -> {
                favoritesList.remove(position); // 表示用リストから削除
                adapter.notifyDataSetChanged(); // アダプタ更新
                Toast.makeText(this, "お気に入りから削除しました", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }


}





