/*
 *  ****************************************************************************
 *  * Created by : Roman on 11/8/2016 at 1:08 PM.
 *  * Email : roman@w3engineers.com
 *  * 
 *  * Last edited by : Roman on 11/8/2016.
 *  * 
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
 *  ****************************************************************************
 */
package com.example.mahadi.edushare;

import android.content.Context;
import android.widget.Toast;

public final class NotifyUtil {
    private NotifyUtil() {
    }

    enum NotifyType {TOAST, NOTIFY}

    public static void shortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
