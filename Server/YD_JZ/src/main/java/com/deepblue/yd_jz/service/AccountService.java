package com.deepblue.yd_jz.service;

import com.deepblue.yd_jz.dto.AccountResponseDto;
import com.deepblue.yd_jz.dto.FlowAddRequestDto;
import com.deepblue.yd_jz.entity.Account;
import com.deepblue.yd_jz.dto.AccountRequestDto;
import com.deepblue.yd_jz.dao.jpa.AccountRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FlowTemplateService flowTemplateService;

    @Autowired
    private FlowService flowService;

    @Transactional(rollbackFor = Exception.class)
    public void addAccount(AccountRequestDto postBean) {
        Account account = new Account();
        BeanUtils.copyProperties(postBean, account);
        account.setAName(postBean.getName());
        account.setExemptMoney(postBean.getExemptMoney());
        account.setDisable(false);
        account.setCreateTime(new Date());
        accountRepository.save(account);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateAccount(int id, AccountRequestDto postBean) throws Exception {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {

            //新增一条流水记录-start-20241224
            if("0".equals(postBean.getIsFlow())) {//需要更新流水
                BigDecimal oldMoney = new BigDecimal(account.getMoney());
                BigDecimal money = new BigDecimal(postBean.getMoney());
                BigDecimal flowMoney = money.subtract(oldMoney);
                if (flowMoney.compareTo(BigDecimal.ZERO) != 0) {
                    FlowAddRequestDto flowAddRequestDto = new FlowAddRequestDto();
                    flowAddRequestDto.setAccountId(id);
                    if (flowMoney.compareTo(BigDecimal.ZERO) > 0) {
                        flowAddRequestDto.setActionId(15);//15-收入，16支出
                        flowAddRequestDto.setMoney(flowMoney.toString());
                    } else {
                        flowAddRequestDto.setActionId(16);//15-收入，16支出
                        flowAddRequestDto.setMoney(BigDecimal.ZERO.subtract(flowMoney).toString());
                    }
                    flowAddRequestDto.setTypeId(128);//余额调整
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String fDate = sdf.format(new Date());
                    flowAddRequestDto.setfDate(fDate);//流水日期

                    flowService.doAddFlow(flowAddRequestDto);
                }
            }
            //新增一条流水记录-end-20241224
            BeanUtils.copyProperties(postBean, account);
            account.setAName(postBean.getName());
            accountRepository.save(account);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public AccountResponseDto getAccount(int id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            AccountResponseDto accountResponseDto = new AccountResponseDto();
            BeanUtils.copyProperties(account, accountResponseDto);
            accountResponseDto.setName(account.getAName());
            return accountResponseDto;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AccountResponseDto> getAllAccountNoLimit() {
        List<Account> accounts = accountRepository.findAll();
        List<AccountResponseDto> accountResponseDtos = new ArrayList<>();
        for (Account a : accounts) {
            AccountResponseDto toClient = new AccountResponseDto();
            toClient.convertToDto(a);
            accountResponseDtos.add(toClient);
        }
        return accountResponseDtos;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AccountResponseDto> getAllAccount() {
        List<Account> accounts = accountRepository.findByDisableFalse();  // 获取所有未禁用的账户
        List<AccountResponseDto> accountResponseDtos = new ArrayList<>();
        for (Account a : accounts) {
            AccountResponseDto toClient = new AccountResponseDto();
            BeanUtils.copyProperties(a, toClient);  // 将 Account 实体的属性复制到 AccountToClient
            toClient.setName(a.getAName());  // 特别设置名称，假设 AccountToClient 有不同的属性名
            accountResponseDtos.add(toClient);  // 将处理后的对象添加到列表中
        }
        return accountResponseDtos;  // 返回转换后的客户端可见的账户列表
    }


    @Transactional(rollbackFor = Exception.class)
    public void disableAccount(int id) {
        // 使用 JPA 的 findById 方法查找账户
        accountRepository.findById(id).ifPresent(account -> {
            account.setDisable(true);  // 设置账户为禁用状态
            accountRepository.save(account);  // 保存更改
            flowTemplateService.clearAccount(id);  // 清除与该账户相关的流程模板
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public Account getOriginAccountById(int id) {
        // 从数据库中获取并返回Account实体，如果找不到则返回null
        return accountRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)  // 使用只读事务，因为这是一个查询操作
    public List<Account> getAllOriginAccounts() {
        return accountRepository.findAll();  // 调用 JPA 的 findAll() 获取所有账户记录
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOriginAccount(Account account) {
        accountRepository.save(account);  // 保存更新后的Account实体
    }


    /*@Transactional(rollbackFor = Exception.class)
    public void disableAccount(int id) {

    }*/
}
