<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<page>
	<row :gutter="10">
		<i-col span="6">
			<collapse accordion v-model="index">
				<panel name="action">Action接口
					<cell-group slot="content" style="max-height:500px;overflow:auto;margin:-16px;">
						<cell v-for="item in actions" :title="item.title" :selected="id==item.className" @click.native="openAction(item.className,item.title+'<br/><small>'+item.description+'</small>')"></cell>
					</cell-group>
				</panel>
				<panel name="model">Model模型
					<cell-group slot="content" style="max-height:500px;overflow:auto;margin:-16px;">
						<cell v-for="item in models" :title="item.name+' '+item.description" :selected="id==item.clazz" @click.native="openModel(item.clazz)"></cell>
					</cell-group>
				</panel>
			</collapse>
		</i-col>
		<i-col span="18">
			<h3 v-html="title"></h3>
			<divider :style="{margin:'5px 0'}"/>
			<div id="action${key}">
				<template v-for="item in selectAction.methods">
					<h4 style="color:#2d8cf0;">{{item.title}} - {{item.description}}</h4>
					<table style="width:100%;margin-bottom:6px;" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td style=""> {{item.path}}</td>
							<td style="width:100px;text-align:center;">权限: {{item.permission}}</td>
							<td style="width:100px;text-align:right;"><i-button type="primary" size="small" @click.native="showTestModal(item.title,item.path)">测试</i-button></td>
						</tr>
					</table>
					<div class="ivu-table-wrapper">
						<table class="ivu-table ivu-table-small" style="width:100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<th style="text-align:left;">参数名称</th>
								<th style="text-align:left;">参数描述</th>
								<th style="text-align:center;">类型</th>
								<th style="text-align:center;">长度</th>
								<th style="text-align:center;">必填</th>
							</tr>
							<tr v-for="f in item.params">
								<td style="padding-left:5px;padding-right:5px;">{{f.name}}</td>
								<td style="padding-left:5px;padding-right:5px;">{{f.description}}</td>
								<td style="padding-left:5px;padding-right:5px;width:70px;text-align:center;">{{f.type}}</td>
								<td style="padding-left:5px;padding-right:5px;width:70px;text-align:center;">{{f.length}}</td>
								<td style="padding-left:5px;padding-right:5px;width:70px;text-align:center;">{{f.notnull}}</td>
							</tr>
							<tr>
								<td colspan="6">
									<h4>返回:</h4>
									<pre>{{item.result}}</pre>
								</td>
							</tr>
						</table>
					</div>
				</template>
			</div>
			<div id="model${key}">
				<table style="widht:100%;">
					<tr>
						<td>
							<h3><span style="color:#2d8cf0;">{{selectModel.description}}</span> <b>类:</b>{{selectModel.clazz}}  <b>表:</b>{{selectModel.name}}</h3>
						</td>
						<td style="text-align:right;">
							<i-button type="primary" size="small" @click.native="showScriptModal(selectModel)">dart</i-button>
						</td>
					</tr>
				</table>
				
				<div class="ivu-table-wrapper">
					<table class="ivu-table ivu-table-small" style="width:100%" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<th style="text-align:center;">字段</th>
							<th style="text-align:center;">列名</th>
							<th style="text-align:center;">描述</th>
							<th style="text-align:center;">类型</th>
							<th style="text-align:center;">长度</th>
							<th style="text-align:center;">必填</th>
						</tr>
						<tr v-for="f in selectModel.fields">
							<td style="padding-left:5px;padding-right:5px;" v-if="f.linkName"><a href="javascript:;" @click="openModel(f.linkClazz)">{{f.field}}</a></td>
							<td style="padding-left:5px;padding-right:5px;" v-if="!f.linkName">{{f.field}}</td>
							<td style="padding-left:5px;padding-right:5px;">{{f.name}}</td>
							<td style="padding-left:5px;padding-right:5px;">{{f.description}}</td>
							<td style="padding-left:5px;padding-right:5px;width:70px;text-align:center;">{{f.type}}</td>
							<td style="padding-left:5px;padding-right:5px;width:70px;text-align:center;">{{f.length}}</td>
							<td style="padding-left:5px;padding-right:5px;width:70px;text-align:center;">{{f.notnull}}</td>
						</tr>
					</table>
				</div>
			</div>
		</i-col>
	</row>
	<modal v-model="testModal" class="table_modal" width="80%" :mask-closable="false">
		<h3>{{testMethod.title}} - {{testMethod.description}}</h3>
		<div class="ivu-table-wrapper">
			<table class="ivu-table ivu-table-default" style="width:100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td colspan="4" style="padding-left:10px;"><h3>访问路径: {{testMethod.path}}</h3></td>
					<td style="text-align:right;"><span v-if="testMethod.permission">权限: </span></td>
					<td style="text-align:center;width:310px;">
						<span v-if="!testMethod.permission">权限: {{testMethod.permission}}</span>
						<i-input v-if="testMethod.permission" v-model="authorization" style="width:300px;"/>
					</td>
				</tr>
				<tr>
					<th style="text-align:center;">参数名称</th>
					<th style="text-align:center;">参数描述</th>
					<th style="text-align:center;">类型</th>
					<th style="text-align:center;">长度</th>
					<th style="text-align:center;">必填</th>
					<th style="text-align:right;padding-right:5px;">
						<i-button size="small" @click.native="testModal=false">关闭</i-button>
						<i-button type="primary" size="small" @click.native="execTest(false)">普通请求</i-button>
						<i-button type="primary" size="small" @click.native="execTest(true)">Body请求</i-button>
					</th>
				</tr>
				<tr v-for="f in testMethod.params">
					<td style="padding-left:5px;padding-right:5px;">{{f.name}}</td>
					<td style="padding-left:5px;padding-right:5px;">{{f.description}}</td>
					<td style="padding-left:5px;padding-right:5px;width:65px;text-align:center;">{{f.type}}</td>
					<td style="padding-left:5px;padding-right:5px;width:50px;text-align:center;">{{f.length}}</td>
					<td style="padding-left:5px;padding-right:5px;width:50px;text-align:center;">{{f.notnull}}</td>
					<td style="padding-left:5px;padding-right:5px;width:310px;text-align:center;">
						<i-input v-if="f.type=='STRING'" v-model="params[f.name]" style="width:300px;"/>
						<date-picker v-if="f.type=='DATE'" v-model="params[f.name]" type="datetime" style="width:300px;"></date-picker>
						<input-number v-if="f.type=='INT'||f.type=='DOUBLE'" v-model="params[f.name]" style="width:300px;"></input-number>
					</td>
				</tr>
				<tr>
					<td colspan="6">
						<h4>返回:</h4>
						<json-val :json-val="json" :current-depth="0" :max-depth="2" :last="true"></json-val>
					</td>
				</tr>
			</table>
		</div>
   </modal>
	<modal v-model="scriptModal" class="table_modal" width="80%" :mask-closable="false">
		<h3>{{selectModel.name}} {{selectModel.description}}</h3>
		<textarea rows="25" style="width:100%;">{{scriptContent}}</textarea>
	</modal>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return {
				//key:'',
				//openKey:'',
				index:"model",
				id:"",
				title:"",
				actions:${map.actions},
				selectAction:{},
				models:${map.models},
				selectModel:{},
				testModal:false,
				testMethod:{},
				params:{},
				authorization:'',
				json:{},
				scriptModal:false,
				scriptContent:'',
			};
		},
		mounted:function(){
			this.openModel(this.models[0].clazz);
		},
		
		methods:{
			openAction:function(id,title){
				this.id=id;
				this.title=title;
				for(var i=0;i<this.actions.length;i++){
					if(id==this.actions[i].className){
						this.selectAction=this.actions[i];
						break;
					}
				}
				$('#model${key}').slideUp(300);
				$('#action${key}').slideDown(300);
			},
			openModel:function(id){
				this.id=id;
				this.title="模型表";
				for(var i=0;i<this.models.length;i++){
					if(id==this.models[i].clazz){
						this.selectModel=this.models[i];
						break;
					}
				}
				$('#action${key}').slideUp(300);
				$('#model${key}').slideDown(300);
			},
			showTestModal:function(title,path){
				for(var i=0;i<this.selectAction.methods.length;i++){
					if(path==this.selectAction.methods[i].path){
						this.testMethod=this.selectAction.methods[i];
						break;
					}
				}
				for(var i=0;i<this.testMethod.params.length;i++){
					this.params[this.testMethod.params[i].name]=null;
				}
				this.testModal=true;
				this.json={};
			},
			execTest:function(flag){
				var data={};
				for(var i=0;i<this.testMethod.params.length;i++){
					data[this.testMethod.params[i].name]=this.params[this.testMethod.params[i].name];
				}
				var authorization=this.authorization;
				var self=this;
				var fn=(function(json){
					this.json=JSON.parse(json);
				}).bind(this);
				
				var spin=true;
				if(spin) pageVue.$Spin.show();
				pageVue.$Loading.start();
				var d={};
				for(var i in data){
					var tmp="";
					if(data[i] instanceof Date){
						tmp=data[i].format('yyyy-MM-dd HH:mm:ss');
					}else{
						tmp=data[i];
					}
					if(flag){
						$.fillJSONData(d,i,tmp);
					}else{
						d[i]=tmp
					}
				}
				$.ajax({
					type:"POST",
					url:self.testMethod.path,
					data:flag?JSON.stringify(d):d,
					dataType:"html",
					headers: {'Content-Type': flag?'application/json':'application/x-www-form-urlencoded'},
					beforeSend: function(xhr) {
						if(authorization){
							xhr.setRequestHeader('Authorization', ''+authorization);
						}
					},
					success:function(ele){
						if(fn) fn(ele);
						else pageVue.$Message.info(ele);
						pageVue.$Spin.hide();
						pageVue.$Loading.finish();
					},
					error:function(res){
						pageVue.$Spin.hide();
						pageVue.$Loading.error();
						pageVue.$Message.error(res.responseText);
					}
				});
			},
			showScriptModal:function(table){
				var ts=table.clazz.split('.');
				var tab=ts[ts.length-1];
				var str="///"+table.description+"\r\n";
				str+="class "+tab+" { \r\n";
				for(var i=0;i<table.fields.length;i++){
					str+="\t///"+table.fields[i].description+"\r\n";
					str+="\t"+this.getFieldType(table.fields[i])+" "+table.fields[i].field+"; \r\n";
				}
				str+="\t///构造方法 : "+table.description+"\r\n";
				str+="\t"+tab+"({\r\n";
				for(var i=0;i<table.fields.length;i++){
					str+="\t\tthis."+table.fields[i].field+",\r\n";
				}
				str+="\t});\r\n";
				str+="\t///反序列化 : "+table.description+"\r\n";
				str+="\tstatic "+tab+" fromJson(Map<String,dynamic> json){\r\n";
				str+="\t\treturn "+tab+"(\r\n";
				for(var i=0;i<table.fields.length;i++){
					str+="\t\t\t"+table.fields[i].field+": "+this.getFieldValue("json",table.fields[i])+",\r\n";
				}
				str+="\t\t);\r\n";
				str+="\t}\r\n";
				str+="\t///序列化 : "+table.description+"\r\n";
				str+="\tstatic Map<String,dynamic> toJson("+tab+" instance) => <String,dynamic>{\r\n";
				for(var i=0;i<table.fields.length;i++){
					if(table.fields[i].linkClazz){
						var ts=table.fields[i].linkClazz.split('.');
						str+="\t\t'"+table.fields[i].field+"': "+ts[ts.length-1]+".toJson(instance."+table.fields[i].field+"),\r\n";
					}else{
						str+="\t\t'"+table.fields[i].field+"': instance."+table.fields[i].field+",\r\n";
					}
				}
				str+="\t};\r\n";
				str+="}";
				
				
				this.scriptContent=str;
				this.scriptModal=true;
			},
			getFieldType:function(field){
				if(field.linkClazz){
					var ts=field.linkClazz.split('.');
					return ts[ts.length-1];
				}else if(field.type=='STRING'){
					return 'String';
				}else if(field.type=='INT'){
					return 'int';
				}else if(field.type=='DOUBLE'){
					return 'double';
				}else if(field.type=='DATE'){
					return 'DateTime';
				}
			},
			getFieldValue:function(a,field){
				if(field.linkClazz){
					var ts=field.linkClazz.split('.');
					return ts[ts.length-1]+".fromJson("+a+"['"+field.field+"'])";
				}else if(field.type=='STRING'){
					return a+"['"+field.field+"'] as String";
				}else if(field.type=='INT'){
					return a+"['"+field.field+"'] as int";
				}else if(field.type=='DOUBLE'){
					return a+"['"+field.field+"'] as double";
				}else if(field.type=='DATE'){
					return a+"['"+field.field+"']==null?null:DateTime.parse(json['"+field.field+"'] as String)";
				}
			}
			//backHandler:function(success,msg){//打开窗体的回调
			//}
		}
	};
})();
</script>
