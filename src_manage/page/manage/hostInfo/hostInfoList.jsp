<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<page>
	<i-table :columns="columns" :data="data" width="100%" size="small"></i-table>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return{
				columns: [
					{title: 'IP',key: 'ip',align:'center'},
					{title: '主控/本服',key: 'main',align:'center'},
					{title: 'JVM内存',key: 'memory',align:'center'},
					{title: 'session',key: 'session',align:'center'},
					{title: '数据库',key: 'dbUseLinkNum',align:'center'},
				],
				data:[]
			}
		},
		mounted:function(){
			<c:forEach var="item" items="${list}">
			this.data.push({ip:'${item.ip }',oid:'${item.oid }',main:'${item.main==1?'主控':'' } ${item.self==1?'本服':'' }',
				memory:'${item.freeMemory} / ${item.totalMemory} / ${item.maxMemory}',session:'${item.loginNum} / ${item.sessionNum}',dbUseLinkNum:${item.dbUseLinkNum}});
			</c:forEach>
		}
	};
})();
</script>
