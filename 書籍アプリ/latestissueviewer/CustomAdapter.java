package com.example.latestissueviewer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> bookList;

    public CustomAdapter(Context context, ArrayList<HashMap<String, String>> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewReleaseDate = convertView.findViewById(R.id.textViewReleaseDate);  // 発売日用のTextView
        ImageView imageView = convertView.findViewById(R.id.imageViewBook);

        HashMap<String, String> book = bookList.get(position);
        textViewTitle.setText(book.get("title"));
        textViewReleaseDate.setText("発売日: " + book.get("releaseDate"));  // 発売日を表示
        Picasso.get().load(book.get("imageUrl")).into(imageView);

        return convertView;
    }
}
