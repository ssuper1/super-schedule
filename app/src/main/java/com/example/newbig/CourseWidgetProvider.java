package com.example.newbig;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.appwidget.AppWidgetProvider;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.newbig.db.CourseDatabaseHelper;

public class CourseWidgetProvider extends AppWidgetProvider {
    private final String[][] buttonIds = {
            {"btnMon1", "btnTue1", "btnWed1", "btnThu1", "btnFri1", "btnSat1", "btnSun1"},
            {"btnMon2", "btnTue2", "btnWed2", "btnThu2", "btnFri2", "btnSat2", "btnSun2"},
            {"btnMon3", "btnTue3", "btnWed3", "btnThu3", "btnFri3", "btnSat3", "btnSun3"},
            {"btnMon4", "btnTue4", "btnWed4", "btnThu4", "btnFri4", "btnSat4", "btnSun4"},
            {"btnMon5", "btnTue5", "btnWed5", "btnThu5", "btnFri5", "btnSat5", "btnSun5"},
    };

    //===============================================
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(context);
//*************周六周日
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showWeekend = prefs.getBoolean("show_weekend", false);
        //初始化不显示第五节课
        boolean showNight= prefs.getBoolean("show_night", false);
//************
        // 创建一个Intent，指向主页面
        Intent intent = new Intent(context, StartActivity.class);
        //放置到一个新的任务栈（Task）中启动，当在后台服务中检测到某个条件满足，这个标志可以确保主页面在新的任务栈中启动
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        // 如果已经存在一个具有相同 Intent 内容，而不是重新创建一个新的 PendingIntent
        //从Android 12开始，创建PendingIntent时必须明确指定它是FLAG_IMMUTABLE（不可变）还是FLAG_MUTABLE（可变）。这是因为PendingIntent可能包含敏感数据，而这个要求有助于提高安全性。
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|FLAG_IMMUTABLE);
        //**********************************************************************
        //循环中每次迭代的变量名
        /*for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            拓展用*/
        for (int appWidgetId : appWidgetIds) {
            //跨进程的场景下创建和更新桌面小组件，在桌面小组件的场景下，小组件运行在系统的进程空间中
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_course_layout);

            // 为每个TextView设置点击事件
            String[] days = {"周一", "周二", "周三", "周四", "周五"};
            for (int i = 0; i < days.length; i++) {
                int textViewId = context.getResources().getIdentifier("textview_" + days[i], "id", context.getPackageName());
                views.setOnClickPendingIntent(textViewId, pendingIntent);
            }

            // buttonIds是一个二维数组，包含了所有按钮的ID，包括工作日和周末的按钮
            for (int row = 0; row < buttonIds.length; row++) {
                for (int col = 0; col < buttonIds[row].length; col++) {
                    String buttonIdStr = buttonIds[row][col];
                    int buttonId = context.getResources().getIdentifier(buttonIdStr, "id", context.getPackageName());

                    // 检查是否是周末按钮
                    if (col >= 5 ) {
                        if (showWeekend) {
                            // 如果是周末按钮且设置为不显示周末，则隐藏按钮
                            views.setViewVisibility(buttonId, View.VISIBLE);
                        } else {
                            views.setViewVisibility(buttonId, View.GONE);
                        }
                    }
                    Cursor cursor = dbHelper.getCourse(buttonIdStr);
                    if (cursor != null && cursor.moveToFirst()) {
                        String courseName = cursor.getString(cursor.getColumnIndex("course_name"));
                        String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                        String colorOption = cursor.getString(cursor.getColumnIndex("color_option"));

                        // 设置字体大小，
                        views.setTextViewTextSize(buttonId, TypedValue.COMPLEX_UNIT_SP, 12);

                        // 设置课程名称和教室名称
                        views.setTextViewText(buttonId, courseName + " (" + classroom + ")");

                        // 调用setButtonColor的逻辑，但使用RemoteViews
                        int backgroundColor = getColorForOption(colorOption, context);
                        views.setInt(buttonId, "setBackgroundColor", backgroundColor);
                    } else {
                        views.setTextViewText(buttonId, "");
                        // 可设其余的没动过的按钮的背景为透明
                        int transparentColorWithAlpha = Color.argb(100, 255, 255, 255);  // 50%透明度的白色
                        views.setInt(buttonId, "setBackgroundColor", transparentColorWithAlpha);
                    }
                    cursor.close();
                }
            }


            // 更新 RemoteViews 实例
            // 获取第五节课行的线性布局的ID
            int nightClassLayoutId = R.id.night_class_layout; // 假设你的第五行线性布局的ID是 night_class_layout            // 根据设置来显示或隐藏第五节课行
            // 根据设置来显示或隐藏第五节课行
            if (showNight) {
                views.setViewVisibility(nightClassLayoutId, View.VISIBLE);
            } else {
                views.setViewVisibility(nightClassLayoutId, View.GONE);
            }

            // 处理周六和周日的标题
            if (showWeekend) {
                views.setViewVisibility(R.id.tvSat, View.VISIBLE);
                views.setViewVisibility(R.id.tvSun, View.VISIBLE);
            } else {
                views.setViewVisibility(R.id.tvSat, View.GONE);
                views.setViewVisibility(R.id.tvSun, View.GONE);
            }


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    //==========用于接收广播消息并处理与桌面小组件=====================================
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //编辑界面 intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        //这里就是看发送的动作（Action）是否是ACTION_APPWIDGET_UPDATE
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            // 确保重新更新小组件
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //从Intent中获取了一个额外的数据，包含了需要更新的App Widget的ID数组。
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (appWidgetIds != null) {
                onUpdate(context, appWidgetManager, appWidgetIds);  // 调用onUpdate重新更新
            }
        }
    }




    private int getColorForOption(String colorOption, Context context) {
        switch (colorOption) {
            case "白色":
                return Color.WHITE;
            case "粉色":
                return Color.parseColor("#EB8C9F");
            case "蓝色":
                return Color.parseColor("#86B1E8");
            case "绿色":
                return Color.parseColor("#8FDFA6");
            case "淡黄":
                return Color.parseColor("#E8D087");
            case "橙色":
                return Color.parseColor("#F1B05B");
            case "青色":
                return Color.parseColor("#9FD4CD");
            case "紫色":
                return Color.parseColor("#D4B6E6");
            default:
                return Color.argb(100, 255, 255, 255);
        }
    }

}