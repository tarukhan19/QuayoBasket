package com.project.quayobasket.customFont;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class TextViewAirenregular extends TextView {

    public TextViewAirenregular(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public TextViewAirenregular(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public TextViewAirenregular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/Laila-Regular.ttf");
        setTypeface(customFont);
    }
}
