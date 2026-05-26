package com.kg.interfaces.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.infrastructure.entity.DictEntity;
import com.kg.infrastructure.mapper.DictMapper;
import com.kg.interfaces.dto.DictVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典查询控制器 —— 供前端获取下拉选项。
 */
@Tag(name = "字典", description = "字典数据查询")
@RestController
public class DictController {

    private final DictMapper dictMapper;

    public DictController(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    /**
     * 根据字典编码查询字典项列表，按 sort_no 排序。
     *
     * @param dictCode 字典编码：role / task_status / module_name
     */
    @Operation(summary = "查询字典项")
    @GetMapping("/dict/{dictCode}")
    public Map<String, Object> getByCode(@PathVariable String dictCode) {
        LambdaQueryWrapper<DictEntity> q = new LambdaQueryWrapper<>();
        q.eq(DictEntity::getDictCode, dictCode);
        q.eq(DictEntity::getStatus, 1);
        q.orderByAsc(DictEntity::getSortNo);
        List<DictEntity> entities = dictMapper.selectList(q);

        List<DictVO> list = entities.stream()
                .map(e -> DictVO.of(e.getDictValue(), e.getDictName()))
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", list);
        return result;
    }
}
