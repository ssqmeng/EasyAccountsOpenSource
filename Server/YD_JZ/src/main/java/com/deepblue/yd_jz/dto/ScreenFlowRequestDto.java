package com.deepblue.yd_jz.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ScreenFlowRequestDto {
    private int chooseHandle;
    private int accountId;
    private String startDate;
    private String endDate;
    private String minMoney;
    private String maxMoney;
    private boolean isSingleMonth;
    private boolean collect;
    private String note;
    private int order;
    private int pageNum;
    private int pageSize;
    private ArrayList<Integer> actions;
    private ArrayList<Integer> types;

    public ArrayList<Integer> getActions() {
        return actions==null?new ArrayList<>():actions;
    }



    public ArrayList<Integer> getTypes() {
        return types==null?new ArrayList<>():types;
    }

    public String getStartDate() {
        return startDate==null?"":startDate;
    }


    public String getEndDate() {
        return endDate==null?"":endDate;
    }

    public String getMinMoney() {
        return minMoney==null?"":minMoney;
    }


    public String getMaxMoney() {
        return maxMoney==null?"":maxMoney;
    }

    public boolean useTypeScreen(){
        return getTypes().size()!=0;
    }

    public boolean useActionScreen(){
        return getActions().size()!=0;
    }
}
