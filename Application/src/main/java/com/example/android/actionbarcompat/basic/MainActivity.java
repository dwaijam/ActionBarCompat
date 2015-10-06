/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.actionbarcompat.basic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.LikeView;

import java.security.NoSuchAlgorithmException;

import java.security.MessageDigest;


/**
 * This sample shows you how to use ActionBarCompat to create a basic Activity which displays
 * action items. It covers inflating items from a menu resource, as well as adding an item in code.
 *
 * This Activity extends from {@link ActionBarActivity}, which provides all of the function
 * necessary to display a compatible Action Bar on devices running Android v2.1+.
 */
public class MainActivity extends ActionBarActivity {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private TextView info;


    public void showButton(){
        LikeView likeView = (LikeView) findViewById(R.id.like_view);
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        likeView.setObjectIdAndType(
                "http://inthecheesefactory.com/blog/understand-android-activity-launchmode/en",
                LikeView.ObjectType.OPEN_GRAPH);
        likeView.setVisibility(View.VISIBLE);
    }

    public void hideButton(){
        LikeView likeView = (LikeView) findViewById(R.id.like_view);
        likeView.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();





        //You need this method to be used only once to configure
        //your key hash in your App Console at
        // developers.facebook.com/apps

        getFbKeyHash("com.example.android.actionbarcompat.basic");

        setContentView(R.layout.sample_main);
//        LikeView likeView = (LikeView) findViewById(R.id.like_view);
//        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
//        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
//        likeView.setObjectIdAndType(
//                "http://inthecheesefactory.com/blog/understand-android-activity-launchmode/en",
//                LikeView.ObjectType.OPEN_GRAPH);
        fbLoginButton = (LoginButton)findViewById(R.id.fb_login_button);


        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                ((TextView)findViewById(R.id.info)).setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                showButton();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login cancelled by user!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Login unsuccessful!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");
            }

        });
    }

    // BEGIN_INCLUDE(create_menu)
    /**
     * Use this method to instantiate your menu, and add your items to it. You
     * should return true if you have added items to it and want the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.main, menu);

        // It is also possible add items here. Use a generated id from
        // resources (ids.xml) to ensure that all menu ids are distinct.
        MenuItem locationItem = menu.add(0, R.id.menu_location, 1, R.string.menu_location);
        locationItem.setIcon(R.drawable.ic_action_location);

        // Need to use MenuItemCompat methods to call any action item related methods
        MenuItemCompat.setShowAsAction(locationItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }
    // END_INCLUDE(create_menu)

    // BEGIN_INCLUDE(menu_item_selected)
    /**
     * This method is called when one of the menu items to selected. These items
     * can be on the Action Bar, the overflow menu, or the standard options menu. You
     * should return true if you handle the selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                showMsg(item.getTitle().toString(),500);
                return true;

            case R.id.menu_location:
                // Here we might call LocationManager.requestLocationUpdates()
                showMsg(item.getTitle().toString(),500);
                return true;

            case R.id.menu_settings:
                // Here we would open up our settings activity
                showMsg(item.getTitle().toString(),500);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // END_INCLUDE(menu_item_selected)


    public void showMsg(String msg, final long duration) {
        final Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
        Thread t = new Thread() {
            public void run(){
                try {
                    sleep(duration);
                    toast.cancel();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally { }
            }
        };
        t.start();
    }

    public void getFbKeyHash(String packageName) {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("YourKeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
