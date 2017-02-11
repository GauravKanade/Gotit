package com.felisys.gotit.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by gpkan on 31-05-2016.
 */
public class ReplaceFont {
    public static void replaceDefaultFont(Context context, String nameOfFontBeingReplaced, String nameOfFontFile){
        Typeface customTypeFace = Typeface.createFromAsset(context.getAssets(), nameOfFontFile);
        replaceFont(nameOfFontBeingReplaced, customTypeFace);
    }

    private static void replaceFont(String nameOfFontBeingReplaced,Typeface customTypeFace) {
        try {
            Field myFeild = Typeface.class.getDeclaredField(nameOfFontBeingReplaced);
            myFeild.setAccessible(true);
            myFeild.set(null, customTypeFace);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
