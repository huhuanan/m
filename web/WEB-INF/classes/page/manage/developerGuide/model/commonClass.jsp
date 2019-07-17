<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<page>
	<ol style="list-style-position:inside">
		<li>Action都需要继承m.common.action.Action类, 并添加注释@ActionMeta(name="");</li>
		<li>Action类的无参方法都可以访问, 访问url为/action/注释name/方法名;</li>
		<li>Action参数设置set方法, 实现自动填充, 支持多级属性填充;</li>
	</ol>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			//key:'',
			//openKey:'',
		},
		mounted:function(){
			SyntaxHighlighter.highlight();
		},
		methods:{
		}
	};
})();
</script>
