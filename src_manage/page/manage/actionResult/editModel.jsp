<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/m_common.tld" prefix="mc" %>
<%@ taglib uri="/WEB-INF/m_manage.tld" prefix="mm" %>
<%@ taglib uri="/WEB-INF/dictionary.tld" prefix="dict" %>
<page>
	<h3 style="padding:0 5px 10px;"><c:if test="${map.openMode=='PAGE' }"><a @click="back(false)" ><i class="iconfont" style="font-size:23px;font-weight:100;">&#xe718;</i>&nbsp;&nbsp;</a></c:if>${map.formTitle }</h3>
<i-form>
	<card :style="{marginBottom:'15px',paddingBottom:'0px'}">
	<c:set var="istab" value="${false}"></c:set>
	<c:forEach var="row" items="${map.formRows }" varStatus="index">
			<c:if test="${row.tabs}"><c:if test="${istab}"></tab-pane></c:if><c:if test="${!istab}"><c:set var="istab" value="${true}"></c:set><row><i-col span="24"><tabs :style="{marginTop:'-13px'}" :animated="false"></c:if><tab-pane label="${empty row.tabTitle?'无标题':row.tabTitle }"></c:if>
			<c:if test="${row.line}"><div style="margin-top:${row.tabs&&istab?'-5px':'-18px'};"><divider orientation="left" :style="{color:'#2d8cf0',margin:'${row.title!=''?'10':'15' }px 0'}">${row.title }</divider></div></c:if>
		<c:if test="${!empty row.alert}">
			<alert show-icon type="${row.alert.type }" style="margin-bottom:18px;">{{convertMessage(${row.alert.title })}}
				<c:if test="${!empty row.alert.icon}"><icon type="${row.alert.icon}" slot="icon"></icon></c:if>
				<c:if test="${!empty row.alert.desc}"><div slot="desc">{{convertMessage(${row.alert.desc })}}</div></c:if>
			</alert>
		</c:if>
		<c:forEach var="vui" items="${row.vulist }"><div style="margin:0 0 18px 0;">${vui }</div></c:forEach>
		<row :gutter="16" :style="{marginRight:'${row.marginRight }px',marginBottom:'${index.last?-18:0 }px',minWidth:'${row.minWidth }px'}">
			<c:forEach var="field" items="${row.fields}">
			<c:if test="${field.type!='HIDDEN' }">
			<i-col span="${field.span }" v-if="(!('${field.nullHidden }'&&(null==fields['${field.nullHidden }']||''==fields['${field.nullHidden }'])))&&((!'${field.hiddenField }'||'${field.hiddenValues }'.indexOf(fields['${field.showField }'])<0)&&(!'${field.showField}'||'${field.showValues }'.indexOf(fields['${field.showField }'])>=0))">
			<c:if test="${field.message!='' }"><tooltip max-width="350" placement="top"><div slot="content">{{convertMessage(${field.message })}}</div></c:if>
				<form-item label="${field.title }" ${field.required?'required':'' } style="margin-bottom:18px;" :label-width="${field.titleWidth }">
				<c:if test="${field.type=='ALERT' }">
					<c:if test="${!empty field.alert}">
						<alert show-icon type="${field.alert.type }" style="margin:0;">{{convertMessage(${field.alert.title })}}
							<c:if test="${!empty field.alert.icon}"><icon type="${field.alert.icon}" slot="icon"></icon></c:if>
							<c:if test="${!empty field.alert.desc}"><div slot="desc">{{convertMessage(${field.alert.desc })}}</div></c:if>
						</alert>
					</c:if>
				</c:if><c:if test="${field.type=='TEXT' }">
					<i-input v-model="fields['${field.field }']" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" ${field.disabled?'disabled':'' }>
						<c:if test="${!empty field.suffix}"><span slot="append">${field.suffix}</span></c:if>
					</i-input>
				</c:if><c:if test="${field.type=='TEXTAREA' }">
					<i-input v-model="fields['${field.field }']" type="textarea" :rows="${field.rows }" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" ${field.disabled?'disabled':'' }>
						<c:if test="${!empty field.suffix}"><span slot="append">${field.suffix}</span></c:if>
					</i-input>
				</c:if><c:if test="${field.type=='TEXTAUTO' }">
					<auto-complete v-model="fields['${field.field }']" :data="selectLabels['${field.field }']" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" ${field.disabled?'disabled':'' }>
					</auto-complete>
				</c:if><c:if test="${field.type=='PASSWORD' }">
					<i-input v-model="fields['${field.field }']" type="password" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" ${field.disabled?'disabled':'' }>
						<c:if test="${!empty field.suffix}"><span slot="append">${field.suffix}</span></c:if>
					</i-input>
				</c:if><c:if test="${field.type=='INT'||field.type=='DOUBLE'}">
					<input-number v-model="fields['${field.field }']" :style="{width:'100%'}" placeholder="${field.hint }" ${field.disabled?'disabled':'' } @on-blur="parseNumber('${field.field }',${field.decimalCount });doClearField('${field.field }');"
						<c:if test="${!empty field.numberUp }">:max="${field.numberUp}"</c:if> <c:if test="${!empty field.numberDown }">:min="${field.numberDown}"</c:if>
						<c:if test="${!empty field.suffix}">:formatter="value=>`\${value}${field.suffix}`" :parser="value=>value.replace('${field.suffix}','')"</c:if>></input-number>
				</c:if><c:if test="${field.type=='DATE'||field.type=='DATETIME' }">
					<date-picker type="${field.type=='DATE'?'date':'datetime' }" :style="{width:'100%'}" v-model="fields['${field.field }']" format="${item.dateFormat }" placeholder="${field.hint }" :transfer="true" @on-change="doClearField('${field.field }');" ${field.disabled?'disabled':'' }></date-picker>
				</c:if><c:if test="${field.type=='SELECT' }">
				<i-select ref="${field.field }" v-model="fields['${field.field }']" :filterable="true" :clearable="true" :transfer="true" @on-change="doClearField('${field.field }');" placeholder="${field.hint }" ${field.disabled?'disabled':'' }>
					<i-option v-for="item in selectDatas['${field.field }']" :value="item.value" :key="item.value">{{ item.label }}</i-option>
				</i-select>
				</c:if><c:if test="${field.type=='SELECT_NODE' }">
				<cascader :data="selectDatas['${field.field }']" trigger="hover" :filterable="true" v-model="cascaders['${field.field }']" @on-change="doClearField('${field.field }',arguments);" placeholder="${field.hint }" ${field.disabled?'disabled':'' }></cascader>
				</c:if><c:if test="${field.type=='RADIO' }">
				<radio-group v-model="fields['${field.field }']" @on-change="doClearField('${field.field }');">
					<radio v-for="item in selectDatas['${field.field }']" :label="item.value" ${field.disabled?'disabled':'' }><span>{{item.label}}&nbsp;</span></radio>
				</radio-group>
				</c:if><c:if test="${field.type=='STEPS' }">
				<steps :current="getCurrentStep('${field.field }')">
					<step v-for="item in selectDatas['${field.field }']" :title="item.label.split('|')[0]" :content="item.label.split('|').length>1?item.label.split('|')[1]:''"></step>
				</steps>
				</c:if><c:if test="${field.type=='CHECKBOX' }">
				<checkbox-group v-model="fields['${field.field }']" @on-change="doClearField('${field.field }');">
					<checkbox v-for="item in selectDatas['${field.field }']" :label="item.value" ${field.disabled?'disabled':'' }><span>{{item.label}}&nbsp;</span></checkbox>
				</checkbox-group>
				</c:if><c:if test="${field.type=='COLOR' }">
					<color-picker v-model="fields['${field.field }']" alpha recommend ${field.disabled?'disabled':'' } :transfer="true" @on-change="doClearField('${field.field }');" ${field.disabled?'disabled':'' }></color-picker>
				</c:if><c:if test="${field.type=='IMAGE' }">
					<span style="display:inline-block;">
					<c:if test="${!field.disabled}"><i-button type="info" @click="openImageModal('${field.field}','${field.imageType }',${field.thumWidth },${field.thumRatio })">选择图片</i-button></c:if>
					<div class="image_input" style="margin-left:0;width:${(empty field.height?30:field.height)*field.thumRatio }px;height:${empty field.height?'':field.height }px;" 
						@click="viewImage('${field.field}');">
						<img id="image_${key}_${field.field}" style="vertical-align:initial;" border="0" alt="" src="${mm:getThumPath(mc:getInAttribute(map.action,field.field)) }" />
					</div>
					</span>
				</c:if><c:if test="${field.type=='ICON' }">
					<span style="display:inline-block;">
					<c:if test="${!field.disabled}"><i-button type="info" @click="openIconModal('${field.field}')">选择图标</i-button></c:if>
					<div class="image_input" style="margin-left:0;" >
						<img id="icon_${key}_${field.field}" style="vertical-align:initial;height:30px;" border="0" alt="" src="${mm:getIconPath(mc:getInAttribute(map.action,field.field)) }" />
					</div>
					</span>
				</c:if><c:if test="${field.type=='FILE' }">
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
				</c:if><c:if test="${field.type=='EDITER' }">
					<vue-html5-editor :content="fields['${field.field }']" :fieldname="'${field.field }'" @change="updateEditerData" :height="${field.height<300?300:field.height }"></vue-html5-editor>
				</c:if><c:if test="${field.type=='MAP' }">
					<i-input v-model="fields['${field.field }']" icon="ios-pin-outline" placeholder="${field.hint }" @on-blur="doClearField('${field.field }')" @on-click="openMapModal('${field.field }',${field.disabled?false:true })" ${field.disabled?'disabled':'' } readonly >
						<c:if test="${!empty field.suffix}"><span slot="append">${field.suffix}</span></c:if>
					</i-input>
				</c:if><c:if test="${field.type=='BUTTON' }">
					<c:forEach var="btn" items="${field.buttons}">
						<i-button type="${btn.style}" @click="submitHandler"><i class="iconfont">${btn.icon }</i>&nbsp;<span class="n-btn_title">${btn.title}</span>&nbsp;</i-button>
					</c:forEach>
				</c:if>
				</form-item>
			<c:if test="${field.message!='' }"></tooltip></c:if>
			</i-col>
			</c:if>
			<c:if test="${(!empty field.alert)&&field.type!='ALERT' }">
				<i-col span="${field.alertSpan }" v-if="(!('${field.nullHidden }'&&(null==fields['${field.nullHidden }']||''==fields['${field.nullHidden }'])))&&((!'${field.hiddenField }'||'${field.hiddenValues }'.indexOf(fields['${field.showField }'])<0)&&(!'${field.showField}'||'${field.showValues }'.indexOf(fields['${field.showField }'])>=0))">
					<form-item style="margin-bottom:18px;" :label-width="0">
						<c:if test="${!empty field.alert}">
							<alert show-icon type="${field.alert.type }" style="margin:0;">{{convertMessage(${field.alert.title })}}
								<c:if test="${!empty field.alert.icon}"><icon type="${field.alert.icon}" slot="icon"></icon></c:if>
								<c:if test="${!empty field.alert.desc}"><div slot="desc">{{convertMessage(${field.alert.desc })}}</div></c:if>
							</alert>
						</c:if>
					</form-item>
				</i-col>
			</c:if>
			<c:if test="${(!empty field.viewuiSpan)&&field.viewuiSpan>0 }">
				<i-col span="${field.viewuiSpan }" v-if="(!('${field.nullHidden }'&&(null==fields['${field.nullHidden }']||''==fields['${field.nullHidden }'])))&&((!'${field.hiddenField }'||'${field.hiddenValues }'.indexOf(fields['${field.showField }'])<0)&&(!'${field.showField}'||'${field.showValues }'.indexOf(fields['${field.showField }'])>=0))">
					<form-item style="margin-bottom:18px;" :label-width="0">
					<c:forEach var="vui" items="${field.vulist }">${vui }</c:forEach>
					</form-item>
				</i-col>
			</c:if>
		</c:forEach>
	</row>
	<row :style="{marginRight:'${row.marginRight }px',marginBottom:'${index.last?-18:0 }px',minWidth:'${row.minWidth }px'}">
		<c:forEach var="other" items="${row.others}">
			<i-col span="24">
			<div id="other_${other.title}_${key}" style="display:none;margin:0 0 10px 0;">
				<div id="other_${other.title}_${key}_content"></div>
			</div>
			</i-col>
		</c:forEach>
	</row>
		<c:if test="${istab&&row.endTabs}"><c:set var="istab" value="${false}"></c:set></tabs></c:if>
	</c:forEach>
	<c:if test="${istab}"></tabs></i-col></row></c:if>
	</card>
	<form-item label=" " style="width:100%;${map.openMode=='MODAL'?'text-align:right;':'' }${map.openMode=='PAGE'?'margin-bottom:15px;':'margin-bottom:0px;' }" :label-width="116">
		<c:forEach var="btn" items="${map.formButtons}">
			<i-button type="${btn.style}" @click="submitHandler" :disabled="'${btn.operField }'&&'${btn.operValues }'.indexOf(fields['${btn.operField }'])<0?true:false">
				<i class="iconfont">${btn.icon }</i>&nbsp;<span class="n-btn_title">${btn.title}</span>&nbsp;
			</i-button>
		</c:forEach>
		<c:if test="${!empty map.openKey}">
			&nbsp;<i-button @click="back(false)">${map.openMode=='PAGE'?'返回':'关闭'}</i-button>
		</c:if>
	</form-item>
