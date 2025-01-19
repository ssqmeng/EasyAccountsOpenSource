<template>
  <div style="width:100vw; height:100vh; background: #F7F8FA">
    <van-nav-bar v-if="this.$route.query.templateId == null" fixed placeholder title="添加周期" left-arrow 
      @click-left="leftClick"  />
    <van-nav-bar v-else fixed placeholder title="编辑周期" left-arrow @click-left="leftClick"
       />

    <van-cell-group :border="false" inset :style="{ marginTop: '20px' }">

      
     
    </van-cell-group>
    <van-cell-group :border="false" inset :style="{ marginTop: '20px' }">
      <van-field input-align="right" v-model="template.name" label="名称" required placeholder="请输入名称" />
      <van-field input-align="right" v-model="template.money" type="number" label="金额" placeholder="请输入金额" />
      <van-cell title="开始日期" :value="startDate" is-link @click="onCalanderClick"/>
      <van-cell title="选择收支" is-link @click="onActionClick">
        <template #default>
          <van-tag :type="chooseAction.style">{{ chooseAction.hname }}</van-tag>
          <van-tag v-show="chooseAction.exempt" style="margin-left: 10px" color="gray" plain type="action.style">{{
            chooseAction.value }}
          </van-tag>
        </template>
      </van-cell>

      <van-cell :title="chooseAction.handle == 2 ? '选择源账户' : '选择账户'" is-link @click="onAccountClick(1)"
        :value="chooseAccount.name" />

      <van-cell v-show="chooseAction.handle == 2" title="选择目标账户" is-link @click="onAccountClick(2)"
        :value="chooseAccountTo.name" />

      <van-cell title="账单分类" :value="chooseType.tname" is-link @click="onTypeClick" />

      <van-cell title="重复周期">
        <template>
          <van-radio-group v-model="cycleTypeStr" direction="horizontal">
            <van-radio name="1">每天</van-radio>
            <van-radio name="2" checked-color="#ee0a24">每月</van-radio>
            <van-radio name="3" >每年</van-radio>
          </van-radio-group>
        </template>
      </van-cell>
      <van-field input-align="right" v-model="template.note" label="备注" placeholder="请输入备注" />
    </van-cell-group>

    <div style="margin: 20px">
      <van-button type="primary" size="large" @click="onSubmit">{{ isEdit ? "修改" : "新建" }}</van-button>
      <van-button v-if="isEdit" type="danger" size="large" style="margin-top: 5px"
        @click="deleteTemplate">删除</van-button>
    </div>

    <van-action-sheet v-if="popupStyle <= 2" v-model="actionShow" :title="popupTitle">
      <van-cell-group v-if="popupStyle == 0">
        <van-cell v-for="action in allActions" :key="action.id" @click="onChooseAction(action)">
          <template #title>
            <span class="custom-title">{{ action.hname }}</span>
          </template>
          <template #label>
            <van-tag :type="action.style">{{ action.handleText }}</van-tag>
            <van-tag v-show="action.exempt" style="margin-left: 10px" color="gray" plain type="action.style">{{
              action.value }}
            </van-tag>
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group v-if="popupStyle == 1 || popupStyle == 2">
        <van-cell v-for="account in allAccounts" :key="account.id" :title="account.name" :value="account.money"
          :label="account.note" @click="onChooseAccount(account, popupStyle)" />
      </van-cell-group>
    </van-action-sheet>
    <van-popup v-model="typeCascaderShow" round position="bottom">
      <van-cascader v-model="cascaderValue" title="选择账单分类" :options="allTypes" active-color="#1989fa"
        @close="typeCascaderShow = false" :field-names="cascaderNames" @finish="onChooseCascader" />
    </van-popup>
    <van-calendar v-model="calanderShow" :show-confirm="false" color="#1989fa" :min-date="minDate"
          :max-date="maxDate " @confirm="onChooseCalendar"/>
  </div>
</template>



