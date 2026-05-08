package com.example.newbig.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDatabaseHelper extends SQLiteOpenHelper {

    // 数据库相关常量
    private static final String DATABASE_NAME = "todo_list.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "todos";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_IS_COMPLETED = "isCompleted";
    public static final String COL_DUE_DATE = "due_date";

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建待办事项表
        String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_IS_COMPLETED + " INTEGER, " +
                COL_DUE_DATE + " TEXT NOT NULL)";
        db.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 保存待办事项
    public boolean saveTodo(String title, int isCompleted, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_IS_COMPLETED, isCompleted);
        values.put(COL_DUE_DATE, dueDate);

        long result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public Cursor getAllTodos() {
        SQLiteDatabase db = this.getReadableDatabase();
        // 按照 due_date 排序，ASC 表示升序（从最早的日期开始）
        return db.query(TABLE_NAME, new String[]{COL_ID, COL_TITLE, COL_IS_COMPLETED, COL_DUE_DATE},
                null, null, null, null, COL_DUE_DATE + " ASC");  // 使用 COL_DUE_DATE 排序
    }


    // 获取待办事项（按ID查询）
    public Cursor getTodoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, new String[]{COL_TITLE, COL_IS_COMPLETED, COL_DUE_DATE},
                COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // 更新待办事项的完成状态
    public boolean updateTodoCompletionStatus(int id, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_COMPLETED, isCompleted);

        int rowsAffected = db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    // 删除待办事项
    public boolean deleteTodo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0;
    }

    // 更新数据库中的任务
    public void updateTask(int taskId, String taskTitle, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, taskTitle);
        values.put(COL_DUE_DATE, dueDate);
        // 更新任务
        db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{String.valueOf(taskId)});
    }


}
