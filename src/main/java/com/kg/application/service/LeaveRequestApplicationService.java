package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.LeaveRequest;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.LeaveRequestRepository;
import com.kg.domain.repository.SysUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.entity.NotificationMessageEntity;
import com.kg.infrastructure.mapper.NotificationMessageMapper;
import com.kg.infrastructure.mapper.LeaveRequestMapper;
import com.kg.interfaces.dto.LeaveApproveRequest;
import com.kg.interfaces.dto.LeaveCreateRequest;
import com.kg.interfaces.dto.LeavePageRequest;
import com.kg.interfaces.dto.LeaveVO;
import com.kg.interfaces.dto.PageVO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 请假申请应用服务 —— 学生请假、老师/班长审批。
 */
@Service
public class LeaveRequestApplicationService {

    private static final Logger log = LoggerFactory.getLogger(LeaveRequestApplicationService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String STATUS_PENDING = "0";
    private static final String STATUS_APPROVED = "1";
    private static final String STATUS_REJECTED = "2";
    private static final String NOTIFY_TYPE_LEAVE = "LEAVE";

    public static BigDecimal twoDay = new BigDecimal(2);


    private final LeaveRequestRepository leaveRepo;
    private final LeaveRequestMapper leaveMapper;
    private final SysUserRepository sysUserRepo;
    private final NotificationMessageMapper notificationMapper;

    public LeaveRequestApplicationService(LeaveRequestRepository leaveRepo,
                                           LeaveRequestMapper leaveMapper,
                                           SysUserRepository sysUserRepo,
                                           NotificationMessageMapper notificationMapper) {
        this.leaveRepo = leaveRepo;
        this.leaveMapper = leaveMapper;
        this.sysUserRepo = sysUserRepo;
        this.notificationMapper = notificationMapper;
    }

    // ======================== 学生：新增请假 ========================

    /**
     * 当前学生新增请假申请。计算请假天数、确定审批人、生成通知消息。
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(LeaveCreateRequest req) {
        SysUser currentUser = requireLogin();
        String role = currentUser.getRole();


        if (RoleEnum.isTeacher(role)) {
            throw new BusinessException(403, "老师无需请假");
        }

        LocalDate startDate = LocalDate.parse(req.getStartDate(), DATE_FMT);
        LocalDate endDate = LocalDate.parse(req.getEndDate(), DATE_FMT);
        BigDecimal leaveDays = calculateLeaveDays(startDate, req.getStartPeriod(), endDate, req.getEndPeriod());

        LeaveRequest leave = new LeaveRequest();
        leave.setUserId(currentUser.getId());
        leave.setLeaveType(req.getLeaveType());
        leave.setStartDate(startDate);
        leave.setStartPeriod(req.getStartPeriod());
        leave.setEndDate(endDate);
        leave.setEndPeriod(req.getEndPeriod());
        leave.setLeaveDays(leaveDays);
        leave.setReason(req.getReason());
        leave.setStatus(STATUS_PENDING);
        leave.setCreateBy(currentUser.getId());
        leave.setCreateTime(LocalDateTime.now());

        // 确定审批人
        resolveApprover(currentUser, leave, leaveDays);

        leaveRepo.save(leave);
        log.info("请假申请创建: id={}, userId={}, leaveDays={}", leave.getId(), currentUser.getId(), leaveDays);

        // 生成通知消息
        createNotifications(leave, currentUser);
    }

    /** 根据角色和请假天数确定审批人与审批角色 */
    private void resolveApprover(SysUser currentUser, LeaveRequest leave, BigDecimal leaveDays) {
        if (RoleEnum.isHeadmaster(currentUser.getRole())) {
            // 班长请假 → 审批角色为老师
            leave.setApproveRole(RoleEnum.TEACHER.getCode());
            leave.setApproverId(null);
        } else {
            // 学生请假
            if (leaveDays.compareTo(twoDay) < 0) {
                // 一天内：找本班班长审批
                Long classId = currentUser.getClassId();
                if (classId != null) {
                    List<SysUser> classUsers = sysUserRepo.findByClassId(classId);
                    SysUser headmaster = classUsers.stream()
                            .filter(u -> RoleEnum.isHeadmaster(u.getRole()))
                            .findFirst().orElse(null);
                    if (headmaster != null) {
                        leave.setApproverId(headmaster.getId());
                        leave.setApproveRole(RoleEnum.HEADMASTER.getCode());
                        return;
                    }
                }
                // 班级无班长，升级为老师审批
                leave.setApproveRole(RoleEnum.TEACHER.getCode());
                leave.setApproverId(null);
            } else {
                // 超过一天 → 老师审批
                leave.setApproveRole(RoleEnum.TEACHER.getCode());
                leave.setApproverId(null);
            }
        }
    }

    /** 生成请假通知消息 */
    private void createNotifications(LeaveRequest leave, SysUser applicant) {
        LocalDateTime now = LocalDateTime.now();
        String title = "请假申请";

        String content = applicant.getName() + "请假" + leave.getLeaveDays() + "天";

        if (leave.getApproverId() != null) {
            // 指定了审批人，只发一条
            insertNotification(leave.getApproverId(), title, content, leave.getId(), now);
        } else if (leave.getApproveRole() != null) {
            // 按角色发送，查询该角色所有用户
            List<String> roles = new ArrayList<>();
            roles.add(leave.getApproveRole());
            List<SysUser> roleUsers = sysUserRepo.listByRoles(roles);
            for (SysUser u : roleUsers) {
                insertNotification(u.getId(), title, content, leave.getId(), now);
            }
        }
    }

    private void insertNotification(Long userId, String title, String content, Long leaveId, LocalDateTime now) {
        NotificationMessageEntity msg = new NotificationMessageEntity();
        msg.setUserId(userId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setPriority(1);
        msg.setType(NOTIFY_TYPE_LEAVE);
        msg.setRelatedId(leaveId);
        msg.setStatus("0");
        msg.setIsRead(0);
        msg.setCreatedAt(now);
        msg.setNotifyTime(now);
        notificationMapper.insert(msg);
    }

    // ======================== 学生：查询自己的请假 ========================

    /** 分页查询当前用户的请假申请 */
    public PageVO<LeaveVO> pageMine(LeavePageRequest req) {
        SysUser user = requireLogin();
        int offset = (req.getPageNum() - 1) * req.getPageSize();
        long total = leaveMapper.countMyLeaves(user.getId(), req.getLeaveType(), req.getStatus(), req.getStartDateBegin(), req.getStartDateEnd());
        List<Map<String, Object>> rows = leaveMapper.pageMyLeaves(user.getId(), req.getLeaveType(), req.getStatus(), req.getStartDateBegin(), req.getStartDateEnd(), offset, req.getPageSize());
        return buildPageResult(total, rowsToVO(rows));
    }

    // ======================== 审批列表（老师 & 班长共用） ========================

    /**
     * 审批列表：老师查 approver_role=老师的审批角色，班长查 approver_id=自己的。
     * LEFT JOIN sys_user 查出 userName。
     * tab 参数：pending=待办(status=0) done=已办(status=1,2)
     */
    public PageVO<LeaveVO> pageApprove(String tab, int pageNum, int pageSize) {
        SysUser user = requireLogin();
        String role = user.getRole();

        if (RoleEnum.isTeacher(role)) {
            return pageApproveForTeacher(tab, pageNum, pageSize);
        } else if (RoleEnum.isHeadmaster(role)) {
            return pageApproveForHeadmaster(user.getId(), tab, pageNum, pageSize);
        } else {
            throw new BusinessException(403, "学生无审批权限");
        }
    }

    /** 老师审批列表：按审批角色查 */
    private PageVO<LeaveVO> pageApproveForTeacher(String tab, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        if ("done".equals(tab)) {
            // 已办：1+2，查询全部再合并排序分页
            List<Map<String, Object>> all = new ArrayList<>();
            all.addAll(leaveMapper.pageByApproveRole(RoleEnum.TEACHER.getCode(), STATUS_APPROVED, 0, 1000));
            all.addAll(leaveMapper.pageByApproveRole(RoleEnum.TEACHER.getCode(), STATUS_REJECTED, 0, 1000));
            all.sort((a, b) -> compareApproveTime(a, b));
            long total = leaveMapper.countByApproveRole(RoleEnum.TEACHER.getCode(), STATUS_APPROVED)
                    + leaveMapper.countByApproveRole(RoleEnum.TEACHER.getCode(), STATUS_REJECTED);
            int to = Math.min(offset + pageSize, all.size());
            return buildPageResult(total, rowsToVO(offset < all.size() ? all.subList(offset, to) : Collections.emptyList()));
        } else {
            List<Map<String, Object>> rows = leaveMapper.pageByApproveRole(RoleEnum.TEACHER.getCode(), STATUS_PENDING, offset, pageSize);
            long total = leaveMapper.countByApproveRole(RoleEnum.TEACHER.getCode(), STATUS_PENDING);
            return buildPageResult(total, rowsToVO(rows));
        }
    }

    /** 班长审批列表：按审批人ID查 */
    private PageVO<LeaveVO> pageApproveForHeadmaster(Long headmasterId, String tab, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        if ("done".equals(tab)) {
            List<Map<String, Object>> all = new ArrayList<>();
            all.addAll(leaveMapper.pageByApproverId(headmasterId, STATUS_APPROVED, 0, 1000));
            all.addAll(leaveMapper.pageByApproverId(headmasterId, STATUS_REJECTED, 0, 1000));
            all.sort((a, b) -> compareApproveTime(a, b));
            long total = leaveMapper.countByApproverId(headmasterId, STATUS_APPROVED)
                    + leaveMapper.countByApproverId(headmasterId, STATUS_REJECTED);
            int to = Math.min(offset + pageSize, all.size());
            return buildPageResult(total, rowsToVO(offset < all.size() ? all.subList(offset, to) : Collections.emptyList()));
        } else {
            List<Map<String, Object>> rows = leaveMapper.pageByApproverId(headmasterId, STATUS_PENDING, offset, pageSize);
            long total = leaveMapper.countByApproverId(headmasterId, STATUS_PENDING);
            return buildPageResult(total, rowsToVO(rows));
        }
    }

    private int compareApproveTime(Map<String, Object> a, Map<String, Object> b) {
        Object ta = a.get("approve_time"), tb = b.get("approve_time");
        if (ta == null && tb == null) return 0;
        if (ta == null) return 1;
        if (tb == null) return -1;
        return String.valueOf(tb).compareTo(String.valueOf(ta));
    }

    // ======================== 审批操作（批量） ========================

    /**
     * 批量审批通过/拒绝。
     * 校验：每条请假记录必须存在且 status=0，否则提示"请假单已审批"。
     * 老师审批后使相关通知消息失效（status='1'），班长审批后不处理通知。
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(LeaveApproveRequest req) {
        SysUser user = requireLogin();
        String role = user.getRole();
        if (RoleEnum.isStudent(role)) {
            throw new BusinessException(403, "学生无审批权限");
        }
        boolean isTeacher = RoleEnum.isTeacher(role);
        LocalDateTime now = LocalDateTime.now();

        for (Long id : req.getIds()) {
            LeaveRequest leave = leaveRepo.findById(id)
                    .orElseThrow(() -> new BusinessException("请假单不存在: id=" + id));
            if (!STATUS_PENDING.equals(leave.getStatus())) {
                throw new BusinessException("请假单已审批: id=" + id);
            }

            // 更新审批状态（带 WHERE status=0 条件防并发）
            com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<com.kg.infrastructure.entity.LeaveRequestEntity> w =
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
            w.eq(com.kg.infrastructure.entity.LeaveRequestEntity::getId, id);
            w.eq(com.kg.infrastructure.entity.LeaveRequestEntity::getStatus, STATUS_PENDING);
            w.set(com.kg.infrastructure.entity.LeaveRequestEntity::getStatus, req.getStatus());
            w.set(com.kg.infrastructure.entity.LeaveRequestEntity::getApproveRemark, req.getApproveRemark());
            w.set(com.kg.infrastructure.entity.LeaveRequestEntity::getApproveTime, now);
            w.set(com.kg.infrastructure.entity.LeaveRequestEntity::getUpdateBy, user.getId());
            int rows = leaveMapper.update(null, w);
            if (rows == 0) {
                throw new BusinessException("请假单已审批，操作失败: id=" + id);
            }

            // 老师审批后：失效所有相关通知消息
            if (isTeacher) {
                LambdaUpdateWrapper<NotificationMessageEntity> nw = new LambdaUpdateWrapper<>();
                nw.eq(NotificationMessageEntity::getRelatedId, id);
                nw.eq(NotificationMessageEntity::getStatus, "0");
                nw.set(NotificationMessageEntity::getStatus, "1");
                notificationMapper.update(null, nw);
            }

            String action = STATUS_APPROVED.equals(req.getStatus()) ? "通过" : "拒绝";
            log.info("请假审批{}: id={}, approver={}", action, id, user.getId());
        }
    }

    // ======================== 请假日计算 ========================

    /** 计算请假天数。一天3个时段（AM/PM/EV），按时段比例换算。 */
    public static BigDecimal calculateLeaveDays(LocalDate startDate, String startPeriod,
                                                 LocalDate endDate, String endPeriod) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("请假日期不能为空");
        }
        int startSegment = periodToInt(startPeriod);
        int endSegment = periodToInt(endPeriod);
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        long segmentCount = days * 3 + (endSegment - startSegment) + 1;
        if (segmentCount <= 0) {
            throw new IllegalArgumentException("请假时间范围非法");
        }
        return BigDecimal.valueOf(segmentCount)
                .divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    }

