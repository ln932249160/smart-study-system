package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.LeaveRequest;
import com.kg.domain.repository.LeaveRequestRepository;
import com.kg.infrastructure.converter.LeaveRequestConverter;
import com.kg.infrastructure.entity.LeaveRequestEntity;
import com.kg.infrastructure.mapper.LeaveRequestMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** 请假申请仓储实现 */
@Repository
public class LeaveRequestRepositoryImpl implements LeaveRequestRepository {

    private final LeaveRequestMapper mapper;

    public LeaveRequestRepositoryImpl(LeaveRequestMapper mapper) { this.mapper = mapper; }

    @Override
    public void save(LeaveRequest leave) {
        LeaveRequestEntity e = LeaveRequestConverter.toEntity(leave);
        mapper.insert(e);
        leave.setId(e.getId());
    }

    @Override
    public Optional<LeaveRequest> findById(Long id) {
        return Optional.ofNullable(LeaveRequestConverter.toDomain(mapper.selectById(id)));
    }

    @Override
    public List<LeaveRequest> pageByUserId(Long userId, String leaveType,
                                            String startDateBegin, String startDateEnd,
                                            int offset, int limit) {
        LambdaQueryWrapper<LeaveRequestEntity> q = buildMyQuery(userId, leaveType, startDateBegin, startDateEnd);
        q.orderByDesc(LeaveRequestEntity::getCreateTime);
        q.last("LIMIT " + offset + "," + limit);
        List<LeaveRequestEntity> entities = mapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(LeaveRequestConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByUserId(Long userId, String leaveType,
                              String startDateBegin, String startDateEnd) {
        return mapper.selectCount(buildMyQuery(userId, leaveType, startDateBegin, startDateEnd));
    }

    private LambdaQueryWrapper<LeaveRequestEntity> buildMyQuery(Long userId, String leaveType,
                                                                  String startDateBegin, String startDateEnd) {
        LambdaQueryWrapper<LeaveRequestEntity> q = new LambdaQueryWrapper<>();
        q.eq(LeaveRequestEntity::getUserId, userId);
        if (leaveType != null && !leaveType.isEmpty()) q.eq(LeaveRequestEntity::getLeaveType, leaveType);
        if (startDateBegin != null && !startDateBegin.isEmpty())
            q.ge(LeaveRequestEntity::getStartDate, startDateBegin);
        if (startDateEnd != null && !startDateEnd.isEmpty())
            q.le(LeaveRequestEntity::getStartDate, startDateEnd);
        return q;
    }

    @Override
    public List<LeaveRequest> pageByApproveRole(String approveRole, String status, int offset, int limit) {
        LambdaQueryWrapper<LeaveRequestEntity> q = new LambdaQueryWrapper<>();
        q.eq(LeaveRequestEntity::getApproveRole, approveRole);
        if (status != null && !status.isEmpty()) q.eq(LeaveRequestEntity::getStatus, status);
        q.orderByDesc(LeaveRequestEntity::getCreateTime);
        q.last("LIMIT " + offset + "," + limit);
        List<LeaveRequestEntity> entities = mapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(LeaveRequestConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByApproveRole(String approveRole, String status) {
        LambdaQueryWrapper<LeaveRequestEntity> q = new LambdaQueryWrapper<>();
        q.eq(LeaveRequestEntity::getApproveRole, approveRole);
        if (status != null && !status.isEmpty()) q.eq(LeaveRequestEntity::getStatus, status);
        return mapper.selectCount(q);
    }

    @Override
    public List<LeaveRequest> pageByApproverId(Long approverId, String status, int offset, int limit) {
        LambdaQueryWrapper<LeaveRequestEntity> q = new LambdaQueryWrapper<>();
        q.eq(LeaveRequestEntity::getApproverId, approverId);
        if (status != null && !status.isEmpty()) q.eq(LeaveRequestEntity::getStatus, status);
        q.orderByDesc(LeaveRequestEntity::getCreateTime);
        q.last("LIMIT " + offset + "," + limit);
        List<LeaveRequestEntity> entities = mapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(LeaveRequestConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByApproverId(Long approverId, String status) {
        LambdaQueryWrapper<LeaveRequestEntity> q = new LambdaQueryWrapper<>();
        q.eq(LeaveRequestEntity::getApproverId, approverId);
        if (status != null && !status.isEmpty()) q.eq(LeaveRequestEntity::getStatus, status);
        return mapper.selectCount(q);
    }

    @Override
    public void update(LeaveRequest leave) {
        LambdaUpdateWrapper<LeaveRequestEntity> w = new LambdaUpdateWrapper<>();
        w.eq(LeaveRequestEntity::getId, leave.getId());
        if (leave.getStatus() != null) w.set(LeaveRequestEntity::getStatus, leave.getStatus());
        if (leave.getApproveRemark() != null) w.set(LeaveRequestEntity::getApproveRemark, leave.getApproveRemark());
        if (leave.getApproveTime() != null) w.set(LeaveRequestEntity::getApproveTime, leave.getApproveTime());
        if (leave.getUpdateBy() != null) w.set(LeaveRequestEntity::getUpdateBy, leave.getUpdateBy());
        mapper.update(null, w);
    }
}
