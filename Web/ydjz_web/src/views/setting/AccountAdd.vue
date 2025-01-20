<template>
  <div>
    <van-nav-bar v-if="this.$route.query.accountId != null" title="添加账号" left-text="返回" right-text="更新" left-arrow
      @click-left="onClickLeft" @click-right="onClickRight" />

    <van-nav-bar v-else title="添加账号" left-text="返回" right-text="保存" left-arrow @click-left="onClickLeft"
      @click-right="onClickRight" />

    <van-cell-group>
      <van-field v-model="aName" label="账号名称" required placeholder="请输入账号名称" />
      <van-field v-model="money" type="number" label="账户余额" required placeholder="请输入账户余额" />
      <van-field name="radio" v-if="this.$route.query.accountId != null" label="是否生成流水">
        <template #input>
          <van-radio-group v-model="isFlow" direction="vertical">
            <van-radio name="0">是</van-radio>
            <van-radio style="margin-top: 10px" name="1">否</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field v-model="exempt" type="number" label="信用额度" placeholder="请输入不计入总金额的金额(如信用卡额度)" />
      <!--
      <van-field v-model="card" label="银行卡号" placeholder="请输入银行卡号" />
      -->
      <van-field label="账户类型" required>
        <template #input>
          <van-radio-group v-model="card" direction="vertical" required>
            <van-radio name="1">储蓄卡</van-radio>
            <van-radio name="2" style="margin-top: 10px" >信用卡</van-radio>
            <van-radio name="3">投资账户</van-radio>
            <van-radio name="9">其他</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field v-model="sortno" label="排序号" required placeholder="排序号" />
      <van-field v-model="note" rows="1" autosize label="账户备注" type="textarea" maxlength="50" placeholder="请输入备注" />
    </van-cell-group>




    <van-divider v-show="this.$route.query.accountId != null"
      :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }"> 最近交易
    </van-divider>

    <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
      <van-button v-show="flows.length != 0" @click=toScreen() type="default" round size="small"
        style="margin-right: 10px;"> 更多交易
      </van-button>
    </div>

    <van-cell-group :border="false">
      <div @click="toUpdateFlow(flow.id)" v-for="flow in flows" :key="flow.id">
        <van-swipe-cell>
          <template #left>
            <van-button size="small" square color="#8c8c8c" type="primary" class="delete-button"
              @click="doShowNote(flow)">
              {{ doGetNotString(flow) }}
            </van-button>
          </template>

          <div style="
            margin-left: 15px;
            margin-top: 5px;
            font-size: 11px;
            color: #4e4e4e;">
            {{ flow.fdate }}
          </div>
          <van-cell size="small" :title="flow.tname" :value="'￥' + flow.money" :label="flow.aname">
            <template #label>
              <div>
                <label style="color: #676767; font-size: 13px; display: block;">{{ flow.aname }}</label>
                <label v-if="flow.note && flow.note.length > 0"
                  style="color: #cea643; font-size: 13px; display: block; margin-top: 4px;">{{
                    "备注：" + doGetNotString(flow)
                  }}</label>
              </div>
            </template>
            <template #default>
              <div style="color: #000; font-size: 16px">￥{{ flow.money }}</div>
              <van-tag :type="flow.tagStyle">{{ flow.hname }}</van-tag>
              <van-tag v-show="flow.exempt" style="margin-left: 10px" color="gray" plain type="action.style">不计入总金额
              </van-tag>
            </template>
          </van-cell>
          <div style="height: 1px"></div>
          <template #right>
            <van-button v-if="flow.collect" square type="warning" class="delete-button" @click="doCollectFlow(flow)">
              取消<br>收藏
            </van-button>
            <van-button v-else type="primary" color="#1989fa" class="delete-button"
              @click="doCollectFlow(flow)">收藏<br>账单
            </van-button>
            <van-button square text="删除" type="danger" class="delete-button" @click="doConfirmDeleteFlow(flow)" />
          </template>
        </van-swipe-cell>
      </div>
    </van-cell-group>
    <van-empty v-show="flows.length == 0" description="当期无账单" />

  </div>
</template>

<script>
// import { Toast } from 'vant';
import { Dialog, Toast } from "vant";
import { Notify } from "vant";
import request from "../../utils/request";

