package com.dev.sdv.radiostreamingdemoapp.ui.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.model.Track;

public abstract class BaseActivity extends AppCompatActivity {

  public static final String TAG = BaseActivity.class.getSimpleName();

  private Toolbar toolbar;
  private ProgressDialog progressDialog;
  private Menu menu;

  // Override methods

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    startApp(savedInstanceState);
  }

  // Abstract methods

  protected abstract int getLayoutResourceId();

  protected abstract void onCreateBase(Bundle savedInstanceState);

  protected abstract void onStateChanged(int state, Track track);

  // Other methods

  private void startApp(Bundle savedInstanceState){
    /*Intent intent = getIntent();
    Bundle args = intent.getExtras();
    boolean isUsersFirstSignIn = args != null &&
        args.containsKey(Const.FLAG_IS_FIRST_SIGN_IN);*/
    startBaseActivity(savedInstanceState, true);
  }

  private void startBaseActivity(Bundle savedInstanceState, boolean firstSignIn){
    int layoutResId = getLayoutResourceId();

    if(layoutResId != -1){
      setContentView(layoutResId);
      View toolbar = findViewById(R.id.toolbar);

      if(toolbar != null){
        this.toolbar = (Toolbar)toolbar;
        setSupportActionBar(this.toolbar);
      }

      if(firstSignIn){
        Log.d(TAG, "User's first sign in");
      }
    }
    onCreateBase(savedInstanceState);
  }

  protected int getMenuResourceId(){
    return -1;
  }
}
