package cn.zhumouren.poetryclub.service;

public interface TaskSchedulingService {

    void addTask(String taskId, String cronExpression, Runnable tasklet);

    void removeTask(String taskId);
}
