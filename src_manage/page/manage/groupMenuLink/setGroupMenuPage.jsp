<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<page>
	<h3>菜单设置</h3>
	<collapse :value="[<c:forEach var="item" items="${list}">'${item.oid }',</c:forEach>]">
	<c:forEach var="item" items="${list}">
		<panel name="${item.oid}">&nbsp;&nbsp;&nbsp;<i class="iconfont">${item.icoStyle }</i>&nbsp;${item.name }
		<p slot="content">
		<c:forEach var="menu" items="${map[item.oid]}">
			<row style="margin-bottom:15px;">
				<i-col span="5" style="padding-left:15px;line-height:24px;"><i class="iconfont">${menu.icoStyle }</i>&nbsp;${menu.name }</i-col>
				<i-col span="3">
					<c:if test="${menu.isPublic=='Y'}"><span style="color:#5fb878;">默认</span></c:if>
					<c:if test="${menu.isPublic=='N'}"><i-switch v-model="${menu.oid }" @on-change="onchange('${item.oid }','${menu.oid }')"></i-switch></c:if>
				</i-col>
				<i-col span="16" style="padding-left:15px;line-height:24px;">${menu.description }</i-col>
			</row>
			<c:forEach var="subMenu" items="${map[menu.oid]}">
				<row style="margin-bottom:15px;">
					<i-col span="6" style="padding-left:50px;line-height:24px;"><icon type="ios-arrow-forward"></icon>${subMenu.name }</i-col>
					<i-col span="2">
						<c:if test="${subMenu.isPublic=='Y'}"><span style="color:#5fb878;line-height:24px;">默认</span></c:if>
						<c:if test="${subMenu.isPublic=='N'}"><i-switch v-model="${subMenu.oid }" @on-change="onchange('${item.oid }','${subMenu.oid }')"></i-switch></c:if>
					</i-col>
					<i-col span="16" style="padding-left:15px;line-height:24px;">${subMenu.description }</i-col>
				</row>
			</c:forEach>
		</c:forEach>
		</p>
		</panel>
	</c:forEach>
	</collapse>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return {
				//key:'',
				//openKey:'',
				adminGroupOid:'${model.adminGroup.oid }',
			<c:forEach var="item" items="${list}">
				<c:forEach var="menu" items="${map[item.oid]}">
					'${menu.oid}':${!empty map['groupMenuLink'][menu.oid]?'true':'false'},
					<c:forEach var="subMenu" items="${map[menu.oid]}">
						'${subMenu.oid}':${!empty map['groupMenuLink'][subMenu.oid]?'true':'false'},
					</c:forEach>
				</c:forEach>
			</c:forEach>
			};
		},
		methods:{
			onchange:function(p,e){
				var self=this;
				var flag=this[e];
				var method=flag?"addGroupMenuLink":"removeGroupMenuLink";
				$.execJSON("action/manageGroupMenuLink/"+method,
					{"model.adminGroup.oid":this.adminGroupOid,"model.module.oid":p,"model.menu.oid":e},
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
			}
			//backHandler:function(success,msg){//打开窗体的回调
			//}
		}
	};
})();
</script>

