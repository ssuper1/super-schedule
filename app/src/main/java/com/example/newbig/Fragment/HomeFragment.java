package com.example.newbig.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.newbig.EditCourseActivity;
import com.example.newbig.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.newbig.db.CourseDatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class HomeFragment extends Fragment {

    private CourseDatabaseHelper dbHelper;
    // 按钮ID数组，按照布局文件顺序列出
    private final String[][] buttonIds = {
            {"btnMon1", "btnTue1", "btnWed1", "btnThu1", "btnFri1", "btnSat1", "btnSun1"},
            {"btnMon2", "btnTue2", "btnWed2", "btnThu2", "btnFri2", "btnSat2", "btnSun2"},
            {"btnMon3", "btnTue3", "btnWed3", "btnThu3", "btnFri3", "btnSat3", "btnSun3"},
            {"btnMon4", "btnTue4", "btnWed4", "btnThu4", "btnFri4", "btnSat4", "btnSun4"},
            {"btnMon5", "btnTue5", "btnWed5", "btnThu5", "btnFri5", "btnSat5", "btnSun5"},
            // 如果有更多节课，继续添加按钮ID
    };
    private Button[][] buttons; // 存储所有按钮对象
    private TextView tvMon, tvTue, tvWed, tvThu, tvFri, tvSat, tvSun;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 这里加载 Fragment 的布局
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        dbHelper = new CourseDatabaseHelper(getActivity());//this关键字指的是当前的Activity实例
        // 初始化周一 ~ 周五——————周日
        buttons = new Button[buttonIds.length][buttonIds[0].length];
        for (int row = 0; row < buttonIds.length; row++) {
            for (int col = 0; col < buttonIds[row].length; col++) {
                //"资源ID名称"，"资源类型"，"应用包名"
                int resId = requireContext().getResources().getIdentifier(buttonIds[row][col], "id", requireContext().getPackageName());

                buttons[row][col] = rootView.findViewById(resId);
                // 设置点击事件
                String buttonId = buttonIds[row][col]; // 获取按钮唯一ID
                buttons[row][col].setOnClickListener(v -> openEditActivity(buttonId));
            }
        }

        // 初始化文本
        tvMon = rootView.findViewById(R.id.tvMon);
        tvTue = rootView.findViewById(R.id.tvTue);
        tvWed = rootView.findViewById(R.id.tvWed);
        tvThu = rootView.findViewById(R.id.tvThu);
        tvFri = rootView.findViewById(R.id.tvFri);
        tvSat = rootView.findViewById(R.id.tvSat);
        tvSun = rootView.findViewById(R.id.tvSun);
        updateWeekDates();

        // 初始化选项来控制显示或隐藏，getDefaultSharedPreferences方法会返回一个默认的SharedPreferences对象
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //如果不存在，就会返回默认值false
        boolean showWeekend = prefs.getBoolean("show_weekend", false);
        // 控制显示或隐藏
        toggleWeekendVisibility(showWeekend);
        //初始化不显示第五行
        boolean showNight= prefs.getBoolean("show_night", false);
        // 控制显示或隐藏
        toggleNightVisibility(showNight);

        // 返回 Fragment 的根视图
        return rootView;
    }



    //*****************时间********************
    private void updateWeekDates() {
        //获取的时间是当前系统时间
        Calendar calendar = Calendar.getInstance();
        // 获取当前是星期几
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 计算本周周一的日期，星期日对应的是1，星期六才是7。
        if (currentDayOfWeek == Calendar.SUNDAY) {
            // 如果今天是周日，设为周一（需要减去6天）
            calendar.add(Calendar.DAY_OF_MONTH, -6);
        } else {
            // 如果今天是周一到周六，计算离当前周一的日期差
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        // 更新周一到周日的日期
        tvMon.setText("\t周一\n" + dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // 周二，月份中的天数
        tvTue.setText("\t周二\n" + dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // 周三
        tvWed.setText("\t周三\n" + dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // 周四
        tvThu.setText("\t周四\n" + dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // 周五
        tvFri.setText("\t周五\n" + dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // 周六
        tvSat.setText("\t周六\n" + dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // 周日
        tvSun.setText("\t周日\n" + dateFormat.format(calendar.getTime()));
    }
    //*************************************设置显示周六
    boolean Weekend = false;
    private void toggleWeekendVisibility(boolean show) {
        final int weekendStartIndex = 5; // 从第6列开始是周末按钮
        if (show) {
            // 设置周六和周日的TextView可见
            tvSat.setVisibility(View.VISIBLE);
            tvSun.setVisibility(View.VISIBLE);
            for (int row = 0; row < buttons.length-1; row++) {
                // 设置周六按钮可见
                buttons[row][weekendStartIndex].setVisibility(View.VISIBLE);
                // 设置周日按钮可见
                buttons[row][weekendStartIndex + 1].setVisibility(View.VISIBLE);
            }
            Weekend = true;

        } else {
            // 设置周六和周日的TextView不可见
            tvSat.setVisibility(View.GONE);
            tvSun.setVisibility(View.GONE);
            // 隐藏周六和周日的按钮
            for (int row = 0; row < buttons.length-1; row++) {
                // 隐藏周六按钮
                buttons[row][weekendStartIndex].setVisibility(View.GONE);
                // 隐藏周日按钮
                buttons[row][weekendStartIndex + 1].setVisibility(View.GONE);
            }
            Weekend = false;
        }
        changeWeekendNight();
    }
    //******************************设置晚上
    boolean Night = false;
    private void toggleNightVisibility(boolean show) {
        if (show) {
            for (int row = 4; row < buttons.length; row++) {
                for (int col = 0; col < 5; col++) {
                    // 设置晚上按钮可见
                    buttons[row][col].setVisibility(View.VISIBLE);
                }
            }
            Night = true;

        } else {
            for (int row = 4; row < buttons.length; row++) {
                for (int col = 0; col < 5; col++) {
                    // 设置晚上按钮可见
                    buttons[row][col].setVisibility(View.GONE);
                }
            }
            Night = false;
        }
        changeWeekendNight();
    }

    private void changeWeekendNight() {
        if(Weekend&&Night){
            buttons[4][5].setVisibility(View.VISIBLE);
            buttons[4][6].setVisibility(View.VISIBLE);
        }
    }

    //******************************
    private void openEditActivity(String buttonId) {
        Intent intent = new Intent(getActivity(), EditCourseActivity.class);
        intent.putExtra("BUTTON_ID", buttonId);
        startActivity(intent);  //
    }

    //当Activity恢复并变得可见时，刷新按钮的状态，确保用户看到的是最新的信息。
    @Override
    public void onResume() {
        super.onResume();
        refreshButtons();
    }

    private void refreshButtons() {
        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                setButtonTextAndColor(buttons[row][col], buttonIds[row][col]);
            }
        }
    }

    private void setButtonTextAndColor(Button button, String buttonId) {
        //从数据库中查询与buttonId对应的课程信息
        Cursor cursor = dbHelper.getCourse(buttonId);
        if (cursor != null && cursor.moveToFirst()) {       //将Cursor移动到第一行数据。
            String courseName = cursor.getString(cursor.getColumnIndex("course_name"));
            String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
            String colorOption = cursor.getString(cursor.getColumnIndex("color_option"));

            // 创建 SpannableString 用于设置字体大小
            String text = courseName + " (" + classroom + ")";

            SpannableString spannableString = new SpannableString(text);
            // 课程------加粗
            /* spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, courseName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 课程名称加粗
             */            // 设置课程名称字体颜色为黑色
            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, courseName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 课程名称黑色

            // 教室------------字体大小
            int classroomStartIndex = courseName.length() + 1; // 跳过" ("字符
            int classroomEndIndex = text.length(); // 去掉右括号
            //SPAN_EXCLUSIVE：这个标记意味着样式标记的起始和结束位置不包含在样式化范围内。也就是说，样式不会应用到标记的起始和结束字符上。
            spannableString.setSpan(new AbsoluteSizeSpan(13, true), classroomStartIndex, classroomEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 小字体，14sp
            // 设置教室名称的字体颜色为类似褐色或灰色
            int classroomColor = Color.parseColor("#8B4513"); // 深褐色
            spannableString.setSpan(new ForegroundColorSpan(classroomColor), classroomStartIndex, classroomEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 教室名称褐色

            // 设置按钮文本
            button.setText(spannableString);
            // 设置按钮背景颜色
            setButtonColor(button, colorOption);
        } else {
            button.setText("");
            setButtonColor(button, "默认"); // 设置为默认背景
        }
        cursor.close();
    }

    private void setButtonColor(Button button, String colorOption) {
        // 根据颜色选项设置背景颜色
        switch (colorOption) {
            case "白色":
                button.setBackgroundColor(Color.WHITE);
                break;
            case "粉色":
                button.setBackgroundColor(Color.parseColor("#EB8C9F"));
                break;
            case "蓝色":
                button.setBackgroundColor(Color.parseColor("#86B1E8"));
                break;
            case "绿色":
                button.setBackgroundColor(Color.parseColor("#8FDFA6"));
                break;
            case "淡黄":
                button.setBackgroundColor(Color.parseColor("#E8D087"));
                break;
            case "橙色":
                button.setBackgroundColor(Color.parseColor("#F1B05B"));
                break;
            case "青色":
                button.setBackgroundColor(Color.parseColor("#9FD4CD"));
                break;
            case "紫色":
                button.setBackgroundColor(Color.parseColor("#D4B6E6"));
                break;
            default:
                //button.setBackgroundColor(Color.WHITE);
                button.setBackgroundColor(Color.argb(0, 255, 255, 255));// 默认背景
                break;
        }

    }








}