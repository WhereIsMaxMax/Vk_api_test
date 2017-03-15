package com.whrsmxmx.vk_api_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView mPhotoImageView;
    private TextView mNameTextView;
    private TextView mScreenTextView;
    private TextView mSexTextView;
    private TextView mBDTextView;
    private TextView mCityTextView;
    private TextView mLangTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        if(!VKSdk.isLoggedIn())
            VKSdk.login(this, null);
        else
            doQuery();
    }

    private void bindViews() {
        mPhotoImageView = (ImageView)findViewById(R.id.photo);
        mNameTextView = (TextView)findViewById(R.id.name);
        mScreenTextView = (TextView)findViewById(R.id.address);
        mBDTextView = (TextView)findViewById(R.id.age);
        mSexTextView = (TextView) findViewById(R.id.sex);
        mCityTextView = (TextView) findViewById(R.id.locate);
        mLangTextView = (TextView) findViewById(R.id.lang);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(MainActivity.this, R.string.auth_wd, Toast.LENGTH_SHORT).show();
                doQuery();
            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.auth_fail) +
                        error.errorMessage, Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doQuery() {
        Log.d(TAG, "doQuery");
        String mParams = "photo_max_orig, sex, bdate, city, country, personal";
        VKRequest vkRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, mParams));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d(TAG, "Response");
                applyResponse(response);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.d(TAG, "attemptFailed");
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d(TAG, error.toString());
            }
        });
    }

    private void applyResponse(VKResponse response) {

        VKList<VKApiUserFull> list = (VKList<VKApiUserFull>) response.parsedModel;
        VKApiUserFull user = list.get(0);
        String name = null;
        String photo = null;
        String screenName = null;
        String bd = null;
        String locate = null;
        String lang = null;
        int sex = 0;
        try {
            name = user.first_name + " " + user.last_name;
            photo = (String) user.fields.get("photo_max_orig");
            screenName = user.screen_name;
            bd = user.bdate;
            if(user.country!=null) locate = user.country.title + " " +user.city;
            sex = user.sex;
            if(user.langs!=null)
                for(int i = 0; i < user.langs.length; i++){
                    if(i==0)
                        lang = user.langs[i];
                    else
                        lang += " " + user.langs[i];
                }

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        applyData(name, screenName, photo, bd, sex, lang, locate);

    }

    private void applyData(String name, String screenName, String photo, String bd, int sex,
                           String lang, String locate) {

        if(photo!=null) Picasso.with(MainActivity.this)
                .load(photo)
                .into(mPhotoImageView);

        mNameTextView.setText(name);

        if (bd!=null&&!bd.isEmpty()) mBDTextView.setText(bd);
        else mBDTextView.setVisibility(View.GONE);

        if (locate!=null&&!locate.equals(" ")) mCityTextView.setText(locate);
        else mCityTextView.setVisibility(View.GONE);

        switch (sex){
            case 1:
                mSexTextView.setText(R.string.female);
                break;
            case 2:
                mSexTextView.setText(R.string.male);
                break;
            default:
                mSexTextView.setVisibility(View.GONE);
        }

        if(lang!=null) mLangTextView.setText(lang);
        else mLangTextView.setVisibility(View.GONE);

        if(screenName!=null) mScreenTextView.setText(screenName);
        else mScreenTextView.setVisibility(View.GONE);
    }
}
