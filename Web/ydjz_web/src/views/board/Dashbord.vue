<template>
  <div>
    <!--    <van-nav-bar fixed placeholder title="总览" right-text="财务分析" @click-right=toAnalysis() />-->
    <van-nav-bar title="总览" />
    <div
      style="display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background-color: #fff; border-bottom: 1px solid #ececec;">
      <div>
        <div style="font-size: 20px; font-weight: 600; color: #333;">总资产：￥{{ this.homeInfo.netAsset }}</div>
        <div style="font-size: 16px; color: #333; margin-top: 5px;">资产：￥{{ this.homeInfo.totalAsset }}</div>
        <div style="font-size: 16px; color: #333; margin-top: 5px;">负债：￥{{ this.homeInfo.cardAsset }}</div>
      </div>
      <!--
      <van-button
          type="default"
          round
          size="small"
          style="margin-top: 5px; margin-left: 20px; font-size: 14px; color: #333; border: 1px solid #ececec;"
          @click="showAccountsDetail=true"
      >
        账户详情
      </van-button>
      -->
    </div>

    <van-tabs v-model:active="activeTab" animated sticky color="#1989fa" title-active-color="#1989fa"
      title-inactive-color="#646566" background="#ffffff" :border="false" class="custom-tabs">
      <van-tab title="账户概览">
        <div class="account-section">
          <div class="section-title">储蓄卡</div>
          <van-cell-group class="account-group">
            <van-cell v-for="account in homeInfo.accounts1" :key="account.id" :title="account.accountName" size="large"
              :label="account.note" @click="toScreen(account.id)" class="account-cell">
              <template #default>
                <div class="account-amount">￥{{ account.accountAsset }}</div>
                <div class="account-sub-amount">{{ account.realAsset }}</div>
              </template>
            </van-cell>
          </van-cell-group>
          <div class="section-title">信用卡</div>
          <van-cell-group class="account-group">
            <van-cell v-for="account in homeInfo.accounts2" :key="account.id" :title="account.accountName" size="large"
              :label="account.note" @click="toScreen(account.id)" class="account-cell">
              <template #default>
                <div class="account-amount">￥{{ account.accountAsset }}</div>
                <div class="account-sub-amount">{{ account.realAsset }}</div>
              </template>
            </van-cell>
          </van-cell-group>
          <div class="section-title">投资账户</div>
          <van-cell-group class="account-group">
            <van-cell v-for="account in homeInfo.accounts3" :key="account.id" :title="account.accountName" size="large"
              :label="account.note" @click="toScreen(account.id)" class="account-cell">
              <template #default>
                <div class="account-amount">￥{{ account.accountAsset }}</div>
                <div class="account-sub-amount">{{ account.realAsset }}</div>
              </template>
            </van-cell>
          </van-cell-group>
          <div class="section-title">其他账户</div>
          <van-cell-group class="account-group">
            <van-cell v-for="account in homeInfo.accounts9" :key="account.id" :title="account.accountName" size="large"
              :label="account.note" @click="toScreen(account.id)" class="account-cell">
              <template #default>
                <div class="account-amount">￥{{ account.accountAsset }}</div>
                <div class="account-sub-amount">{{ account.realAsset }}</div>
              </template>
            </van-cell>
          </van-cell-group>
        </div>
      </van-tab>
      <van-tab title="年度概览">
        <div style=" height:100px;background: #FFF; border-bottom: 1px solid #ececec;padding: 15px 20px;">
          <div style="margin-top: 10px; float: left">
            <div style="margin-bottom: 5px;">
              年度总收入： <span style="color: #42b983">￥{{ this.homeInfo.yearIncome }}</span>
            </div>
            <div style="margin-bottom: 5px;">
              年度总支出： <span style="color: #f54949">￥{{ this.homeInfo.yearOutCome }}</span>
            </div>
            <div>
              年度结余： ￥{{ this.homeInfo.yearBalance }}
            </div>

          </div>
          <div style="display: flex; flex-direction: column; float: right;  margin-top: 5px; ">
            <div style="display: flex; justify-content: space-around; align-items: center; width: 100%;">
              <van-button size="mini" plain type="primary" icon="minus" @click="toLastYear"></van-button>
              <van-button type="primary" round size="small" plain style="margin-left: 5px; margin-right: 5px"
                icon="clock-o" @click="() => { showYearPicker = true }">
                {{ this.chooseYear }}
              </van-button>
              <van-button size="mini" plain type="primary" icon="plus" @click="toNetYear"></van-button>
            </div>
            <van-button type="primary" size="small" @click="showCharts = true"
              style="margin-top: 10px;">查看图表</van-button>
          </div>
        </div>
        <van-divider :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }"> {{ this.chooseYear
          }}年月度概览
        </van-divider>
        <van-cell-group style="margin-left: 10px;margin-right: 10px">
          <!-- 表头 -->
          <van-row style="
      padding: 10px 20px;
      background-color: #fbfbfb;
      border-bottom: 1px solid #ececec;
    " type="flex" justify="space-between">
            <van-col span="3" style="font-weight: bold; color: #333;justify-content: center">月份</van-col>
            <van-col span="7" style="font-weight: bold; color: #333;justify-content: center">收入</van-col>
            <van-col span="7" style="font-weight: bold; color: #333;">支出</van-col>
            <!-- <van-col span="5" style="font-weight: bold; color: #333;">结余</van-col> -->
            <van-col span="7" style="font-weight: bold; color: #333;">净资产</van-col>
          </van-row>

          <!-- 数据行 -->
          <div v-if="homeInfo.monthDetails != null && homeInfo.monthDetails.length > 0">
            <div v-for="month in homeInfo.monthDetails" :key="month.month" @click="toFlow(month.month)">
              <van-row style="padding: 10px 20px;font-size: 14px;color: #555;border-bottom: 1px solid #ececec;"
                type="flex" justify="space-between">
                <van-col span="3">{{ month.month }}月</van-col>
                <van-col span="7" style="color: #42b983">￥{{ month.income }}</van-col>
                <van-col span="7" style="color: #f54949">￥{{ month.outcome }}</van-col>
                <!-- <van-col span="5">￥{{ month.balance }}</van-col> -->
                <van-col span="7" style="color: #42b983">{{ month.netAsset ? '￥' + month.netAsset : '未统计' }}</van-col>
              </van-row>
            </div>
          </div>
          <van-empty v-else description="暂无数据" />
        </van-cell-group>


        <van-popup v-model:show="showYearPicker" round position="bottom">
          <van-picker show-toolbar title="选择年份" :columns="yearList" @cancel="showYearPicker = false"
            @confirm="this.onPickerChoose" />
        </van-popup>
      </van-tab>


    </van-tabs>

    <van-popup v-model:show="showCharts" position="top" :style="{ width: '100%', background: '#F7F8FA' }">
      <van-nav-bar fixed placeholder title="月度收支图表">
        <template #right>
          <van-icon name="cross" size="18" @click="() => { showCharts = false }" />
        </template>
      </van-nav-bar>

      <div style="padding: 10px; margin-top: 46px;">
        <van-radio-group v-model="chartType" direction="horizontal"
          style="display: flex; justify-content: space-around;">
          <van-radio name="income">月收入</van-radio>
          <van-radio name="outcome">月支出</van-radio>
          <van-radio name="netAsset">资产趋势图</van-radio>
        </van-radio-group>
      </div>

      <ApexChart v-if="chartType === 'income'" type="bar" :options="incomeChartOptions" :series="incomeSeries"
        style="margin: 20px 10px"></ApexChart>
      <ApexChart v-if="chartType === 'outcome'" type="bar" :options="outcomeChartOptions" :series="outcomeSeries"
        style="margin: 20px 10px"></ApexChart>
      <ApexChart v-if="chartType === 'netAsset'" type="bar" :options="netAssetChartOptions" :series="netAssetSeries"
        style="margin: 20px 10px"></ApexChart>
    </van-popup>
  </div>
