<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<page>
	<time-line>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				dbconfig.properties 数据库配置文件;
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				该文件很少升级, 放在src根目录下; 
			</div>
			<pre name="code" class="brush:js">
#数据库配置
db_driver=com.mysql.jdbc.Driver
db_url=jdbc:mysql://127.0.0.1:3306/biz?useSSL=false
db_username=root
db_password=root
db_init_connect=SET NAMES utf8mb4
db_max_connect=10

#数据库名称  空:不自动初始化数据库
table_schema=biz

#主控服务地址
server_ip=
server_port=8128</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				mconfig.properties 框架配置文件
			</div>
			<pre name="code" class="brush:js">
#数据库模型包, 多个用逗号分开(,)
model_pack=manage.model,api.model,biz.base.model
#秘密字段 ModelQueryList 查询用*时,过滤掉
secret_field=BusinessInfo.password,BusinessInfo.token,UserInfo.password,UserInfo.token
#静态资源加速配置 多个用(,号)分开  model的属性
static_field=ImageInfo.imgPath,ImageInfo.thumPath
#action包, 多个用逗号分开(,)   继承m.common.action 系统Action
action_pack=manage.action,api.action,biz.base.action,biz.app.action
#启动是需要初始化的类 继承m.system.SystemInitRun并实现run方法
init_class=manage.run.ModuleInitRun,biz.base.run.AppModuleInitRun
#定时任务类 多个用&符号分割  格式 类(继承m.system.SystemTaskRun)|Quartz的cronSchedule  每天0点0分0秒执行: 秒0 分钟0 小时0 日期* 月* 星期? 年
#  例:m.system.task.SystemTask|0/2 * * * * ?&m.system.task.SystemTask|0/2 * * * * ?
task_class=biz.base.timer.BizDayRunTimer|1 0 0 * * ?&biz.base.timer.BizMonthRunTimer|10 0 0 1 * ?
#操作日志类 实现m.common.model.LogModel 只能设置一个
log_class=manage.model.SystemLog
#系统信息类 实现m.common.model.SystemInfoModel 只能设置一个
systeminfo_class=manage.model.SystemInfo
#域名跳转实现类 多个用逗号分开(,) 实现m.system.url.RedirectUrl
domain_class=biz.base.service.BusinessInfoService
#上传文件存放目录
file_path=uploadData/
#调试
debug=true</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				urlmarker.xml
			</div>
			<pre name="code" class="brush:xml">
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;root&gt;
	&lt;redirect&gt;&lt;!-- 重定向 --&gt;
		&lt;url tag="/admin"&gt;&lt;![CDATA[/action/manageAdminLogin/admin]]&gt;&lt;/url&gt;
		&lt;url tag="/u/"&gt;&lt;![CDATA[/action/appApi/toLogin?business=]]&gt;&lt;/url&gt;
		&lt;url tag="/login"&gt;&lt;![CDATA[/action/appApi/login?business=]]&gt;&lt;/url&gt;
		&lt;url tag="/WxServerToken"&gt;&lt;![CDATA[/action/apiWxServer/token]]&gt;&lt;/url&gt;
		&lt;url tag="/notifyPage"&gt;&lt;![CDATA[/action/apiOrderPay/notifyPage]]&gt;&lt;/url&gt;
		&lt;url tag="/returnPage"&gt;&lt;![CDATA[/action/apiOrderPay/returnPage]]&gt;&lt;/url&gt;
	&lt;/redirect&gt;
	&lt;enable&gt;&lt;!-- 允许访问的路径 --&gt;
		&lt;tag&gt;&lt;![CDATA[/resources/]]&gt;&lt;/tag&gt;
		&lt;tag&gt;&lt;![CDATA[/static/]]&gt;&lt;/tag&gt;
	&lt;/enable&gt;
	&lt;referer&gt;&lt;!-- 允许来路的域名 --&gt;
		&lt;tag&gt;&lt;![CDATA[biz.ostudio.cc]]&gt;&lt;/tag&gt;
		&lt;tag&gt;&lt;![CDATA[192.168.137.1]]&gt;&lt;/tag&gt;
	&lt;/referer&gt;
&lt;/root&gt;</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				dbinitsql.xml
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				表创建后执行的sql语句;
			</div>
			<pre name="code" class="brush:xml">
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;root&gt;
	&lt;sql tableName="os_admin_group"&gt;&lt;![CDATA[
	INSERT INTO os_admin_group (oid, name, description, status) VALUES ('1', '管理员', '', '0');
	]]&gt;&lt;/sql&gt;
&lt;/root&gt;</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				dbviewsql.xml
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				表注解的viewSql属性不定义时, 可以在此配置文件中编写;
			</div>
			<pre name="code" class="brush:xml">
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;root&gt;
	&lt;sql tableName="v_image_admin"&gt;&lt;![CDATA[
	select al.oid,al.token,al.username name,'管理员' type from os_admin_login al
	union all
	select ui.oid,ui.token,ui.username name,'用户' from b_user_info ui
	]]&gt;&lt;/sql&gt;
&lt;/root&gt;</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				module.xml
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				用于配置后台权限和菜单;
			</div>
			<pre name="code" class="brush:xml">
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;root loginPage="manage/admin" adminPage="manage/admin"&gt;
	&lt;power&gt;
		&lt;operation name="manage_system_power" description="系统管理的权限"&gt;&lt;/operation&gt;
	&lt;/power&gt;
	&lt;module oid="A" sort="0" name="系统设置" icoStyle="&#xe6ae;" isPublic="N"&gt;
		&lt;menu oid="A11" sort="111" name="系统用户" icoStyle="&#xe70b;" description="" isPublic="N"&gt;
			&lt;menu oid="A1101" sort="11101" name="权限组设置" urlPath="action/manageAdminGroup/toList?method=adminGroupData" description="系统角色的权限组,用于设置权限组的菜单权限和操作权限." isPublic="N"&gt;&lt;/menu&gt;
			&lt;menu oid="A1102" sort="11102" name="管理员设置" urlPath="action/manageAdminLogin/toList?method=adminLoginData" description="设置可登录系统的操作员." isPublic="N"&gt;&lt;/menu&gt;
			&lt;menu oid="A1103" sort="11103" name="字典设置" urlPath="action/manageDictionaryType/toList?method=dictionayTypeList" description="设置数据字典." isPublic="N"&gt;&lt;/menu&gt;
			&lt;menu oid="A1104" sort="11104" name="操作日志" urlPath="action/manageSystemLog/toList?method=systemLogData" description="查询用户操作日志." isPublic="N"&gt;&lt;/menu&gt;
			&lt;menu oid="A1105" sort="11105" name="系统信息" urlPath="action/manageSystemInfo/toEdit" description="设置系统信息" isPublic="N"&gt;&lt;/menu&gt;
			&lt;menu oid="A1106" sort="11106" name="开发指南" urlPath="action/manageDeveloperGuide/toIndex" description="" isPublic="N"&gt;&lt;/menu&gt;
		&lt;/menu&gt;
	&lt;/module&gt;
&lt;/root&gt;</pre>
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
