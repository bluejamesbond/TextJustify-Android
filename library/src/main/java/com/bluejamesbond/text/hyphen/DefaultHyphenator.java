package com.bluejamesbond.text.hyphen;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * DefaultHyphenator.java
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

/*
 * @file   DefaultHyphenator.java
 * @author Murilo Andrade
 * @date   2014-10-20
 */

/*
 * Several performance and memory optimizations
 *
 * @author Martin Fietz
 * @date   2015-09-06
 */

@SuppressLint("UseSparseArrays")
public class DefaultHyphenator implements IHyphenator {

    private static final HashMap<Integer, DefaultHyphenator> cached;

    static {
        cached = new HashMap<>();
    }

    private TrieNode trie;
    private int leftMin;
    private int rightMin;

    private DefaultHyphenator(HyphenPattern pattern) {
        this.trie = this.createTrie(pattern.patternObject);
        this.leftMin = pattern.leftMin;
        this.rightMin = pattern.rightMin;
    }

    public static DefaultHyphenator getInstance(int resId, Context context) {
        synchronized (cached) {
            if (!cached.containsKey(resId)) {
                InputStream is = null;
                try {
                    is = context.getResources().openRawResource(resId);
                    cached.put(resId, new DefaultHyphenator(HyphenatorParser.parser(is)));
                    return cached.get(resId);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return cached.get(resId);
        }
    }

    private TrieNode createTrie(Map<Integer, String> patternObject) {

        TrieNode t, tree = new TrieNode();

        for (Map.Entry<Integer, String> entry : patternObject.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();

            int numPatterns = value.length() / key;
            for (int i = 0; i < numPatterns; i++) {
                String pattern = value.substring(i * key, (i + 1) * key);
                t = tree;

                for (int c = 0; c < pattern.length(); c++) {
                    char chr = pattern.charAt(c);
                    if (Character.isDigit(chr)) {
                        continue;
                    }
                    int codePoint = pattern.codePointAt(c);
                    if (t.codePoint.get(codePoint) == null) {
                        t.codePoint.put(codePoint, new TrieNode());
                    }
                    t = t.codePoint.get(codePoint);
                }

                List<Integer> list = new ArrayList<Integer>();
                int digitStart = -1;
                for (int p = 0; p < pattern.length(); p++) {
                    if (Character.isDigit(pattern.charAt(p))) {
                        if (digitStart < 0) {
                            digitStart = p;
                        }
                        if (p == pattern.length() - 1) {
                            // last number in the pattern
                            String number = pattern.substring(digitStart, pattern.length());
                            list.add(Integer.valueOf(number));
                        }
                    } else if (digitStart >= 0) {
                        // we reached the end of the current number
                        String number = pattern.substring(digitStart, p);
                        list.add(Integer.valueOf(number));
                        digitStart = -1;
                    } else {
                        list.add(0);
                    }
                }
                t.points = new int[list.size()];
                for (int k = 0; k < list.size(); k++) {
                    t.points[k] = list.get(k);
                }
            }
        }

        return tree;
    }

    @Override
    public List<String> hyphenate(String word) {

        word = "_" + word + "_";
        String lowercase = word.toLowerCase();

        int wordLength = lowercase.length();

        int[] points = new int[wordLength];
        int[] characterPoints = new int[wordLength];
        for (int i = 0; i < wordLength; i++) {
            points[i] = 0;
            characterPoints[i] = lowercase.codePointAt(i);
        }

        TrieNode node, trie = this.trie;
        int[] nodePoints;
        for (int i = 0; i < wordLength; i++) {
            node = trie;
            for (int j = i; j < wordLength; j++) {
                node = node.codePoint.get(characterPoints[j]);
                if (node != null) {
                    nodePoints = node.points;
                    if (nodePoints != null) {
                        for (int k = 0, nodePointsLength = nodePoints.length; k < nodePointsLength; k++) {
                            points[i + k] = Math.max(points[i + k], nodePoints[k]);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        List<String> result = new ArrayList<String>();
        int start = 1;
        for (int i = 1; i < wordLength - 1; i++) {
            if (i > this.leftMin && i < (wordLength - this.rightMin) && points[i] % 2 > 0) {
                result.add(word.substring(start, i));
                start = i;
            }
        }
        if (start < word.length() - 1) {
            result.add(word.substring(start, word.length() - 1));
        }
        return result;
    }

    /**
     * HyphenaPattern.java is an adaptation of Bram Steins hypher.js-Project:
     * https://github.com/bramstein/Hypher
     * <p>
     * Code from this project belongs to the following license:
     * Copyright (c) 2011, Bram Stein All rights reserved.
     * <p>
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are met:
     * 1. Redistributions of source code must retain the above copyright notice,
     * this list of conditions and the following disclaimer. 2. Redistributions in
     * binary form must reproduce the above copyright notice, this list of
     * conditions and the following disclaimer in the documentation and/or other
     * materials provided with the distribution. 3. The name of the author may not
     * be used to endorse or promote products derived from this software without
     * specific prior written permission.
     * <p>
     * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
     * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
     * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
     * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
     * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
     * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
     * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
     * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
     * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */

    @SuppressWarnings({"serial", "unused"})
    public static class HyphenPattern {
        public final int leftMin;
        public final int rightMin;

        /*
         * Common language patterns
         * More info at https://github.com/bramstein/hyphenation-patterns
         */
        public final Map<Integer, String> patternObject;

        public HyphenPattern(int leftMin, int rightMin, Map<Integer, String> patternObject) {
            this.leftMin = leftMin;
            this.rightMin = rightMin;
            this.patternObject = patternObject;
        }
    }

    private class TrieNode {
        Map<Integer, TrieNode> codePoint = new HashMap<Integer, TrieNode>();
        int[] points;
    }
}
