<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<page>
	<h2>选择图片</h2>
	<collapse :value="['no','yes']">
		<panel name="no">
			&nbsp;&nbsp;&nbsp;未使用
			<div slot="content">
				<upload ref="upload" multiple accept="image/jpg,image/jpeg,image/png" :on-success="uploadSuccess" :before-upload="beforeUpload"
					action="<%=request.getContextPath() %>/action/manageImageInfo/uploadImage?imageType=${htmlBody}&adminToken=${power}<c:if test="${!empty map['thumWidth']}">&thumWidth=${map['thumWidth']}</c:if><c:if test="${!empty map['thumRatio']}">&thumRatio=${map['thumRatio']}</c:if>">
					<i-button type="primary" :loading="loading"><i class="iconfont">&#xe71d;</i>&nbsp;上传图片&nbsp;</i-button>
				</upload>
				<div>
					<div v-for="img in data.list_false" :id="img.oid" class="image_list_image" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>"><img :src="img.thumPath" @click="pageVue.viewImage(img.oid)" />
						<div class="image_selected" v-if="selected[img.oid]" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>">当前选择</div>
						<div class="image_oper" v-if="!selected[img.oid]" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>"><a href="javascript:;" @click="selectImage(img.oid+'|'+img.thumPath+'|'+img.imgPath)" >选择</a>
						&nbsp;&nbsp;&nbsp;&nbsp; <poptip confirm title="确定要删除吗?" :transfer="true" @on-ok="deleteImage(img.oid)"><a href="javascript:;" >删除</a></poptip></div>
					</div>
				</div>
				<div id="link${key }_false"><i-button type="text" :loading="data.load_false" long @click="loadImage(false)">加载更多</i-button></div>
			</div>
		</panel>
		<panel name="yes">
			&nbsp;&nbsp;&nbsp;已使用
			<div slot="content">
				<div v-for="img in data.list_true" :id="img.oid" class="image_list_image" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>"><img :src="img.thumPath" @click="pageVue.viewImage(img.oid)" />
					<div class="image_selected" v-if="selected[img.oid]" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>">当前选择</div>
					<div class="image_oper" v-if="!selected[img.oid]" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>"><a href="javascript:;" @click="selectImage(img.oid+'|'+img.thumPath+'|'+img.imgPath)" >选择</a></div>
				</div>
				<div id="link${key }_true"><i-button type="text" :loading="data.load_true" long @click="loadImage(true)">加载更多</i-button></div>
			</div>
		</panel>
	</collapse>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return {
				//key:'',
				//openKey:'',
				selected:{},
				loadNum:0,
				loading:false,
				data:{
					list_false:[],
					list_true:[],
					load_false:false,
					load_true:false,
					pageno_false:1,
					pageno_true:1
				},
			};
		},
		mounted:function(){
			<c:forEach var="item" items="${array}">this.selected['${item}']=true;</c:forEach>
			this.loadImage(false);
			this.loadImage(true);
		},
		methods:{
			beforeUpload:function(){
				this.loading=true;
			},
			uploadSuccess:function(response, file, fileList){
				if(this.loadNum==fileList.length-1){
					this.data['list_false'].length=0;
					this.data['pageno_false']=1;
					this.loadImage(false);
					this.loadNum=0;
					this.$refs['upload'].clearFiles();
					this.loading=false;
				}else{
					this.loadNum++;
				}
			},
			loadImage:function(isUsed){
				var self=this;
				self.data['load_'+isUsed]=true;
				$.execJSON("action/manageImageInfo/imageList",{"imageType":"${htmlBody}","adminToken":"${power}","pageNo":self.data['pageno_'+isUsed],"pageNum":10,"isUsed":isUsed},function(data){
					var has=false;
					if(data.code==0&&data.list.length>=10){
						has=true;
					}
					for(var i=0;i<data.list.length;i++){
						self.data['list_'+isUsed].push(data.list[i]);
					}
					if(!has){
						$("#link${key}_"+isUsed).css("display","none");
					}
					self.data['pageno_'+isUsed]++;
					self.data['load_'+isUsed]=false;
				});
			},
			removeData:function(oid){
				for(var i=0;i<this.data.list_false.length;i++){
					if(this.data.list_false[i].oid==oid){
						this.data.list_false.splice(i,1);
						break;
					}
				}
			},
			deleteImage:function(oid){
				var self=this;
				$.execJSON("action/manageImageInfo/delete",{"imageOid":oid,"adminToken":"${power}"},function(data){
					if(data.code==0){
						self.$Message.success(data.msg);
						self.removeData(data.oid);
					}else{
						self.$Message.error(data.msg)
					}
				});
			},
			selectImage:function(str){
				this.back(true,'${map.field}|'+str);
			}
			//backHandler:function(success,msg){//打开窗体的回调
			//}
		}
	};
})();
</script>