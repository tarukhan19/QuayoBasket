package com.project.quayobasket.customFont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

/**
 * Created by shree on 4/20/2018.
 */

public class ButtonAirenRegular extends AppCompatButton
{
    public ButtonAirenRegular(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public ButtonAirenRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public ButtonAirenRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/Laila-Bold.ttf");
        setTypeface(customFont);
    }
}
