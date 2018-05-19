package answer.android.easyandroid.util;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

/**
 * 一个异步任务执行类
 */
public class Task {
    private ITask iTask;

    private Object param;

    private Task(ITask iTask) {
        this.iTask = iTask;
        this.param = this.iTask.getParam();
    }

    private Object rruunn(Object param) {
        Object run = null;
        try {
            run = iTask.run(param);
        }catch (Exception e){
            iTask.onError(e);
            return null;
        }
        return run;
    }

    private void rruunnEnd(Object vaue) {
        iTask.afterRun(vaue);
    }

    /**
     * 异步任务构建使用工具
     */
    public static class TaskHelper {
        private static TaskHelper taskHelper;

        // 在一个Task还没有运行完成的时候，又有多个task需要运行的时候，会尝试建立新的线程去运行这些task
        // 该字段限定了最多创建的线程个数， 当所有线程都有task正在运行的时候，接下来需要运行的task将会被
        // 放入一个集合中，等待前面的task运行完成后，这些放入集合的task将会被运行。
        private int maxThreadCount;

        private ArrayList<TaskThread> taskThreads;

        private TaskHelper(int maxThreadCount) {
            this.maxThreadCount = maxThreadCount;
            if (this.maxThreadCount < 1) {
                this.maxThreadCount = 1;
            }
            this.taskThreads = new ArrayList<>();
        }

        public static TaskHelper getInstance() {
            return getInstance(4);
        }

        public static TaskHelper getInstance(int maxThreadCount) {
            if (taskHelper == null) {
                taskHelper = new TaskHelper(maxThreadCount);
            }
            return taskHelper;
        }

        public void run(ITask iTask) {
            // 从所有的线程中寻找一个最适合运行这个task的线程


            // 如果还没有开启任何一个线程，则开启一个线程，然后使用开启的线程运行这个task
            if (taskThreads.isEmpty()) {
                // System.out.println("没有线程在运行，新建一个，然后直接运行");
                TaskThread taskThread = new TaskThread();
                taskThread.start();
                taskThread.d0(iTask);
                taskThreads.add(taskThread);
            } else {
                // 开启了线程，在所有开启了的线程中寻找一个最适合运行运行这个task的线程

                int minRunningTaskSize = Integer.MAX_VALUE;
                TaskThread minRunningTaskThread = null;
                for (int index = 0; index < taskThreads.size(); index++) {
                    TaskThread taskThread = taskThreads.get(index);

                    if (!taskThread.isItaskrunning()) { // 如果这个线程没有task在运行，直接选用这个
                        minRunningTaskThread = taskThread;
                        // System.out.println("发现一个没有任务运行的线程，直接使用");
                        break;
                    }

                    int runningTaskSize = taskThread.getNeedRunTasks().size();
                    if (runningTaskSize < minRunningTaskSize) {
                        minRunningTaskSize = runningTaskSize;
                        minRunningTaskThread = taskThread;
                    }
                }

                // 检查这个最优的线程中是否有正在运行的
                if (!minRunningTaskThread.isItaskrunning()) {
                    // 没有，那么就直接使用这个线程来运行这个itask
                    // System.out.println("最优的线程中没有正在运行的任务，直接使用");
                    minRunningTaskThread.d0(iTask);
                } else {
                    // 有正在运行的，检查是否可以继续开启新线程
                    if (taskThreads.size() < maxThreadCount) {
                        // System.out.println("可以开启新线程执行任务");
                        // 还可以开启线程
                        // 开启一个
                        TaskThread taskThread = new TaskThread();
                        taskThread.start();
                        taskThread.d0(iTask);
                        taskThreads.add(taskThread);
                    } else {
                        // System.out.println("直接在最优线程中运行");
                        // 不可以再开启新线程了
                        minRunningTaskThread.d0(iTask);
                    }
                }
            }

        }

        public void stopAfterLastTaskFlish () {
            if (taskThreads!=null){
                for (TaskThread t: taskThreads) {
                    t.stopAfterLastTaskFlish();
                }
                taskThreads.clear();
            }
        }
    }

    /**
     * 用于执行Task的线程。
     */
    private static class TaskThread extends Thread {
        private final Object lock = new Object();

        // 标记这个线程是否正在工作中。
        private boolean isRunning = false;

        /**
         * 存放需要运行的task的集合
         */
        private ArrayList<Task> needRunTasks;

        private Handler handler;

        // 标记是否有itask正在运行
        private boolean itaskrunning = false;

        @Override
        public void run() {
            // Log.i("Mic", "线程开始：" + Thread.currentThread().getName());

            handler = new Handler(Looper.getMainLooper());

            while (isRunning) {
                try {
                    synchronized (lock) {
                        // 在集合中没有需要运行的task的时候，线程进入wait状态，该线程将运行到这里暂停
                        while (needRunTasks == null || needRunTasks.isEmpty()) {
                            // Log.i("Mic", "任务集合为空，暂停：" + Thread.currentThread().getName());
                            if (!isRunning){
                                return;
                            }
                            lock.wait();
                        }
                    }
                    itaskrunning = true;
                    // Log.i("Mic", "开始执行:" + Thread.currentThread().getName());

                    // 在新的task加入的时候，会通知该线程醒过来，继续执行任务。
                    final Task task = needRunTasks.get(0);

                    // 执行task的内容。
                    final Object returnData = task.rruunn(task.param);

                    // 执行完成，通知主线程
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            task.rruunnEnd(returnData);
                        }
                    });

                    needRunTasks.remove(task);
                    itaskrunning = false;
                    // Log.i("Mic", "执行完成:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Log.i("Mic", "线程结束:" + Thread.currentThread().getName());
        }

        /**
         * 开启线程执行
         */
        @Override
        public synchronized void start() {
            isRunning = true;
            super.start();
        }

        public boolean isItaskrunning() {
            return itaskrunning;
        }

        /**
         * 调用该方法后，如果有task正在运行，该线程将在这个task运行完成后结束。
         */
        private void stopAfterLastTaskFlish() {
            isRunning = false;
            try{
                synchronized (lock){lock.notifyAll();}
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         * 发起一个运行任务
         *
         * @param iTask
         * @return
         */
        private Task d0(ITask iTask) {
            if (null == iTask) {
                throw new RuntimeException("null iTask can't run");
            }

            Task task = new Task(iTask);

            if (null == needRunTasks) {
                needRunTasks = new ArrayList<>();
            }

            needRunTasks.add(task);

            try {
                synchronized (lock){lock.notifyAll();}
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Log.i("Mic", "发起新任务");
            return task;
        }

        public ArrayList<Task> getNeedRunTasks() {
            return needRunTasks;
        }
    }

    public static abstract class ITask {
        /**
         * 重写该方法返回参数，该方法运行在主线
         *
         * @return
         */
        public Object getParam() {
            return null;
        }

        /**
         * 子线程
         *
         * @param param
         * @return
         */
        abstract public Object run(Object param) throws Exception;

        /**
         * 主线程
         *
         * @param value
         */
        public void afterRun(Object value){};

        public void onError(Exception e){
            e.printStackTrace();
        }
    }
}