</template>

<script>

import ApexCharts from 'vue3-apexcharts';
import { showFailToast } from "vant";

export default {
  components: {
    ApexChart: ApexCharts,
  },
  data() {
    return {
      activeTab: 0, // 新增：当前激活的标签页
      money: 999,
      chooseYear: new Date().getFullYear(),
      minYear: 2021,
      yearList: [],
      showYearPicker: false,
      showNextYearButton: false,
      showAccountsDetail: true,
      homeInfo: {},

      showCharts: false,
      chartType: 'income',
      incomeSeries: [],
      outcomeSeries: [],
      netAssetSeries: [],
      incomeChartOptions: {},
      netAssetChartOptions: {},
    };
  },
  mounted() {
    this.getHomeInfo();
    this.prepareYearColum();
  },
  methods: {
    makeChartOptions() {
      // 收入图表配置
      this.incomeSeries = [{
        name: '收入',
        data: this.homeInfo.monthDetails.map(item => parseFloat(item.income))
      }];

      this.incomeChartOptions = {
        chart: {
          type: 'bar',
          height: 350
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%',
            borderRadius: 5,
            dataLabels: {
              position: 'top'
            }
          }
        },
        dataLabels: {
          enabled: true,
          formatter: function (val) {
            //return '￥' + val;
            if (val) {
              return '';//不显示
            }
            return '';//不显示
          },
          style: {
            colors: ['#333'],
            fontSize: '12px'
          },
          offsetY: -20
        },
        xaxis: {
          categories: this.homeInfo.monthDetails.map(item => item.month + '月')
        },
        yaxis: {
          title: {
            text: '金额 (元)'
          }
        },
        colors: ['#42b983'],
        title: {
          text: this.chooseYear + "年收入统计",
          align: 'center'
        }
      };

      // 支出图表配置
      this.outcomeSeries = [{
        name: '支出',
        data: this.homeInfo.monthDetails.map(item => parseFloat(item.outcome))
      }];

      this.outcomeChartOptions = {
        ...this.incomeChartOptions,
        colors: ['#f54949'],
        title: {
          text: this.chooseYear + "年支出统计",
          align: 'center'
        }
      };

      // 净资产图表配置
      this.netAssetSeries = [{
        name: '净资产',
        data: this.homeInfo.monthDetails.map(item => item.netAsset ? parseFloat(item.netAsset) : null),
        type: 'line'
      }];

      this.netAssetChartOptions = {
        chart: {
          type: 'line',
          height: 350
        },
        stroke: {
          curve: 'smooth',
          width: 2
        },
        markers: {
          size: 5
        },
        dataLabels: {
          enabled: true,
          formatter: function (val) {
            //return '￥' + val;
            if (val) {
              return '';//不显示
            }
            return '';//不显示
          },
          style: {
            colors: ['#333'],
            fontSize: '12px'
          },
          offsetY: -20
        },
        xaxis: {
          categories: this.homeInfo.monthDetails.map(item => item.month + '月')
        },
        yaxis: {
          title: {
            text: '金额 (元)'
          }
        },
        colors: ['#1989fa'],
        title: {
          text: this.chooseYear + "年净资产趋势",
          align: 'center'
        }
      }
    },
    prepareYearColum() {
      var year = new Date().getFullYear();
      for (var i = year; i >= this.minYear; i--) {
        this.yearList.push({ text: i + '年', value: i });
      }
    },

    toLastYear() {
      if (this.chooseYear === this.minYear) {
        showFailToast('已经到最前了');
        return
      }
      this.chooseYear = this.chooseYear - 1;
      this.controlNextYearButton()
      this.getHomeInfo();
    },

    toNetYear() {
      if (this.chooseYear === new Date().getFullYear()) {
        showFailToast('已经到最后了');
        return
      }
      this.chooseYear = this.chooseYear + 1;
      this.controlNextYearButton()
      this.getHomeInfo();
    },

    onPickerChoose({ selectedValues }) {
      if (selectedValues[0] === this.chooseYear) {
        this.showYearPicker = false;
        return;
      }
      this.chooseYear = selectedValues[0];

      this.showYearPicker = false;
      this.controlNextYearButton()

      this.getHomeInfo()
    },

    controlNextYearButton() {
      var year = new Date().getFullYear();
      if (this.chooseYear < year) {
        this.showNextYearButton = true;
      } else {
        this.showNextYearButton = false;
      }
    },

    toAnalysis() {
      this.$router.push({ path: "/analysis" });
    },
    toScreen(acid) {
      window.scrollTo(0, 0); // 
      //this.$router.push({ path: "/screen", query: { acid: acid } });
      this.$router.push({ path: "/account/add", query: { accountId: acid, key: Date.now()  } });
    },

    toFlow(month) {
      var str = this.chooseYear + '-' + month.toString().padStart(2, "0");
      console.log(str);
      this.$router.push({ path: "/flow", query: { month: str, key: Date.now() } });
    },

    getHomeInfo() {
      this.$http({
        url: "/home/getHomeInfoV2/" + this.chooseYear,
        method: "get",
      }).then((resp) => {
        console.log(resp.data.data);
        this.homeInfo = resp.data.data
        this.homeInfo.accounts2.map(account => {
          if (account.exemptAsset != '' && account.exemptAsset != null) {
            var fNum = parseFloat(account.accountAsset) - parseFloat(account.exemptAsset);
            account.realAsset = '可用额度 ￥ ' + (account.accountAsset)
            account.accountAsset = (fNum.toFixed(2))
            //account.realAsset ='可用额度 ￥ '+( fNum.toFixed(2))
          } else {
            account.realAsset == '';
          }
        })

        this.homeInfo.accounts9.map(account => {
          if (account.exemptAsset != '' && account.exemptAsset != null) {
            var fNum = parseFloat(account.accountAsset) - parseFloat(account.exemptAsset);
            //account.realAsset = '可用额度 ￥ ' + (account.accountAsset)
            account.accountAsset = account.exemptAsset
            account.realAsset = '净资产 ￥ ' + (fNum.toFixed(2))
          } else {
            account.realAsset == '';
          }
        })
        this.makeChartOptions(); // 在数据加载完成后生成图表
      }).catch((error) => {
        console.error("获取首页信息失败:", error);
      });
    },
  },
};
</script>

<style scoped>
/* 保留原有的 custom-tabs 样式 */

.account-section {
  padding: 16px;
  background: #f7f8fa;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1989fa;
  margin: 16px 0 8px;
  padding-left: 12px;
  border-left: 4px solid #1989fa;
}

.account-group {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.account-cell {
  margin: 0;
  padding: 16px !important;
}

.account-cell:active {
  background-color: #f5f5f5;
}

.account-amount {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.account-sub-amount {
  font-size: 13px;
  color: #999;
}

/* 移除最后一个cell的边框 */
.account-group .van-cell:last-child::after {
  display: none;
}

.custom-tabs :deep(.van-tabs__wrap) {
  padding: 0 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.custom-tabs :deep(.van-tab) {
  font-size: 15px;
  padding: 12px 0;
}

.custom-tabs :deep(.van-tabs__line) {
  height: 3px;
  border-radius: 3px;
}
</style>
