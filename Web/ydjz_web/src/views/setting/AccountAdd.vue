<template>
  <div>
    <van-nav-bar
      v-if="this.$route.query.accountId != null"
      title="添加账号"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />

    <van-nav-bar
      v-else
      title="添加账号"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />

    <van-cell-group>
      <van-field
        v-model="aName"
        label="账号名称"
        required
        placeholder="请输入账号名称"
      />
      <van-field
        v-model="money"
        type="number"
        label="账户余额"
        required
        placeholder="请输入账户余额"
      />

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


      <van-field
        v-model="note"
        rows="3"
        autosize
        label="账户信息备注"
        type="textarea"
        maxlength="50"
        placeholder="请输入备注"
        show-word-limit
      />
    </van-cell-group>
    <div style="display: flex; justify-content: space-around; margin: 20px; gap: 15px;">
      <van-button 
        @click="onClickRight()" 
        type="primary" 
        round 
        size="normal"
        icon="success"
        color="linear-gradient(to right, #07c160, #10b981)"
        style="width: 40%; padding: 10px; box-shadow: 0 2px 4px rgba(7, 193, 96, 0.2);"
      >
        {{ this.$route.query.accountId ? '更新账户' : '保存账户' }}
      </van-button>

      <van-button 
        v-if="this.$route.query.accountId"
        @click="toScreen()" 
        type="primary" 
        round 
        size="normal"
        icon="chart-trending-o"
        color="linear-gradient(to right, #1989fa, #39b9fa)"
        style="width: 40%; padding: 10px; box-shadow: 0 2px 4px rgba(25, 137, 250, 0.2);"
      >
        查看交易记录
      </van-button>
    </div>
  </div>
</template>

<script>
// import { Toast } from 'vant';
import {closeToast, showFailToast, showLoadingToast, showToast} from "vant";

export default {
  data() {
    return {
      aName: "",
      isFlow: "0",
      card: "",
      money: "",
      exempt: "",
      sortno: "",
      note: ""
    };
  },
  mounted() {
    if (this.$route.query.accountId != null) {
      this.getAccountSingle();
    }
  },

  methods: {
    getAccountSingle() {
      this.$http({
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
      }).catch((error) => {
        console.log(error);
      });
    },

    onClickLeft() {
      this.$router.go(-1);
    },
    toScreen() {
      this.$router.push({ path: "/screen", query: { acid: this.$route.query.accountId } });
    },

    doAdd() {

      showLoadingToast({
        message: "保存中...",
        duration: 0,
        forbidClick: true,
        loadingType: "spinner"
      });
      this.$http({
        url: "/account/addAccount",
        method:"post",
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
          closeToast();
          showToast({ type: "success", message: "保存成功" });
          this.$router.go(-1);
          console.log(response.data);
        })
        .catch((error) => {
          closeToast();
         // Notify({ type: "danger", message: "保存失败" });
          console.log(error);
        });
    },

    doUpdate() {
      showLoadingToast({
        message: "更新中...",
        duration: 0,
        forbidClick: true,
        loadingType: "spinner"
      });
      this.$http({
        url: "/account/updateAccount/"+this.$route.query.accountId,
        method:"put",
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
          closeToast();
          showToast({ type: "success", message: "更新成功" });
          this.$router.go(-1);
        })
        .catch((error) => {
          closeToast();
          showToast({ type: "danger", message: "更新失败" ,duration:600});
          console.log(error);
        });
    },

    onClickRight() {
      /* */
      if (this.aName === "") {
        showFailToast("账号名为空");
        return;
      }
      if (this.money === "") {
        showFailToast("初始金额为空");
        return;
      }
      //if (parseInt(this.exempt)>parseInt(this.money)){
      //  showFailToast("不计入总金额钱数不得大于账户余额")
      //  return
      //}
      if (this.$route.query.accountId != null) {
        this.doUpdate();
      } else {
        this.doAdd();
      }
    }
  }
};
</script>

<style scoped></style>
