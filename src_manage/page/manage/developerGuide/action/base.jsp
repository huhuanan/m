<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<page>
	<time-line>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				Action都需要继承m.common.action.Action类, 并添加注释@ActionMeta(name="");
			</div>
			<pre name="code" class="brush:javafx">
@ActionMeta(name="manageAdminLogin")
public class AdminLoginAction extends StatusAction {
	private AdminLogin model;
}</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				Action类的无参方法都可以访问, 访问url为/action/注释name/方法名;
			</div>
			<pre name="code" class="brush:javafx">
http://ip:port/action/manageAdminLogin/doLogin</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				Action参数设置set方法, 实现自动填充, 支持多级属性填充;
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				支持Model对象, String, Date, Double, Integer, Boolean, List, Map
			</div>
			<pre name="code" class="brush:javafx">
private AdminLogin model; // 传参 model.oid=
private List&lt;String&gt; list; // 传参 list[0]=
private Map&lt;String, String&gt; map; // 传参 map[aaa]=</pre>
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
