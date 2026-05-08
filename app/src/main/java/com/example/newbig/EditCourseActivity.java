package com.example.newbig;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.newbig.db.CourseDatabaseHelper;

public class EditCourseActivity extends AppCompatActivity {
    private EditText etCourseName, etClassroom;
    private Spinner spinnerColorOptions;  // 用于选择颜色的 Spinner
    private Button btnConfirm, btnDelete;
    private String buttonId; // 用于标识当前编辑的按钮
    private CourseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        // 初始化数据库助手
        dbHelper = new CourseDatabaseHelper(this);

        // 获取传递的按钮 ID
        buttonId = getIntent().getStringExtra("BUTTON_ID");

        // 初始化控件
        etCourseName =findViewById(R.id.etCourseName);
        etClassroom = findViewById(R.id.etClassroom);
        spinnerColorOptions = findViewById(R.id.spinnerColorOptions);  // 初始化 Spinner
        btnConfirm = findViewById(R.id.btnConfirm);
        btnDelete = findViewById(R.id.btnDelete);

        // 字符序列类型，定义了 Spinner 在未展开下拉列表时，每个选项在 Spinner 控件主体部分呈现的基本显示样式
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.color_options, android.R.layout.simple_spinner_item);
        //一个系统提供的布局资源ID，定义了Spinner中每个选项的显示样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColorOptions.setAdapter(adapter);

        // 加载已有课程信息
        loadCourseData();

        // 点击保存按钮
        btnConfirm.setOnClickListener(v -> {
            String courseName = etCourseName.getText().toString();
            String classroom = etClassroom.getText().toString();
            String colorOption = spinnerColorOptions.getSelectedItem().toString(); // 获取选择的颜色

            // 保存到数据库
            boolean isSaved = dbHelper.saveCourse(buttonId, courseName, classroom, colorOption);
            if (isSaved) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

                //发送一个广播，通知系统更新与CourseWidgetProvider相关的所有App Widget。========================================
                Intent intent = new Intent(this, CourseWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);  // 触发更新
                //AppWidgetManager 是 Android 系统中用于管理桌面小部件的一个重要类，它提供了诸多操作小部件的方法，比如查询、更新、删除等功能
                //针对 CourseWidgetProvider 这个小部件组件来获取其 ID
                int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CourseWidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                //带有更新动作以及小部件 ID 数据的 Intent 作为广播发送出去
                this.sendBroadcast(intent);
                finish(); // 返回主界面
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 点击删除按钮
        btnDelete.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteCourse(buttonId);
            if (isDeleted) {
                Toast.makeText(EditCourseActivity.this, "课程已删除", Toast.LENGTH_SHORT).show();
                //===============================================
                Intent intent = new Intent(this, CourseWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);  // 触发更新
                int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CourseWidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                this.sendBroadcast(intent);


                // 返回 MainActivity 并刷新
                setResult(RESULT_OK);
                finish(); // 返回 MainActivity
            } else {
                Toast.makeText(EditCourseActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadCourseData() {
        // 获取当前按钮ID的课程数据
        Cursor cursor = dbHelper.getCourse(buttonId);
        if (cursor != null && cursor.moveToFirst()) {
            // 填充已有数据
            etCourseName.setText(cursor.getString(cursor.getColumnIndex("course_name")));
            etClassroom.setText(cursor.getString(cursor.getColumnIndex("classroom")));

            // 获取表示颜色选项的字符串，然后在 Spinner中找到与之匹配的选项位置，并将 Spinner 的选中项设置为该位置对应的选项
            String colorOption = cursor.getString(cursor.getColumnIndex("color_option"));
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerColorOptions.getAdapter();
            int position = adapter.getPosition(colorOption);  // 获取颜色选项的位置
            spinnerColorOptions.setSelection(position);  // 设置 Spinner 中的选项

        }
        cursor.close();
    }
}
