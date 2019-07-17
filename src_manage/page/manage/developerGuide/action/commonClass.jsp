<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<page>
	<time-line>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				ManageAction 后台页面通用类
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				ManageAction常用方法
			</div>
			<pre name="code" class="brush:javafx">
/**
 * 获取登录用户信息 返回null说明没有登录
 * @return
 */
public AdminLogin getSessionAdmin() {...}
/**
 * 验证操作权限 
 * @param power 在module.xml配置文件中配置
 * @throws Exception 
 */
public void verifyAdminOperPower(String power) throws Exception{...}
/**
 * 验证登录 未登录则报错
 * @return
 * @throws Exception
 */
public AdminLogin verifyAdminLogin(){...}</pre>
		</timeline-item>
	</time-line>
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
