package com.deepblue.yd_jz.dto;

import com.deepblue.yd_jz.entity.FlowCycleTemplate;
import com.deepblue.yd_jz.entity.FlowTemplate;
import lombok.Data;

@Data
public class FlowCycleTemplateRequestDto {
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


    public FlowCycleTemplate convertToEntity() {
        FlowCycleTemplate flowCycleTemplate = new FlowCycleTemplate();
        flowCycleTemplate.setId(this.id);
        flowCycleTemplate.setName(this.name);
        flowCycleTemplate.setCycleType(this.cycleType);
        flowCycleTemplate.setStartDate(this.startDate);
        flowCycleTemplate.setMoney(this.money);
        flowCycleTemplate.setNote(this.note);
        flowCycleTemplate.setTypeId(this.typeId);
        flowCycleTemplate.setActionId(this.actionId);
        flowCycleTemplate.setAccountId(this.accountId);
        flowCycleTemplate.setAccountToId(this.accountToId==null?0:this.accountToId);
        return flowCycleTemplate;
    }
}
