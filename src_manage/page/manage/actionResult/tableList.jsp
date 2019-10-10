<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<page>
	<transition name="slide-fade-right">
	<div name="_table_list" v-show="showList">
		<button-group style="margin-bottom:10px;" >
		<c:if test="${map.openMode=='PAGE' }">
			<i-button type="default" @click="back(false)"><i class="iconfont">&#xe718;</i>&nbsp;返回&nbsp;</i-button>
		</c:if>
		<c:if test="${!map.hiddenQueryList }">
			<i-button type="default" @click="searchPanel=true;"><i class="iconfont">&#xe6ac;</i>&nbsp;筛选&nbsp;</i-button>
			<modal width="480" v-model="searchPanel" >
				<div slot="header" style="font-size:14px;line-height:20px;">筛选条件</div>
				<i-form :model="param" :label-width="130" inline>
				<c:if test="${!empty map.searchHint }">
					<form-item label="搜索:" style="margin-bottom:10px;">
						<i-input v-model="param['searchText']" placeholder="${map.searchHint }" style="width:260px;"></i-input>
					</form-item>
					<divider :style="{margin:'3px 0 11px 0'}"></divider>
				</c:if>
				<c:forEach var="item" items="${map.tableQueryList }">
				<c:if test="${item.type=='HIDDEN' }">
					<input type="hidden" v-model="param['params[${item.field }]']" />
				</c:if>
				<c:if test="${item.type!='HIDDEN' }">
				<div>
					<form-item label="${item.name }:" style="margin-bottom:10px;">
					<c:if test="${item.type=='TEXT' }">
						<i-input v-model="param['params[${item.field }]']" placeholder="${item.hint }" @on-blur="doClearField('${item.field }')" style="width:260px;"></i-input>
					</c:if>
					<c:if test="${item.type=='SELECT' }">
					<i-select ref="${item.field }" v-model="param['params[${item.field }]']" style="width:260px;" @on-change="doClearField('${item.field }');" :filterable="true" :transfer="true" :clearable="true">
						<i-option v-for="item in selectDatas['${item.field }']" :value="item.value" :key="item.value">{{ item.label }}</i-option>
					</i-select>
					</c:if>
					<c:if test="${item.type=='INT_RANGE' }">
					<input-number v-model="param['params[${item.field }down]']" :style="{width:'123px'}" @on-blur="doClearField('${item.field }')" placeholder="下限"></input-number> ~ 
					<input-number v-model="param['params[${item.field }up]']" :style="{width:'123px'}" @on-blur="doClearField('${item.field }')" placeholder="上限"></input-number>
					</c:if>
					<c:if test="${item.type=='DOUBLE_RANGE' }">
					<input-number v-model="param['params[${item.field }down]']" :style="{width:'123px'}" @on-blur="parseNumber('params[${item.field }down]',6);doClearField('${item.field }')" placeholder="下限"></input-number> ~ 
					<input-number v-model="param['params[${item.field }up]']" :style="{width:'123px'}" @on-blur="parseNumber('params[${item.field }up]',6);doClearField('${item.field }')" placeholder="上限"></input-number>
					</c:if>
					<c:if test="${item.type=='DATE_RANGE' }">
					<date-picker type="datetime" v-model="param['params[${item.field }down]']" format="${item.dateFormat }" @on-change="doClearField('${item.field }');" placeholder="下限" style="width:123px;" :transfer="true"></date-picker> ~ 
					<date-picker type="datetime" v-model="param['params[${item.field }up]']" format="${item.dateFormat }" @on-change="doClearField('${item.field }');" placeholder="上限" style="width:123px;" :transfer="true"></date-picker>
					</c:if>
					</form-item>
				</div>
				</c:if>
				</c:forEach>
				</i-form>
				<div slot="footer" style="text-align:center;">
					<i-button type="primary" @click="queryList" ><i class="iconfont">&#xe6ac;</i>&nbsp;查询&nbsp;</i-button>
				</div>
			</modal>
		</c:if>
			<i-button type="default" @click="query"><i class="iconfont">&#xe6aa;</i>&nbsp;刷新&nbsp;</i-button>
		</button-group>
		<button-group style="margin-left:10px;margin-bottom:10px;">
			<i-button v-for="item in tableButtons" :key="item.title" :type="item.style" @click="toolsHandler(item.param)"><i class="iconfont" v-html="item.icon"></i>&nbsp;<span>{{item.title}}</span>&nbsp;</i-button>
			<i-button v-for="item in tableDropButtons" :key="item.title" :type="item.style">
				<dropdown @on-click="toolsHandler(item.buttons[$event].param)" :transfer="true">
					<i class="iconfont" v-html="item.icon"></i>&nbsp;<span>{{item.title}}</span>&nbsp;
					<icon type="ios-arrow-down"></icon>
					<dropdown-menu slot="list">
						<dropdown-item v-for="(btn,index) in item.buttons" :name="index"><i class="iconfont" v-html="btn.icon"></i>&nbsp;<span>{{btn.title}}</span>&nbsp;</dropdown-item>
					</dropdown-menu>
				</dropdown>
			</i-button>
		</button-group>
		<div>
		<i-table ref="itable" :loading="tableLoading" :height="tableHeight" :columns="columns" :data="datas" size="small" :stripe="false" :border="true" :highlight-row="true" @on-sort-change="sortHandler" @on-selection-change="selectHandler"
			:show-summary="null!=countData" :summary-method="summaryMethod" :span-method="spanMethod"></i-table>
		</div>
		<page style="margin-top:10px;" size="small" :total="count" :current="param.pageNo" :page-size="param.pageNum" show-elevator show-total show-sizer transfer @on-change="changePageNo" @on-page-size-change="changePageNum"></page>
	</div>
	</transition>
	<transition name="slide-fade-right">
	<div v-show="showPage" >
		<div id="_table_page_${key }"></div>
	</div>
	</transition>
	<modal v-model="showModal" :footer-hide="true" :width="modalWidth" :mask-closable="false" @on-cancel="handlerResult(backEvent,'MODAL',false)">
		<div id="_table_modal_${key }"></div>
	</modal>
	<modal v-model="loadModal.show" :footer-hide="true" width="300" :closable="false">
		<div style="text-align:center"><i-progress :percent="loadModal.percent"></i-progress></div>
		<div style="text-align:center" v-html="loadModal.content"></div>
	</modal>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return{
				//key:'',
				//openKey:'',
				openMode:'',
				backEvent:'',
				backSuccess:false,
				searchPanel:false,
				showList:true,
				showPage:false,
				showModal:false,
				loadModal:{
					show:false,
					content:"",
					percent:0
				},
				tableLoading:true,
				modalWidth:0,
				param:[],
				tableHeight:${map.tableHeight},
				dataUrl:'${map.dataUrl}',
				rowspanIndex:${map.rowspanIndex},
				rowspanNum:${map.rowspanNum},
				datas:[],
				countData:null,
				selected:[],
				count:0,
				page:{no:1,size:10},
				tableButtons:${map.tableButtons},
				tableDropButtons:${map.tableDropButtons},
				columns:${map.tableCols},
				selectMethod:{},
				selectDatas:{},
				clearField:{}
			};
		},
		mounted:function(){
			var param={};
			param['pageNo']=1;
			param['pageNum']=10;
			var selectMethod={};
			var selectDatas={};
			var clearField={};
			<c:if test="${!empty map.searchHint }">
			param['searchText']="";
			</c:if>
			<c:forEach var="item" items="${map.tableQueryList }">
				clearField['${item.field}']='${item.clearField}';
				<c:if test="${item.type=='HIDDEN'||item.type=='TEXT'||item.type=='SELECT'}">
					param['params[${item.field }]']="${map.params[item.field]}";
				</c:if>
				<c:if test="${item.type=='SELECT'}">
					selectDatas["${item.field}"]=[];
					<c:forEach var="op" items="${item.selectData}">selectDatas["${item.field}"].push({value:"${op[0] }",label:"${op[1] }"});</c:forEach>
					<c:if test="${!empty item.selectParam}">selectMethod["${item.field}"]=${item.selectParam};</c:if>
				</c:if>
				<c:if test="${item.type=='INT_RANGE'||item.type=='DOUBLE_RANGE'||item.type=='DATE_RANGE'}">
					param['params[${item.field }down]']="${map.params[item.field.concat('down')]}";
					param['params[${item.field }up]']="${map.params[item.field.concat('up')]}";
				</c:if>
			</c:forEach>
			this.param=param;
			this.selectMethod=selectMethod;
			this.selectDatas=selectDatas;
			this.clearField=clearField;
			this.initSelectMethod();
			this.initSort();
			this.query();
		},
		methods:$.vueTableListMethods
	};
})();
</script>
