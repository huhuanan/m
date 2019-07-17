<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<page>
	<div style="padding:0 0 3px 5px;">
	<c:forEach var="item" items="${list }">
		<img style="width:150px;" @click="pageVue.viewImage('${item.oid }')" src="${item.thumPath }" />
	</c:forEach>
	</div>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return {
			};
		},
		mounted:function(){
		},
		methods:{
		}
	};
})();
</script>