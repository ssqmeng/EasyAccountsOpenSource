package com.deepblue.yd_jz.dao.jpa;
import com.deepblue.yd_jz.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query("select a from Account a where a.disable = 0 order by a.sortno")
    List<Account> findByDisableFalse();

    @Query("select a from Account a where a.disable = 0 and a.id <> 1 order by a.sortno")
    List<Account> queryAllAccount();
}
