package com.deepblue.yd_jz.dto;

import lombok.Data;

import java.util.List;

@Data
public class HomeDto {

    private String totalAsset;
    private String netAsset;
    private String cardAsset;//信用卡账单
    private String curIncome;
    private String curOutCome;
    //年度收入
    private String yearIncome;
    //年度支出
    private String yearOutCome;
    //年度结余
    private String yearBalance;
    private List<HomeAccountBean> accounts;

    private List<HomeAccountBean> accounts1;//储蓄卡

    private List<HomeAccountBean> accounts2;//信用卡

    private List<HomeAccountBean> accounts3;//投资账户

    private List<HomeAccountBean> accounts9;//其他账户


    @Data
    public static class HomeAccountBean {
        private int id;
        private String accountName;
        private String accountAsset;
        private String exemptAsset;
        private String percent;
        private String note;

    }
}
