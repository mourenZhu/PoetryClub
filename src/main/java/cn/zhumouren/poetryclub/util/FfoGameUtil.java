package cn.zhumouren.poetryclub.util;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class FfoGameUtil {

    /**
     * 通过 LocalDateTime 来获取 cron 表达式
     *
     * @param timeout
     * @return
     */
    public static String getFfoSentenceTimeoutCron(LocalDateTime timeout) {
        return timeout.getSecond() + " " + timeout.getMinute() + " " + timeout.getHour() + " "
                + " " + timeout.getDayOfMonth() + " " + timeout.getMonthValue() + " ? " + timeout.getYear();
    }

    /**
     * 获取飞花令下一个发言者超时发言的时间
     *
     * @param ffoGameDTO
     * @return
     */
    public static LocalDateTime getFfoSpeakerNextEndTime(FfoGameDTO ffoGameDTO) {
        LocalDateTime timeout;
        if (ffoGameDTO.getUserSentences().size() == 0) {
            timeout = ffoGameDTO.getCreateTime().plusSeconds(ffoGameDTO.getPlayerPreparationSecond());
        } else {
            timeout = Iterables.getLast(ffoGameDTO.getUserSentences())
                    .getCreateTime().plusSeconds(ffoGameDTO.getPlayerPreparationSecond());
        }
        return timeout;
    }
}
