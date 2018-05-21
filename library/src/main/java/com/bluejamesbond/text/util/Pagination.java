package com.bluejamesbond.text.util;

import com.bluejamesbond.text.IDocumentLayout;

import java.util.ArrayList;
import java.util.List;

public class Pagination {
    private final IDocumentLayout layout;
    private final List<CharSequence> pages;
    private int pageHeight;

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public Pagination(IDocumentLayout layout, int pageHeight) {
        this.layout = layout;
        this.pages = new ArrayList<>();
        this.pageHeight = pageHeight;
    }

    private int addPageByTokenIndex(int pageFirstTokenIndex, int i) {
        final int tokenLine = layout.getLineForToken(i);
        while (i < layout.getTokensCount() && layout.getLineForToken(i) == tokenLine) {
            i++;
        }

        pages.add(
                layout.getText().subSequence(
                        layout.getTokenStart(pageFirstTokenIndex),
                        layout.getTokenEnd(i - 1)));

        return i;
    }

    public void layout() {
        final int lines = layout.getLineCount();
        float height = pageHeight;
        final int tokensCount = layout.getTokensCount();
        int pageFirstTokenIndex = 0;

        for (int i = 0; i < tokensCount; i++) {
            final float lineHeight = layout.getTokenAscent(i) + layout.getTokenDescent(i);

            if (height < layout.getTokenTopAt(i) + lineHeight) {
                // When the layout height has been exceeded
                pageFirstTokenIndex = i = addPageByTokenIndex(pageFirstTokenIndex, i);
                height += pageHeight;
            }
        }

        if (layout.getLineForToken(pageFirstTokenIndex) < lines) {
            // Put the rest of the text into the last page
            pages.add(
                    layout.getText().subSequence(
                            layout.getTokenStart(pageFirstTokenIndex),
                            layout.getText().length()));
        }
    }

    public int size() {
        return pages.size();
    }

    public CharSequence get(int index) {
        return (index >= 0 && index < pages.size()) ? pages.get(index) : null;
    }
}