</i-form>
	<div style="margin:0 0 16px 0;">
	<c:forEach var="other" items="${map.others}" varStatus="status">
	<div id="other_${other.title}_${key}" style="display:none;margin:${status.last?'0':'0 0 10px 0'};">
		<divider orientation="left" :style="{margin:'5px 0 5px',color:'#2d8cf0'}">
			<c:if test="${map.openMode=='PAGE' }"><a @click="back(false)" ><i class="iconfont" style="font-size:20px;font-weight:100;">&#xe718;</i>&nbsp;&nbsp;</a></c:if>
			${other.title }
		</divider>
		<div id="other_${other.title}_${key}_content"></div>
	</div>
	</c:forEach>
	</div>
	<c:forEach var="vui" items="${map.vulist }"><div style="margin:0 0 10px 0;">${vui }</div></c:forEach>
	<modal v-model="showModal" :footer-hide="true" :width="modalWidth" :mask-closable="false" @on-cancel="handlerResult(backEvent,'MODAL',false)">
		<div id="_table_modal_${key }"></div>
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
							<c:if test="${field.type!='CHECKBOX'}">'${field.field}':${mc:getInAttribute(map.action,field.field)},</c:if>
							<c:if test="${field.type=='CHECKBOX'}">'${field.field}':[${mc:getInAttributeByArray(map.action,field.field)}],</c:if>
						<c:forEach var="vup" items="${field.vplist}">
							'${vup}':${mc:getInAttribute(map.action,vup)},
						</c:forEach>
						</c:forEach>
						<c:forEach var="vup" items="${row.vplist}">
							'${vup}':${mc:getInAttribute(map.action,vup)},
						</c:forEach>
					</c:forEach>
					<c:forEach var="vup" items="${map.vplist}">
						'${vup}':${mc:getInAttribute(map.action,vup)},
					</c:forEach>
				},
				cascaders:{},
				initFields:{"no":"no"},
				buttons:{},
				others:{},
				selectMethod:{},
				selectDatas:{},
				selectLabels:{},
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
					<c:if test="${field.type=='SELECT'||field.type=='SELECT_NODE'||field.type=='CHECKBOX'||field.type=='RADIO'||field.type=='STEPS'||field.type=='TEXTAUTO'}">
						this.$set(this.selectDatas,"${field.field}",[]); this.$set(this.selectLabels,"${field.field}",[]);
						<c:forEach var="op" items="${field.selectData}">this.selectDatas["${field.field}"].push({value:"${op[0] }",label:"${op[1] }"});this.selectLabels["${field.field}"].push("${op[1] }");</c:forEach>
						<c:if test="${field.type=='SELECT_NODE'}">this.setCascaderValue("${field.field}");</c:if>
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