<script>
import request from "../../../utils/request";
import { Dialog, Toast } from "vant";

export default {
  name: "CycleTemplateAdd.vue",
  data() {
    return {

      activeNames: ['1'],
      tagManageShow: true,
      isEdit: this.$route.query.templateId != null,

      popupStyle: 0,
      popupTitle: "",

      //标签相关
      allTags: [],
      chooseTag: {},

      //收支相关
      actionShow: false,
      allActions: [],
      chooseAction: {},

      //账户相关
      allAccounts: [],
      chooseAccount: {},
      chooseAccountTo: {},

      calanderShow: false,
      minDate: new Date(2021, 0, 1),
      maxDate: new Date(),
      startDate: this.formatDate(new Date()),

      //分类相关
      cascaderValue: '',//已选则分类的id
      chooseType: {},
      showTname: "",
      allTypes: [],
      typeCascaderShow: false,
      cascaderNames: {
        text: 'tname',
        value: 'id',
        children: 'childrenTypes',
      },

      cycleTypeStr: "",
      template: {
        id: "",
        name: "",
        money: "",
        note: "",
        actionId: "",
        accountId: "",
        accountToId: "",
        typeId: "",
        cycleType: "",
        startDate: "",
      },

      requestLocks: {},
    };
  },
  mounted() {
    this.doGetActions();
    this.doGetAccounts();
  },
  methods: {
    //路由
    leftClick() {
      this.$router.go(-1);
    },

    onCalanderClick() {
      this.calanderShow = true;
    },

    onChooseCalendar(date) {
      this.calanderShow = false;
      this.startDate = this.formatDate(date);
      console.log(this.chooseDate)
    },

    formatDate(date) {
      return `${date.getFullYear()}-${((date.getMonth() + 1) + "").padStart(2, '0')}-${(date.getDate() + "").padStart(2, '0')}`;
    },

    //点击事件
    onActionClick() {
      this.actionShow = true;
      this.popupTitle = "选择账单收支";
      this.popupStyle = 0;
    },

    onChooseAction(action) {
      this.actionShow = false;
      if (action === this.chooseAction) {
        return
      }
      this.chooseAction = action;
      this.chooseAccountTo = {};
      this.chooseType = {}
      this.doGetTypes()
    },

    onTypeClick() {
      if (this.chooseAction.id == null) {
        Toast.fail("请先选择收支")
        return;
      }
      this.typeCascaderShow = true;
    },

    onChooseCascader({ selectedOptions }) {
      this.typeCascaderShow = false;
      this.chooseType.id = this.cascaderValue
      this.chooseType.tname = selectedOptions.map((option) => option.tname).join('/');

      console.log(this.chooseType)
    },

    onAccountClick(popupStyle) {
      this.actionShow = true;
      this.popupTitle = "选择账户";
      this.popupStyle = popupStyle;
    },

    onChooseAccount(account, popupStyle) {
      this.actionShow = false;
      if (popupStyle == 1) {
        this.chooseAccount = account
      } else {
        this.chooseAccountTo = account
      }
    },

    onDateClick() {
      Dialog({
        title: '模板账单日期',
        message: '补上月-上个月最后一天\n记本月-本月记账当天',
      }).then(() => {
        // on close
      });
    },

    onSubmit() {
      if (this.template.name == null || this.template.name === "") {
        Toast.fail("请填写模板名称")
        return
      }
      Dialog.confirm({
        title: '确认',
        message: '确定提交“' + this.template.name + '”吗？'
      }).then(() => {
        this.doSubmitRequest()
      }).catch(() => {
        // on cancel
      });
    },

    doSubmitRequest() {
      request({
        url: this.isEdit ? "/cycle/updateTemplate" : "/cycle/addTemplate",
        method: this.isEdit ? "put" : "post",
        data: {
          id: this.isEdit ? this.template.id : null,
          name: this.template.name,
          money: this.template.money,
          note: this.template.note,
          actionId: this.chooseAction.id,
          accountId: this.chooseAccount.id,
          accountToId: this.chooseAccountTo.id,
          typeId: this.chooseType.id,
          cycleType: this.cycleTypeStr,
          startDate: this.startDate,
        }
      }).then((response) => {
        console.log(response);
        this.$router.go(-1);
      }).catch((error) => {
        console.log(error);
      });
    },

    doGetActions() {
      request({
        url: "/action/getAction",
        method: "get"
      }).then((response) => {
        this.requestLocks.action = true;
        if (this.isEdit) {
          this.unLockRequest()
        }
        this.allActions = response.data.data;
        this.allActions.forEach((action) => {
          this.setActionStyle(action)
        });
        console.log(this.allActions);
      });
    },

    doGetAccounts() {
      request({
        url: "/account/getAccount",
        method: "get",
      })
        .then((response) => {
          this.requestLocks.account = true;
          if (this.isEdit) {
            this.unLockRequest()
          }
          const baseData = response.data.data;
          baseData.forEach((item) => {
            item.money = "￥" + item.money;
            item.exemptMoney = "￥" + item.exemptMoney;
          });
          this.allAccounts = baseData;
          console.log(this.allAccounts);
        })
        .catch((error) => {
          console.log(error);
        });
    },

    doGetTypes() {
      request({
        url: "/type/getTypeByActionId/" + this.chooseAction.id,
        method: "get",
      })
        .then((response) => {
          this.allTypes = response.data.data;
          console.log(this.allTypes)
        })
    },

    unLockRequest() {
      if (this.requestLocks.action && this.requestLocks.account) {
        this.getTemplateById()
      }
    },

    getTemplateById() {

      request({
        url: "/cycle/getTemplateById/" + this.$route.query.templateId,
        method: "get",
      }).then((response) => {
        this.template = response.data.data;
        if (this.template.actionId == null) {
          this.chooseAction = {}
        } else {
          this.chooseAction = this.allActions.find((action) => action.id === this.template.actionId);
        }

        if (this.template.accountId == null) {
          this.chooseAccount = {}
        } else {
          this.chooseAccount = this.allAccounts.find((account) => account.id === this.template.accountId);
          this.doGetTypes()
        }

        if (this.template.accountToId == null) {
          this.chooseAccountTo = {}
        } else {
          this.chooseAccountTo = this.allAccounts.find((account) => account.id === this.template.accountToId);
        }

        if (this.template.typeId == null) {
          this.chooseType = {}
        } else {
          this.chooseType.id = this.template.typeId;
          this.chooseType.tname = this.template.type.tname;
        }

        this.cycleTypeStr = this.template.cycleType == null ? "" : this.template.cycleType.toString();
        this.startDate = this.template.startDate == null ? "" : this.template.startDate.toString();
        console.log(this.template);
      }).catch((error) => {
        console.log(error);
      });
    },

    setActionStyle(action) {
      action.value = action.exempt ? "不计入总金额" : "";
      action.handleText =
        action.handle === 0
          ? "账户金额增加"
          : action.handle === 1
            ? "账户金额减少"
            : "账户金额不变";
      action.style =
        action.handle === 0
          ? "success"
          : action.handle === 1
            ? "danger"
            : "primary";
      return action
    },

    deleteTemplate() {
      Dialog.confirm({
        title: '确认',
        message: '确定删除“' + this.template.name + '”吗？'
      }).then(() => {
        request({
          url: "/cycle/deleteTemplate/" + this.template.id,
          method: "delete",
        }).then((response) => {
          console.log(response);
          this.$router.go(-1);
        }).catch((error) => {
          console.log(error);
        });
      }).catch(() => {
        // on cancel
      });
    },
  }
}
</script>
<style scoped>
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  /* 设置标签之间的间隔 */
  padding: 15px;
  /* 容器的内边距 */
}

.tag-item {
  margin-bottom: 5px;
  /* 增加标签下方的间隔，避免视觉上的拥挤 */
}
</style>