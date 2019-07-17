<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<page>
	<i-table :columns="columns" :data="data" width="100%"></i-table>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return{
				columns: [
					{title: 'IP',key: 'ip'},
					//{title: 'OID',key: 'oid'},
					{title: '主控/本服',key: 'main'},
					{title: 'JVM总内存',key: 'totalMemory'},
					{title: 'JVM分配内存',key: 'freeMemory'},
					{title: 'JVM最大内存',key: 'maxMemory'},
					{title: 'session数量',key: 'sessionNum'},
					{title: '数据库使用连接数',key: 'dbUseLinkNum'},
				],
				data:[]
			}
		},
		mounted:function(){
			<c:forEach var="item" items="${list}">
			this.data.push({ip:'${item.ip }',oid:'${item.oid }',main:'${item.main==1?'主控':'' } ${item.self==1?'本服':'' }',
				totalMemory:${item.totalMemory},freeMemory:${item.freeMemory},maxMemory:${item.maxMemory},sessionNum:${item.sessionNum},dbUseLinkNum:${item.dbUseLinkNum}});
			</c:forEach>
		}
	};
})();
</script>