    private static int periodToInt(String period) {
        switch (period) {
            case "AM": return 1;
            case "PM": return 2;
            case "EV": return 3;
            default: throw new IllegalArgumentException("无效时段: " + period);
        }
    }

    // ======================== 工具方法 ========================

    private SysUser requireLogin() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user;
    }

    /** 从联表查询结果 Map 转换为 LeaveVO（userName 来自 LEFT JOIN sys_user） */
    private List<LeaveVO> rowsToVO(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) return Collections.emptyList();
        return rows.stream().map(row -> {
            LeaveVO vo = new LeaveVO();
            vo.setId(toLong(row.get("id")));
            vo.setUserId(toLong(row.get("user_id")));
            vo.setLeaveType((String) row.get("leave_type"));
            vo.setStartDate(fmtDate(row.get("start_date")));
            vo.setStartPeriod((String) row.get("start_period"));
            vo.setEndDate(fmtDate(row.get("end_date")));
            vo.setEndPeriod((String) row.get("end_period"));
            Object ld = row.get("leave_days");
            vo.setLeaveDays(ld instanceof BigDecimal ? (BigDecimal) ld : null);
            vo.setReason((String) row.get("reason"));
            vo.setApproverId(toLong(row.get("approver_id")));
            vo.setApproveRole((String) row.get("approve_role"));
            vo.setStatus((String) row.get("status"));
            vo.setApproveRemark((String) row.get("approve_remark"));
            vo.setApproveTime(fmtDateTime(row.get("approve_time")));
            vo.setCreateTime(fmtDateTime(row.get("create_time")));
            vo.setUserName((String) row.get("userName"));
            return vo;
        }).collect(Collectors.toList());
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof BigDecimal) return ((BigDecimal) val).longValue();
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private String fmtDate(Object val) {
        if (val == null) return null;
        if (val instanceof java.sql.Date) return ((java.sql.Date) val).toLocalDate().format(DATE_FMT);
        return val.toString();
    }

    private String fmtDateTime(Object val) {
        if (val == null) return null;
        if (val instanceof java.sql.Timestamp) return ((java.sql.Timestamp) val).toLocalDateTime().format(DT_FMT);
        if (val instanceof LocalDateTime) return ((LocalDateTime) val).format(DT_FMT);
        return val.toString();
    }

    private PageVO<LeaveVO> buildPageResult(long total, List<LeaveVO> list) {
        return PageVO.of(total, list);
    }
}
