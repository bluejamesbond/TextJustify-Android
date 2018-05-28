package com.bluejamesbond.text.sample.helper;

/*
 * Copyright 2015 Mathew Kurian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * TestActivity.java
 * @author Mathew Kurian
 *
 * From TextJustify-Android Library v2.0
 * https://github.com/bluejamesbond/TextJustify-Android
 *
 * Please report any issues
 * https://github.com/bluejamesbond/TextJustify-Android/issues
 *
 * Date: 1/27/15 3:35 AM
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.sample.R;
import com.bluejamesbond.text.style.TextAlignment;

@SuppressLint("Registered")
public class TestActivity extends Activity {

    public static final int TEXT_SIZE = 12;
    public static final float INSET_PADDING_LEFT = 30f;
    public static final float INSET_PADDING_RIGHT = 30f;
    public static final float INSET_PADDING_TOP = 30f;
    public static final float INSET_PADDING_BOTTOM = 30f;
    public static final float LINE_HEIGHT_MULTIPLIER = 1f;
    public static final int FADE_IN_DURATION = 800;
    public static final int FADE_IN_ANIMATION_STEP_DELAY = 30;
    public String testName;
    private boolean debugging = false;
    private int cacheConfig = 0;

    protected int getContentView() {
        return R.layout.test_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentView());

        testName = Utils.splitCamelCase(getClass().getSimpleName());

        TextView titleBar = ((TextView) findViewById(R.id.titlebar));

        if (titleBar != null) {
            titleBar.setText(testName);
        }
    }

    public DocumentView addDocumentView(CharSequence article, int type, boolean rtl) {
        final DocumentView documentView = new DocumentView(this, type);
        documentView.getDocumentLayoutParams().setTextColor(R.color.black);
        documentView.getDocumentLayoutParams().setTextTypeface(Typeface.DEFAULT);
        documentView.getDocumentLayoutParams().setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        documentView.getDocumentLayoutParams().setTextAlignment(TextAlignment.JUSTIFIED);
        documentView.getDocumentLayoutParams().setInsetPaddingLeft(INSET_PADDING_LEFT);
        documentView.getDocumentLayoutParams().setInsetPaddingRight(INSET_PADDING_RIGHT);
        documentView.getDocumentLayoutParams().setInsetPaddingTop(INSET_PADDING_TOP);
        documentView.getDocumentLayoutParams().setInsetPaddingBottom(INSET_PADDING_BOTTOM);
        documentView.getDocumentLayoutParams().setLineHeightMultiplier(LINE_HEIGHT_MULTIPLIER);
        documentView.getDocumentLayoutParams().setReverse(rtl);
        documentView.getDocumentLayoutParams().setDebugging(debugging);
        documentView.setText(article);
        documentView.setProgressBar((ProgressBar) findViewById(R.id.progressBar));
        documentView.setFadeInDuration(FADE_IN_DURATION);
        documentView.setFadeInAnimationStepDelay(FADE_IN_ANIMATION_STEP_DELAY);
        documentView.setFadeInTween(new FadeInAnymation());

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.addView(documentView);

        LinearLayout articleList = (LinearLayout) findViewById(R.id.articleList);
        articleList.addView(linearLayout);

        debugging = documentView.getDocumentLayoutParams().isDebugging();
        cacheConfig = documentView.getCacheConfig().getId();

        final TextView debugButton = (TextView) findViewById(R.id.debugButton);
        final String debugText = String.format("%s DEBUG", debugging ? "DISABLE" : "ENABLE");

        if (debugButton != null) {
            debugButton.setText(debugText);
            debugButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    debugging = !debugging;
                    debugButton.setText(debugText);
                    documentView.getDocumentLayoutParams().setDebugging(debugging);
                }
            });
        }

        final TextView cacheButton = (TextView) findViewById(R.id.cacheButton);
        final Toast cacheConfigToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        if (cacheButton != null) {
            cacheConfigToast.setText("Activated " + documentView.getCacheConfig().name());
            cacheConfigToast.show();
            cacheButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cacheConfig = (cacheConfig + 1) % 5;
                    DocumentView.CacheConfig newCacheConfig = DocumentView.CacheConfig.getById(cacheConfig);
                    cacheConfigToast.setText("Activated " + newCacheConfig.name());
                    cacheConfigToast.show();
                    documentView.setCacheConfig(newCacheConfig);
                    documentView.destroyCache();
                    documentView.invalidate();
                }
            });
        }
        return documentView;
    }

    public DocumentView addDocumentView(CharSequence article, int type) {
        return addDocumentView(article, type, false);
    }

    private static class FadeInAnymation implements DocumentView.ITween {
        @Override
        public float get(float t, float b, float c, float d) {
            return c * (t /= d) * t * t + b;
        }
    }
}
