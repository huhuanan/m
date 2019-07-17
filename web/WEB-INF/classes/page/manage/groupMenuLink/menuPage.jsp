<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="menu${model.oid }">
	<div id="menu${model.oid }_query_content"></div>
	<script type="text/javascript">
	$.loadVuePage($('#menu${model.oid }_query_content'),'${model.urlPath}',$.parseParams('${model.urlPath}'),function(){
		
	});
	</script>
</div>