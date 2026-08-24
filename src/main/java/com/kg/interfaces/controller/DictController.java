package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.application.service.DictApplicationService;
import com.kg.infrastructure.entity.DictEntity;
import com.kg.infrastructure.mapper.DictMapper;
import com.kg.interfaces.dto.DictGroupSaveRequest;
import com.kg.interfaces.dto.DictGroupVO;
import com.kg.interfaces.dto.DictVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 字典管理控制器 */
@Tag(name = "系统管理", description = "字典管理")
@RestController
public class DictController {

    private final DictMapper dictMapper;
    private final DictApplicationService service;

    public DictController(DictMapper dictMapper, DictApplicationService service) {
        this.dictMapper = dictMapper;
        this.service = service;
    }

    // ======================== 下拉查询（不变） ========================

    @Operation(summary = "字典下拉")
    @GetMapping("/dict/{dictCode}")
    public Map<String, Object> getByCode(@PathVariable String dictCode) {
        LambdaQueryWrapper<DictEntity> q = new LambdaQueryWrapper<>();
        q.eq(DictEntity::getDictCode, dictCode);
        q.eq(DictEntity::getStatus, 1);
        q.orderByAsc(DictEntity::getSortNo);
        List<DictEntity> entities = dictMapper.selectList(q);
        List<DictVO> list = entities.stream()
                .map(e -> DictVO.of(e.getDictValue(),
                        e.getDictLabel() != null ? e.getDictLabel() : e.getDictName()))
                .collect(Collectors.toList());
        return AjaxResult.success("查询成功", list);
    }

    // ======================== 分组列表 ========================

    @Operation(summary = "字典分组列表")
    @GetMapping("/dict/group/page")
    public Map<String, Object> pageGroups(@RequestParam(required = false) String dictName,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return AjaxResult.success("查询成功", service.pageGroups(dictName, pageNum, pageSize));
    }

    // ======================== 详情（按 dict_code） ========================

    @Operation(summary = "字典组详情")
    @GetMapping("/dict/group/{dictCode}")
    public Map<String, Object> getGroup(@PathVariable String dictCode) {
        return AjaxResult.success("查询成功", service.getByCode(dictCode));
    }

    // ======================== 新增/编辑（整体保存） ========================

    @Operation(summary = "新增字典组")
    @PostMapping("/dict/group")
    public Map<String, Object> createGroup(@Valid @RequestBody DictGroupSaveRequest req) {
        service.saveGroup(req, true);
        return AjaxResult.success("新增成功");
    }

    @Operation(summary = "编辑字典组（整体替换）")
    @PutMapping("/dict/group/{dictCode}")
    public Map<String, Object> updateGroup(@PathVariable String dictCode,
                                           @Valid @RequestBody DictGroupSaveRequest req) {
        req.setDictCode(dictCode);
        service.saveGroup(req, false);
        return AjaxResult.success("编辑成功");
    }

    // ======================== 删除整组 ========================

    @Operation(summary = "删除字典组")
    @DeleteMapping("/dict/group/{dictCode}")
    public Map<String, Object> deleteGroup(@PathVariable String dictCode) {
        service.deleteByCode(dictCode);
        return AjaxResult.success("删除成功");
    }
}
