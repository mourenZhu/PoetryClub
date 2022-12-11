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
import cn.zhumouren.poetryclub.constant.MessageDestinations;
import cn.zhumouren.poetryclub.constant.RedisKey;
import cn.zhumouren.poetryclub.constant.games.FfoGamePoemType;
import cn.zhumouren.poetryclub.constant.games.FfoStateType;
import cn.zhumouren.poetryclub.dao.*;
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

import java.util.*;
import java.util.stream.Collectors;

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
    private final PoemRepository poemRepository;

    public FfoServiceImpl(RedisUtil redisUtil, RedisUserService redisUserService,
                          FfoGameRoomRedisDAO ffoGameRoomRedisDao, UserGameStateDAO userGameStateDAO,
                          StompFfoGameNotice ffoGameNotice, FfoGameRepository ffoGameRepository,
                          UserRepository userRepository, PoemRepository poemRepository) {
        this.redisUtil = redisUtil;
        this.redisUserService = redisUserService;
        this.ffoGameRoomRedisDao = ffoGameRoomRedisDao;
        this.userGameStateDAO = userGameStateDAO;
        this.ffoGameNotice = ffoGameNotice;
        this.ffoGameRepository = ffoGameRepository;
        this.userRepository = userRepository;
        this.poemRepository = poemRepository;
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
        if (ObjectUtils.isEmpty(ffoGameRoomDTO)) {
            return ResponseResult.failedWithMsg(roomId + " 房间不存在");
        }
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            return ResponseResult.failedWithMsg("游戏进行中，不可加入");
        }
        UserGameStateDTO userGameStateDTO = userGameStateDAO.getUserGameStateDTO(user.getUsername());
        if (ObjectUtils.isNotEmpty(userGameStateDTO)) {
            if (!userGameStateDTO.getRoomId().equals(roomId)) {
                return ResponseResult.failedWithMsg("玩家在其他房间中，不允许加入多个房间");
            }
        }
        UserDTO userDTO = new UserDTO(user);
        if (ffoGameRoomDTO.getUsers().contains(userDTO)) {
            // 如果玩家已经在本房间中，直接返回true
            return ResponseResult.success();
        }
        if (ffoGameRoomDTO.getUsers().size() == ffoGameRoomDTO.getMaxPlayers()) {
            return ResponseResult.failedWithMsg("房间人数已满");
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
        boolean isRemove = ffoGameRoomDTO.removeUser(new UserDTO(user));
        if (!isRemove) {
            return ResponseResult.failedWithMsg("不在游戏房间中");
        }
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
        ffoGameNotice.ffoGameRoomNotice(ffoGameRoomDTO);
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
    public ResponseResult<List<FfoGameRoomResVO>> listFfoGameRoom() {
        Map<String, FfoGameRoomDTO> ffoGameRoomDTOHashMap = ffoGameRoomRedisDao.getFfoGameRoomDTOMap();
        List<FfoGameRoomResVO> ffoGameRoomResVOs = new ArrayList<>();
        ffoGameRoomDTOHashMap.forEach((roomId, ffoGameRoomDTO) -> {
            if (ffoGameRoomDTO.getDisplay()) {
                ffoGameRoomResVOs.add(FfoGameMapper.INSTANCE.ffoGameRoomDTOToFfoGameRoomResVO(ffoGameRoomDTO));
            }
        });
        return ResponseResult.success(ffoGameRoomResVOs);
    }

    @Override
    public ResponseResult<List<FfoGameRoomResVO>> listFfoGameRoom(
            String roomId, String keyword, Boolean allowWordInAny,
            FfoGamePoemType ffoGamePoemType, FfoStateType ffoStateType) {
        Map<String, FfoGameRoomDTO> ffoGameRoomDTOHashMap = ffoGameRoomRedisDao.getFfoGameRoomDTOMap();
        List<FfoGameRoomResVO> ffoGameRoomResVOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(roomId)) {
            FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomDTOHashMap.get(roomId);
            if (ffoGameRoomDTO.getDisplay()) {
                ffoGameRoomResVOs.add(FfoGameMapper.INSTANCE
                        .ffoGameRoomDTOToFfoGameRoomResVO(ffoGameRoomDTO));
            }
            return ResponseResult.success(ffoGameRoomResVOs);
        }
        for (Map.Entry<String, FfoGameRoomDTO> stringFfoGameRoomDTOEntry : ffoGameRoomDTOHashMap.entrySet()) {
            if (stringFfoGameRoomDTOEntry.getValue().getDisplay()) {
                ffoGameRoomResVOs.add(FfoGameMapper.INSTANCE.ffoGameRoomDTOToFfoGameRoomResVO(stringFfoGameRoomDTOEntry
                        .getValue()));
            }
        }
        if (ObjectUtils.isNotEmpty(keyword)) {
            ffoGameRoomResVOs = ffoGameRoomResVOs.stream().filter(fgr ->
                    fgr.getKeyword().equals(keyword)).collect(Collectors.toList());
        }
        if (ObjectUtils.isNotEmpty(allowWordInAny)) {
            ffoGameRoomResVOs = ffoGameRoomResVOs.stream().filter(fgr ->
                    fgr.getAllowWordInAny().equals(allowWordInAny)).collect(Collectors.toList());
        }
        if (ObjectUtils.isNotEmpty(ffoGamePoemType)) {
            ffoGameRoomResVOs = ffoGameRoomResVOs.stream().filter(fgr ->
                    fgr.getFfoGamePoemType().equals(ffoGamePoemType)).collect(Collectors.toList());
        }
        if (ObjectUtils.isNotEmpty(ffoStateType)) {
            ffoGameRoomResVOs = ffoGameRoomResVOs.stream().filter(fgr ->
                    fgr.getFfoStateType().equals(ffoStateType)).collect(Collectors.toList());
        }
        return ResponseResult.success(ffoGameRoomResVOs);
    }

    @Override
    public void kickOutUser(String roomId, UserEntity homeowner, String kickOutUser) {
        if (!redisUtil.hHasKey(RedisKey.FFO_GAME_ROOM_KEY.name(), roomId)) {
            log.debug("飞花令游戏房间不存在");
            return;
        }
        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(roomId);
        UserDTO homeownerDTO = new UserDTO(homeowner);
        if (!ffoGameRoomDTO.getHomeowner().equals(homeownerDTO)) {
            log.debug("只有房主才能踢出用户");
            return;
        }
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            log.debug("游戏进行中，不能踢出用户");
            return;
        }
        if (homeownerDTO.getUsername().equals(kickOutUser)) {
            log.debug("房主不能踢自己");
            return;
        }
        UserDTO kickOutUserDTO = ffoGameRoomDTO.getUserDTOByUsername(kickOutUser);
        if (ObjectUtils.isEmpty(kickOutUserDTO)) {
            log.debug("{} 不在房间中", kickOutUser);
            return;
        }
        ffoGameRoomDTO.removeUser(kickOutUserDTO);
        redisUserService.userLeaveGameRoom(kickOutUser);
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        ffoGameNotice.ffoGameRoomUserKickOutNotice(kickOutUser);
        ffoGameNotice.userGameRoomActionNotice(kickOutUserDTO, kickOutUserDTO.getNickname() + "被踢出了房间!",
                ffoGameRoomDTO.getUsernames());
        ffoGameNotice.ffoGameRoomUsersNotice(ffoGameRoomDTO.getUsers());
        ffoGameNotice.ffoGameRoomNotice(ffoGameRoomDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveFfoGame(FfoGameDTO ffoGameDTO) {
        FfoGameEntity ffoGameEntity = new FfoGameEntity();
        ffoGameEntity.setFfoGamePoemType(ffoGameDTO.getFfoGamePoemType());
        ffoGameEntity.setKeyword(ffoGameDTO.getKeyword());
        ffoGameEntity.setPlayerPreparationSecond(ffoGameDTO.getPlayerPreparationSecond());
        ffoGameEntity.setAllowWordInAny(ffoGameDTO.getAllowWordInAny());
        ffoGameEntity.setMaxSentenceLength(ffoGameDTO.getMaxSentenceLength());
        ffoGameEntity.setConstantSentenceLength(ffoGameDTO.getConstantSentenceLength());
        // 开始添加用户在本局中的信息（第几名，和说飞花令的序号）
        int ranking = 0;
        while (!ffoGameDTO.getRanking().isEmpty()) {
            ranking++;
            var userDTO = ffoGameDTO.getRanking().pollFirst();
            UserEntity userEntity = userRepository.findByUsername(userDTO.getUsername());
            ffoGameEntity.addUserInfo(new FfoGameUserInfoEntity(userEntity,
                    ffoGameDTO.getUserSequence(userDTO), ranking));
        }
        // 添加用户发的飞花令句子
        ffoGameDTO.getUserSentences().forEach(sentence -> {
            UserEntity user = userRepository.findByUsername(sentence.getUser().getUsername());
            FfoGameUserSentenceEntity ffoGameUserSentenceEntity =
                    new FfoGameUserSentenceEntity(user, sentence.getSentence(), sentence.getSentenceJudgeType(),
                            sentence.getCreateTime());
            if (ObjectUtils.isNotEmpty(sentence.getPoemId())) {
                Optional<PoemEntity> poem = poemRepository.findById(sentence.getPoemId());
                poem.ifPresent(ffoGameUserSentenceEntity::setPoemEntity);
            }
            // 添加本个句子的投票
            sentence.getUserVotes().forEach(vote -> {
                UserEntity voteUser = userRepository.findByUsername(vote.getUser().getUsername());
                ffoGameUserSentenceEntity.addUserVote(new FfoGameUserVoteEntity(voteUser,
                        vote.getFfoVoteType(), vote.getCreateTime()));
            });
            ffoGameEntity.addUserSentence(ffoGameUserSentenceEntity);
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

    @Override
    public void updateGameRoom(String roomId, UserEntity homeowner, FfoGameRoomReqVO ffoGameRoomReqVO) {
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
        if (ffoGameRoomDTO.getFfoStateType().equals(FfoStateType.PLAYING)) {
            log.info("{} 正在游戏中，不能修改", roomId);
            return;
        }
        ffoGameRoomDTO.update(ffoGameRoomReqVO);
        ffoGameRoomRedisDao.saveFfoGameRoomDTO(ffoGameRoomDTO);
        ffoGameNotice.ffoGameRoomNotice(ffoGameRoomDTO);
    }


    @Transactional
    @Override
    public ResponseResult userCreateGameRoom(UserEntity user, FfoGameRoomReqVO ffoGameRoomReqVO) {
        if (ObjectUtils.isNotEmpty(userGameStateDAO.getUserGameStateDTO(user.getUsername()))) {
            return ResponseResult.failedWithMsg("玩家已在房间中");
        }
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
    public void disconnect(UserEntity user) {
        userLeaveGameRoom(user);
    }

    @Override
    public void subscribe(String destination, UserEntity userEntity) {
        UserGameStateDTO userGameStateDTO = userGameStateDAO.getUserGameStateDTO(userEntity.getUsername());
        if (ObjectUtils.isEmpty(userGameStateDTO)) {
            return;
        }
        FfoGameRoomDTO ffoGameRoomDTO = ffoGameRoomRedisDao.getFfoGameRoomDTO(userGameStateDTO.getRoomId());
        if (destination.contains(MessageDestinations.USER_GAME_ROOM_MESSAGE_DESTINATION)) {
            ffoGameNotice.userGameRoomActionNotice(userEntity, userEntity.getNickname() + "进入了房间",
                    ffoGameRoomDTO.getUsernames());
        }
        if (destination.contains(MessageDestinations.USER_GAME_FFO_USERS_DESTINATION)) {
            ffoGameNotice.ffoGameRoomUsersNotice(ffoGameRoomDTO.getUsers());
        }
        if (destination.contains(MessageDestinations.USER_GAME_FFO_ROOM_DESTINATION)) {
            ffoGameNotice.ffoGameRoomNotice(ffoGameRoomDTO);
        }
    }
}
