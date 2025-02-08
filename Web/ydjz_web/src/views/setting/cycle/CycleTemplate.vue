<template>
  <div style="width:100vw; height:100vh; background: #F7F8FA">
    <van-nav-bar fixed placeholder title="周期记账" left-arrow right-text="添加" @click-left="leftClick"
      @click-right="onClickRight">
    </van-nav-bar>

    <div v-if="allTemplates.length > 0">

      <van-cell-group :border="false">
      <div @click="onEditClick(template.id)" v-for="template in allTemplates" :key="template.id">
        <van-swipe-cell >
          <div style="
            margin-left: 15px;
            margin-top: 5px;
            font-size: 13px;
            color: #4e4e4e;
          "> 
            {{ template.startDate }}   {{ template.cycleType }} 
          </div>
         

          <van-cell size="small" :title="template.name" :value="'￥' + template.money" :label="template.account.name">
            <template #label>
              <div>
                <label style="color: #676767; font-size: 13px; display: block;">{{ template.account.name }}</label>
                <label v-if=" template.note && template.note.length > 0 "
                  style="color: #cea643; font-size: 13px; display: block; margin-top: 4px;">{{
                    "备注：" + template.note
                  }}</label>
              </div>
            </template> 
            <template #default>
              <div style="color: #000; font-size: 16px">￥{{ template.money }}</div>
              <van-tag :type="template.action.style">{{ template.action.hname }}</van-tag>
            </template>
          </van-cell>
          
          <div style="height: 1px"></div>

          <template #right>
            <van-button square text="删除" type="danger" class="delete-button" @click="deleteTemplate(template)"/>
          </template>
        </van-swipe-cell>
      </div>
    </van-cell-group>
<!--
      <van-cell-group inset :border="false" :style="{ marginTop: '10px' }" v-for="template in allTemplates"
        :key="template.id">
        <van-collapse :border="false" accordion v-model="template.show">
          <van-collapse-item :title="template.name" name="1">
            <template #value>
              <div style="display: flex; justify-content: flex-end;">
                <div v-if="template.tag">
                  <van-tag :color="template.tag.color" style="margin-right: 15px">{{ template.tag.name }}</van-tag>
                </div>
                <div style="color: cornflowerblue; font-size: 14px; margin-right: 8px; cursor: pointer;"
                  @click="onEditClick(template.id)">
                  编辑
                </div>
              </div>
            </template>
            <van-cell v-if="template.money" title="金额" :value="template.money" />
            <van-cell v-if="template.actionId" title="收支">
              <template #default>
                <van-tag plain :type="template.action.style">{{ template.action.hname }}</van-tag>
              </template>
            </van-cell>
            <van-cell v-if="template.accountId" title="账户" :value="template.account.name" />
            <van-cell v-if="template.accountToId" title="目标账户" :value="template.accountTo.name" />
            <van-cell v-if="template.typeId" title="分类选择" :value="template.type.tname" />
            <van-cell v-if="cycleTypeStr" title="重复周期" :value="cycleTypeStr" />
            <van-cell v-if="template.startDate" title="开始日期" :value="template.startDate" />
          </van-collapse-item>
        </van-collapse>
      </van-cell-group>
      -->
    </div>
    <van-empty v-else description="当前无记录" />
  </div>
</template>

<script>
import request from "../../../utils/request";
import { Toast,Dialog } from "vant";

export default {
  name: "FlowAuto.vue",
  data() {
    return {
      activeNames: ['1'],
      tagManageShow: false,
      allTemplates: [],
      chooseColor: "",
      allTags: [],
      chooseTag: {},
      cycleTypeStr:"",
    };
  },

  mounted() {
    this.getAllTemplate();
  },

  methods: {
    leftClick() {
      this.$router.go(-1);
    },
    onClickRight() {
      this.$router.push({ path: "/cycle/add" })
    },

    onEditClick(id) {
      this.$router.push({ path: "/cycle/add", query: { templateId: id } });
    },

    getAllTemplate() {
      request({
        url: "/cycle/getAllTemplates",
        method: "get",
      }).then((response) => {
        this.allTemplates = response.data.data;
        this.allTemplates.forEach((template) => {
          if (template.action) {
            template.action = this.setActionStyle(template.action);
          }
          if (template.cycleType != null) {
            template.cycleType  = this.setCycleStyle(template.cycleType)
          } else {
            this.cycleTypeStr = "";
          }
        });
        console.log(this.allTemplates);
      }).catch((error) => {
        console.log(error);
      });
    },

    setActionStyle(action) {
      action.value = action.exempt ? "不计入总金额" : ""
      action.handleText = action.handle === 0 ? "账户金额增加" : action.handle === 1 ? "账户金额减少" : "账户金额不变"
      action.style = action.handle === 0 ? "success" : action.handle === 1 ? "danger" : "primary"
      return action
    },
    setCycleStyle(cycleType) {
      if (cycleType === 1) {
        this.cycleTypeStr = '每天'
      } else if (cycleType === 2) {
        this.cycleTypeStr = '每月'
      } else if (cycleType === 3) {
        this.cycleTypeStr = '每年'
      } else { this.cycleTypeStr = '' }
      return this.cycleTypeStr
    },

    showLoading() {
      Toast.loading({
        message: '加载中...',
        forbidClick: true,
      });
    },
    deleteTemplate(template) {
      Dialog.confirm({
        title: '确认',
        message: '确定删除“' + template.name + '”吗？'
      }).then(() => {
        request({
          url: "/cycle/deleteTemplate/" + template.id,
          method: "delete",
        }).then((response) => {
          console.log(response);
          this.$router.go("/setting/cycle");
        }).catch((error) => {
          console.log(error);
        });
      }).catch(() => {
        // on cancel
      });
    },
  },
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
.delete-button {
  height: 100%;
  white-space: pre-wrap;
}
</style>