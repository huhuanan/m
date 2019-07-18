<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<page>
	<row :gutter="10">
		<i-col span="6">
			<collapse accordion v-model="index">
				<panel name="action">Action接口
					<cell-group slot="content" style="max-height:500px;overflow:auto;margin:-16px;">
						<cell v-for="item in actions" :title="item.title" :selected="id==item.className" @click.native="openAction(item.className,item.title+' - '+item.description)"></cell>
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
			<card :padding="10">
				<p slot="title">{{title}}</p>
				<div id="action${key}">
					<div v-for="item in selectAction.methods" class="ivu-table-wrapper">
						<table class="ivu-table ivu-table-small" style="width:100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<th colspan="6"><h3>{{item.title}} - {{item.description}}</h3></th>
							</tr>
							<tr>
								<td colspan="2" style="padding-left:10px;"><h3>访问路径: {{item.path}}</h3></td>
								<td colspan="2" style="text-align:center;">权限: {{item.permission}}</td>
								<td style="text-align:center;"><i-button type="primary" @click.native="showTestModal(item.title,item.path)">测试</i-button></td>
							</tr>
							<tr>
								<th style="text-align:center;">参数名称</th>
								<th style="text-align:center;">参数描述</th>
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
				</div>
				<div id="model${key}">
					<div class="ivu-table-wrapper">
						<table class="ivu-table ivu-table-small" style="width:100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<th colspan="6"><h3>{{selectModel.name}} - {{selectModel.description}}</h3></th>
							</tr>
							<tr>
								<td colspan="6"><h3>{{selectModel.clazz}}</h3></td>
							</tr>
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
			</card>
		</i-col>
	</row>
	<modal v-model="testModal" width="80%" :mask-closable="false">
		<p slot="header">{{testTitle}}</p>
		<div class="ivu-table-wrapper">
			<table class="ivu-table ivu-table-default" style="width:100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<th colspan="6"><h3>{{testMethod.title}} - {{testMethod.description}}</h3></th>
				</tr>
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
					<th style="text-align:center;"></th>
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
		<div slot="footer">
			<i-button type="primary" size="large" @click.native="execTest(false)">普通请求</i-button>
			<i-button type="primary" size="large" @click.native="execTest(true)">Body请求</i-button>
			<i-button size="large" @click.native="testModal=false">关闭</i-button>
		</div>
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
				testTitle:"",
				testMethod:{},
				params:{},
				authorization:'',
				json:{}
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
				this.testTitle=title;
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
			}
			//backHandler:function(success,msg){//打开窗体的回调
			//}
		}
	};
})();
</script>