export default {
  data() {
    return {
      aName: "",
      isFlow: "0",
      card: "",
      money: "",
      handle: 3,//操作 0流入，1流出，2内部转账，3全部
      accountId: this.$route.query.accountId != null ? this.$route.query.accountId : -1,//选择的账号
      accountName: "全部账号",
      startDate: "",//开始日期
      endDate: " ",//截止日期
      collect: false,//是否收藏
      //singleMonth: true,//是否查看当月
      flows: [],
      exempt: "",
      note: "",
      sortno: ""
    };
  },
  mounted() {
    //this.startDate = this.fomatTime(new Date())
    var data = new Date()
    this.startDate = this.fomatTime(new Date(data.getFullYear(), 0, 1))
    this.endDate = this.fomatTime(new Date(data.getFullYear(), data.getMonth(), data.getDate()))
    if (this.$route.query.accountId != null) {
      this.getAccountSingle();
      this.doGetCurrentFlow();
    }
  },

  methods: {
    getAccountSingle() {
      request({
        url: "/account/getAccount/" + this.$route.query.accountId,
        method: "get"
      }).then((respons) => {
        const account = respons.data.data;
        console.log(account);
        this.aName = account.name;
        this.card = account.card;
        this.exempt = account.exemptMoney;
        this.note = account.note;
        this.money = account.money;
        this.sortno = account.sortno;
      });
    },
    toUpdateFlow(flowid) {
      this.$router.push({ path: "/flow/add", query: { flowId: flowid } });
    },
    doShowNote(flow) {
      if (flow.note != null && flow.note.length > 4) {
        Dialog.alert({
          title: '备注',
          message: flow.note,
        }).then(() => {
          // on close
        });
      }
    },
    doConfirmDeleteFlow(flow) {
      console.log(this.flow)
      Dialog.confirm({
        title: '确定删除吗？',
        message:
          '确定删除 ￥' + flow.money + " 的  '" + flow.tname + "'  记录吗？",
      })
        .then(() => {
          this.flowId = flow.id
          this.doDeleteFlow()
        })
        .catch(() => {
          // on cancel
        });

    },
    toScreen() {
      this.$router.push({ path: "/screen", query: { acid: this.accountId } });
    },

    doDeleteFlow() {
      request({
        url: "/flow/deleteFlow/" + this.flowId,
        method: "delete"
      }).then(() => {
        this.doGetCurrentFlow();
      });
    },

    doCollectFlow(flow) {
      console.log("/flow/collectFlow/" + flow.id + "/" + (flow.collect ? 0 : 1))
      request({
        url: "/flow/collectFlow/" + flow.id + "/" + (flow.collect ? 0 : 1),
        method: "put"
      }).then(() => {
        this.doGetCurrentFlow();
      });
    },
    fomatTime(date) {
      var year = date.getFullYear(),
        month = date.getMonth() + 1,//月份是从0开始的
        day = date.getDate()

      return year + '-' +
        (month < 10 ? '0' + month : month) + '-' +
        (day < 10 ? '0' + day : day) + ''
    },
    doGetNotString(flow) {
      if (flow.note == null || flow.note == "") {
        return "无备注"
      } else if (flow.note.length > 4) {
        return flow.note.substring(0, 3) + ".."
      } else {
        return flow.note
      }
    },

    onClickLeft() {
      this.$router.go(-1);
    },

    doAdd() {

      const toast = Toast.loading({
        message: "保存中...",
        duration: 0,
        forbidClick: true,
        loadingType: "spinner"
      });
      request({
        url: "/account/addAccount",
        method: "post",
        data: {
          name: this.aName,
          money: this.money,
          card: this.card,
          exemptMoney: this.exempt,
          note: this.note,
          sortno: this.sortno
        }
      })
        .then((response) => {
          toast.clear();
          Notify({ type: "success", message: "保存成功" });
          this.$router.go(-1);
          console.log(response.data);
        })
        .catch((error) => {
          toast.clear();
          // Notify({ type: "danger", message: "保存失败" });
          console.log(error);
        });
    },

    doUpdate() {
      const toast = Toast.loading({
        message: "更新中...",
        duration: 0,
        forbidClick: true,
        loadingType: "spinner"
      });
      request({
        url: "/account/updateAccount/" + this.$route.query.accountId,
        method: "put",
        data: {
          name: this.aName,
          isFlow: this.isFlow,
          money: this.money,
          card: this.card,
          exemptMoney: this.exempt,
          note: this.note,
          sortno: this.sortno
        }
      })
        .then(() => {
          toast.clear();
          Notify({ type: "success", message: "更新成功" });
          this.$router.go(-1);
        })
        .catch((error) => {
          toast.clear();
          Notify({ type: "danger", message: "更新失败", duration: 600 });
          console.log(error);
        });
    },

    onClickRight() {
      /* */
      if (this.aName === "") {
        Toast.fail("账号名为空");
        return;
      }
      if (this.money === "") {
        Toast.fail("初始金额为空");
        return;
      }
      //if (parseInt(this.exempt)>parseInt(this.money)){
      //Toast.fail("不计入总金额钱数不得大于账户余额")
      //return
      //}
      if (this.$route.query.accountId != null) {
        this.doUpdate();
      } else {
        this.doAdd();
      }
    },
    doGetCurrentFlow() {
      request({
        url: "/screen/getFlowByScreen",
        method: "post",
        data: {
          chooseHandle: this.handle,
          accountId: this.accountId,
          startDate: this.startDate,
          endDate: this.endDate,
          singleMonth: this.singleMonth,
          collect: this.collect
          //types: this.chooseTypes,
          //actions: this.chooseActions,
          //note: this.note
        }
      }).then((response) => {
        console.log(response.data.data);
        const flow = response.data.data;
        this.flows = response.data.data.flows;
        this.totalIn = flow.totalIn;
        this.totalOut = flow.totalOut;
        this.totalEarn = flow.totalEarn;
        this.allTypesMoney = flow.typeList;
        this.flows.forEach((flow) => {
          if (flow.handle === 0) {
            flow.handleName = "流入";
            flow.baseColor = "#4ae75a";
            flow.tagStyle = "success";
          } else if (flow.handle === 1) {
            flow.handleName = "流出";
            flow.tagStyle = "danger";
            flow.baseColor = "#f54949";
          } else if (flow.handle === 2) {
            flow.handleName = "内部转账";
            flow.tagStyle = "primary";
            flow.baseColor = "#39bdfa";
            flow.aname = flow.aname + "->" + flow.toAName
          }
          if (this.handle === 3) {
            this.detail = this.chooseMonth + "总收入： ￥" + this.totalIn + "  总支出： ￥" + this.totalOut;
          }
          flow.dateSub = flow.fdate.substring(5, 10);
          flow.moneyNum = parseFloat(flow.money);
        });
      })
    },
  }
};
</script>

<style scoped>
.delete-button {
  height: 100%;
  white-space: pre-wrap;
}
</style>
