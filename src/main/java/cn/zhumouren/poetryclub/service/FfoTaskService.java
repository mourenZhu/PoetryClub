package cn.zhumouren.poetryclub.service;

public interface FfoTaskService {

    /**
     * 增加用户发言超时任务
     *
     * @param roomId
     * @param cronExpression
     * @param tasklet
     */
    void addSpeakTimeOutTask(String roomId, String cronExpression, Runnable tasklet);

    /**
     * 移除用户发言超时任务
     *
     * @param roomId
     */
    void removeSpeakTimeOutTask(String roomId);

    /**
     * 增加用户投票超时任务
     *
     * @param roomId
     * @param cronExpression
     * @param tasklet
     */
    void addVoteTimeOutTask(String roomId, String cronExpression, Runnable tasklet);

    /**
     * 移除用户投票超时任务
     *
     * @param roomId
     */
    void removeVoteTimeOutTask(String roomId);
}
