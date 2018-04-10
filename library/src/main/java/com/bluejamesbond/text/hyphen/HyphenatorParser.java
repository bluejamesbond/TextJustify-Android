package com.bluejamesbond.text.hyphen;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project TextJustify-Android
 * @date 10.04.18
 *
 * @url https://gist.github.com/LionZXY/171ab2bc25a6d134ff1c3f190bd6e9e9
 */
public class HyphenatorParser {
    public static DefaultHyphenator.HyphenPattern parser(InputStream is) throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String tmp = r.readLine();
        final String[] minValue = tmp.split(" ");

        final int leftMin = Integer.parseInt(minValue[0]);
        final int rightMin = Integer.parseInt(minValue[1]);
        @SuppressLint("UseSparseArrays") final HashMap<Integer, String> hypMap = new HashMap<>();

        while ((tmp = r.readLine()) != null && tmp.length() != 0) {
            int firstSpace = tmp.indexOf(" ");
            int number = Integer.parseInt(tmp.substring(0, firstSpace));
            String line = tmp.substring(firstSpace + 1);
            hypMap.put(number, line);
        }

        return new DefaultHyphenator.HyphenPattern(leftMin, rightMin, hypMap);
    }


}
