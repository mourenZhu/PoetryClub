package cn.zhumouren.poetryclub.dao;

import cn.zhumouren.poetryclub.bean.entity.FfoGameEntity;
import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.util.PageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

@SpringBootTest
public class FfoGameRepositoryTest {

    @Autowired
    private FfoGameRepository ffoGameRepository;

    @Autowired
    private FfoGameMapper ffoGameMapper;

    @Test
    public void listUserFfoGameTest() {
        Page page = ffoGameRepository.findByUserInfoEntities_UserEntity_UsernameOrderByEndTimeDesc
                ("test01", PageUtil.getPageable());
        System.out.println(page);
        page.getContent().forEach(System.out::println);
    }
}
