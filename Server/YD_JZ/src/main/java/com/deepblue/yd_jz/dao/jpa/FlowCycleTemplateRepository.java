package com.deepblue.yd_jz.dao.jpa;

import com.deepblue.yd_jz.entity.FlowCycleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowCycleTemplateRepository extends JpaRepository<FlowCycleTemplate, Integer> {


}
