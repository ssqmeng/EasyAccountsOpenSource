<template>
  <div style="width:100vw; height:100vh; background: #F7F8FA">
    <van-nav-bar v-if="this.$route.query.flowId==null" title="新增账单" left-arrow @click-left="onClickLeft"
                 right-text="快记模板" @click-right="fastPopupShow = true"/>
    <van-nav-bar v-else title="修改账单" left-arrow @click-left="onClickLeft"/>
    <van-cell-group>
       

      <!-- 简化版的收支选择 -->
      <div class="action-selector">
        <div 
          v-for="action in allActions" 
          :key="action.id" 
          class="action-button"
          :class="{ 
            'selected': chooseAction.id === action.id,
            'income': action.handle === 0,
            'expense': action.handle === 1,
            'transfer': action.handle === 2
          }"
          @click="onChooseAction(action)"
        >
          {{ action.hname }}
        </div>
      </div>
     
      <!-- 账户和分类选择区域 -->
      <van-cell-group inset style="margin-top: 6px;">
        <!-- 付款账户选择 -->
        <div class="account-wrapper">
          <div class="account-label">付款账户</div>
          <div class="account-selector">
            <div 
              v-for="account in orderedAccounts.slice(0, 3)" 
              :key="account.id" 
              class="account-item"
              :class="{ 'selected': chooseAccount.id === account.id }"
              @click="onChooseAccount(account, 1)"
            >
              {{ account.name }}
            </div>
            <div 
              class="account-item account-more"
              @click="onAccountClick(1)"
            >
              全部
            </div>
          </div>
        </div>

        <!-- 目标账户选择 -->
        <div v-show="chooseAction.handle==2" class="account-wrapper to-account-wrapper">
          <div class="account-label">目标账户</div>
          <div class="account-selector to-account-selector">
            <div 
              v-for="account in orderedToAccounts.slice(0, 3)" 
              :key="account.id" 
              class="account-item to-account-item"
              :class="{ 'selected': chooseToAccount.id === account.id }"
              @click="onChooseAccount(account, 2)"
            >
              {{ account.name }}
            </div>
            <div 
              class="account-item account-more to-account-item"
              @click="onAccountClick(2)"
            >
              全部
            </div>
          </div>
        </div>
        
        <!-- 账单分类选择 -->
        <div class="type-wrapper">
          <div class="type-label">项目</div>
          
          <!-- 统一的分类选择：上面是大类 -->
          <div class="type-grid parent-types">
            <!-- 显示大类 -->
            <div 
              v-for="type in allTypes" 
              :key="type.id" 
              class="type-item"
              :class="{ 'selected': selectedParentType && selectedParentType.id === type.id }"
              @click="onSelectParentType(type)"
            >
              {{ type.tname }}
            </div>
          </div>
          
          <!-- 小类区域 -->
          <div v-if="selectedParentType && selectedParentType.childrenTypes && selectedParentType.childrenTypes.length > 0">
            <div class="type-label">分类</div>
            <div class="type-grid child-types">
              <div 
                v-for="childType in selectedParentType.childrenTypes" 
                :key="childType.id" 
                class="type-item"
                :class="{ 'selected': isTypeSelected(childType) }"
                @click="onChooseChildType(childType)"
              >
                {{ childType.tname }}
              </div>
            </div>
          </div>
        </div>

        <!-- 账单金额 -->
        <div class="bill-info-wrapper" @click="showKeyboard = true">
          <div class="bill-info-label">账单金额</div>
          <div class="bill-info-value">
            <span v-if="money">{{ money }}</span>
            <span v-else class="placeholder">请输入账单金额</span>
          </div>
        </div>

        <!-- 账单日期 -->
        <div class="bill-info-wrapper" @click="onCalanderClick">
          <div class="bill-info-label">账单日期</div>
          <div class="bill-info-value">
            {{ chooseDate }}
          </div>
        </div>

        <!-- 是否收藏 
        <div class="bill-info-wrapper">
          <div class="bill-info-label">是否收藏</div>
          <div class="bill-info-value">
            <van-switch v-model="isCollect" size="20"/>
          </div>
        </div>-->

        <!-- 备注 -->
        <div class="bill-info-wrapper note-wrapper">
          <div class="bill-info-label">备注</div>
          <div class="bill-info-value note-input">
            <textarea 
              v-model="note"
              placeholder="请输入备注"
              maxlength="500"
              ref="noteTextarea"
              @input="resizeTextarea"
              @change="resizeTextarea"
            ></textarea>
            <span class="word-limit">{{ note.length }}/500</span>
          </div>
        </div>
      </van-cell-group>

      <van-number-keyboard
          v-model="money"
          :show="showKeyboard"
          :extra-key="['-', '.']"
          theme="custom"
          close-button-text="完成"
          @blur="showKeyboard = false"
          @input="handleNumberInput"
          @delete="handleNumberDelete"
      />
    </van-cell-group>
    <van-action-sheet v-if="popupStyle<=2" v-model:show="actionShow" :title="popupTitle">
      <van-cell-group v-if="popupStyle == 0">
        <van-cell
            v-for="action in allActions"
            :key="action.id"
            @click="onChooseAction(action)">
          <template #title>
            <span class="custom-title">{{ action.hname }}</span>
          </template>
          <template #label>
            <van-tag :type="action.style">{{ action.handleText }}</van-tag>
            <van-tag
                v-show="action.exempt"
                style="margin-left: 10px"
                color="gray"
                plain
                type="action.style"
            >{{ action.value }}
            </van-tag
            >
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group v-if="popupStyle==1||popupStyle==2">
        <van-cell v-for="account in allAccounts" :key="account.id" :title="account.name" :value="account.money"
                  :label="account.note" @click="onChooseAccount(account,popupStyle)"/>
      </van-cell-group>
    </van-action-sheet>
    <van-calendar v-model:show="calanderShow" :show-confirm="false" color="#1989fa" :min-date="minDate"
                  :max-date="maxDate " @confirm="onChooseCalendar"/>

    <!--  追加账单 1.6版本  -->
    <van-cell-group v-for="child in childMoneyItem" :key="child.index" inset
                    style="margin-top: 10px;margin-bottom: 10px">
      <van-swipe-cell>
        <div class="bill-info-wrapper">
          <div class="bill-info-label">账单金额</div>
          <div class="bill-info-value">
            <input 
              v-model="child.money"
              type="number"
              placeholder="请输入追加账单金额"
              class="child-input"
            />
          </div>
        </div>
        <div class="bill-info-wrapper note-wrapper">
          <div class="bill-info-label">追加备注</div>
          <div class="bill-info-value note-input">
            <textarea 
              v-model="child.note"
              placeholder="请输入追加备注"
              maxlength="500"
              ref="childNoteTextarea"
              @input="resizeChildTextarea"
              class="child-textarea"
            ></textarea>
          </div>
        </div>
        <template #right>
          <van-button square type="danger" text="删除" style="height: 100%" @click="doRemoveMoneyItem(child)"/>
        </template>
      </van-swipe-cell>
    </van-cell-group>

    <!--<div style="margin: 16px">-->
    <div style="display: flex; justify-content: center; margin: 8px 15px; gap: 8px;">
      <van-button @click="onSubmitBtnClick" round block type="primary" icon="paid" 
        style="flex: 1; padding: 6px 0; font-size: 13px; box-shadow: 0 2px 4px rgba(25, 137, 250, 0.2);">
        {{ this.$route.query.flowId != null ? "修改" : "提交" }}
      </van-button>
    </div>
    <!--  追加账单 1.6版本  -->
    
    <div style="display: flex; justify-content: center; margin: 0 15px 8px; gap: 8px;">
      <van-button @click="doAddNewItemMoney()" round icon="success"
        color="linear-gradient(to right, #07c160, #10b981)"
        style="flex: 1; padding: 6px 0; font-size: 13px; box-shadow: 0 2px 4px rgba(7, 193, 96, 0.2);">
        追加
      </van-button>
      <van-button @click="doSubNewItemMoney()" type="warning" round icon="chart-trending-o" 
        style="flex: 1; padding: 6px 0; font-size: 13px; box-shadow: 0 2px 4px rgba(25, 137, 250, 0.2);">
        退款
      </van-button>
    </div>
    

    <!--  快记模板 2.1版本  -->
    <van-popup
        v-model:show="fastPopupShow"
        position="top"
        :style="{ width: '100%',height: '100%' ,background:'#F7F8FA' }"
    >
      <van-nav-bar title="选择模板" left-arrow fixed placeholder right-text="模板管理" @click-right="fastNavToTemplateManage">
        <template #left>
          <van-icon name="cross" size="18" @click="fastPopupShow=false"/>
        </template>
      </van-nav-bar>
      <van-cell-group inset :border="false" style="margin-top: 10px;margin-bottom: 10px">
        <van-collapse :border="false" v-model="activeNames">
          <van-collapse-item title="标签筛选" :toggle="tagChooseShow" name="1">
            <template #value>
              <van-tag v-if="chooseTag.id!=null" :color="chooseTag.color" closeable
                       @close="chooseTag = {};getAllTemplate()">{{ chooseTag.name }}
              </van-tag>
            </template>
            <div v-if="allTags.length !== 0" class="tags-container">
              <div class="tag-item" v-for="tag in allTags" :key="tag.id"
                   @click="chooseTag = tag;getAllTemplate();tagChooseShow = false">
                <van-tag size="small" :color="tag.color">{{ tag.name }}</van-tag>
              </div>
            </div>
            <van-empty v-else description="暂无标签" style="margin-top: 10px"/>

          </van-collapse-item>

        </van-collapse>
      </van-cell-group>

      <van-cell-group :border="false" inset style="margin-top: 10px;">
        <van-cell title="模板列表" :border="false"/>
        <van-grid :border="true" :column-num="3" :gutter="10" style="margin-bottom: 10px" :center="false"
                  v-if="allTemplates.length!=0">
          <van-grid-item v-for="template in allTemplates" :key="template.id">
            <template #default>
              <div class="template-container" @click="fastChooseClick(template)">
                <div class="template-info">
                  <div class="template-name">{{ template.name }}</div>
                  <van-tag v-if="template.tag" :color="template.tag.color">{{ template.tag.name }}</van-tag>
                </div>
                <!-- <div class="template-action">
                  <van-button plain hairline size="small" type="primary" style="margin-left: 10px"
                              @click.stop="fastDialogClick(template)">详情
                  </van-button>
                </div> -->
              </div>
            </template>
          </van-grid-item>
        </van-grid>
        <van-empty v-else description="暂无模板" style="margin-top: 10px"/>
      </van-cell-group>
    </van-popup>

    <van-dialog v-model:show="fastDialogShow" closeOnClickOverlay cancel-button-text="编辑" confirm-button-text="选择"
                cancel-button-color="#3D8AF2" @cancel="fastDialogToEdit(chooseTemplate.id)" :title="chooseTemplate.name"
                @confirm="fastChooseClick(chooseTemplate)"
                show-cancel-button>
      <van-cell-group :border="false" v-model="chooseTemplate.show">

        <van-cell v-if="chooseTemplate.money" title="金额" :value="chooseTemplate.money"/>
        <van-cell v-if="chooseTemplate.actionId" title="收支">
          <template #default>
            <van-tag plain :type="chooseTemplate.action.style">{{ chooseTemplate.action.hname }}</van-tag>
          </template>
        </van-cell>
        <van-cell v-if="chooseTemplate.accountId" title="账户" :value="chooseTemplate.account.name"/>
        <van-cell v-if="chooseTemplate.accountToId" title="目标账户" :value="chooseTemplate.accountTo.name"/>
        <van-cell v-if="chooseTemplate.typeId" title="分类选择" :value="chooseTemplate.type.tname"/>
        <van-cell v-if="chooseTemplate.dateTypeStr" title="日期类型"
                  :value="chooseTemplate.dateTypeStr==='1'?'补上月':'记本月'"/>
        <van-cell title="标签" name="1">
          <template #default>
            <div style="display: flex; justify-content: flex-end;">
              <div v-if="chooseTemplate.tag">
                <van-tag :color="chooseTemplate.tag.color" style="margin-right: 15px">{{ chooseTemplate.tag.name }}
                </van-tag>
              </div>
            </div>
          </template>
        </van-cell>
      </van-cell-group>
    </van-dialog>
  </div>
