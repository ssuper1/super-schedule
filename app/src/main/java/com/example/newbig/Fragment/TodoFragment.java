package com.example.newbig.Fragment;



import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.newbig.R;
import com.example.newbig.Todo.Todo;
import com.example.newbig.Todo.TodoAdapter;
import com.example.newbig.db.TodoDatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TodoFragment extends Fragment {

    private TodoDatabaseHelper dbHelper;
    private RecyclerView recyclerViewTodos;
    private FloatingActionButton fabAddTodo;
    private TodoDatabaseHelper db;
    private TextView tvDate;
    private TodoAdapter todoAdapter;
    private List<Todo> todoList;

    private AlertDialog dialog; // 将 dialog 定义为类的成员变量

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);


        recyclerViewTodos  = rootView.findViewById(R.id.recyclerViewTodos);
        recyclerViewTodos .setLayoutManager(new LinearLayoutManager(getActivity()));
        dbHelper = new TodoDatabaseHelper(getActivity());

        // 获取任务列表并设置 Adapter
        todoList = getAllTasks();
        todoAdapter = new TodoAdapter(getActivity(), todoList);
        recyclerViewTodos.setAdapter(todoAdapter);

        // 获取添加任务按钮点击 FAB 时，弹出添加任务的弹窗
        fabAddTodo = rootView.findViewById(R.id.fabAddTodo);
        fabAddTodo.setOnClickListener(v -> showAddTaskDialog());

        //显示日期
        tvDate = rootView.findViewById(R.id.tv_date);
        updateDate();

        return rootView;
    }

    //显示日期
    private void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d ,EEEE", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        tvDate.setText(currentDate);
    }



    // 弹出添加任务的对话框
    private void showAddTaskDialog() {
        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("创建作业");

        // 设置自定义视图
        View view = getLayoutInflater().inflate(R.layout.todo_edit, null);
        builder.setView(view);

        // 获取视图中的组件
        EditText editTextTitle = view.findViewById(R.id.et_task_title);
        DatePicker datePicker = view.findViewById(R.id.dp_datetimepicker_date);
        TimePicker timePicker = view.findViewById(R.id.tp_datetimepicker_time);
        Button btnSave = view.findViewById(R.id.btn_save_task);
        Button btnCancel = view.findViewById(R.id.cancel);  // 获取取消按钮
        // 让 EditText 获取焦点
        editTextTitle.requestFocus();

        // 使用 Handler 延迟执行软键盘弹出操作，确保视图已经绘制完成
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextTitle, InputMethodManager.SHOW_IMPLICIT);
        }, 300);
        // 设置 TimePicker 的初始时间为 23:00
        timePicker.setHour(23);
        timePicker.setMinute(00);
        // 设置 TimePicker 为 24 小时制
        timePicker.setIs24HourView(true);

        // 显示对话框
        builder.setCancelable(true);
        dialog = builder.create();

        dialog.show();


        // 设置按钮的点击事件
        btnSave.setOnClickListener(v -> {
            String taskTitle = editTextTitle.getText().toString();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            int hour= timePicker.getHour();
            int minute =timePicker.getMinute();

            // 格式化截止日期
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);
            Date dueDate = calendar.getTime();
            String dueDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(dueDate);
            //yyyy-MM-dd HH:mm


            // 保存作业数据到数据库
            saveTask(taskTitle, dueDateString);
            // 关闭弹窗
            dialog.dismiss();  // 保存后关闭对话框

        });
        // 设置取消按钮的点击事件
        btnCancel.setOnClickListener(v -> {
            // 关闭弹窗
            dialog.dismiss();  // 点击取消关闭对话框
        });

    }

    // 保存任务到数据库
    private void saveTask(String taskTitle, String dueDate) {
        // 使用 TodoDatabaseHelper 保存任务
        boolean result = dbHelper.saveTodo(taskTitle, 0, dueDate); // 默认完成状态是 0（未完成）

        if (result) {
            // 如果保存成功，更新 UI 或显示提示
            Toast.makeText(getActivity(), "保存成功!", Toast.LENGTH_SHORT).show();

            // 添加任务后刷新 RecyclerView
            todoList.clear();
            todoList.addAll(getAllTasks());
            todoAdapter.notifyDataSetChanged();
        } else {
            // 如果保存失败，显示错误提示
            Toast.makeText(getActivity(), "保存失败...", Toast.LENGTH_SHORT).show();
        }

    }

    // 获取所有任务
    private List<Todo> getAllTasks() {
        List<Todo> tasks = new ArrayList<>();
        Cursor cursor = dbHelper.getAllTodos();  // 假设你已在数据库 helper 中创建了这个方法
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                int isCompleted = cursor.getInt(cursor.getColumnIndex("isCompleted"));
                String dueDate = cursor.getString(cursor.getColumnIndex("due_date"));
                tasks.add(new Todo(id, title, isCompleted, dueDate));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return tasks;
    }








}