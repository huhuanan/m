<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<page>
	<time-line>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				Model都需要继承m.common.model.Model类, 默认主键为oid;
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				Model.java
			</div>
			<pre name="code" class="brush:javafx">
public class Model implements Serializable {
	@KeyFieldMeta(name="oid")
	private String oid;
	...
}</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				添加注释@TableMeta(name=""), 支持启动自动创建或添加数据表结构;
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				@TableMeta(name="") 表注解<br />
				name:表名; description:描述; <br />
				isView:默认false, 实体表; viewSql:视图sql语句
			</div>
			<pre name="code" class="brush:javafx">
@TableMeta(name="os_image_info",description="图片表")
public class ImageInfo extends Model {
	...
}</pre>
		</timeline-item>
		<timeline-item>
			<div class="line_height_xs color_blue text_size_sm" style="padding-bottom:5px;">
				字段注释, 分关联表注解和普通属性注解; 
			</div>
			<div class="line_height_lg text_indent text_size_sm">
				@LinkTableMeta() 关联表注解;<br />
				name:数据库表中的字段; table:对应的Model对象; description:描述;<br />
				notnull:默认false, 不是非空字段;
			</div>
			<pre name="code" class="brush:javafx">
	@LinkTableMeta(name="image_admin_oid",table=ImageAdmin.class,description="用户表")
	private ImageAdmin imageAdmin;</pre>
			<div class="line_height_lg text_indent text_size_sm">
				@FieldMeta() 普通属性注解;<br />
				name:数据库表中的字段; type:属性类型(FieldType); length:字符串长度; description:描述;<br />
				notnull:默认false, 不是非空字段; defaultValue:默认值;
			</div>
			<pre name="code" class="brush:javafx">
	@FieldMeta(name="img_path",type=FieldType.STRING,length=200,description="图片路径")
	private String imgPath;
	@FieldMeta(name="thum_ratio",type=FieldType.DOUBLE,description="缩略图比例|宽/高")
	private Double thumRatio;
	@FieldMeta(name="create_date",type=FieldType.DATE,description="创建时间")
	private Date createDate;</pre>
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