</template>

<script>
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";

export default {
  name: "FlowAdd",
  computed: {
    // 重新排列账户列表，将选中的账户放在第一位
    orderedAccounts() {
      if (!this.allAccounts || this.allAccounts.length === 0) {
        return [];
      }
      
      // 如果没有选中账户，直接返回原数组
      if (!this.chooseAccount || !this.chooseAccount.id) {
        return this.allAccounts.slice(0, 3);
      }
      
      // 找到选中的付款账户
      const selectedAccount = this.allAccounts.find(account => account.id === this.chooseAccount.id);
      
      // 如果没有找到选中的账户，直接返回原数组
      if (!selectedAccount) {
        return this.allAccounts.slice(0, 3);
      }
      
      // 创建新数组，将选中的账户放在第一位
      const result = [selectedAccount];
      
      // 添加其他账户，最多3个
      let count = 1;
      for (const account of this.allAccounts) {
        if (account.id !== this.chooseAccount.id && count < 3) {
          result.push(account);
          count++;
        }
      }
      
      return result;
    },
    
    // 重新排列目标账户列表，将选中的账户放在第一位
    orderedToAccounts() {
      if (!this.allAccounts || this.allAccounts.length === 0) {
        return [];
      }
      
      // 如果没有选中目标账户，直接返回原数组
      if (!this.chooseToAccount || !this.chooseToAccount.id) {
        return this.allAccounts.slice(0, 3);
      }
      
      // 找到选中的目标账户
      const selectedAccount = this.allAccounts.find(account => account.id === this.chooseToAccount.id);
      
      // 如果没有找到选中的账户，直接返回原数组
      if (!selectedAccount) {
        return this.allAccounts.slice(0, 3);
      }
      
      // 创建新数组，将选中的账户放在第一位
      const result = [selectedAccount];
      
      // 添加其他账户，最多3个
      let count = 1;
      for (const account of this.allAccounts) {
        if (account.id !== this.chooseToAccount.id && count < 3) {
          result.push(account);
          count++;
        }
      }
      
      return result;
    }
  },
  data() {
    return {
      // 2.1版本
      tagChooseShow: false,
      activeNames: [],
      allTemplates: [],
      chooseTemplate: {},
      allTags: [],
      chooseTag: {},
      fastPopupShow: false,
      fastDialogShow: false,


      childMoneyItem: [],
      money: "",
      calanderShow: false,
      actionShow: false,
      popupStyle: 0,
      popupTitle: "",

      chooseAction: {}, //已选择的action
      allActions: [], //全部的action

      chooseAccount: {},
      chooseToAccount: {},
      allAccounts: [],

      chooseType: {}, //小类
      fatherType: {}, //大类
      allTypes: [],
      selectedParentType: null,

      chooseDate: this.formatDate(new Date()),

      isCollect: false,

      note: "",
      submitMoney: "",
      submitNote: "",

      minDate: new Date(2023, 0, 1),
      maxDate: new Date(),
      showKeyboard: false,
      currentInput: '',
    };
  },
  mounted() {
    if (this.$route.query.flowId != null) {
      // 修改账单时，先确保加载所有actions
      this.doGetActions().then(() => {
        this.doGetCurrentFlow()
      });
    } else {
      this.getAllTags()
      // 确保获取所有模板
      this.getAllTemplate()
      // 先加载所有actions，然后加载默认的action
      this.doGetActions().then(() => {
        if (this.allActions && this.allActions.length > 0) {
          // 默认选择第一个action（收入）
          const defaultAction = this.allActions[1];
          this.onChooseAction(defaultAction);
        }
      });
    }
    this.doGetAccounts()
    
    // 初始化备注文本框高度
    this.$nextTick(() => {
      this.resizeTextarea()
      this.resizeChildTextarea()
    })
    
    // 如果是修改账单，确保备注文本框高度正确
    if (this.$route.query.flowId != null) {
      this.$nextTick(() => {
        // 延迟执行以确保DOM完全加载
        setTimeout(() => {
          this.resizeTextarea();
        }, 100);
      });
    }
  },
  methods: {
    // 自适应文本框高度
    resizeTextarea() {
      const textarea = this.$refs.noteTextarea;
      if (textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = textarea.scrollHeight + 'px';
      }
    },
    
    // 追加账单备注自适应高度
    resizeChildTextarea() {
      // 由于可能有多个追加账单，需要特殊处理
      this.$nextTick(() => {
        const textareas = document.querySelectorAll('.child-textarea');
        textareas.forEach(textarea => {
          textarea.style.height = 'auto';
          textarea.style.height = textarea.scrollHeight + 'px';
        });
      });
    },
    
    handleNumberInput(value) {
      if (value === '.') {
        // 处理小数点输入
        if (!this.currentInput.includes('.')) {
          this.currentInput = (this.currentInput || '0') + '.';
          this.money = this.currentInput;
        }
      } else if (value === '-') {
        // 处理负号输入
        if (!this.currentInput) {
          this.currentInput = '-';
          this.money = this.currentInput;
        }
      } else {
        // 处理数字输入
        if (this.currentInput.includes('.') && this.currentInput.split('.')[1]?.length >= 2) {
          return; // 限制小数位数为2位
        }
        this.currentInput = (this.currentInput || '') + value;
        this.money = this.currentInput;
      }
    },

    handleNumberDelete() {
      this.currentInput = this.currentInput.slice(0, -1);
      this.money = this.currentInput;
    },
    fastChooseClick(template) {
      this.fastPopupShow = false;
      this.fastDialogShow = false;
      showSuccessToast(template.name);
      if(template.money){
        this.money = template.money;
      }
      if(template.note){
        this.note = template.note;
      }
      if(template.account){
        this.chooseAccount = template.account;
      }
      if (template.action != null) {
        this.chooseAction = this.setActionStyle(template.action);
        if (this.chooseAction.id != null) {
          this.doGetTypes().then(() => {
            // 分类加载完成后，设置分类高亮
            if (template.type && template.type.tname) {
              this.setupCategoryHighlight(template.type);
            }
          });
        }

        if (template.action.handle.toString() === "2") {
          if(template.accountTo){
            this.chooseToAccount = template.accountTo
            this.chooseToAccount.name = template.accountTo.name
          }
        }
      }
      // 暂时保存type信息，等分类列表加载完成后再设置高亮
      if(template.type){
        this.chooseType = template.type;
        // 如果分类数据已经加载完成，直接设置高亮
        if (this.allTypes && this.allTypes.length > 0) {
          this.setupCategoryHighlight(template.type);
        }
      }
      if (template.dateType != null) {
        if (template.dateType.toString() === "0") {
          this.chooseDate = this.formatDate(new Date())
        } else {
          //选择的是上月最后一天
          var date = new Date();
          date.setDate(0);
          this.chooseDate = this.formatDate(date)
        }
      }

    },
    fastDialogClick(template) {
      this.chooseTemplate = template;
      this.fastDialogShow = true;
      // 阻止冒泡可以放在这里或直接在模板中使用 .stop 修饰符
    },
    fastDialogToEdit(id) {
      this.fastDialogShow = false;
      this.$router.push({path: "/template/add", query: {templateId: id}});
    },
    fastNavToTemplateManage() {
      this.$router.push({path: "/setting/template"});
    },

    getAllTags() {
      this.$http({
        url: "/tag/getTags",
        method: "get",
      }).then((response) => {
        this.allTags = response.data.data;
        this.getAllTemplate()
        console.log(this.allTags);
      }).catch((error) => {
        console.log(error);
      });
    },

    getAllTemplate() {
      return this.$http({
        url: this.chooseTag.id != null ? "/template/getAllTemplatesByTag/" + this.chooseTag.id : "/template/getAllTemplates",
        method: "get",
      }).then((response) => {
        this.allTemplates = response.data.data;
        this.allTemplates.forEach((template) => {
          if (template.action) {
            template.action = this.setActionStyle(template.action);
          }
          if (template.date != null) {
            template.dateTypeStr = template.dateType.toString();
          } else {
            template.dateTypeStr = "";
          }
        });
        console.log(this.allTemplates);
      }).catch((error) => {
        console.log(error);
      });
    },

    doRemoveMoneyItem(item) {
      console.log(item)
      this.childMoneyItem.splice(this.childMoneyItem.indexOf(item), 1)
    },
    doAddNewItemMoney() {
      var childItem = {
        index: Date.now(),
        note: ""
      }
      this.childMoneyItem.push(childItem)
      this.chooseDate = this.formatDate(new Date())
      console.log(this.childMoneyItem)
      
      // 添加新项后调整文本框高度
      this.$nextTick(() => {
        this.resizeChildTextarea()
      })
    },
    doSubNewItemMoney() {
      var childItem = {
        index: Date.now(),
        note: "退款",
        money: "-"
      }
      this.childMoneyItem.push(childItem)
      this.chooseDate = this.formatDate(new Date())
      console.log(this.childMoneyItem)
    },

    doGetCurrentFlow() {
      console.log('FlowAdd - doGetCurrentFlow 开始执行，flowId:', this.$route.query.flowId);
      this.$http({
        url: "/flow/getFlow/" + this.$route.query.flowId,
        method: "get"
      }).then(response => {
        const flow = response.data.data;
        console.log('FlowAdd - 加载账单数据:', flow);
        console.log('FlowAdd - 账单分类信息:', flow.type);
        this.money = flow.money;
        this.chooseAccount = flow.account;
        this.chooseAccount.name = flow.account.aname;
        
        // 确保所有actions已加载后再设置chooseAction
        console.log('FlowAdd - 设置chooseAction前，allActions状态:', this.allActions && this.allActions.length > 0 ? '已加载' : '未加载');
        
        if (this.allActions && this.allActions.length > 0) {
          // 从已加载的actions中找到匹配的action
          console.log('FlowAdd - 从已加载的actions中查找匹配项，flow.action.id:', flow.action.id);
          const matchedAction = this.allActions.find(action => action.id === flow.action.id);
          
          if (matchedAction) {
            console.log('FlowAdd - 找到匹配的action:', matchedAction);
            this.chooseAction = this.setActionStyle(matchedAction);
          } else {
            console.log('FlowAdd - 未找到匹配的action，使用flow中的action:', flow.action);
            this.chooseAction = this.setActionStyle(flow.action);
          }
        } else {
          // 如果actions还未加载，直接使用flow中的action
          console.log('FlowAdd - actions未加载，直接使用flow中的action:', flow.action);
          this.chooseAction = this.setActionStyle(flow.action);
        }
        
        console.log('FlowAdd - chooseAction设置完成:', this.chooseAction);
        console.log('FlowAdd - 即将调用doGetTypes()，chooseAction.id:', this.chooseAction.id);
        
        // 先加载分类列表，然后设置 chooseType
        this.doGetTypes().then(() => {
          console.log('FlowAdd - doGetTypes 完成，allTypes数据:', this.allTypes);
            
          this.selectedParentType = null;
          this.fatherType = null;
          this.chooseType = {};
          
          if (flow.type && flow.type.tname) {
            console.log('FlowAdd - 开始处理账单分类，flow.type:', flow.type);
            const parentId = flow.type.parent;
            const currentTypeId = flow.type.id;
            console.log('FlowAdd - 分类父ID:', parentId);
            console.log('FlowAdd - 当前分类ID:', currentTypeId);
            
            // 统一处理所有类型的分类选择
            if (parentId !== -1) {
              // 有父分类和子分类结构
              console.log('FlowAdd - 子类模式，通过parentId查找父分类');
              
              // 首先在父分类列表中查找对应的父分类
              let parentType = this.allTypes.find(t => t.id === parentId);
              console.log('FlowAdd - 直接通过ID找到父分类:', parentType);
              
              // 如果直接通过ID找不到，遍历所有父分类的子分类来查找
              if (!parentType) {
                console.log('FlowAdd - 通过ID未找到父分类，尝试遍历查找');
                for (const type of this.allTypes) {
                  if (type.childrenTypes) {
                    const foundChild = type.childrenTypes.find(c => c.id === currentTypeId);
                    if (foundChild) {
                      parentType = type;
                      console.log('FlowAdd - 通过子分类遍历找到父分类:', parentType);
                      break;
                    }
                  }
                }
              }
              
              if (parentType) {
                console.log('FlowAdd - 查找子分类，当前分类ID:', currentTypeId);
                // 查找对应的子分类
                const childType = parentType.childrenTypes.find(c => c.id === currentTypeId);
                console.log('FlowAdd - 找到子分类:', childType);
                
                if (childType) {
                  console.log('FlowAdd - 找到完整分类路径，开始设置状态');
                  // 分步骤设置状态，确保UI能正确更新
                  // 1. 先设置父分类
                  this.selectedParentType = parentType;
                  this.fatherType = parentType;
                  
                  console.log('FlowAdd - 设置父分类后:', {
                    selectedParentType: this.selectedParentType,
                    fatherType: this.fatherType
                  });
                  
                  // 2. 在下一个tick中设置子分类，确保父分类已经渲染完成
                  this.$nextTick(() => {
                    console.log('FlowAdd - 在nextTick中设置子分类:', childType);
                    // 构造完整的子分类对象
                    this.chooseType = {
                      ...childType,
                      id: childType.id,
                      tname: flow.type.tname,
                      displayName: flow.type.tname
                    };
                    
                    console.log('FlowAdd - 子分类设置完成:', this.chooseType);
                    // 强制更新以确保高亮生效
                    this.$forceUpdate();
                    console.log('FlowAdd - 强制UI更新完成');
                  });
                  return; // 提前返回，避免后续代码执行
                }
              }
            } else {
              // 只有父分类
              console.log('FlowAdd - 只有父分类，通过ID查找:', flow.type.id);
              // 优先通过ID查找
              let matchedType = this.allTypes.find(t => t.id === flow.type.id);
              console.log('FlowAdd - 通过ID找到父分类:', matchedType);
              
              // 如果通过ID没找到，再尝试通过tname查找
              if (!matchedType) {
                console.log('FlowAdd - 通过ID未找到，尝试通过tname查找:', flow.type.tname);
                matchedType = this.allTypes.find(t => t.tname === flow.type.tname);
                console.log('FlowAdd - 通过tname找到父分类:', matchedType);
              }
              
              if (matchedType) {
                // 分步骤设置状态
                this.selectedParentType = matchedType;
                this.fatherType = matchedType;
                
                console.log('FlowAdd - 设置父分类后:', {
                  selectedParentType: this.selectedParentType,
                  fatherType: this.fatherType
                });
                
                this.$nextTick(() => {
                  console.log('FlowAdd - 在nextTick中设置chooseType为父分类:', matchedType);
                  this.chooseType = matchedType;
                  console.log('FlowAdd - chooseType设置完成:', this.chooseType);
                  this.$forceUpdate();
                  console.log('FlowAdd - 强制UI更新完成');
                });
                return; // 提前返回，避免后续代码执行
              }
            }
          }
          
          // 如果分类设置未成功完成或没有分类信息，则使用备选方案
          console.log('FlowAdd - 进入备选方案逻辑');
          
          // 方案1：如果有flow.type但未在allTypes中找到匹配项，尝试直接使用
          if (flow.type && flow.type.tname) {
            console.log('FlowAdd - 使用备选方案1：直接使用flow.type');
            this.$nextTick(() => {
              console.log('FlowAdd - 设置直接使用flow.type:', flow.type);
              this.chooseType = flow.type;
              this.fatherType = flow.type;
              this.selectedParentType = flow.type;
              console.log('FlowAdd - 备选方案1设置完成:', {
                chooseType: this.chooseType,
                fatherType: this.fatherType,
                selectedParentType: this.selectedParentType
              });
              this.$forceUpdate();
              console.log('FlowAdd - 备选方案1强制UI更新完成');
            });
          } 
          // 方案2：使用默认选择
          else if (this.allTypes && this.allTypes.length > 0) {
            console.log('FlowAdd - 使用备选方案2：选择第一个分类');
            const firstType = this.allTypes[0];
            console.log('FlowAdd - 选择第一个分类:', firstType);
            // 直接调用已有的选择方法，这样可以确保所有相关状态都被正确设置
            this.onSelectParentType(firstType);
            console.log('FlowAdd - 备选方案2选择方法调用完成');
          }
          
          // 确保UI更新
          this.$nextTick(() => {
            console.log('FlowAdd - 最终强制UI更新');
            this.$forceUpdate();
            console.log('FlowAdd - 最终强制UI更新完成');
          });
          
          // 延迟再次更新，确保所有异步操作完成
          setTimeout(() => {
            console.log('FlowAdd - 延迟后最终状态检查:', {
              chooseType: this.chooseType,
              fatherType: this.fatherType,
              selectedParentType: this.selectedParentType,
              allTypes: this.allTypes && this.allTypes.length > 0 ? this.allTypes.length + '个分类' : '无分类'
            });
            this.$forceUpdate();
            console.log('FlowAdd - 延迟强制UI更新完成');
          }, 500);
        });
        
        this.isCollect = flow.collect
        this.note = flow.note
        if (flow.action.handle == "2") {
          this.chooseToAccount = flow.accountTo
          this.chooseToAccount.name = flow.accountTo.aname
        }
        this.chooseDate = flow.fdate
        
        // 调整备注文本框高度
        this.$nextTick(() => {
          this.resizeTextarea();
        });
      })
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

    onSubmitHandle() {
      var moneyInt = parseFloat(this.money)
      this.submitNote = this.note

      if (this.childMoneyItem.length > 0 && !this.submitNote.includes("(￥")) {
        this.submitNote = this.submitNote + "(￥" + this.money + ")"
      }
      this.childMoneyItem.forEach(chileMoney => {
        if (chileMoney.money != null) {
          moneyInt = (moneyInt * 100 + parseFloat(chileMoney.money) * 100) / 100;
          this.submitNote = this.submitNote + "\n" + chileMoney.note + "(￥" + chileMoney.money + ")"
        }
      })
      if (this.childMoneyItem.length > 0) {
        this.submitMoney = moneyInt.toFixed(2) + ""
      } else {
        this.submitMoney = this.money
      }
      showConfirmDialog({
        title: '请确认账单',
        message:
            '总金额: ￥' + this.submitMoney + '\n' +
            "备注：" + this.submitNote,
        confirmButtonText: "确认无误"
      })
          .then(() => {
            this.doSubmitRequest()
          })
          .catch(() => {
            // on cancel
          });
    },

    onSubmitBtnClick() {
      if (!this.doVertify()) {
        return
      }
      if (this.chooseType.action != null && this.chooseType.action.id != this.chooseAction.id) {
        return showConfirmDialog({
          title: '提示',
          message:
              '当前分类存在修改收支类型的情况\n是否继续提交？',
          confirmButtonText: "继续提交"
        })
            .then(() => {
              this.onSubmitHandle()
            })
            .catch(() => {
            });
      } else {
        this.onSubmitHandle()
      }
    },

    doSubmitRequest() {
      const api = this.$route.query.flowId == null ? "/flow/addFlow" : "/flow/updateFlow/" + this.$route.query.flowId
      const method = this.$route.query.flowId == null ? "post" : "put"
      this.$http({
        url: api,
        method: method,
        data: {
          money: this.submitMoney,
          fDate: this.chooseDate,
          actionId: parseInt(this.chooseAction.id),
          accountId: parseInt(this.chooseAccount.id),
          accountToId: parseInt(this.chooseToAccount.id),
          typeId: parseInt(this.chooseType.id),
          collect: this.isCollect,
          note: this.submitNote
        }
      }).then(() => {
        //this.$router.go(-1)
        this.$router.push({path: "/flow"});
      })
    },

    doVertify() {
      if (this.money == "" || this.money == null) {
        showFailToast("请输入金额")
        return false;
      }
      if (this.chooseAction.id == null) {
        showFailToast("请选择收支")
        return false;
      }
      if (this.chooseAccount.id == null) {
        showFailToast("请选择账户")
        return false;
      }
      if (this.chooseAction.handle == 2) {
        if (this.chooseToAccount.id == null) {
          showFailToast("请选择目标账户")
          return false;
        }
      }
      if (this.chooseType.id == "" || this.chooseType.id == null) {
        showFailToast("请选择分类")
        return false;
      }
      if (this.chooseDate == "") {
        showFailToast("请选择账单日期")
        return false
      }
      return true
    },

    onClickLeft() {
      //this.$router.go(-1);
      this.$router.push({path: "/flow"});
    },

    onCalanderClick() {
      this.calanderShow = true;
    },

    onTypeClick() {
      if (this.chooseAction.id == null) {
        showFailToast("请先选择收支")
        return;
      }
      // 清空分类选择
      this.selectedParentType = null;
      this.chooseType = {};
    },

    onChooseType(type) {
      this.chooseType = type;
      console.log('选择分类:', type);
    },

    onAccountClick(popupStyle) {
      this.actionShow = true;
      this.popupTitle = "选择账户";
      this.popupStyle = popupStyle;
    },

    doGetActions() {
      return this.$http({
        url: "/action/getAction",
        method: "get"
      }).then((response) => {
        this.allActions = response.data.data;
        this.allActions.forEach((action) => {
          this.setActionStyle(action)
        });
        console.log(this.allActions);
      });
    },

    doGetAccounts() {
      this.$http({
        url: "/account/getAccount",
        method: "get",
      })
          .then((response) => {
            const baseData = response.data.data;
            baseData.forEach((item) => {
              item.money = "￥" + item.money;
              item.exemptMoney = "￥" + item.exemptMoney;
            });
            this.allAccounts = baseData;
            
            // 默认选择江西银行
            if (!this.chooseAccount.id) {
              const defaultAccount = baseData.find(account => account.name === '江西银行');
              if (defaultAccount) {
                this.chooseAccount = defaultAccount;
              }
            }
            
            console.log(this.allAccounts);
          })
          .catch((error) => {
            console.log(error);
          });
    },
    
    // 设置分类高亮显示
    setupCategoryHighlight(type) {
      if (type && type.tname) {
        const parts = type.tname.split('/');
        
        // 统一处理所有类型的分类选择
        if (parts.length > 1) {
          // 有父分类和子分类结构
          const parentTypeName = parts[0];
          const childTypeName = parts[1];
          
          // 查找对应的父分类
          const parentType = this.allTypes.find(t => t.tname === parentTypeName);
          
          if (parentType && parentType.childrenTypes) {
            // 正确设置父分类和子分类
            this.selectedParentType = parentType;
            this.fatherType = parentType; // 确保fatherType指向父分类
            
            // 查找对应的子分类
            const childType = parentType.childrenTypes.find(c => c.tname === childTypeName);
            if (childType) {
              // 构造完整的子分类对象
              this.chooseType = {
                ...childType,
                id: childType.id,
                tname: type.tname,
                displayName: type.tname
              };
            }
          }
        } else {
          // 只有父分类
          const matchedType = this.allTypes.find(t => t.tname === type.tname);
          if (matchedType) {
            // 设置为当前选中的父分类
            this.selectedParentType = matchedType;
            this.fatherType = matchedType; // 确保fatherType指向父分类
            this.chooseType = matchedType;
          }
        }
        
        // 强制更新UI以显示正确的高亮状态
        this.$nextTick(() => {
          this.$forceUpdate();
        });
      }
    },

    doGetTypes() {
      return this.$http({
        url: "/type/getTypeByActionId/" + this.chooseAction.id,
        method: "get",
      })
          .then((response) => {
            this.allTypes = response.data.data;
            console.log(this.allTypes)
          })
    },

    doGetAction(id) {
      return this.$http({
        url: "/action/getAction/" + id,
        method: "get",
      })
          .then((response) => {
            //this.chooseAction = response.data.data;
            this.onChooseAction(response.data.data)
            console.log(this.allTypes)
          })
    },

    onChooseAction(action) {
      this.setActionStyle(action);
      if (action === this.chooseAction) {
        return
      }
      this.chooseAction = action;
      this.chooseToAccount = {};
      this.chooseType = {}
      this.selectedParentType = null; // 重置选中的大类
      this.doGetTypes().then(() => {
        console.log('FlowAdd - doGetTypes完成，开始选择默认分类');
        // 默认选择第一个大类及对应第一个小类
        this.$nextTick(() => {
          console.log('FlowAdd - 第一级nextTick，allTypes数量:', this.allTypes ? this.allTypes.length : 0);
          if (this.allTypes && this.allTypes.length > 0) {
            const firstType = this.allTypes[0];
            console.log('FlowAdd - 选择第一个分类:', firstType);
            this.onSelectParentType(firstType);
          }
          
          // 增强UI更新机制
          this.$nextTick(() => {
            console.log('FlowAdd - 动作选择后第二级UI更新');
            this.$forceUpdate();
            
            this.$nextTick(() => {
              console.log('FlowAdd - 动作选择后第三级UI更新');
              this.$forceUpdate();
              
              // 延迟再次更新，确保所有异步操作完成
              setTimeout(() => {
                console.log('FlowAdd - 动作选择后延迟更新');
                this.$forceUpdate();
              }, 300);
            });
          });
        });
      });
    },

    onChooseAccount(account, popupStyle) {
      this.actionShow = false;
      if (popupStyle == 1) {
        this.chooseAccount = account
      } else {
        this.chooseToAccount = account
      }
    },

    formatDate(date) {
      return `${date.getFullYear()}-${((date.getMonth() + 1) + "").padStart(2, '0')}-${(date.getDate() + "").padStart(2, '0')}`;
    },

    onChooseCalendar(date) {
      this.calanderShow = false;
      this.chooseDate = this.formatDate(date);
      console.log(this.chooseDate)
    },

    // 判断分类是否选中（统一处理所有类型）
    isTypeSelected(type) {
      // 如果没有选择分类，直接返回false
      if (!this.chooseType || !type) {
        return false;
      }
      
      // 如果是大类（有子类）
      if (type.childrenTypes && type.childrenTypes.length > 0) {
        // 只有当它被设置为selectedParentType时才高亮（表明当前正在查看该大类的子类）
        return this.selectedParentType && this.selectedParentType.id === type.id;
      }
      
      // 如果是子类，检查是否选中
      // 优先通过id匹配
      if (this.chooseType.id === type.id) {
        return true;
      }
      
      // 检查路径匹配 - 当chooseType的tname包含父类名/子类名格式时
      if (this.selectedParentType) {
        const expectedPath = `${this.selectedParentType.tname}/${type.tname}`;
        return this.chooseType.tname === expectedPath || this.chooseType.displayName === expectedPath;
      }
      
      // 简单的tname完全匹配
      return this.chooseType.tname === type.tname;
    },

    // 处理支出大类选择
    onSelectParentType(type) {
      console.log('选择支出大类:', type);
      // 设置选中的大类
      this.selectedParentType = type;
      this.fatherType = type; // 设置fatherType
      
      // 如果没有子分类，直接选中该大类
      if (!type.childrenTypes || type.childrenTypes.length === 0) {
        this.chooseType = type;
      } else {
        // 有子类时默认选择第一个子类
        const firstChildType = type.childrenTypes[0];
        // 构造完整的子分类路径并设置
        this.chooseType = {
          ...firstChildType,
          id: firstChildType.id,
          tname: `${type.tname}/${firstChildType.tname}`,
          displayName: `${type.tname}/${firstChildType.tname}`
        };
      }
      
      // 增强UI更新机制：使用多级nextTick确保DOM完全更新
      this.$nextTick(() => {
        console.log('FlowAdd - 父分类选择后第一级UI更新');
        this.$forceUpdate();
        
        this.$nextTick(() => {
          console.log('FlowAdd - 父分类选择后第二级UI更新');
          this.$forceUpdate();
          
          // 延迟再次更新，确保所有异步操作完成
          setTimeout(() => {
            console.log('FlowAdd - 父分类选择后延迟更新');
            this.$forceUpdate();
          }, 200);
        });
      });
    },
    
    // 处理支出子类选择
    onChooseChildType(childType) {
      
      // 设置父分类引用
      this.fatherType = this.selectedParentType;
      
      // 构造完整的分类路径
      this.chooseType = {
        ...childType,
        id: childType.id,
        tname: `${this.selectedParentType.tname}/${childType.tname}`,
        displayName: `${this.selectedParentType.tname}/${childType.tname}`
      };
      
      // 增强UI更新机制：使用多级nextTick确保DOM完全更新
      this.$nextTick(() => {
        console.log('FlowAdd - 子分类选择后第一级UI更新');
        this.$forceUpdate();
        
        this.$nextTick(() => {
          console.log('FlowAdd - 子分类选择后第二级UI更新');
          this.$forceUpdate();
        });
      });
    },
    


  }
};
</script>

<style scoped>
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px; /* 设置标签之间的间隔 */
  padding: 15px; /* 容器的内边距 */
}

