package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.DictItem;
import com.kg.domain.repository.DictRepository;
import com.kg.exception.BusinessException;
import com.kg.interfaces.dto.DictGroupSaveRequest;
import com.kg.interfaces.dto.DictGroupVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 字典管理应用服务 — 按 dict_code 分组管理 */
@Service
public class DictApplicationService {
    private static final Logger log = LoggerFactory.getLogger(DictApplicationService.class);
    private final DictRepository repo;
    public DictApplicationService(DictRepository repo) { this.repo = repo; }

    /** 分组列表：按 dict_code 去重，支持 dict_name 模糊筛选 */
    public Map<String, Object> pageGroups(String dictNameFilter, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = repo.countDistinctCodes(dictNameFilter);
        List<DictItem> codes = repo.listDistinctCodes(dictNameFilter, offset, pageSize);

        List<DictGroupVO> list = codes.stream().map(c -> {
            DictGroupVO vo = new DictGroupVO();
            vo.setDictCode(c.getDictCode());
            vo.setDictName(c.getDictName());
            List<DictItem> items = repo.findByCode(c.getDictCode());
            vo.setItems(items.stream().map(i -> {
                DictGroupVO.DictGroupItemVO iv = new DictGroupVO.DictGroupItemVO();
                iv.setDictValue(i.getDictValue());
                iv.setDictLabel(i.getDictLabel());
                iv.setSortNo(i.getSortNo());
                iv.setStatus(i.getStatus());
                iv.setDisplayColor(i.getDisplayColor());
                return iv;
            }).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", total);
        r.put("list", list);
        return r;
    }

    /** 根据 dict_code 查询详情（整组 items） */
    public DictGroupVO getByCode(String dictCode) {
        List<DictItem> items = repo.findByCode(dictCode);
        if (items.isEmpty()) throw new BusinessException("字典编码不存在: " + dictCode);
        DictGroupVO vo = new DictGroupVO();
        vo.setDictCode(dictCode);
        vo.setDictName(items.get(0).getDictName());
        vo.setItems(items.stream().map(i -> {
            DictGroupVO.DictGroupItemVO iv = new DictGroupVO.DictGroupItemVO();
            iv.setDictValue(i.getDictValue());
            iv.setDictLabel(i.getDictLabel());
            iv.setSortNo(i.getSortNo());
            iv.setStatus(i.getStatus());
            iv.setDisplayColor(i.getDisplayColor());
            return iv;
        }).collect(Collectors.toList()));
        return vo;
    }

    /** 整体保存：先删除旧组，再批量插入 */
    @Transactional(rollbackFor = Exception.class)
    public void saveGroup(DictGroupSaveRequest req, boolean isCreate) {
        Long uid = getUserId();
        String code = req.getDictCode();

        // task_type 的 dictValue 与代码枚举绑定，禁止增删改
        if ("task_type".equals(code)) {
            throw new BusinessException("task_type 字典值由系统维护，不允许通过接口修改");
        }

        if (isCreate) {
            // 新增：检查 dict_code 是否已存在
            if (!repo.findByCode(code).isEmpty()) {
                throw new BusinessException("字典编码已存在: " + code);
            }
        } else {
            // 编辑：先删除旧组
            repo.deleteByCode(code);
        }

        if (req.getItems() == null || req.getItems().isEmpty()) return;

        List<DictItem> batch = new ArrayList<>();
        for (DictGroupSaveRequest.Item item : req.getItems()) {
            if (item.getDictValue() == null || item.getDictValue().isEmpty()) continue;
            DictItem d = new DictItem();
            d.setDictCode(code);
            d.setDictName(req.getDictName());
            d.setDictValue(item.getDictValue());
            d.setDictLabel(item.getDictLabel());
            d.setDisplayColor(item.getDisplayColor());
            d.setSortNo(item.getSortNo() != null ? item.getSortNo() : 0);
            d.setStatus(item.getStatus() != null ? item.getStatus() : 1);
            d.setCreateBy(uid);
            d.setCreateTime(LocalDateTime.now());
            batch.add(d);
        }
        for (DictItem d : batch) { repo.save(d); }
        log.info("{}字典组: code={}, items={}", isCreate ? "新增" : "编辑", code, batch.size());
    }

    /** 按 dict_code 删除整组 */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCode(String dictCode) {
        if ("task_type".equals(dictCode)) {
            throw new BusinessException("task_type 字典值由系统维护，不允许删除");
        }
        repo.deleteByCode(dictCode);
        log.info("删除字典组: code={}", dictCode);
    }

    private Long getUserId() {
        if (UserContext.getUser() == null) throw new BusinessException(401, "未登录");
        return UserContext.getUser().getId();
    }
}
