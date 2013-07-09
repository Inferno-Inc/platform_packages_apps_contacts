/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.contacts.common.format;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Preconditions;

/**
 * Highlights the text in a text field.
 */
public class TextHighlighter {
    private final String TAG = TextHighlighter.class.getSimpleName();
    private final static boolean DEBUG = false;

    private final int mTextHighlightColor;

    private ForegroundColorSpan mTextColorSpan;

    public TextHighlighter(int textHighlightColor) {
        mTextHighlightColor = textHighlightColor;
    }

    /**
     * Sets the text on the given text view, highlighting the word that matches the given prefix.
     *
     * @param view the view on which to set the text
     * @param text the string to use as the text
     * @param prefix the prefix to look for
     */
    public void setPrefixText(TextView view, String text, String prefix) {
        view.setText(applyPrefixHighlight(text, prefix));
    }

    /**
     * Sets a mask for text highlighting. The mask should be a string of the same length as text,
     * where each character is either 0 or 1. If the character is 1, the letter in text at the same
     * position should be highlighted. Otherwise the letter should not be highlighted.
     *
     * @param view TextView where the highlighted text should go.
     * @param text Text to be highlighted.
     * @param mask Mask indicating which letter to highlight.
     */
    public void setMaskingText(TextView view, String text, String mask) {
        view.setText(applyMaskingHighlight(text, mask));
    }

    /**
     * Applies highlight span to the text.
     * @param text Text sequence to be highlighted.
     * @param mask Mask indicating where highlight should be.
     * @return Highlighted text sequence.
     */
    public CharSequence applyMaskingHighlight(CharSequence text, String mask) {
        Preconditions.checkNotNull(text);
        Preconditions.checkNotNull(mask);

        if (text.length() != mask.length() || text.length() == 0) {
            if (DEBUG) {
                Log.v(TAG, "Mask size mismatch or text length is 0" + text + " " + mask);
            }
            return text;
        }

        /** Sets text color of the masked locations to be highlighted. */
        final SpannableString result = new SpannableString(text);
        for (int i = 0; i < mask.length(); ++i) {
            if (mask.charAt(i) == '1') {
                 result.setSpan(new ForegroundColorSpan(mTextHighlightColor), i, i + 1, 0);
            }
        }
        return result;
    }

    /**
     * Returns a CharSequence which highlights the given prefix if found in the given text.
     *
     * @param text the text to which to apply the highlight
     * @param prefix the prefix to look for
     */
    public CharSequence applyPrefixHighlight(CharSequence text, String prefix) {
        if (prefix == null) {
            return text;
        }

        // Skip non-word characters at the beginning of prefix.
        int prefixStart = 0;
        while (prefixStart < prefix.length() &&
                !Character.isLetterOrDigit(prefix.charAt(prefixStart))) {
            prefixStart++;
        }
        final String trimmedPrefix = prefix.substring(prefixStart);

        int index = FormatUtils.indexOfWordPrefix(text, trimmedPrefix);
        if (index != -1) {
            if (mTextColorSpan == null) {
                mTextColorSpan = new ForegroundColorSpan(mTextHighlightColor);
            }

            final SpannableString result = new SpannableString(text);
            result.setSpan(mTextColorSpan, index, index + trimmedPrefix.length(), 0 /* flags */);
            return result;
        } else {
            return text;
        }
    }
}
