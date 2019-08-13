<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/m_common.tld" prefix="mc" %>
<%@ taglib uri="/WEB-INF/m_manage.tld" prefix="mm" %>
<%@ taglib uri="/WEB-INF/dictionary.tld" prefix="dict" %>
<page>
	<h2 style="padding:0 5px 10px;"><c:if test="${map.openMode=='PAGE' }"><a @click="back(false)" ><i class="iconfont" style="font-size:23px;font-weight:100;">&#xe718;</i>&nbsp;&nbsp;</a></c:if>${map.formTitle }</h2>
	<i-form>
	<card :style="{marginBottom:'15px',paddingBottom:'0px'}">
	<c:forEach var="row" items="${map.formRows }" varStatus="index">
		<c:if test="${row.line}"><div style="margin-top:-18px;"><divider orientation="left" :style="{color:'#2d8cf0',margin:'${row.title!=''?'10':'15' }px 0'}">${row.title }</divider></div></c:if>
	<row :gutter="10" :style="{marginRight:'${row.marginRight }px',marginBottom:'${index.last?-18:0 }px',minWidth:'${row.minWidth }px'}">
		<c:forEach var="field" items="${row.fields}">
		<c:if test="${field.type=='HIDDEN' }">
			<input type="hidden"/> 
		</c:if>
		<c:if test="${field.type!='HIDDEN' }">
		<i-col span="${field.span }" v-if="!('${field.nullHidden }'&&(null==fields['${field.nullHidden }']||''==fields['${field.nullHidden }']))">
		<c:if test="${field.message!='' }"><tooltip max-width="350" placement="top"><div slot="content">${field.message }</div></c:if>
			<form-item label="${field.title }" ${field.required?'required':'' } style="margin-bottom:18px;" :label-width="${field.titleWidth }">
			<c:if test="${field.type=='TEXT' }">
				<i-input v-model="fields['${field.field }']" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" ${field.disabled?'disabled':'' }></i-input>
			</c:if>
			<c:if test="${field.type=='TEXTAREA' }">
				<i-input v-model="fields['${field.field }']" type="textarea" :rows="${field.rows }" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" ${field.disabled?'disabled':'' }></i-input>
			</c:if>
			<c:if test="${field.type=='PASSWORD' }">
				<i-input v-model="fields['${field.field }']" type="password" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" ${field.disabled?'disabled':'' }></i-input>
			</c:if>
			<c:if test="${field.type=='INT'||field.type=='DOUBLE'}">
				<input-number v-model="fields['${field.field }']" :style="{width:'100%'}" placeholder="${field.hint }" ${field.disabled?'disabled':'' } @on-blur="parseNumber('${field.field }',${field.decimalCount });doClearField('${field.field }');"
					<c:if test="${!empty field.numberUp }">:max="${field.numberUp}"</c:if> <c:if test="${!empty field.numberDown }">:min="${field.numberDown}"</c:if>></input-number>
			</c:if>
			<c:if test="${field.type=='DATE'||field.type=='DATETIME' }">
			<date-picker type="${field.type=='DATE'?'date':'datetime' }" :style="{width:'100%'}" v-model="fields['${field.field }']" format="${item.dateFormat }" placeholder="${field.hint }" :transfer="true" @on-change="doClearField('${field.field }');" ${field.disabled?'disabled':'' }></date-picker>
			</c:if>
			<c:if test="${field.type=='SELECT' }">
			<i-select ref="${field.field }" v-model="fields['${field.field }']" :filterable="true" :clearable="true" :transfer="true" @on-change="doClearField('${field.field }');" ${field.disabled?'disabled':'' }>
				<i-option v-for="item in selectDatas['${field.field }']" :value="item.value" :key="item.value">{{ item.label }}</i-option>
			</i-select>
			</c:if>
			<c:if test="${field.type=='RADIO' }">
			<radio-group v-model="fields['${field.field }']" @on-change="doClearField('${field.field }');">
				<radio v-for="item in selectDatas['${field.field }']" :label="item.value" ${field.disabled?'disabled':'' }><span>{{item.label}}&nbsp;</span></radio>
			</radio-group>
			</c:if>
			<c:if test="${field.type=='CHECKBOX' }">
			<checkbox-group v-model="fields['${field.field }']" @on-change="doClearField('${field.field }');">
				<checkbox v-for="item in selectDatas['${field.field }']" :label="item.value" ${field.disabled?'disabled':'' }><span>{{item.label}}&nbsp;</span></checkbox>
			</checkbox-group>
			</c:if>
			<c:if test="${field.type=='COLOR' }">
				<color-picker v-model="fields['${field.field }']" alpha recommend ${field.disabled?'disabled':'' } :transfer="true" @on-change="doClearField('${field.field }');" ${field.disabled?'disabled':'' }></color-picker>
			</c:if>
			<c:if test="${field.type=='IMAGE' }">
				<span style="display:inline-block;">
				<c:if test="${!field.disabled}"><i-button type="info" @click="openImageModal('${field.field}','${field.imageType }',${field.thumWidth },${field.thumRatio })">选择图片</i-button></c:if>
				<div class="image_input" style="margin-left:0;width:${(empty field.height?32:field.height)*field.thumRatio }px;height:${empty field.height?'':field.height }px;" 
					@click="viewImage('${field.field}');">
					<img id="image_${key}_${field.field}" border="0" alt="" src="${mm:getThumPath(mc:getInAttribute(map.action,field.field)) }" />
				</div>
				</span>
			</c:if>
			<c:if test="${field.type=='FILE' }">
				<c:if test="${!field.disabled}">
					<upload :on-success="fileUploadSuccess" :on-preview="fileUploadPrewiew" :show-upload-list="false" 
						action="<%=request.getContextPath() %>/action/manageFileInfo/upload?field=${field.field}&type=${field.fileType}&path=${field.filePath}">
						<i-button type="primary"><i class="iconfont">&#xe71d;</i>&nbsp;上传文件&nbsp;</i-button>&nbsp;
						<span v-html="fileName['${field.field}']"></span>
					</upload>
				</c:if>
				<c:if test="${field.disabled}">
					<span v-html="fileName['${field.field}']"></span>
				</c:if>
			</c:if>
			<c:if test="${field.type=='EDITER' }">
				<vue-html5-editor :content="fields['${field.field }']" :fieldname="'${field.field }'" @change="updateEditerData" :height="${field.height<300?300:field.height }"></vue-html5-editor>
			</c:if>
			<c:if test="${field.type=='MAP' }">
				<i-input v-model="fields['${field.field }']" icon="ios-pin-outline" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" @on-click="openMapModal('${field.field }',${field.disabled?false:true })" ${field.disabled?'disabled':'' } readonly ></i-input>
			</c:if>
			<c:if test="${field.type=='BUTTON' }">
				<c:forEach var="btn" items="${field.buttons}">
					<i-button type="${btn.style}" @click="submitHandler"><i class="iconfont">${btn.icon }</i>&nbsp;<span class="n-btn_title">${btn.title}</span>&nbsp;</i-button>
				</c:forEach>
			</c:if>
			</form-item>
		<c:if test="${field.message!='' }"></tooltip></c:if>
		</i-col>
		</c:if>
		</c:forEach>
		<c:forEach var="other" items="${row.others}">
			<div id="other_${other.title}_${key}" style="display:none;margin:0 0 10px 0;">
				<div id="other_${other.title}_${key}_content"></div>
			</div>
		</c:forEach>
	</row>
	</c:forEach>
	</card>
	<form-item label=" " style="margin-bottom:10px;width:100%;${map.openMode=='MODAL'?'text-align:center;':'' }" :label-width="${map.openMode=='MODAL'?'0':'100' }">
		<c:forEach var="btn" items="${map.formButtons}">
			<i-button type="${btn.style}" @click="submitHandler"><i class="iconfont">${btn.icon }</i>&nbsp;<span class="n-btn_title">${btn.title}</span>&nbsp;</i-button>
		</c:forEach>
		<c:if test="${!empty map.openKey}">
			&nbsp;<i-button @click="back(false)">返回</i-button>
		</c:if>
	</form-item>
	</i-form>
	<c:forEach var="other" items="${map.others}">
	<div id="other_${other.title}_${key}" style="display:none;margin:0 0 10px 0;">
		<divider orientation="left" :style="{margin:'5px 0 5px',color:'#2d8cf0'}">
			<c:if test="${map.openMode=='PAGE' }"><a @click="back(false)" ><i class="iconfont" style="font-size:20px;font-weight:100;">&#xe718;</i>&nbsp;&nbsp;</a></c:if>
			${other.title }
		</divider>
		<div id="other_${other.title}_${key}_content"></div>
	</div>
	</c:forEach>
	<modal v-model="showModal" class="table_modal" :width="modalWidth" :mask-closable="false" @on-cancel="handlerResult(backEvent,'MODAL',false)">
		<div id="_table_modal_${key }"></div>
		<div slot="footer">
		</div>
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
				fields1:{'model.color':''},
				backEvent:'',
				backSuccess:false,
				showModal:false,
				modalWidth:0,
				fields:{
					<c:forEach var="row" items="${map.formRows }">
						<c:forEach var="field" items="${row.fields}">
						<c:if test="${field.type=='SELECT'||field.type=='CHECKBOX'||field.type=='RADIO'}">
							<c:if test="${field.type!='CHECKBOX'}">'${field.field}':""+${mc:getInAttribute(map.action,field.field)},</c:if>
							<c:if test="${field.type=='CHECKBOX'}">'${field.field}':[${mc:getInAttributeByArray(map.action,field.field)}],</c:if>
						</c:if>
						<c:if test="${!(field.type=='SELECT'||field.type=='CHECKBOX'||field.type=='RADIO')}">
							'${field.field}':${mc:getInAttribute(map.action,field.field)},
						</c:if>
						</c:forEach>
					</c:forEach>
				},
				initFields:{"no":"no"},
				buttons:{},
				others:{},
				selectMethod:{},
				selectDatas:{},
				clearField:{},
				requiredField:{},
				fileName:{
					<c:forEach var="row" items="${map.formRows }">
						<c:forEach var="field" items="${row.fields}">
						<c:if test="${field.type=='FILE'}">
							'${field.field}':"${mm:getFileName(mc:getInAttribute(map.action,field.field))}",
						</c:if>
						</c:forEach>
					</c:forEach>
				},
			};
		},
		mounted:function(){
			<c:forEach var="row" items="${map.formRows }">
				<c:forEach var="field" items="${row.fields}">
				this.requiredField['${field.field}']=${field.required};
				this.clearField['${field.field}']='${field.clearField}';
				<c:if test="${field.type=='SELECT'||field.type=='CHECKBOX'||field.type=='RADIO'}">
					this.$set(this.selectDatas,"${field.field}",[]);
					<c:forEach var="op" items="${field.selectData}">this.selectDatas["${field.field}"].push({value:"${op[0] }",label:"${op[1] }"});</c:forEach>
					<c:if test="${!empty field.selectParam}">this.selectMethod["${field.field}"]=${field.selectParam};</c:if>
					this.initFields['${field.field}']=this.fields['${field.field}'];
				</c:if>
				<c:if test="${field.type=='BUTTON'}">
					<c:forEach var="btn" items="${field.buttons}">
					this.buttons['${btn.title}']=${btn.param};
					</c:forEach>
				</c:if>
				</c:forEach>
				<c:forEach var="other" items="${row.others}">
				this.others["other_${other.title}"]=${other.param};
				</c:forEach>
			</c:forEach>
			<c:forEach var="btn" items="${map.formButtons}">
			this.buttons['${btn.title}']=${btn.param};
			</c:forEach>
			<c:forEach var="other" items="${map.others}">
			this.others["other_${other.title}"]=${other.param};
			</c:forEach>
			this.refreshOthers();
			this.initSelectMethod();
		},
		methods:$.vueFormEditMethods
	};
})();
</script>