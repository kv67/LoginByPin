package com.kve.android.loginbypin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  static final public int LOG_OFF = 0;
  static final public int LOG_ON = 1;
  static final private int LOG_TOK = 12345;
  static private boolean logined = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = new Intent(this, LoginActivity.class);
    // startActivity(intent);
    if (!logined)
      startActivityForResult(intent, LOG_TOK);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LOG_TOK && resultCode == LOG_ON){
      logined = true;
    } else {
      finish();
      System.exit(0);
    }

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean("logined", logined );
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    logined = savedInstanceState.getBoolean("logined");
  }

}
