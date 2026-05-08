package com.example.newbig.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CourseDatabaseHelper extends SQLiteOpenHelper {

    // 数据库相关常量
    private static final String DATABASE_NAME = "course_schedule.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "courses";
    public static final String COL_BUTTON_ID = "button_id";
    public static final String COL_COURSE_NAME = "course_name";
    public static final String COL_CLASSROOM = "classroom";

    public static final String COL_COLOR_OPTION = "color_option";

    public CourseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建课程表，新增 COL_COLOR_OPTION 列用于存储颜色选项
        String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_BUTTON_ID + " TEXT PRIMARY KEY, " +
                COL_COURSE_NAME + " TEXT, " +
                COL_CLASSROOM + " TEXT, " +
                COL_COLOR_OPTION + " TEXT)";
        db.execSQL(createTableSQL);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 保存课程信息
    public boolean saveCourse(String buttonId, String courseName, String classroom, String colorOption) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BUTTON_ID, buttonId);
        values.put(COL_COURSE_NAME, courseName);
        values.put(COL_CLASSROOM, classroom);
        values.put(COL_COLOR_OPTION, colorOption); // 保存颜色选项

        long result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }


    // 获取课程信息
    public Cursor getCourse(String buttonId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, new String[]{COL_COURSE_NAME, COL_CLASSROOM, COL_COLOR_OPTION},
                COL_BUTTON_ID + "=?", new String[]{buttonId}, null, null, null);
    }


    // 删除课程信息
    public boolean deleteCourse(String buttonId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COL_BUTTON_ID + "=?", new String[]{buttonId});
        return rowsDeleted > 0; // 如果删除了至少一行数据，返回 true
    }


    //======================================================
    public boolean exportDatabase(String absolutePath) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            //行访问查询返回的数据。这里先将其初始化为 null
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor == null) {
                return false;
            }
            if (!cursor.moveToFirst()) {
                return false;
            }

            // 获取 Download 文件夹的路径
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadFolder.exists()) {
                downloadFolder.mkdirs(); // 如果文件夹不存在，则创建它
            }

            // 指定文件名
            File file = new File(downloadFolder, "courses.csv");
            FileWriter writer = new FileWriter(file);

            // 写入 CSV 头信息
            writer.append(COL_BUTTON_ID).append(",")
                    .append(COL_COURSE_NAME).append(",")
                    .append(COL_CLASSROOM).append(",")
                    .append(COL_COLOR_OPTION).append("\n");

            // 写入数据，追加字符数据的方法
            do {
                writer.append(cursor.getString(cursor.getColumnIndex(COL_BUTTON_ID))).append(",")
                        .append(cursor.getString(cursor.getColumnIndex(COL_COURSE_NAME))).append(",")
                        .append(cursor.getString(cursor.getColumnIndex(COL_CLASSROOM))).append(",")
                        .append(cursor.getString(cursor.getColumnIndex(COL_COLOR_OPTION))).append("\n");
            } while (cursor.moveToNext());
            writer.flush();//flush 方法可以确保在关闭文件之前，缓冲区中的所有数据都已经真正写入到文件里
            writer.close();
            return true; // 如果执行到这里，说明文件写入成功
        } catch (IOException e) {
            e.printStackTrace();
            return false; // 如果有异常，返回失败
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //==================
    public boolean importDatabase(String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_BUTTON_ID, data[0]);
            values.put(COL_COURSE_NAME, data[1]);
            values.put(COL_CLASSROOM, data[2]);
            values.put(COL_COLOR_OPTION, data[3]);
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}


