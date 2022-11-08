package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.service.TaskSchedulingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    private final TaskScheduler taskScheduler;

    public Map<String, ScheduledFuture<?>> tasksMap = new ConcurrentHashMap<>();

    public TaskSchedulingServiceImpl(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void addTask(String taskId, String cronExpression, Runnable tasklet) {
        log.debug("task id = {}, cron = {}", taskId, cronExpression);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(tasklet, new CronTrigger(cronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        tasksMap.put(taskId, scheduledTask);
    }

    @Override
    public void removeTask(String taskId) {
        ScheduledFuture<?> scheduledTask = tasksMap.get(taskId);
        if (ObjectUtils.isNotEmpty(scheduledTask)) {
            scheduledTask.cancel(true);
            tasksMap.remove(taskId);
        }
    }
}
