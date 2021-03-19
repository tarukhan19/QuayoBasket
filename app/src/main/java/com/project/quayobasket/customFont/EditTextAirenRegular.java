package com.project.quayobasket.customFont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * Created by shree on 4/20/2018.
 */

public class EditTextAirenRegular extends AppCompatEditText {

    public EditTextAirenRegular(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public EditTextAirenRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public EditTextAirenRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/Laila-Regular.ttf");
        setTypeface(customFont);
    }
}
