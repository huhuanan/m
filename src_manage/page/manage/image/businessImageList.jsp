<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<page>
<c:if test="${power!='view' }">
	<upload ref="upload" multiple accept="image/jpg,image/jpeg,image/png" :on-success="uploadSuccess" :before-upload="beforeUpload"
		action="<%=request.getContextPath() %>/action/manageImageInfo/uploadBusinessImage?imageType=${map.imageType}&businessOid=${map.businessOid}&adminToken=${map.adminToken}<c:if test="${!empty map['thumWidth']}">&thumWidth=${map['thumWidth']}</c:if><c:if test="${!empty map['thumRatio']}">&thumRatio=${map['thumRatio']}</c:if>">
		<i-button type="primary" :loading="loading"><i class="iconfont">&#xe71d;</i>&nbsp;上传图片&nbsp;</i-button>
	</upload>
</c:if>
	<div>
		<div v-for="img in data.list_false" :id="img.oid" class="image_list_image" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>"><img :src="img.thumPath" @click="pageVue.viewImage(img.oid)" />
			<c:if test="${power!='view' }"><div class="image_oper" style="<c:if test="${!empty map['thumRatio']}">width:${map['thumRatio']*150}px;</c:if>">
			<poptip confirm title="确定要删除吗?" :transfer="true" @on-ok="deleteImage(img.oid)"><a href="javascript:;" >删除</a></poptip></div></c:if>
		</div>
	</div>
	<div ref="link_false"><i-button type="text" :loading="data.load_false" long @click="loadImage(false)">加载更多</i-button></div>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return {
				//key:'',
				//openKey:'',
				loadNum:0,
				loading:false,
				data:{
					list_false:[],
					load_false:false,
					pageno_false:1,
				},
			};
		},
		mounted:function(){
			this.loadImage(false);
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
				$.execJSON("action/manageImageInfo/businessImageList",{"imageType":"${map.imageType}","businessOid":"${map.businessOid}","adminToken":"${map.adminToken}","pageNo":self.data['pageno_'+isUsed],"pageNum":10},function(data){
					var has=false;
					if(data.code==0&&data.list.length>=10){
						has=true;
					}
					for(var i=0;i<data.list.length;i++){
						self.data['list_'+isUsed].push(data.list[i]);
					}
					self.$refs["link_"+isUsed].style.display=!has?"none":"";
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
				$.execJSON("action/manageImageInfo/deleteBusinessImage",{"imageOid":oid,"businessOid":"${map.businessOid}","adminToken":"${map.adminToken}"},function(data){
					if(data.code==0){
						self.$Message.success(data.msg);
						self.removeData(data.oid);
					}else{
						self.$Message.error(data.msg)
					}
				});
			}
			//backHandler:function(success,msg){//打开窗体的回调
			//}
		}
	};
})();
</script>