.tag-item {
  margin-bottom: 5px; /* 增加标签下方的间隔，避免视觉上的拥挤 */
}

.template-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px; /* 减小内边距 */
}

.template-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  gap: 2px; /* 添加更紧凑的间距 */
}

.template-name {
  font-size: 13px; /* 稍微减小字体 */
  margin-bottom: 2px; /* 减小名称下方间距 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100px; /* 控制最大宽度，避免文字过长 */
}

.template-tag {
  display: block; /* 确保标签作为独立的行显示 */
}

.template-action {
  text-align: right; /* 右侧对齐详情按钮 */
}

/* 收支选择按钮样式 */
.action-selector {
  display: flex;
  justify-content: center;
  padding: 6px 15px;
  gap: 8px;
  background-color: #f7f8fa;
}

.action-button {
  flex: 1;
  padding: 5px 0;
  border-radius: 16px;
  text-align: center;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #f0f0f0;
  color: #999;
  border: 1px solid transparent;
}

/* 收入（绿色） */
.action-button.income.selected {
  background: linear-gradient(135deg, #07c160 0%, #00b894 100%);
  color: white;
  box-shadow: 0 3px 10px rgba(7, 193, 96, 0.3);
}

/* 支出（红色） */
.action-button.expense.selected {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  box-shadow: 0 3px 10px rgba(238, 90, 82, 0.3);
}

/* 转账（蓝色） */
.action-button.transfer.selected {
  background: linear-gradient(135deg, #1989fa 0%, #0f7ae5 100%);
  color: white;
  box-shadow: 0 3px 10px rgba(25, 137, 250, 0.3);
}

.action-button:hover {
  transform: translateY(-2px);
}

/* 账户选择器样式 */
.account-wrapper {
  background-color: #fff;
  border-bottom: 1px solid #ebedf0;
  padding-bottom: 4px;
}

.account-label {
  padding: 4px 15px 0;
  font-size: 12px;
  color: #969799;
}

.account-selector {
  display: flex;
  padding: 4px 15px;
  gap: 8px;
}

.account-item {
  flex: 1;
  padding: 4px 8px;
  border-radius: 16px;
  text-align: center;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #f7f8fa;
  color: #666;
  border: 1px solid #ebedf0;
}

.account-item.selected {
  background-color: #1989fa;
  color: white;
  border-color: #1989fa;
}

.account-item.account-more {
  background-color: #fff;
  border: 1px dashed #ccc;
  color: #999;
}

.account-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 目标账户特殊样式 */
.to-account-wrapper {
  padding-bottom: 4px;
}

.to-account-selector {
  padding: 4px 15px;
}

.to-account-item {
  padding: 4px 8px;
  font-size: 12px;
}

.to-account-item.selected {
  background: linear-gradient(135deg, #07c160 0%, #00b894 100%);
  color: white;
  border-color: #07c160;
}

/* 分类选择器样式 */
.type-wrapper {
  background-color: #fff;
  border-bottom: 1px solid #ebedf0;
  padding-bottom: 4px;
}

.type-label {
  padding: 4px 15px 0;
  font-size: 12px;
  color: #969799;
}

.type-selector {
  display: flex;
  padding: 4px 15px;
  gap: 8px;
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  padding: 4px 15px;
  gap: 8px;
}

/* 独立一行显示大类，每行3个 */
.type-grid.parent-types {
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

/* 独立一行显示小类，每行4个 */
.type-grid.child-types {
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.type-item {
  flex: 1;
  padding: 4px 8px;
  border-radius: 16px;
  text-align: center;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #f7f8fa;
  color: #666;
  border: 1px solid #ebedf0;
}

.type-item.selected {
  background-color: #ff9500;
  color: white;
  border-color: #ff9500;
}

.type-item.type-more {
  background-color: #fff;
  border: 1px dashed #ccc;
  color: #999;
}

.type-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.type-selected {
  padding: 4px 15px;
  font-size: 12px;
  line-height: 1.4;
}

.type-label-text {
  color: #969799;
}

.type-value {
  color: #ee0a24;
  font-weight: 500;
}

/* 账户选中显示 */
.account-selected {
  padding: 4px 15px;
  font-size: 12px;
  line-height: 1.4;
}

.account-label-text {
  color: #969799;
}

.account-value {
  color: #1989fa;
  font-weight: 500;
}

/* 目标账户已选显示（绿色） */
.to-account-value {
  color: #07c160;
  font-weight: 500;
}

/* 账单信息样式 */
.bill-info-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 15px;
  border-bottom: 1px solid #ebedf0;
  min-height: 36px;
  height: auto;
}

.bill-info-wrapper:last-child {
  border-bottom: none;
}

.bill-info-label {
  font-size: 12px;
  color: #969799;
  flex-shrink: 0;
}

.bill-info-value {
  font-size: 12px;
  color: #323233;
  text-align: right;
  flex: 1;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: auto;
  min-height: auto;
}

.bill-info-value .placeholder {
  color: #c8c9cc;
}

.note-wrapper {
  align-items: flex-start;
  min-height: auto;
  height: auto;
}

.note-input {
  flex-direction: column;
  align-items: flex-end;
  height: auto;
  min-height: auto;
}

.note-input textarea {
  width: 100%;
  border: none;
  outline: none;
  resize: none;
  font-size: 12px;
  color: #323233;
  text-align: right;
  background: transparent;
  padding: 0;
  margin-bottom: 4px;
  min-height: 20px;
  overflow-y: hidden;
}

.note-input textarea::placeholder {
  color: #c8c9cc;
}

.note-input .word-limit {
  font-size: 11px;
  color: #969799;
}

.child-input {
  width: 100%;
  border: none;
  outline: none;
  font-size: 12px;
  color: #323233;
  text-align: right;
  background: transparent;
  padding: 0;
}

.child-input::placeholder {
  color: #c8c9cc;
}

.child-textarea {
  width: 100%;
  border: none;
  outline: none;
  resize: none;
  font-size: 12px;
  color: #323233;
  text-align: right;
  background: transparent;
  padding: 0;
  margin-bottom: 4px;
  min-height: 20px;
  overflow-y: hidden;
}

.child-textarea::placeholder {
  color: #c8c9cc;
}

</style>
