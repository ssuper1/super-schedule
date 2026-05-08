package com.example.newbig.Todo;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

;

import com.example.newbig.Fragment.TodoFragment;
import com.example.newbig.R;
import com.example.newbig.db.TodoDatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private Context context;
    private List<Todo> todoList;
    private TodoDatabaseHelper db;
    private AlertDialog dialog;  // 声明 dialog 变量
    private TodoDatabaseHelper dbHelper;

    // 构造函数，传入数据
    public TodoAdapter(Context context, List<Todo> todoList) {
        this.context = context;
        this.todoList = todoList;
        this.db = new TodoDatabaseHelper(context);  // 初始化数据库助手
        this.dbHelper = new TodoDatabaseHelper(context);  // 初始化 dbHelper
    }

    // 创建 RecyclerView 中每个列表项对应的 ViewHolder 实例
    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    // 绑定数据到 ViewHolder
    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.title.setText(todo.getTitle());
        holder.dueDate.setText(todo.getDueDate());
        holder.checkbox.setChecked(todo.getIsCompleted() == 1);

        // 计算剩余天数
        String dueDateString = todo.getDueDate(); // 任务的截止日期，格式为 "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            // 解析截止日期
            Date dueDate = dateFormat.parse(dueDateString);

            // 获取当前日期并清除时间部分（将小时、分钟、秒、毫秒清零）
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
            currentCalendar.set(Calendar.MINUTE, 0);
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);

            // 获取当前日期的时间戳（以毫秒为单位）
            Date currentDate = currentCalendar.getTime();

            // 计算剩余天数
            long differenceInMillis = dueDate.getTime() - currentDate.getTime();
            long remainingDays = differenceInMillis / (1000 * 60 * 60 * 24); // 将毫秒转换为天数

            // 更新 UI 显示剩余天数
            if (remainingDays > 1) {
                holder.txvHave.setText("还剩 " + remainingDays + " 天");
                holder.txvHave.setTextColor(Color.BLACK);  // 默认黑色
            } else if (remainingDays == 1) {
                holder.txvHave.setText("明天截止");
                holder.txvHave.setTextColor(Color.parseColor("#F4A6C3"));  // 设置红色
            } else if (remainingDays == 0) {
                holder.txvHave.setText("今天截止");
                holder.txvHave.setTextColor(Color.parseColor("#FF6347"));
            }  else {
                // 显示已过期
                holder.txvHave.setText("已过期");
                holder.txvHave.setTextColor(Color.GRAY);  // 设置灰色
            }

        } catch (ParseException e) {
            e.printStackTrace();
            holder.txvHave.setText("日期错误");
        }


        // 为复选框设置状态改变监听器
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // 任务完成，删除任务
                deleteTodo(todo.getId(), position);
            } else {
                // 取消完成，更新任务状态
                updateTodoStatus(todo.getId(), false);
            }
        });

        // 为每个 item 设置点击监听，点击后打开编辑页面
        holder.itemView.setOnClickListener(v -> {
            showEditTaskDialog(todo);
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    // 删除待办事项的方法
    private void deleteTodo(int id, int position) {
        db.deleteTodo(id); // 从数据库中删除
        todoList.remove(position); // 从列表中移除
        notifyItemRemoved(position); // 通知适配器项已删除
        notifyItemRangeChanged(position, todoList.size()); // 通知适配器数据已更改
    }

    // 更新待办事项完成状态的方法
    private void updateTodoStatus(int id, boolean isCompleted) {
        db.updateTodoCompletionStatus(id, isCompleted); // 更新数据库中的完成状态
    }

    // ViewHolder 类
    public class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, txvHave;
        CheckBox checkbox;

        public TodoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            dueDate = itemView.findViewById(R.id.textViewDueDate);
            checkbox = itemView.findViewById(R.id.checkboxCompleted);
            txvHave = itemView.findViewById(R.id.txv_have);
        }
    }

    private void showEditTaskDialog(Todo todo) {
        // 获取 LayoutInflater 和创建对话框
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.todo_edit, null);

        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("编辑作业");
        builder.setView(view);

        // 获取视图中的组件
        EditText editTextTitle = view.findViewById(R.id.et_task_title);
        DatePicker datePicker = view.findViewById(R.id.dp_datetimepicker_date);
        TimePicker timePicker = view.findViewById(R.id.tp_datetimepicker_time);
        Button btnSave = view.findViewById(R.id.btn_save_task);
        Button btnCancel = view.findViewById(R.id.cancel);

        // 填充现有任务数据
        editTextTitle.setText(todo.getTitle());
        // 拆分日期和时间
        String[] dateParts = todo.getDueDate().split(" ");
        String[] date = dateParts[0].split("-");
        datePicker.updateDate(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));

        String[] timeParts = dateParts[1].split(":");
        timePicker.setHour(Integer.parseInt(timeParts[0]));
        timePicker.setMinute(Integer.parseInt(timeParts[1]));
        timePicker.setIs24HourView(true);

        // 设置保存按钮的点击事件
        btnSave.setOnClickListener(v -> {
            String taskTitle = editTextTitle.getText().toString();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // 格式化截止日期
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);
            Date dueDate = calendar.getTime();
            String dueDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(dueDate);
            ////yyyy-MM-dd HH:mm
            // 更新任务到数据库
            updateTask(todo.getId(), taskTitle, dueDateString);

            // 关闭对话框
            dialog.dismiss();  // 保存后关闭对话框
        });

        // 设置取消按钮的点击事件
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();  // 取消时关闭对话框
        });

        // 显示对话框
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    // 更新任务的方法
    private void updateTask(int taskId, String taskTitle, String dueDate) {
        // 更新任务数据到数据库
        dbHelper.updateTask(taskId, taskTitle, dueDate);
        // 更新列表并刷新 RecyclerView
        todoList.clear();
        todoList.addAll(getAllTasks());  // 获取所有任务并更新列表
        notifyDataSetChanged();  // 通知适配器更新视图
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
