package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.bean.mapper.PoemMapper;
import cn.zhumouren.poetryclub.bean.vo.PoemResVo;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.dao.PoemRepository;
import cn.zhumouren.poetryclub.service.PoemEntityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PoemEntityServiceImpl implements PoemEntityService {

    private final PoemRepository poemRepository;
    private final PoemMapper poemMapper;

    public PoemEntityServiceImpl(PoemRepository poemRepository, PoemMapper poemMapper) {
        this.poemRepository = poemRepository;
        this.poemMapper = poemMapper;
    }

    @Override
    public List<PoemEntity> listBySentenceWordIndex(Character word, int index) {
        String regex = getFfoRegex(word, index);
        return poemRepository.findByRegex(regex);
    }

    private String getFfoRegex(Character word, int index) {
        return String.format("(^[\\u4e00-\\u9fa5]{%d}%c.*|^.*?[，。？；]+[\\u4e00-\\u9fa5]{%d}%c.*)", index, word, index, word);
    }

    @Override
    public List<PoemEntity> listBySentence(String sentence) {
        String regex = String.format("(^%s[，。？；].*|^.*?[，。？]%s[，。？；].*)", sentence, sentence);
        log.debug("飞花令查询句子的正则表达式 {}", regex);
        return poemRepository.findByRegex(regex);
    }

    @Override
    public ResponseResult<PoemResVo> getPoem(Long id) {
        Optional<PoemEntity> optional = poemRepository.findById(id);
        return optional.map(poemEntity ->
                        ResponseResult.success(poemMapper.toDto(poemEntity)))
                .orElseGet(() -> ResponseResult.failedWithMsg("该诗不存在"));
    }

    @Override
    public ResponseResult<Page<PoemResVo>> listPoem(
            String author, String era, String title, String content, Set<String> tags,
            Pageable pageable) {
        Specification<PoemEntity> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> listAnd = new ArrayList<>();
            List<Predicate> listOr = new ArrayList<>();
            Join<PoemEntity, AuthorEntity> authorJoin = root.join("author", JoinType.LEFT);
            Join<PoemEntity, LiteratureTagEntity> tagJoin = root.join("tags", JoinType.LEFT);
            if (StringUtils.isNotBlank(author)) {
                listAnd.add(criteriaBuilder.like(authorJoin.get("name").as(String.class), "%" + author + "%"));
            }
            if (StringUtils.isNotBlank(era)) {
                listAnd.add(criteriaBuilder.like(authorJoin.get("era").as(String.class), "%" + era + "%"));
            }
            if (StringUtils.isNotBlank(title)) {
                listAnd.add(criteriaBuilder.like(root.get("title").as(String.class), "%" + title + "%"));
            }
            if (StringUtils.isNotBlank(content)) {
                listAnd.add(criteriaBuilder.like(root.get("content").as(String.class), "%" + content + "%"));
            }
            if (ObjectUtils.isNotEmpty(tags)) {
                for (String tag : tags) {
                    listOr.add(criteriaBuilder.like(tagJoin.get("tag"), "%" + tag + "%"));
                }
            }
            Predicate[] andPres = new Predicate[listAnd.size()];
            Predicate[] orPres = new Predicate[listOr.size()];
            query.distinct(true);
            if (andPres.length != 0 && orPres.length != 0) {
                Predicate andPre = criteriaBuilder.and(listAnd.toArray(andPres));
                Predicate orPre = criteriaBuilder.or(listOr.toArray(orPres));
                return query.where(andPre, orPre).getRestriction();
            } else if (andPres.length != 0) {
                return criteriaBuilder.and(listAnd.toArray(andPres));
            } else if (orPres.length != 0) {
                return criteriaBuilder.or(listOr.toArray(orPres));
            }
            return query.where().getRestriction();
        };
        Page<PoemEntity> page = poemRepository.findAll(specification, pageable);
        return ResponseResult.success(page.map(poemMapper::toDto));
    }

    @Override
    public ResponseResult<Page<PoemResVo>> listPoem(Character keyword, Integer keywordIndex, Pageable pageable) {
        Page<PoemEntity> page = poemRepository.findByRegex(getFfoRegex(keyword, keywordIndex), pageable);
        return ResponseResult.success(page.map(poemMapper::toDto));
    }
}
