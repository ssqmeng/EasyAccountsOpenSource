package com.deepblue.yd_jz.dto;

import com.deepblue.yd_jz.entity.Action;
import com.deepblue.yd_jz.entity.FlowCycleTemplate;
import com.deepblue.yd_jz.entity.FlowTemplate;
import com.deepblue.yd_jz.entity.TemplateTag;
import lombok.Data;

@Data
public class FlowCycleTemplateResponseDto {

    private Integer id;
    private String name;
    private Integer cycleType;
    private String startDate;
    private String money;
    private String note;
    private Integer typeId;
    private Integer actionId;
    private Integer accountId;
    private Integer accountToId;

    private TypeListResponseDto type;
    private Action action;
    private AccountResponseDto account;
    private AccountResponseDto accountTo;

    public FlowCycleTemplateResponseDto convertToDto(FlowCycleTemplate flowCycleTemplate) {
        FlowCycleTemplateResponseDto flowTemplateResponseDto = this;
        flowTemplateResponseDto.setId(flowCycleTemplate.getId());
        flowTemplateResponseDto.setName(flowCycleTemplate.getName());
        flowTemplateResponseDto.setCycleType(flowCycleTemplate.getCycleType());
        flowTemplateResponseDto.setStartDate(flowCycleTemplate.getStartDate());
        flowTemplateResponseDto.setMoney(flowCycleTemplate.getMoney());
        flowTemplateResponseDto.setNote(flowCycleTemplate.getNote());
        flowTemplateResponseDto.setTypeId(flowCycleTemplate.getTypeId());
        flowTemplateResponseDto.setActionId(flowCycleTemplate.getActionId());
        flowTemplateResponseDto.setAccountId(flowCycleTemplate.getAccountId());
        flowTemplateResponseDto.setAccountToId(flowCycleTemplate.getAccountToId());
        TypeListResponseDto typeListResponseDto = new TypeListResponseDto();
        flowTemplateResponseDto.setType(typeListResponseDto.convertToDto(flowCycleTemplate.getType()));

        if (flowCycleTemplate.getAction() == null) {
            flowTemplateResponseDto.setAction(new Action());
        }else {
            flowTemplateResponseDto.setAction(flowCycleTemplate.getAction());
        }
        AccountResponseDto accountResponseDto = new AccountResponseDto();
        flowTemplateResponseDto.setAccount(accountResponseDto.convertToDto(flowCycleTemplate.getAccount()));
        AccountResponseDto accountToResponseDto = new AccountResponseDto();
        flowTemplateResponseDto.setAccountTo(accountToResponseDto.convertToDto(flowCycleTemplate.getAccountTo()));
        return flowTemplateResponseDto;
    }
}
