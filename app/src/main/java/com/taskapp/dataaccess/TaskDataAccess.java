package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<Task>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            //タイトル行を読み飛ばす
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                //間違いがあればスキップ
                if (values.length != 4) {
                    continue;
                }

                //変数に代入し、Taskオブジェクトへマッピング
                int code = Integer.parseInt(values[0]);
                String name = values[1];
                int status = Integer.parseInt(values[2]);
                int user = Integer.parseInt(values[3]);

                User repUser = userDataAccess.findByCode(user);
                Task task = new Task(code, name, status, repUser);
                tasks.add(task);
            }
        } catch (IOException e) {
        e.printStackTrace();
        }
        return tasks;
    }

    /**
     * タスクをCSVに保存します。
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = createLine(task);
            // 改行を追加する
            writer.newLine();
            // データを1行分追加する
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            //タイトル行を読み飛ばす
            reader.readLine();

            //ファイル内を読み込み、カンマで区切って配列に格納
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                //タスクコードをチェックし、一致する場合はそのタスクを返す
                if (values.length == 4) {
                    int taskCode = Integer.parseInt(values[0]);
                    if (taskCode == code) {
                        String taskName = values[1];
                        int status = Integer.parseInt(values[2]);
                        int userCode = Integer.parseInt(values[3]);

                        //ユーザー情報の取得
                        User repUser = userDataAccess.findByCode(userCode);
                        //一致したタスクを返す
                        return new Task(taskCode, taskName, status, repUser);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * タスクデータを更新します。
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        List<Task> tasks = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("code,name,status,user");
            writer.newLine();

            // 既存タスクを上書き
            for (Task task : tasks) {
                if (task.getCode() == updateTask.getCode()) {
                    writer.write(createLine(updateTask));
                } else {
                    writer.write(createLine(task));
                }
                writer.newLine();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * @param code 削除するタスクのコード
     */
    // public void delete(int code) {
    //     try () {

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        return task.getCode() + "," + task.getName() + "," + task.getStatus()
            + "," + task.getRepUser().getCode();
    }
}