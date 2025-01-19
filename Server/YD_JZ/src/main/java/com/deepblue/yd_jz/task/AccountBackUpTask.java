package com.deepblue.yd_jz.task;

import com.deepblue.yd_jz.dao.mybatis.AccountBackupDao;
import com.deepblue.yd_jz.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling
public class AccountBackUpTask {

    @Autowired
    AccountBackupDao accountBackupDao;

    @Autowired
    ExcelService excelService;

    @Scheduled(cron = "${cron.accountBackupTime}")
    public void doBackupAccount() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        accountBackupDao.backupAccount(today);
        //生成月度报表
        excelService.makeMonthExcel(today);

    }
}
