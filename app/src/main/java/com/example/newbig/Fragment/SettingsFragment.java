package com.example.newbig.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.newbig.CourseWidgetProvider;
import com.example.newbig.R;
import com.example.newbig.StartActivity;
import com.example.newbig.db.CourseDatabaseHelper;
import com.example.newbig.web.GuideActivity;
import com.example.newbig.web.WebActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SettingsFragment extends Fragment {

    private CourseDatabaseHelper dbHelper; // 声明 dbHelper
    private static final int REQUEST_CODE_IMPORT_FILE = 1;
    private ActivityResultLauncher<Intent> resultLauncher;

    private static final String PREFS_NAME = "WebPrefs";
    private static final String KEY_URL = "url";

    private Button showWeekendButton;
    private Button showNightButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        dbHelper = new CourseDatabaseHelper(getActivity());//this关键字指的是当前的Activity实例

        //******************************************************
        // 获取“使用指南”按钮
        Button guideButton = rootView.findViewById(R.id.btn_guide);
        // 设置点击事件
        guideButton.setOnClickListener(v -> {
            // 跳转到 GuideActivity 页面
            Intent intent = new Intent(getActivity(), GuideActivity.class);
            startActivity(intent);
        });
//*********************************显示周六*********************
        showWeekendButton = rootView.findViewById(R.id.showWeekend); // 获取按钮
        // 获取当前是否显示周末的设置
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean showWeekend = prefs.getBoolean("show_weekend", false);
        // 设置按钮文本
        showWeekendButton.setText(showWeekend ? "隐藏周六周日" : "显示周六周日");
        showWeekendButton.setOnClickListener(view -> {
            // 切换状态
            boolean newState = !prefs.getBoolean("show_weekend", false);
            SharedPreferences.Editor editor = prefs.edit();
            // 实际上没有真正完成数据存储到持久化存储（如文件）的过程。它只是在内存中的一个SharedPreferences.Editor对象里记录了要进行的修改操作
            editor.putBoolean("show_weekend", newState);
            //将修改提交到内存中
            editor.apply();

            // 更新按钮文本
            showWeekendButton.setText(newState ? "隐藏周六周日" : "显示周六周日");
            // 通知主界面更新显示
            // 设置结果为 OK，通知主界面数据已更新
            //===============================================
            Intent intent = new Intent(getActivity(), CourseWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);  // 触发更新
            int[] appWidgetIds = AppWidgetManager.getInstance(getActivity()).getAppWidgetIds(new ComponentName(getActivity(), CourseWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            requireActivity().sendBroadcast(intent);
            // 设置结果并返回，通知 MainActivity 数据已更新
            // 通知主界面更新显示
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment());  // R.id.fragment_container 是你的容器视图 ID
            transaction.addToBackStack(null);  // 如果需要将此事务添加到回退栈（可以让用户按返回键时回到原来的 Fragment）
            transaction.commit();
            // 更新底部导航条选中项
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation); // 获取 BottomNavigationView
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_schedule); // 更新选中项，假设 home 的 id 是 navigation_home
            }
        });
        //*********************************显示晚上*********************
        showNightButton = rootView.findViewById(R.id.showNight); // 获取按钮
        // 获取当前是否显示周末的设置
        boolean showNight= prefs.getBoolean("show_night", false);
        // 设置按钮文本
        showNightButton.setText(showNight ? "隐藏第五节课" : "显示第五节课");
        showNightButton.setOnClickListener(view -> {
            // 切换状态
            boolean newState = !prefs.getBoolean("show_night", false);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("show_night", newState);
            editor.apply();
            // 更新按钮文本
            showNightButton.setText(newState ? "隐藏第五节课" : "显示第五节课");
            // 通知主界面更新显示
            // 设置结果为 OK，通知主界面数据已更新
            //===============================================
            Intent intent = new Intent(getActivity(), CourseWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);  // 触发更新
            int[] appWidgetIds = AppWidgetManager.getInstance(getActivity()).getAppWidgetIds(new ComponentName(getActivity(), CourseWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            requireActivity().sendBroadcast(intent);
            // 设置结果并返回，通知 MainActivity 数据已更新
            // 通知主界面更新显示
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment());  // R.id.fragment_container 是你的容器视图 ID
            transaction.addToBackStack(null);  // 如果需要将此事务添加到回退栈（可以让用户按返回键时回到原来的 Fragment）
            transaction.commit();
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation); // 获取 BottomNavigationView
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_schedule); // 更新选中项，假设 home 的 id 是 navigation_home
            }
        });



//*************************网站**************************
        // 找到按钮
        Button webButton = rootView.findViewById(R.id.webButton);
        // 设置点击监听器
        webButton.setOnClickListener(v -> {
            // 创建一个 Intent 来启动 WebActivity
            Intent intent = new Intent(getActivity(), WebActivity.class);
            startActivity(intent);
        });
        // 设置长按监听器，清空保存的网址
        webButton.setOnLongClickListener(v -> {
            // 获取 SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            // 清空保存的网址
            sharedPreferences.edit().remove(KEY_URL).apply();
            // 显示提示信息
            Toast.makeText(requireContext(), "已清空保存的网址", Toast.LENGTH_SHORT).show();
            return true;
        });


//***********************导出****************************
        // 导出按钮的点击事件
        Button exportButton = rootView.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(v -> {
            //是否具有外部存储管理器权限的方法,大于30
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                requestManageExternalStoragePermission(requireActivity());
            } else {
                File exportFile = new File(requireContext().getExternalFilesDir(null), "courses.csv");
                boolean success = dbHelper.exportDatabase(exportFile.getAbsolutePath());
                if (success) {
                    Toast.makeText(getActivity(), "导出成功", Toast.LENGTH_SHORT).show();
                    // 可能需要通知用户文件已保存到特定位置
                } else {
                    Toast.makeText(getActivity(), "导出失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
//
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 处理结果
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedFileUri = result.getData().getData();
                        handleImportFile(selectedFileUri);
                    }
                });
        //***********************导入****************************
        Button importButton = rootView.findViewById(R.id.importButton);
        importButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/csv");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            resultLauncher.launch(intent);

        });



        return rootView;
    }


    //申请管理所有文件的权限，在这里传入的 activity 主要是为了提供必要的上下文环境以及启动 Intent 所需的启动能力
    private void requestManageExternalStoragePermission(Activity activity) {
        // Android 11 (Api 30)或更高版本的写文件权限需要特殊申请，需要动态申请管理所有文件的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent appIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            appIntent.setData(Uri.parse("package:" + getContext().getPackageName()));
            try {
                activity.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                //不进入该应用，只进权限设置界面
                Intent allFileIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivity(allFileIntent);
            }
        }
    }

    // 处理文件选择结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMPORT_FILE && resultCode == RESULT_OK) {
            // 用户选择了一个文件
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                // 调用方法处理选择的文件
                handleImportFile(selectedFileUri);
            }
        }
    }
    // 读取并导入文件内容
    private void handleImportFile(Uri selectedFileUri) {
        try {
            // 获取内容解析器以读取文件
            ContentResolver contentResolver = requireContext().getContentResolver();
            //ContentResolver 类提供的一个方法
            InputStream inputStream = contentResolver.openInputStream(selectedFileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // 读取文件内容并导入到数据库
            String line;
            reader.readLine(); // 跳过头信息
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // 插入或更新数据库
                dbHelper.importDatabase(data); // 传递一个字符串数组
            }
            reader.close();
            Toast.makeText(getActivity(), "导入成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "导入失败", Toast.LENGTH_SHORT).show();
        }
    }





}