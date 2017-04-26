package com.bluejamesbond.text.sample.test;

import android.os.Bundle;

import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.sample.R;
import com.bluejamesbond.text.sample.helper.TestActivity;

public class LineSpacingTest extends TestActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DocumentView documentView = addDocumentView("Document view now supports both String and Spannables. To support this, there are two (2) types of layouts: (a) " +
                        "DocumentLayout and (b) SpannedDocumentLayout. " +
                        "DocumentLayout supports just plain Strings just like the text you are reading. However, Spannables require the " +
                        "constructor to have SpannedDocumentLayout.class as a parameter. For now, DocumentLayout will offer significant speed improvements " +
                        "compared to SpannedDocumentLayout, so use each class accordingly. DocumentLayout also supports hyphenation. To learn more about" +
                        "these layouts and what they have to offer visit the link in the titlebar above. And please report all the issues on GitHub!"
                , DocumentView.PLAIN_TEXT);

        documentView.getDocumentLayoutParams().setLineSpacingExtra(getResources().getDimension(R.dimen.line_spacing));
    }

}
