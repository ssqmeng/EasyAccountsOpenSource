package com.deepblue.yd_jz.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "flow_cycle_template")
public class FlowCycleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cycle_type", nullable = true)
    private Integer cycleType;

    @Column(name = "start_date", nullable = true)
    private String startDate;

    @Column(name = "money", nullable = true)
    private String money;

    @Column(name = "note", nullable = true)
    private String note;

    @Column(name = "type_id", nullable = true)
    private Integer typeId;

    @Column(name = "action_id", nullable = true)
    private Integer actionId;

    @Column(name = "account_id", nullable = true)
    private Integer accountId;

    @Column(name = "account_to_id", nullable = true)
    private Integer accountToId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_to_id", insertable = false, updatable = false)
    private Account accountTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "action_id", insertable = false, updatable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private Type type;

}
