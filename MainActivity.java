package com.example.maptel;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Resources res;//リソース
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        res = getResources();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

      //電話連携のメソッド
    public void call(View view){
       //文字列取得

        String msg = res.getString(R.string.call_msg);
       //電話番号取得
        EditText textInput = findViewById(R.id.editText1);
        String call= textInput.getText().toString();
        //先頭が0か判断
        if (call.charAt(0) == '0'){
        //電話インテント
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+call));
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    //地図連携メソッド
    public void map(View view){
//文字列取得
        Resources res = getResources();
        String address = res.getString(R.string.map_address);
//地図インテント
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:0,0?q="+address));
        startActivity(intent);
    }

    //---------------LifeCycleLog-------------------------------------

    public void onStart(){
        super.onStart();
        Log.i("LifeCycleLog", "onStart() moved");
    }

    public void onRestart(){
        super.onRestart();
        Log.i("LifeCycleLog", "onRestart() moved");
    }

    public void onResume(){
        super.onResume();
        Log.i("LifeCycleLog", "onResume() moved");
    }

    public void onPause(){
        super.onPause();
        Log.i("LifeCycleLog", "onPause() moved");
    }

    public void onStop(){
        super.onStop();
        Log.i("LifeCycleLog", "onStop() moved");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.i("LifeCycleLog", "onDestroy() moved");
    }
}