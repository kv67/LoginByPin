package com.kve.android.loginbypin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;

import javax.crypto.Cipher;


public class LoginActivity extends AppCompatActivity {

  final String LOG_TAG = "myLogs";

  private static final String PIN = "pin";
  private SharedPreferences mPreferences;
  private FingerprintHelper mFingerprintHelper;
  private String code = "";

  private void repaintCode(){
    Button button1 = findViewById(R.id.checkbtn1);
    if (code.length() > 0)
      button1.setVisibility(View.VISIBLE);
    else
      button1.setVisibility(View.INVISIBLE);

    Button button2 = findViewById(R.id.checkbtn2);
    if (code.length() > 1)
      button2.setVisibility(View.VISIBLE);
    else
      button2.setVisibility(View.INVISIBLE);

    Button button3 = findViewById(R.id.checkbtn3);
    if (code.length() > 2)
      button3.setVisibility(View.VISIBLE);
    else
      button3.setVisibility(View.INVISIBLE);

    Button button4 = findViewById(R.id.checkbtn4);
    if (code.length() > 3)
      button4.setVisibility(View.VISIBLE);
    else
      button4.setVisibility(View.INVISIBLE);

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    Button btn = findViewById(R.id.button_bio);
    btn.setEnabled(FingerprintUtils.checkFingerprintCompatibility(this));
    mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    repaintCode();
  }

  public void clickBackBtn(View view) {
    if (code.length() > 0){
      if (code.length() == 1){
        code = "";
      } else {
        code = code.substring(0, code.length() - 1);
      }
    }
    repaintCode();
  }

  public void clickBtn(View view) {
    switch (view.getId()) {
      case R.id.button_0:
        code += "0";
        break;
      case R.id.button_1:
        code += "1";
        break;
      case R.id.button_2:
        code += "2";
        break;
      case R.id.button_3:
        code += "3";
        break;
      case R.id.button_4:
        code += "4";
        break;
      case R.id.button_5:
        code += "5";
        break;
      case R.id.button_6:
        code += "6";
        break;
      case R.id.button_7:
        code += "7";
        break;
      case R.id.button_8:
        code += "8";
        break;
      case R.id.button_9:
        code += "9";
        break;
    }

    repaintCode();

    if (code.length() == 4) {
      if (code.equals("0000")){
        code = "";
        repaintCode();
        clearPin();
        Intent answerIntent = new Intent();
        setResult(MainActivity.LOG_OFF, answerIntent);
        finish();
        return;
      }
      if (hasPin()){
        String decoded = CryptoUtils.decode(mPreferences.getString(PIN, null));
        if (code.equals(decoded)) {
          Intent answerIntent = new Intent();
          setResult(MainActivity.LOG_ON, answerIntent);
          finish();
        } else {
          code = "";
          Toast.makeText(getApplicationContext(), getString(R.string.pin_faild), Toast
              .LENGTH_SHORT).show();
          repaintCode();
        }
      } else {
        Log.d(LOG_TAG, "Save pin");
        savePin(code);
        Log.d(LOG_TAG, "Pin has saved");
        Intent answerIntent = new Intent();
        setResult(MainActivity.LOG_OFF, answerIntent);
        finish();
      }
    }
  }

  @Override
  public void onBackPressed()
  {
    Intent answerIntent = new Intent();
    setResult(MainActivity.LOG_OFF, answerIntent);
    finish();
  }

  private boolean hasPin(){
    Log.d(LOG_TAG, "Has pin?");
    return mPreferences.getString(PIN, null) != null;
  }

  private void savePin(String pin) {
    String encoded = CryptoUtils.encode(pin);
    mPreferences.edit().putString(PIN, encoded).apply();
  }

  private void clearPin() {
    mPreferences.edit().putString(PIN, null).apply();
  }

  public class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback {
    private Context mContext;
    private CancellationSignal mCancellationSignal;

    FingerprintHelper(Context context) {
      mContext = context;
    }

    void startAuth(FingerprintManagerCompat.CryptoObject cryptoObject) {
      mCancellationSignal = new CancellationSignal();
      FingerprintManagerCompat manager = FingerprintManagerCompat.from(mContext);
      manager.authenticate(cryptoObject, 0, mCancellationSignal, this, null);
    }

    void cancel() {
      if (mCancellationSignal != null) {
        mCancellationSignal.cancel();
      }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
      Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
      Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
      Cipher cipher = result.getCryptoObject().getCipher();
      String encoded = mPreferences.getString(PIN, null);
      String decoded = CryptoUtils.decode(encoded, cipher);
      // mEditText.setText(decoded);
      Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
      Toast.makeText(mContext, "try again", Toast.LENGTH_SHORT).show();
    }

  }
}
