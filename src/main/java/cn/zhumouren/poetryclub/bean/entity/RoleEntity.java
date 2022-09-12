package cn.zhumouren.poetryclub.bean.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/12 16:37
 **/
@Entity
@Data
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 30)
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleEntity that = (RoleEntity) o;

        return getRole().equals(that.getRole());
    }

    @Override
    public int hashCode() {
        return getRole().hashCode();
    }
}