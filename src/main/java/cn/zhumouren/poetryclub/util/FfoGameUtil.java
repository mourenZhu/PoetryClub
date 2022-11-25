package cn.zhumouren.poetryclub.util;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import cn.zhumouren.poetryclub.property.FfoGameProperty;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class FfoGameUtil {

    private static int usersVoteSecond = 30;
    private final FfoGameProperty ffoGameProperty;

    public FfoGameUtil(FfoGameProperty ffoGameProperty) {
        this.ffoGameProperty = ffoGameProperty;
        usersVoteSecond = ffoGameProperty.getUsersVoteSecond();
    }

    /**
     * 通过 LocalDateTime 来获取 cron 表达式
     *
     * @param timeout
     * @return
     */
    public static String getTimeoutCron(LocalDateTime timeout) {
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

    public static LocalDateTime getFfoVoteEndTime(FfoGameDTO ffoGameDTO) {
        LocalDateTime timeout;
        if (ffoGameDTO.getUserSentences().size() == 0) {
            timeout = ffoGameDTO.getCreateTime().plusSeconds(usersVoteSecond);
        } else {
            timeout = Iterables.getLast(ffoGameDTO.getUserSentences())
                    .getCreateTime().plusSeconds(usersVoteSecond);
        }
        return timeout;
    }

    /**
     * 按users的顺序对userDTOS进行排序
     *
     * @param userDTOS
     * @param users
     */
    public static void usersSequenceSort(List<UserDTO> userDTOS, List<String> users) {
        for (UserDTO userDTO : userDTOS) {
            if (!users.contains(userDTO.getUsername())) {
                return;
            }
        }
        userDTOS.sort((o1, o2) -> {
            int i1 = users.indexOf(o1.getUsername());
            int i2 = users.indexOf(o2.getUsername());
            if (i1 == i2) {
                return 0;
            }
            return i1 > i2 ? -1 : 1;
        });
    }

}
