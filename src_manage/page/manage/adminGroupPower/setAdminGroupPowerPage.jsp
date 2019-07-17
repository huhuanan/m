<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<page>
	<h2>权限设置</h2>
	<c:forEach var="item" items="${array}">
	<row style="margin-top:15px;">
		<i-col span="16" style="padding-left:15px;line-height:24px;">${item[1] }</i-col>
		<i-col span="8" style="text-align:right;">
			<i-switch v-model="${item[0] }" @on-change="onchange('${item[0] }')"></i-switch>
		</i-col>
	</row>
	</c:forEach>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return {
				//key:'',
				//openKey:'',
				adminGroupOid:'${model.adminGroup.oid }',
				<c:forEach var="item" items="${array}">'${item[0]}':${power[item[0]]?'true':'false'},</c:forEach>
			};
		},
		methods:{
			onchange:function(e){
				var self=this;
				var flag=this[e];
				var method=flag?"addAdminGroupPower":"removeAdminGroupPower";
				$.execJSON("action/manageAdminGroupPower/"+method,
					{"model.adminGroup.oid":this.adminGroupOid,"model.name":e},
					function(data){
						if(data.code!=0){
							self[e]=!flag;
							self.$Message.error(data.msg);
						}else{
							self.$Message.success(data.msg);
						}
					}
				);
				console.log(flag);
			},
			//backHandler:function(success,msg){//打开窗体的回调
			//}
		}
	};
})();
</script>

