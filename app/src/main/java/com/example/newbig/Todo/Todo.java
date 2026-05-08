package com.example.newbig.Todo;

public class Todo {
    private int id;
    private String title;
    private int isCompleted;
    private String dueDate;

    // 构造函数
    public Todo(int id, String title, int isCompleted, String dueDate) {
        this.id = id;
        this.title = title;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
    }
//将待办事项所涉及的关键属性（如 id、title、isCompleted、dueDate）进行了统一的封装，清晰地定义了一个待办事项在程序中所需要包含的各项信息
    // Getter and Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
