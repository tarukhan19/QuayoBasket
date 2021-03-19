package com.project.quayobasket.customFont;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class TextViewBoldFont extends TextView {

    public TextViewBoldFont(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public TextViewBoldFont(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public TextViewBoldFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/Laila-Bold.ttf");
        setTypeface(customFont);
    }
}
