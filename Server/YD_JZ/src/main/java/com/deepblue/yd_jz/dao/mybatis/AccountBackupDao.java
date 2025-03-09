package com.deepblue.yd_jz.dao.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBackupDao {
    @Insert("insert into account_backup(b_date,a_name,money,exempt_money,card,create_time,note,sortno)  " +
            " select #{bDate},a_name,money,exempt_money,card,create_time,note,sortno from account t where t.disable=0 ")
    void backupAccount(@Param("bDate") String bDate);


}
