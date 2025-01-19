package com.deepblue.yd_jz.dao.jpa;

import com.deepblue.yd_jz.entity.FlowCycleTemplate;
import com.deepblue.yd_jz.entity.FlowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowCycleTemplateRepository extends JpaRepository<FlowCycleTemplate, Integer> {


}
