<?php
try {
  //DB名、ユーザー名、パスワードを変数に格納
  $dsn = 'mysql:dbname=webDB;port=8889;host=localhost;charset=utf8';
  $user = 'root';
  $password = 'root';
 
  $PDO = new PDO($dsn, $user, $password); //PDOでMySQLのデータベースに接続
  $PDO->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); //PDOのエラーレポートを表示
 
  //input.phpの値を取得
  $name = $_POST['name'];
  $email = $_POST['email'];
  $subject = $_POST['subject'];
  $message = $_POST['message'];
 
  $sql = "INSERT INTO テーブル名 (name, email,subject, message) VALUES (:name, :mail, :subject, :message)"; // テーブルに登録するINSERT INTO文を変数に格納　VALUESはプレースフォルダーで空の値を入れとく
  $stmt = $PDO->prepare($sql); //値が空のままSQL文をセット
  $params = array(':name' => $name, ':email' => $email,':subject' => $subject, ':message' => $message); // 挿入する値を配列に格納
  $stmt->execute($params); //挿入する値が入った変数をexecuteにセットしてSQLを実行
 
  // 登録内容確認・メッセージ
  echo "<p>名前: " . $name . "</p>";
  echo "<p>メールアドレス: " . $email . "</p>";
  echo "<p>サブジェクト: " . $subject . "</p>";
  echo "<p>メッセージ: " . $message . "</p>";
  echo '<p>上記の内容をデータベースへ登録しました。</p>';
} catch (PDOException $e) {
  exit('データベースに接続できませんでした。' . $e->getMessage());
}
?>