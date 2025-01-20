package com.deepblue.yd_jz.task;

import com.deepblue.yd_jz.dao.jpa.FlowCycleTemplateRepository;
import com.deepblue.yd_jz.dao.mybatis.FlowDao;
import com.deepblue.yd_jz.entity.FlowCycleTemplate;
import com.deepblue.yd_jz.service.FlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling
public class FlowCycleTask {

    @Autowired
    FlowDao flowDao;

    @Autowired
    FlowCycleTemplateRepository flowCycleTemplateRepository;

    @Autowired
    FlowService flowService;


    @Scheduled(cron = "${cron.flowCycleTime}")
    @Transactional(rollbackFor = Exception.class)
    public void doAddCycleFlow()  throws Exception{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        //String today = "2025-02-28";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);  // 增加一天
        //String nextDay = "2025-03-01";
        String nextDay = sdf.format(calendar.getTime());
        List<FlowCycleTemplate> templates= flowCycleTemplateRepository.findAll();
        for (FlowCycleTemplate template: templates) {
            boolean isAdd = false;
            if(template.getCycleType()==1)
            {
                //每天
                isAdd = true;
            }
            else if(template.getCycleType()==2)
            {
                if(template.getStartDate().substring(8,10).equals(today.substring(8,10))) {
                    isAdd = true;
                    //flowDao.addCycleFlow(template, today);
                }
                else if(("29".equals(template.getStartDate().substring(8,10))
                        ||"30".equals(template.getStartDate().substring(8,10))
                        ||"31".equals(template.getStartDate().substring(8,10)))
                        && "02" .equals(today.substring(5,7))) {
                    //每月(需考虑28，29，30，31）
                    //2月份
                    if("28" .equals(today.substring(8,10)) && !"29" .equals(nextDay.substring(8,10)))
                    {
                        isAdd = true;
                    }else if("29" .equals(today.substring(8,10)))
                    {
                        isAdd = true;
                    }
                }else if(("04" .equals(today.substring(5,7))||"06" .equals(today.substring(5,7))
                            ||"09" .equals(today.substring(5,7))
                            ||"11" .equals(today.substring(5,7)))&&
                        ("30" .equals(today.substring(5,7)))) {
                    //4/6/9/11 月 只有30天
                    if ("31".equals(template.getStartDate().substring(8, 10))) {
                        isAdd = true;
                    }
                }
            }else if(template.getCycleType()==3)
            {
                //每年
                if(template.getStartDate().substring(5,10).equals(today.substring(5,10))) {
                    //flowDao.addCycleFlow(template, today);
                    isAdd = true;
                }
            }
            if(isAdd)
            {
                if(template.getAccountToId()==null)
                {
                    template.setAccountToId(0);
                }
                flowService.setNewFlow(template);
                flowDao.addCycleFlow(template,today);
            }
        }
    }
//    public static void main(String args[])
//    {
//        String today = "2025-02-28";
//        System.out.println(today.substring(5,10));
//        System.out.println(today.substring(5,7));
//        System.out.println(today.substring(8,10));
//    }
}
