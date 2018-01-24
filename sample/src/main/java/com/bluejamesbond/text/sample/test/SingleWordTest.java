package com.bluejamesbond.text.sample.test;

import android.os.Bundle;

import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.sample.helper.TestActivity;
import com.bluejamesbond.text.style.TextAlignment;


public class SingleWordTest extends TestActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DocumentView centerView = addDocumentView("Center", DocumentView.PLAIN_TEXT);
        centerView.getDocumentLayoutParams().setTextAlignment(TextAlignment.CENTER);
    }
}
