package com.deepblue.yd_jz.service;

import com.deepblue.yd_jz.dao.jpa.FlowCycleTemplateRepository;
import com.deepblue.yd_jz.dao.jpa.FlowTemplateRepository;
import com.deepblue.yd_jz.dto.FlowCycleTemplateRequestDto;
import com.deepblue.yd_jz.dto.FlowCycleTemplateResponseDto;
import com.deepblue.yd_jz.dto.FlowTemplateRequestDto;
import com.deepblue.yd_jz.dto.FlowTemplateResponseDto;
import com.deepblue.yd_jz.entity.FlowCycleTemplate;
import com.deepblue.yd_jz.entity.FlowTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FlowCycleTemplateService {
    @Autowired
    private FlowCycleTemplateRepository flowCycleTemplateRepository;

    @Autowired
    private TypeService typeService;

    @Transactional(rollbackFor = Exception.class)
    public List<FlowCycleTemplateResponseDto> getAllTemplates() {
        List<FlowCycleTemplate> templates= flowCycleTemplateRepository.findAll();
        ArrayList<FlowCycleTemplateResponseDto> flowCycleTemplateResponseList= new ArrayList<>();
        for (FlowCycleTemplate template: templates) {
            FlowCycleTemplateResponseDto flowCycleTemplateResponseDto = new FlowCycleTemplateResponseDto();
            flowCycleTemplateResponseDto = flowCycleTemplateResponseDto.convertToDto(template);
            if (template.getType()!=null&&template.getType().getParent()!=-1){
                String parentName =  typeService.queryTypeParent(template.getTypeId()).getTName();
                flowCycleTemplateResponseDto.getType().setTName(parentName+"/"+flowCycleTemplateResponseDto.getType().getTName());
            }
            flowCycleTemplateResponseList.add(flowCycleTemplateResponseDto);
        }
        return flowCycleTemplateResponseList;
    }



    @Transactional(rollbackFor = Exception.class)
    public FlowCycleTemplateResponseDto getTemplateById(Integer id) {
        FlowCycleTemplate template = flowCycleTemplateRepository.findById(id).get();
        FlowCycleTemplateResponseDto flowCycleTemplateResponseDto = new FlowCycleTemplateResponseDto();
        flowCycleTemplateResponseDto = flowCycleTemplateResponseDto.convertToDto(template);
        if (template.getType()!=null&&template.getType().getParent()!=-1){
            String parentName =  typeService.queryTypeParent(template.getTypeId()).getTName();
            flowCycleTemplateResponseDto.getType().setTName(parentName+"/"+flowCycleTemplateResponseDto.getType().getTName());
        }
        return flowCycleTemplateResponseDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addTemplate(FlowCycleTemplateRequestDto flowCycleTemplateRequestDto) {
        flowCycleTemplateRequestDto.setId(null);
        FlowCycleTemplate flowCycleTemplate = flowCycleTemplateRequestDto.convertToEntity();
        flowCycleTemplateRepository.save(flowCycleTemplate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(FlowCycleTemplateRequestDto flowCycleTemplateRequestDto) {
        FlowCycleTemplate flowCycleTemplate = flowCycleTemplateRequestDto.convertToEntity();
        if (flowCycleTemplate.getId() == null) {
            throw new RuntimeException("id is null");
        }else {
            FlowCycleTemplate existingFlowCycleTemplate = flowCycleTemplateRepository.findById(flowCycleTemplate.getId()).get();
            if (existingFlowCycleTemplate == null) {
                throw new RuntimeException("FlowCycleTemplate not found");
            }else {
                flowCycleTemplateRepository.save(flowCycleTemplate);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Integer id) {
        if (!flowCycleTemplateRepository.existsById(id)) {
            throw new RuntimeException("FlowTemplate not found");
        }
        flowCycleTemplateRepository.deleteById(id);
    }
}
