package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.dto.FfoGameDTO;
import cn.zhumouren.poetryclub.bean.dto.FfoGameRoomDTO;
import cn.zhumouren.poetryclub.bean.dto.UserDTO;
import cn.zhumouren.poetryclub.bean.dto.UserGameStateDTO;
import cn.zhumouren.poetryclub.bean.entity.*;
import cn.zhumouren.poetryclub.bean.mapper.FfoGameMapper;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomReqVO;
import cn.zhumouren.poetryclub.bean.vo.FfoGameRoomResVO;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.constant.GamesType;
import cn.zhumouren.poetryclub.constant.RedisKey;
import cn.zhumouren.poetryclub.constant.games.FfoStateType;
import cn.zhumouren.poetryclub.dao.FfoGameRepository;
import cn.zhumouren.poetryclub.dao.FfoGameRoomRedisDAO;
import cn.zhumouren.poetryclub.dao.UserGameStateDAO;
import cn.zhumouren.poetryclub.dao.UserRepository;
import cn.zhumouren.poetryclub.notice.StompFfoGameNotice;
import cn.zhumouren.poetryclub.service.FfoService;
import cn.zhumouren.poetryclub.service.RedisUserService;
import cn.zhumouren.poetryclub.util.FfoGameUtil;
import cn.zhumouren.poetryclub.util.RedisUtil;
import cn.zhumouren.poetryclub.util.RoomIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FfoServiceImpl implements FfoService {

    private final RedisUtil redisUtil;
    private final RedisUserService redisUserService;
    private final FfoGameRoomRedisDAO ffoGameRoomRedisDao;
    private final UserGameStateDAO userGameStateDAO;
    private final StompFfoGameNotice ffoGameNotice;
    private final FfoGameRepository ffoGameRepository;
    private final UserRepository userRepository;

    public FfoServiceImpl(RedisUtil redisUtil, RedisUserService redisUserService,
                          FfoGameRoomRedisDAO ffoGameRoomRedisDao, UserGameStateDAO userGameStateDAO, StompFfoGameNotice ffoGameNotice, FfoGameRepository ffoGameRepository, UserRepository userRepository) {
        this.redisUtil = redisUtil;
        this.redisUserService = redisUserService;
        this.ffoGameRoomRedisDao = ffoGameRoomRedisDao;
        this.userGameStateDAO = userGameStateDAO;
        this.ffoGameNotice = ffoGameNotice;
        this.ffoGameRepository = ffoGameRepository;
        this.userRepository = userRepository;
    }

    /**
     * 只能是单机同步
     *
     * @param user
     * @param roomId
     * @return
     */
    @Transactional
    @Override
    public synchronized ResponseResult<Boolean> userEnterGameRoom(UserEntity user, String roomId) {

        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(roomId);
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("游戏进行中，不可加入");
        }
        if (ffoGameRoomDTO.getUsers().size() == ffoGameRoomDTO.getMaxPlayers()) {
            return ResponseResult.failedWithMsg("房间人数已满");
        }
        UserDTO userDTO = new UserDTO(user);
        if (ffoGameRoomDTO.getUsers().contains(userDTO)) {
            return ResponseResult.failedWithMsg("用户已在房间中");
        }
        ffoGameRoomDTO.getUsers().add(userDTO);
        redisUtil.hset(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId, ffoGameRoomDTO);
        redisUserService.userEnterGameRoom(user, GamesType.FFO, roomId);
        return ResponseResult.success();
    }


    /**
     * 1、判断房间是否存在
     * 2、用户退出后，房间用户是否为空，为空则删除房间
     * 3、用户退出后，房间用户不为空，随机分配房主
     *
     * @param user
     * @return
     */
    @Transactional
    @Override
    public synchronized ResponseResult<Boolean> userLeaveGameRoom(UserEntity user) {
        String roomId = redisUserService.getUserGameRoomId(user, GamesType.FFO);
        if (ObjectUtils.isEmpty(roomId)) {
            return ResponseResult.failedWithMsg("用户不在游戏中");
        }
        if (!redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            return ResponseResult.failedWithMsg("飞花令游戏房间不存在");
        }
        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(roomId);
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("游戏进行中，不能离开游戏");
        }
        ffoGameRoomDTO.removeUser(new UserDTO(user));
        redisUserService.userLeaveGameRoom(user);
        // 如果users 长度为0，则删除房间
        // 否则从新设置房主
        if (ffoGameRoomDTO.getUsers().size() == 0) {
            delFfoGameRoom(ffoGameRoomDTO);
            return ResponseResult.success();
        } else {
            setFfoGameRoomOwner(ffoGameRoomDTO);
        }
        boolean b = ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        ffoGameNotice.userGameRoomActionNotice(user, user.getNickname() + "离开了房间!", ffoGameRoomDTO.getUsernames());
        ffoGameNotice.ffoGameRoomUsersNotice(ffoGameRoomDTO.getUsers());
        return ResponseResult.bool(b);
    }

    private void delFfoGameRoom(FfoGameRoomDTO ffoGameRoomDTO) {
        redisUtil.hdel(RedisKey.FFO_GAME_ROOM_KEY.name(), ffoGameRoomDTO.getId());
    }

    private void setFfoGameRoomOwner(FfoGameRoomDTO ffoGameRoomDTO) {
        UserDTO next = ffoGameRoomDTO.getUsers().iterator().next();
        ffoGameRoomDTO.setHomeowner(next);
    }

    @Override
    public ResponseResult<Set<FfoGameRoomResVO>> listFfoGameRoom() {
        HashMap<String, FfoGameRoomDTO> ffoGameRoomDTOHashMap = (HashMap<String, FfoGameRoomDTO>) redisUtil.hmget(RedisKey.FFO_GAME_ROOM_KEY.name());
        Set<FfoGameRoomResVO> ffoGameRoomResVOs = new HashSet<>();
        ffoGameRoomDTOHashMap.forEach((roomId, ffoGameRoomDTO) -> {
            ffoGameRoomResVOs.add(FfoGameMapper.INSTANCE.ffoGameRoomDTOToFfoGameRoomResVO(ffoGameRoomDTO));
        });
        return ResponseResult.success(ffoGameRoomResVOs);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveFfoGame(FfoGameDTO ffoGameDTO) {
        FfoGameEntity ffoGameEntity = new FfoGameEntity();
        ffoGameEntity.setKeyword(ffoGameDTO.getKeyword());
        ffoGameEntity.setPlayerPreparationSecond(ffoGameDTO.getPlayerPreparationSecond());
        ffoGameEntity.setAllowWordInAny(ffoGameDTO.getAllowWordInAny());
        ffoGameEntity.setMaxSentenceLength(ffoGameDTO.getMaxSentenceLength());
        ffoGameEntity.setConstantSentenceLength(ffoGameDTO.getConstantSentenceLength());
        // 开始添加用户在本局中的信息（第几名，和说飞花令的序号）
        int ranking = 0;
        while (!ffoGameDTO.getRanking().isEmpty()) {
            ranking++;
            UserDTO userDTO = ffoGameDTO.getRanking().pop();
            UserEntity userEntity = userRepository.findByUsername(userDTO.getUsername());
            ffoGameEntity.addUserInfo(new FfoGameUserInfoEntity(userEntity, ranking,
                    ffoGameDTO.getUserSequence(userDTO)));
        }
        // 添加用户发的飞花令句子
        ffoGameDTO.getUserSentences().forEach(sentence -> {
            UserEntity user = userRepository.findByUsername(sentence.getUser().getUsername());
            FfoGameUserSentenceEntity ffoGameUserSentenceEntity =
                    new FfoGameUserSentenceEntity(user, sentence.getSentence(), sentence.getSentenceJudgeType(),
                            sentence.getCreateTime());
            // 添加本个句子的投票
            sentence.getUserVotes().forEach(vote -> {
                UserEntity voteUser = userRepository.findByUsername(vote.getUser().getUsername());
                ffoGameUserSentenceEntity.addUserVote(new FfoGameUserVoteEntity(voteUser,
                        vote.getFfoVoteType(), vote.getCreateTime()));
            });
        });

        ffoGameEntity.setCreateTime(ffoGameDTO.getCreateTime());
        ffoGameEntity.setEndTime(ffoGameDTO.getEndTime());
        ffoGameRepository.save(ffoGameEntity);
    }

    @Override
    public void updateUsersSequence(String roomId, UserEntity homeowner, List<String> users) {
        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(roomId);
        if (ObjectUtils.isEmpty(ffoGameRoomDTO)) {
            log.info("房间 {} 不存在", roomId);
            return;
        }
        UserDTO userDTO = new UserDTO(homeowner);
        if (!ffoGameRoomDTO.getHomeowner().equals(userDTO)) {
            log.info("{} 不是房间 {} 的房主", userDTO, roomId);
            return;
        }
        FfoGameUtil.usersSequenceSort(ffoGameRoomDTO.getUsers(), users);
        ffoGameNotice.ffoGameRoomUsersNotice(ffoGameRoomDTO.getUsers());
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
    }


    @Transactional
    @Override
    public ResponseResult<String> userCreateGameRoom(UserEntity user, FfoGameRoomReqVO ffoGameRoomReqVO) {
        if (!redisUtil.hasKey(RedisKey.FFO_GAME_ROOM_KEY.name())) {
            redisUtil.hmset(RedisKey.FFO_GAME_ROOM_KEY.name(), new HashMap<>());
        }
        String roomId = RoomIdUtil.generateRoomId();
        while (redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            roomId = RoomIdUtil.generateRoomId();
        }
        UserDTO userDTO = new UserDTO(user);
        FfoGameRoomDTO ffoGameRoomDTO = FfoGameRoomDTO.creatFfoGameRoomDTO(ffoGameRoomReqVO, roomId,
                userDTO, userDTO);
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        redisUserService.userEnterGameRoom(user, GamesType.FFO, roomId);
        return ResponseResult.success(roomId);
    }

    @Override
    public void sessionDisconnect(UserEntity user) {
        userLeaveGameRoom(user);
    }

    @Override
    public void userSubscribeChatroom(UserEntity userEntity) {
        UserGameStateDTO userGameStateDTO = userGameStateDAO.getUserGameStateDTO(userEntity.getUsername());
        if (ObjectUtils.isEmpty(userGameStateDTO)) {
            return;
        }
        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(userGameStateDTO.getRoomId());
        ffoGameNotice.userGameRoomActionNotice(userEntity, userEntity.getNickname() + "进入了房间",
                ffoGameRoomDTO.getUsernames());
    }
}
