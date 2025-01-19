package com.deepblue.yd_jz.controller;

import com.deepblue.yd_jz.dto.*;
import com.deepblue.yd_jz.service.FlowCycleTemplateService;
import com.deepblue.yd_jz.service.FlowTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(value = "FlowCycleController", tags = {"周期记账"})
@RequestMapping("/cycle")
public class FlowCycleController {
    @Autowired
    private FlowCycleTemplateService flowCycleTemplateService;

    @ApiOperation(value = "添加周期", notes = "添加周期")
    @PostMapping("/addTemplate")
    public BaseDto addTemplate(@RequestBody FlowCycleTemplateRequestDto flowCycleTemplateRequestDto) {
        log.info("addTemplate");
        flowCycleTemplateService.addTemplate(flowCycleTemplateRequestDto);
        log.info("flowCycleTemplateRequestDto:{}", flowCycleTemplateRequestDto);
        return BaseDto.setSuccessBean();
    }

    @ApiOperation(value = "更新模板", notes = "更新模板")
    @PutMapping("/updateTemplate")
    public BaseDto updateTemplate(@RequestBody FlowCycleTemplateRequestDto flowCycleTemplateRequestDto) {
        log.info("updateTemplate");
        flowCycleTemplateService.updateTemplate(flowCycleTemplateRequestDto);
        log.info("flowCycleTemplateRequestDto:{}", flowCycleTemplateRequestDto);
        return BaseDto.setSuccessBean();
    }

    @ApiOperation(value = "获取全部模板", notes = "获取全部模板")
    @GetMapping("/getAllTemplates")
    public BaseDto<List<FlowCycleTemplateResponseDto>> getAllTemplates() {
        log.info("getAllTemplates");
        List<FlowCycleTemplateResponseDto> flowCycleTemplateResponseList = flowCycleTemplateService.getAllTemplates();
        log.info("flowCycleTemplateResponseList:{}", flowCycleTemplateResponseList);
        BaseDto<List<FlowCycleTemplateResponseDto>> baseDto = new BaseDto<>();
        baseDto.setData(flowCycleTemplateResponseList);
        return baseDto;
    }


    @ApiOperation(value = "获取单个模板", notes = "获取单个模板")
    @GetMapping("/getTemplateById/{id}")
    public BaseDto<FlowCycleTemplateResponseDto> getTemplateById(@PathVariable("id") Integer id) {
        log.info("getTemplateById");
        FlowCycleTemplateResponseDto flowCycleTemplateResponseDto = flowCycleTemplateService.getTemplateById(id);
        BaseDto<FlowCycleTemplateResponseDto> baseDto = new BaseDto<>();
        baseDto.setData(flowCycleTemplateResponseDto);
        return baseDto;
    }

    @ApiOperation(value = "删除模板", notes = "删除模板")
    @DeleteMapping("/deleteTemplate/{id}")
    public BaseDto deleteTemplate(@PathVariable("id") Integer id) {
        log.info("deleteTemplate");
        flowCycleTemplateService.deleteTemplate(id);
        return BaseDto.setSuccessBean();
    }
}
