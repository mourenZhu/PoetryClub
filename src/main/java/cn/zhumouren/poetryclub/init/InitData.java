package cn.zhumouren.poetryclub.init;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/10 16:16
 **/
@Component
@Log4j2
public class InitData {


    private final List<IInitData> initDataList;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String jpaHibernateDdlAuto;

    public InitData(List<IInitData> initDataList) {
        this.initDataList = initDataList;
    }

    @PostConstruct
    public void initData() {
        log.info("jpaHibernateDdlAuto ====  {}", jpaHibernateDdlAuto);
        if (jpaHibernateDdlAuto.equals("create") || jpaHibernateDdlAuto.equals("create-drop")) {
            for (IInitData initData : initDataList) {
                log.info("initData ==== {}", initData);
                initData.init();
            }
        }
    }
}
