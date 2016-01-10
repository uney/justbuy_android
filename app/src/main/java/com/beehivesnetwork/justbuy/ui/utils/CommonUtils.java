package com.beehivesnetwork.justbuy.ui.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static void LogD(String tag, String message) {
        if (AppConstant.LOGGING) {
            Log.d(tag, "" + message);
        }
    }

    public static void LogE(String tag, String message) {
        if (AppConstant.LOGGING) {
            Log.e(tag, "" + message);
        }
    }

    public static void LogE(String tag, String message, Exception e) {
        if (AppConstant.LOGGING) {
            Log.e(tag, "" + message, e);
        }
    }

    public static void LogI(String tag, String message) {
        if (AppConstant.LOGGING) {
            Log.i(tag, "" + message);
        }
    }

    public static void LogW(String tag, String message) {
        if (AppConstant.LOGGING) {
            Log.w(tag, "" + message);
        }
    }

    public static String md5(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(), 0, s.length());
            String hash = new BigInteger(1, digest.digest()).toString(16);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getImageHeight(Activity ctx, int imageid) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        Drawable d = ctx.getResources().getDrawable(imageid);

        BitmapDrawable bd = (BitmapDrawable) ctx.getResources().getDrawable(
                imageid);

        int ih = bd.getBitmap().getHeight();
        int iw = bd.getBitmap().getWidth();

        return ih;
    }

    public static int getDrawableHeight(Activity ctx, int imageid) {
        BitmapDrawable bd = (BitmapDrawable) ctx.getResources().getDrawable(
                imageid);
        return bd.getBitmap().getHeight();
    }

    public static ImageButton addAbsoluteImageButton(Activity ctx,
                                                     int layoutid, int imageid, int left, int top) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        RelativeLayout rl = (RelativeLayout) ctx.findViewById(layoutid);
        Drawable d = ctx.getResources().getDrawable(imageid);

        BitmapDrawable bd = (BitmapDrawable) ctx.getResources().getDrawable(
                imageid);

        int ih = bd.getBitmap().getHeight();
        int iw = bd.getBitmap().getWidth();
        ImageButton iv = new ImageButton(ctx);
        iv.setImageResource(imageid);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                iw, ih);
        params.leftMargin = Math.round(left * displayMetrics.density);
        params.topMargin = Math.round(top * displayMetrics.density);
        iv.setBackgroundColor(Color.TRANSPARENT);
        rl.addView(iv, params);
        return iv;
    }

    public static ImageView addAbsoluteImageView(Activity ctx, int layoutid,
                                                 int imageid, int left, int top) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        RelativeLayout rl = (RelativeLayout) ctx.findViewById(layoutid);
        Drawable d = ctx.getResources().getDrawable(imageid);
        int ih = d.getIntrinsicHeight();
        int iw = d.getIntrinsicWidth();
        ImageView iv = new ImageView(ctx);
        iv.setImageResource(imageid);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                iw, ih);
        params.leftMargin = Math.round(left * displayMetrics.density);
        params.topMargin = Math.round(top * displayMetrics.density);
        iv.setBackgroundColor(Color.TRANSPARENT);
        rl.addView(iv, params);
        return iv;
    }

    public static int getScreenWidth(Context ctx) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE); // the results will
        // be higher than
        // using the
        // activity context
        // object or the
        // getWindowManager()
        // shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        return screenWidth;
    }

    public static float getScreenWidthInDp(Context ctx) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        float density = ctx.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        return dpWidth;
    }

    public static float getScreenHeightInDp(Context ctx) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        float density = ctx.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        return dpHeight;
    }

    public static int getScreenHeight(Context ctx) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE); // the results will
        // be higher than
        // using the
        // activity context
        // object or the
        // getWindowManager()
        // shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        return screenHeight;
    }

    public static float getDensity(Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return density;
    }

    public float SetTextSize(String text, int width, int height) {
        Paint paint = new Paint();
        float textWidth = paint.measureText(text);
        float textSize = (int) ((width / textWidth) * paint.getTextSize());
        paint.setTextSize(textSize);

        textWidth = paint.measureText(text);
        textSize = (int) ((width / textWidth) * paint.getTextSize());

        // Re-measure with font size near our desired result
        paint.setTextSize(textSize);

        // Check height constraints
        FontMetricsInt metrics = paint.getFontMetricsInt();
        float textHeight = metrics.descent - metrics.ascent;
        if (textHeight > height) {
            textSize = (int) (textSize * (height / textHeight));
            paint.setTextSize(textSize);
        }
        return textSize;
    }



    public static void showAlertDialog(Context ctx, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg).setPositiveButton("OK", null);

        AlertDialog dialog = builder.show();
        // Set title divider color
        int titleDividerId = ctx.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
    }

    public static void showAlertDialog(Context ctx, AlertDialog.Builder builder) {
        AlertDialog dialog = builder.show();
        // Set title divider color
        int titleDividerId = ctx.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
    }

    public static boolean checkNetworkConnection(Context context){
        if(context!=null){
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo i = conMgr.getActiveNetworkInfo();
            if (i == null){
                return false;
            }
            if (!i.isConnected()){
                return false;
            }
            if (!i.isAvailable()){
                return false;
            }
            return true;
        }
        return false;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static boolean isEmailValid(String email){
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches()){
            return true;
        }
        else{
            return false;
        }
    }

    public static ArrayList<String> jsonToArrayList(String json){
        JSONArray jsonArray = null;
        ArrayList<String> list = new ArrayList<String>();

        try{
            jsonArray = new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                list.add(jsonArray.getString(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }


    public static String getCurrentAppName(Context context){
        String appName = "";
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRecentTasks(1, ActivityManager.RECENT_WITH_EXCLUDED);
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try {
                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(
                        info.processName, PackageManager.GET_META_DATA));
                appName = c.toString();
            } catch (Exception e) {
                // Name Not Found Exception
            }
        }
        return appName;
    }



    public static String getAppName(Context context, String appId){
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(appId, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    public static Set<String> getLauncherList(Context context){
        CommonUtils.LogI("Defalut Launcher", "default launcher check");
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = context.getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        PackageManager packageManager = context.getPackageManager();

        Set<String> launcherName = new HashSet<>();
        Intent i = new Intent("android.intent.action.MAIN");
        i.addCategory("android.intent.category.HOME");
        List<ResolveInfo> lst = packageManager.queryIntentActivities(i, 0);
        if (!lst.isEmpty()){
            for (ResolveInfo resolveInfo : lst) {
                launcherName.add(resolveInfo.activityInfo.packageName);
                CommonUtils.LogI("Launcher list", "launcher list checking: "+resolveInfo.activityInfo.packageName);
            }
        }
        // You can use name of your package here as third argument
        packageManager.getPreferredActivities(filters, activities, context.getPackageName());

        return launcherName;
    }



    public static int[] timeStampToHourMinute(long time){
        int [] result = new int[2];
        int hr = 0;
        int min = 0;
        int sec = 0;
        hr = (int)time/1000/60/60;
        min = (int)(time - hr*1000*60*60)/1000/60;
        result[0] = hr;
        result[1] = min;
        return result;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStatsAllowed(Context context){
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e){
            return false;
        }
    }
}
