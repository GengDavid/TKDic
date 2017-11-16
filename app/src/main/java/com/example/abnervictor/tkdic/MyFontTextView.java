package com.example.abnervictor.tkdic;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by abnervictor on 2017/11/15.
 */

public class MyFontTextView extends AppCompatTextView {

    public MyFontTextView(Context context) {
        super(context);
        init(context);
        // TODO Auto-generated constructor stub
    }

    public MyFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        // TODO Auto-generated constructor stub
    }

    public MyFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        // TODO Auto-generated constructor stub
    }

    private void init(Context context) {
        // TODO Auto-generated method stub
        AssetManager aManager=context.getAssets();
        Typeface font=Typeface.createFromAsset(aManager, "fonts/xingkaifan.ttf");
        setTypeface(font);
    }
}
