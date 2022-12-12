package cn.zhumouren.poetryclub.bean.mapper;

import cn.zhumouren.poetryclub.bean.entity.FfoGameUserInfoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FfoGameUserInfoMapperTest {

    @Autowired
    FfoGameUserInfoMapper ffoGameUserInfoMapper;

    @Test
    public void toDtoTest() {
        ffoGameUserInfoMapper.toDto(new FfoGameUserInfoEntity());
    }
}
