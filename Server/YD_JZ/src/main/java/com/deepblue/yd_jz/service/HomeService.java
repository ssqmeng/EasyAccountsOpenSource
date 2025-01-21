package com.deepblue.yd_jz.service;

import com.deepblue.yd_jz.dto.HomeDto;
import com.deepblue.yd_jz.entity.Account;
import com.deepblue.yd_jz.dao.jpa.AccountRepository;
import com.deepblue.yd_jz.dao.mybatis.FlowDao;
import com.deepblue.yd_jz.entity.FlowYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeService {
    @Autowired
    FlowDao flowDao;

    @Autowired
    AccountRepository accountDao;

    @Autowired
    ActionService actionDao;


    @Transactional(rollbackFor = Exception.class)
    public HomeDto getHomeBean() {
        List<Account> accounts = accountDao.queryAllAccount();
        BigDecimal totalAsset = new BigDecimal("0");
        BigDecimal exemptAsset = new BigDecimal("0");
        BigDecimal cardAsset = new BigDecimal("0");//信用卡余额
        for (Account account : accounts) {
            BigDecimal accountAsset = new BigDecimal(account.getMoney());
            String exemptStr = account.getExemptMoney();
            if (exemptStr.equals("")){
                exemptStr = "0";
            }
            BigDecimal exemptAccountAsset = new BigDecimal(exemptStr);
            totalAsset = totalAsset.add(accountAsset);
            exemptAsset = exemptAsset.add(exemptAccountAsset);
            if(!"0".equals(exemptStr))
            {
                cardAsset=cardAsset.add(exemptAccountAsset).subtract(accountAsset);//信用卡账单=额度金额-额度余额
            }
        }
        HomeDto homeDto = new HomeDto();
        homeDto.setTotalAsset(totalAsset.subtract(exemptAsset).add(cardAsset).toString());
        homeDto.setNetAsset(totalAsset.subtract(exemptAsset).toString());
        homeDto.setCardAsset(cardAsset.toString());
        NumberFormat nf = NumberFormat.getPercentInstance();
        List<HomeDto.HomeAccountBean> homeAccounts = new ArrayList<>();
        List<HomeDto.HomeAccountBean> homeAccounts1 = new ArrayList<>();
        List<HomeDto.HomeAccountBean> homeAccounts2 = new ArrayList<>();
        List<HomeDto.HomeAccountBean> homeAccounts3 = new ArrayList<>();
        List<HomeDto.HomeAccountBean> homeAccounts9 = new ArrayList<>();
        for (Account account : accounts) {
            HomeDto.HomeAccountBean hab = new HomeDto.HomeAccountBean();
            hab.setId(account.getId());
            hab.setAccountName(account.getAName());
            hab.setAccountAsset(account.getMoney());
            hab.setExemptAsset(account.getExemptMoney());
            hab.setNote(account.getNote());
            BigDecimal accountAsset = new BigDecimal(account.getMoney());
            BigDecimal percent = accountAsset.divide(totalAsset, 3, RoundingMode.HALF_DOWN);
            nf.setMaximumFractionDigits(2);
            String percentStr = nf.format(percent.doubleValue());
            hab.setPercent(percentStr.substring(0,percentStr.length()-1));
            homeAccounts.add(hab);
            if("1".equals(account.getCard())){homeAccounts1.add(hab);}
            else if("2".equals(account.getCard())){homeAccounts2.add(hab);}
            else if("3".equals(account.getCard())){homeAccounts3.add(hab);}
            else if("9".equals(account.getCard())){homeAccounts9.add(hab);}
        }

        homeDto.setAccounts(homeAccounts);
        homeDto.setAccounts1(homeAccounts1);
        homeDto.setAccounts2(homeAccounts2);
        homeDto.setAccounts3(homeAccounts3);
        homeDto.setAccounts9(homeAccounts9);


        int currentYear = Year.now().getValue();

        FlowYear flowYear = flowDao.getYearlySummary(currentYear);
        BigDecimal totalEarns = new BigDecimal(flowYear.getTotalEarns());
        BigDecimal totalCosts = new BigDecimal(flowYear.getTotalCosts());
        BigDecimal totalBalance = new BigDecimal(flowYear.getTotalBalance());
        String formattedTotalCosts = totalCosts.setScale(2, RoundingMode.HALF_UP).toString();
        String formattedTotalEarns = totalEarns.setScale(2, RoundingMode.HALF_UP).toString();
        String formattedTotalBalance = totalBalance.setScale(2, RoundingMode.HALF_UP).toString();

        homeDto.setYearOutCome(formattedTotalCosts);
        homeDto.setYearIncome(formattedTotalEarns);
        homeDto.setYearBalance(formattedTotalBalance);
        return homeDto;
    }
}
