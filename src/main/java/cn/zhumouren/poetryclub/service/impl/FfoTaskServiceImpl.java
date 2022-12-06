package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.service.FfoTaskService;
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
public class FfoTaskServiceImpl implements FfoTaskService {

    private static final String TASK_SPEAK = "speak_";
    private static final String TASK_VOTE = "vote_";
    private final TaskScheduler taskScheduler;
    public Map<String, ScheduledFuture<?>> tasksMap = new ConcurrentHashMap<>();

    public FfoTaskServiceImpl(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void addTask(String taskId, String cronExpression, Runnable tasklet) {
        log.debug("task id = {}, cron = {}", taskId, cronExpression);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(tasklet, new CronTrigger(cronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        tasksMap.put(taskId, scheduledTask);
    }

    public void removeTask(String taskId) {
        ScheduledFuture<?> scheduledTask = tasksMap.get(taskId);
        if (ObjectUtils.isNotEmpty(scheduledTask)) {
            scheduledTask.cancel(true);
            tasksMap.remove(taskId);
        }
    }

    @Override
    public void addSpeakTimeOutTask(String roomId, String cronExpression, Runnable tasklet) {
        log.info("飞花令定时任务: roomId {}, cronExpression {}", roomId, cronExpression);
        addTask(TASK_SPEAK + roomId, cronExpression, tasklet);
    }

    @Override
    public void removeSpeakTimeOutTask(String roomId) {
        log.info("移除定时任务: roomId {}", roomId);
        removeTask(TASK_SPEAK + roomId);
    }

    @Override
    public void addVoteTimeOutTask(String roomId, String cronExpression, Runnable tasklet) {
        log.info("投票定时任务: roomId {}, cronExpression {}", roomId, cronExpression);
        addTask(TASK_VOTE + roomId, cronExpression, tasklet);
    }

    @Override
    public void removeVoteTimeOutTask(String roomId) {
        log.info("移除投票定时任务: roomId {}", roomId);
        removeTask(TASK_VOTE + roomId);
    }
}
