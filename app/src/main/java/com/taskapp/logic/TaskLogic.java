package com.taskapp.logic;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

import java.time.LocalDate;
import java.util.List;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        //データの一覧を取得
        List<Task> tasks = taskDataAccess.findAll();

        //取得したデータを表示する
        tasks.forEach(task -> {
            String status = null;
            if (task.getStatus() == 0) {
                status = "未着手";
            } else if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            }

            //担当者情報を取得する
            User repUser = task.getRepUser();
            String manager = null;
            
            if (repUser.getCode() == loginUser.getCode()) {
                manager = "あなた";
            } else {
                manager = repUser.getName();
            }
            //一覧を表示
            System.out.println(task.getCode() + ". タスク名 : " + task.getName()
                + ", 担当者名 : " + manager + "が担当しています, ステータス : " + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {

        //入力値をTaskオブジェクト、Logオブジェクトそれぞれにマッピング
        Task task = new Task(code, name, repUserCode, loginUser);
        Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        // saveメソッドを呼び出して、入力されたデータを保存
        taskDataAccess.save(task);
        logDataAccess.save(log);
        System.out.println(name + "の登録が完了しました。");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
        //タスクコードでタスクを検索
        Task task = taskDataAccess.findByCode(code);
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }

        //現在のステータスを確認して変更可能か確認
        int currentStatus = task.getStatus();
        if ((currentStatus == 0 && status != 1) ||
        (currentStatus == 1 && status != 2)) {
        throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
    }

    //ステータスを更新
    task.setStatus(status);
    taskDataAccess.update(task);

    //ログに記録を残す
    Log log = new Log(code, loginUser.getCode(), status, LocalDate.now());
    logDataAccess.save(log);

    System.out.println("ステータスの変更が完了しました。");
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}