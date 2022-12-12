package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.FfoGameEntity;
import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FfoGameRepositoryTest {

    @Autowired
    private FfoGameRepository ffoGameRepository;

    @Autowired
    private FfoGameMapper ffoGameMapper;

    @Test
    public void listUserFfoGameTest() {
        List<FfoGameEntity> data = ffoGameRepository.findByUserInfoEntities_UserEntity_UsernameOrderByEndTimeDesc
                ("test01", PageUtil.getPageable());
        data.forEach(d -> {
            System.out.println(ffoGameMapper.toDto(d));
        });
    }
}
