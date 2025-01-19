package com.deepblue.yd_jz.dao.mybatis;

import com.deepblue.yd_jz.entity.Flow;
import com.deepblue.yd_jz.entity.FlowType;
import com.deepblue.yd_jz.entity.FlowYear;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AccountBackupDao {
    @Insert("insert into account_backup(b_date,a_name,money,exempt_money,card,create_time,note,sortno)  " +
            " select #{bDate},a_name,money,exempt_money,card,create_time,note,sortno from account t where t.disable=0 ")
    void backupAccount(@Param("bDate") String bDate);


}
