<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/m_common.tld" prefix="mc" %>
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
					{title: 'JVM内存',key: 'memory',align:'center'},
					{title: 'db链接',key: 'dbLinkNum',align:'center'},
					{title: '',key: 'createDate',align:'center'},
				],
				data:[],
				sessionList:{},
			}
		},
		mounted:function(){
			<c:forEach var="item" items="${list}">
			this.data.push({ip:'${item.oid } - ${item.ip }',memory:'${item.freeMemory} / ${item.totalMemory} / ${item.maxMemory}',dbLinkNum:'${item.dbUseLinkNum} / ${item.dbMaxLinkNum}',
				createDate:'${mc:toFormatStyle(item.createDate,"yyyy-MM-dd HH:mm")}'});
			</c:forEach>
		},
		methods:{
		}
	};
})();
</script>
