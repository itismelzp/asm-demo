package com.sensorsdata.analytics.android.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.widget.SwitchCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class SensorsDataPrivate {

    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    public static void mergeJSONObject(final JSONObject source, JSONObject dest)
            throws JSONException {
        Iterator<String> superPropertiesIterator = source.keys();
        while (superPropertiesIterator.hasNext()) {
            String key = superPropertiesIterator.next();
            Object value = source.get(key);
            if (value instanceof Date) {
                synchronized (mDateFormat) {
                    dest.put(key, mDateFormat.format(value));
                }
            } else {
                dest.put(key, value);
            }
        }
    }

    public static Map<String, Object> getDeviceInfo(Context context) {
        final Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("$lib", "Android");
        deviceInfo.put("$lib_version", SensorsDataAPI.SDK_VERSION);
        deviceInfo.put("$os", "Android");
        deviceInfo.put("$os_version", Build.VERSION.RELEASE == null ? "UNKNOWN" : Build.VERSION.RELEASE);
        deviceInfo.put("&manufacturer", Build.MANUFACTURER == null ? "UNKNOWN" : Build.MANUFACTURER);
        if (TextUtils.isEmpty(Build.MODEL)) {
            deviceInfo.put("$model", "UNKNOWN");
        } else {
            deviceInfo.put("$model", Build.MODEL.trim());
        }

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            deviceInfo.put("$app_version", packageInfo.versionName);

            int labelRes = packageInfo.applicationInfo.labelRes;
            deviceInfo.put("$app_name", context.getResources().getString(labelRes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        deviceInfo.put("$screen_height", displayMetrics.heightPixels);
        deviceInfo.put("$screen_width", displayMetrics.widthPixels);

        return Collections.unmodifiableMap(deviceInfo);
    }

    /**
     * 获取 Android ID
     * @param context Context
     * @return String
     */
    @SuppressWarnings("HardwareIds")
    public static String getAndroidID(Context context) {
        String androidID = "";
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidID;
    }

    public static String getViewId(View view) {
        String idString = null;
        if (view.getId() != View.NO_ID) {
            idString = view.getContext().getResources().getResourceEntryName(view.getId());
        }
        return idString;
    }

    public static String getElementType(View view) {
        if (view == null) {
            return null;
        }

        String viewType = null;
        if (view instanceof CheckBox) { // CheckBox
            viewType = "CheckBox";
        } else if (view instanceof SwitchCompat) {
            viewType = "SwitchCompat";
        } else if (view instanceof RadioButton) { // RadioButton
            viewType = "RadioButton";
        } else if (view instanceof ToggleButton) { // ToggleButton
            viewType = "ToggleButton";
        } else if (view instanceof Button) { // Button
            viewType = "Button";
        } else if (view instanceof CheckedTextView) { // CheckedTextView
            viewType = "CheckedTextView";
        } else if (view instanceof TextView) { // TextView
            viewType = "TextView";
        } else if (view instanceof ImageButton) { // ImageButton
            viewType = "ImageButton";
        } else if (view instanceof ImageView) { // ImageView
            viewType = "ImageView";
        } else if (view instanceof RatingBar) {
            viewType = "RatingBar";
        } else if (view instanceof SeekBar) {
            viewType = "SeekBar";
        }
        return viewType;
    }

    public static String traverseViewContent(StringBuilder stringBuilder, ViewGroup root) {

        if (root == null) {
            return stringBuilder.toString();
        }

        int childCount = root.getChildCount();
        for (int i=0;i<childCount;i++) {
            View child = root.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) {
                continue;
            }

            if (child instanceof ViewGroup) {
                traverseViewContent(stringBuilder, (ViewGroup) child);
            } else {
                CharSequence viewText;
                viewText = getElementContent(child);
                if (!TextUtils.isEmpty(viewText)) {
                    stringBuilder.append(viewText.toString());
                    stringBuilder.append("-");
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取 View 上显示的文本
     *
     * @param view View
     * @return String
     */
    public static String getElementContent(View view) {
        if (view == null) {
            return null;
        }

        CharSequence viewText = null;
        if (view instanceof CheckBox) { // CheckBox
            CheckBox checkBox = (CheckBox) view;
            viewText = checkBox.getText();
        } else if (view instanceof SwitchCompat) {
            SwitchCompat switchCompat = (SwitchCompat) view;
            viewText = switchCompat.getTextOn();
        } else if (view instanceof RadioButton) { // RadioButton
            RadioButton radioButton = (RadioButton) view;
            viewText = radioButton.getText();
        } else if (view instanceof ToggleButton) { // ToggleButton
            ToggleButton toggleButton = (ToggleButton) view;
            boolean isChecked = toggleButton.isChecked();
            if (isChecked) {
                viewText = toggleButton.getTextOn();
            } else {
                viewText = toggleButton.getTextOff();
            }
        } else if (view instanceof Button) { // Button
            Button button = (Button) view;
            viewText = button.getText();
        } else if (view instanceof CheckedTextView) { // CheckedTextView
            CheckedTextView textView = (CheckedTextView) view;
            viewText = textView.getText();
        } else if (view instanceof TextView) { // TextView
            TextView textView = (TextView) view;
            viewText = textView.getText();
        } else if (view instanceof SeekBar) {
            SeekBar seekBar = (SeekBar) view;
            viewText = String.valueOf(seekBar.getProgress());
        } else if (view instanceof RatingBar) {
            RatingBar ratingBar = (RatingBar) view;
            viewText = String.valueOf(ratingBar.getRating());
        }
        if (viewText != null) {
            return viewText.toString();
        }
        return null;
    }

    public static Activity getActivityFromView(View view) {
        Activity activity;
        if (view == null) {
            return null;
        }

        Context context = view.getContext();
        activity = getActivityFromContext(context);
        return activity;
    }

    public static Activity getActivityFromContext(Context context) {
        Activity activity = null;
        if (context == null) {
            return null;
        }
        try {
            if (context instanceof Activity) {
                activity = (Activity) context;
            } else if (context instanceof ContextWrapper) {
                while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                }
                if (context instanceof Activity) {
                    activity = (Activity) context;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i=0;i<indent;i++) {
            sb.append('\t');
        }
    }

    public static String formatJson(String jsonStr) {
        try {
            if (null == jsonStr || "".equals(jsonStr)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            char last;
            char current = '\0';
            int indent = 0;
            boolean isInQuotationMarks = false;
            for (int i = 0; i < jsonStr.length(); i++) {
                last = current;
                current = jsonStr.charAt(i);
                switch (current) {
                    case '"':
                        if (last != '\\') {
                            isInQuotationMarks = !isInQuotationMarks;
                        }
                        sb.append(current);
                        break;
                    case '{':
                    case '[':
                        sb.append(current);
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent++;
                            addIndentBlank(sb, indent);
                        }
                        break;
                    case '}':
                    case ']':
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent--;
                            addIndentBlank(sb, indent);
                        }
                        sb.append(current);
                        break;
                    case ',':
                        sb.append(current);
                        if (last != '\\' && !isInQuotationMarks) {
                            sb.append('\n');
                            addIndentBlank(sb, indent);
                        }
                        break;
                    default:
                        sb.append(current);
                }
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}







