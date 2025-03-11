package com.deepblue.yd_jz.dao.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBackupDao {
    @Insert("insert into account_backup(b_date,account_id,a_name,money,exempt_money,card,create_time,note,sortno)  " +
            " select #{bDate},id,a_name,money,exempt_money,card,create_time,note,sortno from account t where t.disable=0 ")
    void backupAccount(@Param("bDate") String bDate);

    @Insert("insert into account_backup(b_date,account_id,a_name,money )  " +
            " select #{bDate},888888,'总资产',round(sum(money-exempt_money),2) from account t where t.disable=0 ")
    void backupAccountAll(@Param("bDate") String bDate);

    /**
     * 获取净资产
     * @param month
     * @return
     */
    @Select("select sum(money) from account_backup t where  substr(t.b_date ,1,7)=#{month} and account_id=888888 ")
    String getNetAsset(@Param("month") String month);


}
