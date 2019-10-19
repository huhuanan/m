Date.prototype.format=function(fmt){ 
	var o={"M+": this.getMonth() + 1,"d+": this.getDate(),"H+": this.getHours(),"m+": this.getMinutes(),"s+": this.getSeconds(),"q+": Math.floor((this.getMonth() + 3) / 3),"S": this.getMilliseconds()}; 
	if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length)); 
	for (var k in o) if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length))); 
	return fmt; 
};
Array.prototype.indexOf = function(val) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] == val) return i;
	}
	return -1;
};
Array.prototype.remove = function(val) {
	var index = this.indexOf(val);
	if (index > -1) {
		this.splice(index, 1);
	}
};
var vui={
	util:{
		back:function(flag,msg){
			console.log(this.openKey,this);
			if(this.openKey&&$.vue[this.openKey]&&$.vue[this.openKey].backHandler){
				$.vue[this.openKey].backHandler(flag,msg);
			}
		},
		fn_formatDate:function(date,style){
			if(!date){
				return "";
			}else if(date instanceof Date){
				return date.format(style);
			}else{
				return new Date(Date.parse(date.replace(/-/g, "/"))).format(style);
			}
		},
		fn_formatShow:function(dateString){
			var str="";
			var date=new Date();
			if(date.getFullYear()==parseInt(dateString.substring(0,4))){
				if(date.getMonth()+1==parseInt(dateString.substring(5,7))){
					if(date.getDate()==parseInt(dateString.substring(8,10))){
						str="今天";
					}else if(date.getDate()-parseInt(dateString.substring(8,10))==1){
						str="昨天";
					}else if(date.getDate()-parseInt(dateString.substring(8,10))==-1){
						str="明天";
					}else if(date.getDate()-parseInt(dateString.substring(8,10))==2){
						str="前天";
					}else if(date.getDate()-parseInt(dateString.substring(8,10))==-2){
						str="后天";
					}else{
						str=parseInt(dateString.substring(5,7))+"月"+parseInt(dateString.substring(8,10))+"日";
					}
				}else{
					str=parseInt(dateString.substring(5,7))+"月"+parseInt(dateString.substring(8,10))+"日";
				}
				str=str+dateString.substring(10);
			}else{
				str=parseInt(dateString.substring(0,4))+"年"+parseInt(dateString.substring(5,7))+"月"+parseInt(dateString.substring(8,10))+"日"+dateString.substring(10);
			}
			return str;
		}
	}
};


//////////json格式化
var padstr = '&nbsp;&nbsp;&nbsp;&nbsp;';
function valueType(value) {
  var tf = typeof value;
  var ts = Object.prototype.toString.call(value);
  return value === null ? 'Null' :
    'boolean' === tf ? 'Boolean' :
      'number' === tf ? 'Number' :
        'string' === tf ? 'String' :
          '[object Array]' === ts ? 'Array' : 'Object';
}
Vue.component('json-key', {
  template: `<span class="key">
    <span v-html="pad"></span><strong class="json_key" v-if="render">"{{jsonKey}}"</strong><template v-if="render">:</template>
  </span>`,
  props: ['json-key', 'current-depth'],
  computed: {
    pad: function () {
      return new Array(this.currentDepth+1).join(padstr);
    },
    render: function () {
      return isNaN(this.jsonKey);
    }
  },
});

Vue.component('json-val', {
  template: `<span class="val">
    <template v-if="canToggle">
      <template v-if="open">
        <!-- Array -->
        <template v-if="type === 'Array'"><i class="iconfont" @click="toggle">&#xe6f2;</i> [<br>
          <json-item class="item" :json-val="jsonVal" :current-depth="currentDepth+1" :max-depth="maxDepth"></json-item><span v-html="pad"></span>]<template v-if="!last">,</template><br>
        </template>
        <!-- Object -->
        <template v-else-if="type === 'Object'"><i class="iconfont" @click="toggle">&#xe6f2;</i> {<br>
          <json-item class="item" :json-val="jsonVal" :current-depth="currentDepth+1" :max-depth="maxDepth"></json-item><span v-html="pad"></span>}<template v-if="!last">,</template><br>
        </template>
      </template>
      <template v-else>
        <!-- Array -->
        <template v-if="type === 'Array'">
          <span @click="toggle"><i class="iconfont">&#xe87a;</i> <span class="json_hide">Array[<span class="json_number">{{jsonVal.length}}</span>]</span><span><template v-if="!last">,</template><br>
        </template>
        <!-- Object -->
        <template v-else-if="type === 'Object'">
          <span @click="toggle"><i class="iconfont">&#xe87a;</i> <span class="json_hide">Object{<span class="json_string">...</span>}</span></span><template v-if="!last">,</template><br>
        </template>
      </template>
    </template>
    <template v-else>
      <!-- Null -->
      <template v-if="type === 'Null'">
        <span class="json_null">null</span><template v-if="!last">,</template><br>
      </template>
      <!-- String -->
      <template v-else-if="type === 'String'">
        <span class="json_string">"{{jsonVal}}"</span><template v-if="!last">,</template><br>
      </template>
      <!-- Number -->
      <template v-else-if="type === 'Number'">
        <span class="json_number">{{jsonVal}}</span><template v-if="!last">,</template><br>
      </template>
      <!-- Boolean -->
      <template v-else-if="type === 'Boolean'">
        <span class="json_boolean">{{jsonVal ? 'true' : 'false'}}</span><template v-if="!last">,</template><br>
      </template>
    </template>
  </span>`,
  props: ['json-val', 'current-depth', 'max-depth', 'last'],
  data: function () {
    return { open: this.currentDepth < this.maxDepth };
  },
  computed: {
    pad: function () {
      return new Array(this.currentDepth+1).join(padstr);
    },
    type: function () {
      return valueType(this.jsonVal);
    },
    canToggle: function () {
      return this.type === 'Array' || this.type === 'Object';
    }
  },
  methods: {
    toggle: function () {
      this.open = !this.open;
    }
  }
});
Vue.component('json-item', {
  template: `<span>
    <template v-for="(key, i) in keys">
      <json-key :json-key="key" :current-depth="currentDepth"></json-key>
      <json-val :last="i === keys.length-1"
        :json-val="jsonVal[key]"
        :current-depth="currentDepth"
        :max-depth="maxDepth">
      </json-val>
    </template>
  </span>`,
  props: ['json-key', 'json-val', 'current-depth', 'max-depth'],
  computed: {
    pad: function () {
      return new Array(this.currentDepth).join(padstr);
    },
    type: function () {
      return valueType(this.jsonVal);
    },
    keys: function () {
      return Object.keys(this.jsonVal);
    }
  }
});

(function() {
	Vue.use(VueHtml5Editor, {
		name: "vue-html5-editor",
		showModuleName: true,
		image: {
			sizeLimit: 20 * 1024 * 1024,
			upload: {
				url: "action/manageImageInfo/uploadImage?imageType=editerImage",
				headers: {},
				params: {},
				fieldName: {}
			},
			compress: {
				width: 1600,
				height: 1600,
				quality: 100
			},
			uploadHandler:function(responseText){
				var json = JSON.parse(responseText)
				if (json.code==0) {
					return json.model.imgPath
				} else {
					alert(json.msg);
				}
			}
		},
		selectImage:{
			selectMethod:function(fn){
				pageVue.selectImage('','editerImage',300,1,function(msg){
					var arr=msg.split("|");
					fn(arr[3]);
				});
			}
		},
		language: "zh-cn",
		hiddenModules: ["info","undo","link","unlink","list","tabulation","image",],
		visibleModules: ["font","align","text","color","selectImage","hr","eraser","full-screen",]
	});
	/////////////////////////////////
	$.extend({
		getLocationHash:function(){
			var hash=location.hash;
			if(hash.substring(0, 1)=='#') hash=hash.substring(1);
			return hash;
		},
		fillJSONData:function(data,name,value){
			var flag=true;
			var index=0;
			var arr=name.split("");
			for(var i=0;i<arr.length;i++){
				if(arr[i]=="[") flag=false;
				else if(arr[i]=="]") flag=true;
				if(flag&&arr[i]=="."){
					index=i;
					break;
				}
			}
			if(index>0){
				var f1=name.substring(0,index);
				var f2=name.substring(f1.length+1);
				if(!data[f1]){
					data[f1]={};
				}
				$.fillJSONData(data[f1],f2,value);
			}else{
				data[name]=value;
			}
		},
		convertJSONData:function(url,d){
			var tmp=url.indexOf("?")>=0?url.split("?")[0]:url;
			console.log(tmp.indexOf(".html"),tmp.length-5);
			if(tmp.indexOf(".html")==tmp.length-5){
				var newData=$.parseParams(url);
				for(var key in d){
					this.fillJSONData(newData,key,d[key]);
				}
				return newData;
			}else{
				return d;
			}
		},
		parseParams: function(url) {
			var obj = {};
			var keyvalue = [];
			var key = "",value = "";
			var arr = url.substring(url.indexOf("?") + 1, url.length).split("&");
			for (var i=0;i<arr.length;i++) {
				keyvalue = arr[i].split("=");
				key = keyvalue[0];
				value = keyvalue[1];
				$.fillJSONData(obj,key,value);
			}
			return obj;
		},
		jsonToParams:function(a,json){
			var arr={};
			if(typeof(json) == "object" && Object.prototype.toString.call(json).toLowerCase() == "[object object]" && !json.length){
				for(var k in json){
					var key=a+"."+k;
					var obj=$.jsonToParams(key,json[k]);
					for(var n in obj){
						arr[n]=obj[n];
					}
				}
			}else{
				arr[a]=json;
			}
			return arr;
		},
		//execJSON 后台返回后的处理, 返回true后才执行execJSON的回调
		execProcessJSON:function(json){
			return true;
		},
		execJSON:function(url,data,fn,isbody,nospin){
			var spin=nospin?false:true;
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
				if(isbody){
					$.fillJSONData(d,i,tmp);
				}else{
					var obj=$.jsonToParams(i,tmp);
					for(var n in obj){
						d[n]=obj[n];
					}
				}
			}
			$.ajax({
				type:"POST",
				url:url,
				data:isbody?JSON.stringify(d):d,
				dataType:"json",
				headers: {'Content-Type': isbody?'application/json':'application/x-www-form-urlencoded'},
				success:function(ele){
					if($.execProcessJSON(ele)&&fn) fn(ele);
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
		execHTML:function(url,data,fn,isbody,nospin){
			var spin=nospin?false:true;
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
				if(isbody){
					$.fillJSONData(d,i,tmp);
				}else{
					var obj=$.jsonToParams(i,tmp);
					for(var n in obj){
						d[n]=obj[n];
					}
				}
			}
			$.ajax({
				type:"POST",
				url:url,
				data:isbody?JSON.stringify(d):d,
				dataType:"html",
				headers: {'Content-Type': isbody?'application/json':'application/x-www-form-urlencoded'},
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
		//不可以使用动态页面url
		/* 基础模板
	<page>
		<!-- html标签 -->
	</page>
	<script>
	(function(){
		return { //vue对象属性
			data(){
				return {
					//key:'',
					//openKey:'',
				};
			},
			methods:{
				backHandler:function(success,msg){//打开窗体的回调
				}
			}
		};
	})();
	</script>
		
		*/
		loadVuePage:function(page,url,params,fn,efn){
			params['key']="page_"+$.vueId;
			$.vueId++;
			var self=$(page);
			self.html("");
			var pt=$.parseParams(url);
			console.log(pt);
			$.extend(params,pt);
			url=url.indexOf("?")>-1?url.substring(0,url.indexOf("?")):url;
			console.log(url,params);
			var tpage=$.vuePage[url];
			var exec=function(html){
				var pageId=self.attr("id")||params.key;
				var $html=$('<div>'+html+'</div>');
				if($html.children('style').length){
					self.parent().append("<style>"+$html.children('style').html()+"</style>");
				}
				var $page=$("<div></div>");
				$page.attr('id',"vue_"+pageId);
				$page.append($html.children('page').html());
				self.append($page);
				$.vueParams[params.key]=params;
				var js='<script>var vue_js_'+params.key+'='+$html.children('script').html()+'\r\n'
					+'vue_js_'+params.key+'["el"]="#vue_'+pageId+'";\r\n'
					+'vue_js_'+params.key+'["mixins"]=[{data(){return $.vueParams["'+params.key+'"];},methods:vui.util}];\r\n'
					+'$.vue["'+params.key+'"]=new Vue(vue_js_'+params.key+');console.log(vue_js_'+params.key+'["el"])</script>';
				self.append(js);
				if(fn){
					fn($.vue[params.key],params.key);
				}
			};
			if(tpage){
				pageVue.$Loading.finish();
				exec(tpage);
			}else{
				$.execHTML(url,params,function(txt){
					if(url.substring(url.indexOf("."))=='.html'){
						$.vuePage[url]=txt;
					}
					exec(txt);
				},efn);
			}
		}
	});
	$.extend({
		vueId:1,
		vue:{},
		vueParams:{},
		vuePage:{},
		parseNumber:function(str,decimalCount){
			var num;
			var d=parseFloat(str);
			if(d||d==0){
				if(decimalCount){
					var n=Math.pow(10,decimalCount);
					num=Math.round(d*n)/n;
				}else{
					num=parseInt(str);
				}
			}else{
				num="";
			}
			return num;
		},
		vueFormEditMethods:{
			backHandler:function(success,msg){//打开窗体的回调
				if(success)this.backSuccess=true;
				this.handlerResult(this.backEvent,this.openMode,this.backSuccess,msg);
			},
			handlerResult:function(backEvent,openMode,success,msg){
				if(openMode=='MODAL'){
					this.showModal=false;
					//$("#_table_modal_"+this.key).html("");
				}
				console.log(backEvent,openMode,success,msg);
				if(success){
					switch(backEvent){
						case "selectImageBack":
							var arr=msg.split("|");
							$("#image_"+this.key+"_"+arr[0].replace(/\./g,'\\.')).attr("src",arr[2]);
							this.fields[arr[0]]=arr[1];
							break;
						case "selectIconBack":
							var arr=msg.split("|");
							$("#icon_"+this.key+"_"+arr[0].replace(/\./g,'\\.')).attr("src",arr[2]);
							this.fields[arr[0]]=arr[1];
							break;
						case "selectMapBack":
							var arr=msg.split("|");
							this.fields[arr[0]]=arr[1];
							break;
						case "BACK":
							this.back(false);
							break;
						case "DONE_BACK":
							this.back(true);
							break;
						case "REFRESH_OTHER":
							this.refreshOthers();
							break;
						default:
					}
				}
			},
			viewImage:function(f){
				pageVue.viewImage(this.fields[f]);
			},
			openImageModal:function(f,imageType,width,ratio){
				this.backEvent='selectImageBack';
				this.openMode='MODAL';
				var self=this;
				this.modalWidth=870;
				$.loadVuePage($("#_table_modal_"+self.key),
					"action/manageImageInfo/selectImagePage",
					{"selected":this.fields[f],"field":f,"imageType":imageType,"adminToken":"","thumWidth":width||"","thumRatio":ratio||"",openKey:this.key},
					(function(vueObj,vueId){
						self.showModal=true;
					}).bind(this)
				);
			},
			openIconModal:function(f,imageType,width,ratio){
				this.backEvent='selectIconBack';
				this.openMode='MODAL';
				var self=this;
				this.modalWidth=840;
				$.loadVuePage($("#_table_modal_"+self.key),
					"page/manage/image/iconManageList.html",
					{"selected":this.fields[f],"field":f,"oper":"select",openKey:this.key},
					(function(vueObj,vueId){
						self.showModal=true;
					}).bind(this)
				);
			},
			openMapModal:function(f,edit){
				this.backEvent='selectMapBack';
				this.openMode='MODAL';
				var self=this;
				this.modalWidth=700;
				$.loadVuePage($("#_table_modal_"+self.key),
					"action/managePageUtil/selectMapPage",{"selected":this.fields[f],"field":f,edit:edit,openKey:this.key},
					(function(vueObj,vueId){
						self.showModal=true;
					}).bind(this)
				);
			},
			parseNumber:function(f,decimalCount){
				var str=this.fields[f],num;
				num=$.parseNumber(str,decimalCount);
				this.fields[f]=num;
			},
			fileUploadSuccess:function(res, file, fileList){
				if(res.code==0){
					this.fields[res.field]=res.model.oid;
					this.fileName[res.field]=res.model.name;
					console.log(res, file, fileList);
					pageVue.$Spin.hide();
					pageVue.$Loading.finish();
				}else{
					pageVue.$Spin.hide();
					pageVue.$Loading.error();
					pageVue.$Message.error(res.msg);
				}
			},
			fileUploadPrewiew:function(file){
				pageVue.$Spin.show();
				pageVue.$Loading.start();
			},
			initSelectMethod:function(){
				var obj={};
				for(var f in this.selectMethod){
					obj[f]=true;
				}
				for(var f in this.clearField){
					obj[this.clearField[f]]=false;
				}
				for(var f in obj){
					if(obj[f]) this.execSelectMethod(f);
				}
			},
			execSelectMethod:function(f){
				var method=this.selectMethod[f];
				if(method){
					if(method.conditions&&method.conditions.length){
						for(var i=0;i<method.conditions.length;i++){
							var cond=method.conditions[i];
							if(cond.type=='EQ_MODEL'){
								cond.type='EQ';
								cond.value=this.fields[cond.value];
							}
						}
					}
					method.field=f;
					method.valueFieldValue="";
					if(method.valueField){
						var vfarr=method.valueField.split("|");
						for(var i=0;i<vfarr.length;i++){
							if(i!=0) method.valueFieldValue+="|";
							method.valueFieldValue+=this.fields[vfarr[i]];
						}
					}
					if(!method.linkField||method.linkField&&method.valueFieldValue){
						$.execJSON("action/managePageUtil/getSelectData",method,(function(json){
							var field=json.field;
							this.selectDatas[field].length=0;
							this.selectLabels[field].length=0;
							for(var i=0;i<json.data.length;i++){
								this.$set(this.selectDatas[field],i,json.data[i]);
								if(this.selectLabels[field].indexOf(json.data[i].label)<0){
									this.$set(this.selectLabels[field],this.selectLabels[field].length,json.data[i].label);
								}
							}
							this.setCascaderValue(field);
							if(this.$refs[field]){
								this.$refs[field].clearSingleSelect();
							}
							this.fields[field]=this.initFields[field];
							this.doClearField(field);
						}).bind(this));
					}
				}
			},
			setCascaderValue:function(f){
				var v=this.fields[f];
				var fn=function(ls){
					for(var i=0;i<ls.length;i++){
						var arr=ls[i];
						if(arr.children){
							var rs=fn(arr.children);
							if(rs){
								rs.unshift(arr.value);
								return rs;
							}
						}else if(arr.value==v){
							return [v];
						}
					}
				};
				var vs=fn(this.selectDatas[f]);
				if(vs){
					this.cascaders[f]=vs;
					console.log(this.cascaders[f]);
				}
			},
			doClearField:function(f,other){
				if(other){//级联选择传参
					var arr=other[0];
					this.cascaders[f]=arr;
					if(arr.length>0){
						this.fields[f]=arr[arr.length-1];
					}else{
						this.fields[f]="";
					}
				}
				var cf=this.clearField[f];
				if(cf){
					if(this.fields[cf] instanceof Array){
						this.fields[cf]=[];
					}else{
						this.fields[cf]='';
					}
					this.execSelectMethod(cf);
				}
			},
			submitHandler:function(e){
				var ele=$(e.srcElement);
				ele=ele.attr("type")=="button"?ele:ele.parent();
				var text=ele.find("span.n-btn_title").text();
				var self=this;
				var param=this.buttons[text];
				this.backEvent=param.success;
				this.openMode=param.event;
				var dd;
				if(param.method=='FORM_SUBMIT'){
					dd=this.fields;
				}else if(param.method=='PARAMS_SUBMIT'&&param.params){
					dd={};
					for(var i=0;i<param.params.length;i++){
						var arr=param.params[i];
						dd[arr[0]]=arr[1]?this.fields[arr[1]]:arr[2];
					}
				}
				var flag=true;
				for(var fd in dd){
					if((null===dd[fd]||""===dd[fd]||undefined===dd[fd])&&this.requiredField[fd]){
						flag=false;
					}
				}
				if(!flag){
					self.$Message.error("必填项不能为空");
					return;
				}
				dd['openKey']=this.key;
				dd['openMode']=param.event;
				var fn=function(){
					if("AJAX"==param.event){
						$.execJSON(param.url,dd,function(json){
							if(json.code==0){
								self.$Message.success(json.msg);
								for(var f in self.fields){
									if(null!=json[f]){
										self.fields[f]=json[f];
									}
								}
								self.backSuccess=true;
								self.handlerResult(param.success,'',true);
							}else{
								self.$Message.error(json.msg)
							}
						});
					}else if("MODAL"==param.event){
						self.modalWidth=param.width;
						$.loadVuePage($("#_table_modal_"+self.key),
							param.url,$.convertJSONData(param.url,dd),
							(function(vueObj,vueId){
								self.showModal=true;
							}).bind(this)
						);
					}
				};
				if(param.confirm){
					var self=this;
					this.$Modal.confirm({
						title: '操作确认',
						content: '<p>'+param.confirm+'</p>',
						loading: true,
						onOk:function(){
							fn();
							self.$Modal.remove();
						}
					});
				}else fn();
			},
			refreshOthers:function(){
				for(var k in this.others){
					var param=this.others[k];
					var b=true;
					var d={};
					if(param.field){
						if(param.valueField){
							var vfarr=param.valueField.split("|");
							for(var i=0;i<vfarr.length;i++){
								var v=this.fields[vfarr[i]];
								if(v){
									d[vfarr[i]]=v;
								}else{
									b=false;
								}
							}
						}else{
							b=false;
						}
					}
					if(b){
						$("#"+k+"_"+this.key).css("display","");
						if(param.url){
							$.loadVuePage($("#"+k+"_"+this.key+"_content"),
								param.url,$.convertJSONData(param.url,d),
								(function(vueObj,vueId){
								}).bind(this)
							);
						}
					}else{
						$("#"+k+"_"+this.key).css("display","none");
						$("#"+k+"_"+this.key+"_content").html("");
					}
				}
			},
			updateEditerData:function(content,field){
				this.fields[field]=content;
			},
			getCurrentStep:function(field){
				var arr=this.selectDatas[field];
				if(arr){
					for(var i=0;i<arr.length;i++){
						if(arr[i].value==this.fields[field]) return i;
					}
				}
			},
			convertMessage:function(msg){
				if(!msg) return "";
				var arr=msg.match(/\#\{.+?\}/gi);
				if(null!=arr&&arr.length>0){
					for(var i=0,len=arr.length;i<len;i++){
						var str=arr[i];
						var key=str.substring(2,str.length-1);
						var ls=this.selectDatas[key];
						var vc=this.cascaders[key];
						var value=this.fields[key];
						var lab="";
						if(vc){
							var idx=0;
							for(var n=0;n<vc.length;n++){
								var vls=ls;
								if(n>0) vls=ls[idx].children;
								for(var m=0;m<vls.length;m++){
									if(vls[m].value==vc[n]){
										idx=m;
										lab+=(n>0?" / ":"")+vls[m].label;
										continue;
									}
								}
							}
						}else if(ls){
							for(var n=0;n<ls.length;n++){
								if(ls[n].value==value){
									lab=ls[n].label;
									continue;
								}
							}
						}
						if(!lab){
							lab=value;
						}
						msg=msg.replace(str,lab);
					}
				}
				return msg;
			}
		},
		vueTableListMethods:{
			backHandler:function(success,msg){//打开窗体的回调
				if(success)this.backSuccess=true;
				this.handlerResult(this.backEvent,this.openMode,this.backSuccess,msg);
			},
			handlerResult:function(backEvent,openMode,success,msg){
				console.log(backEvent,openMode,success,msg);
				if(openMode=='PAGE'){
					this.showList=true;
					this.showPage=false;
					//$("#_table_page_"+this.key).html("");
				}else if(openMode=='MODAL'){
					this.showModal=false;
					//$("#_table_modal_"+this.key).html("");
				}
				if(success){
					switch(backEvent){
						case "REFRESH":
							this.query();
							break;
						default:
					}
				}
				switch(backEvent){
					case "MUST_REFRESH":
						this.query();
						break;
					default:
				}
			},
			parseNumber:function(f,decimalCount){
				var str=this.param[f],num;
				num=$.parseNumber(str,decimalCount);
				this.param[f]=num;
			},
			initSelectMethod:function(){
				var obj={};
				for(var f in this.selectMethod){
					obj[f]=true;
				}
				for(var f in this.clearField){
					obj[this.clearField[f]]=false;
				}
				for(var f in obj){
					if(obj[f]) this.execSelectMethod(f);
				}
			},
			execSelectMethod:function(f){
				var self=this;
				var method=this.selectMethod[f];
				if(method){
					console.log(method);
					if(method.conditions&&method.conditions.length){
						for(var i=0;i<method.conditions.length;i++){
							var cond=method.conditions[i];
							if(cond.type=='EQ_MODEL'){
								cond.type='EQ';
								cond.value=this.param['params['+cond.value+']'];
							}
						}
					}
					method.field=f;
					method.valueFieldValue="";
					if(method.valueField){
						var vfarr=method.valueField.split("|");
						for(var i=0;i<vfarr.length;i++){
							if(i!=0) method.valueFieldValue+="|";
							method.valueFieldValue+=this.param['params['+vfarr[i]+']'];
						}
					}
					if(!method.linkField||method.linkField&&method.valueFieldValue){
						$.execJSON("action/managePageUtil/getSelectData",method,function(json){
							var field=json.field;
							self.selectDatas[field]=json.data;
							if(self.$refs[field]){
								self.$refs[field].clearSingleSelect();
							}
							self.setCascaderValue(field);
							self.doClearField(field);
						});
					}
				}
			},
			setCascaderValue:function(f){
				var v=this.param['params['+f+']'];
				var fn=function(ls){
					for(var i=0;i<ls.length;i++){
						var arr=ls[i];
						if(arr.children){
							var rs=fn(arr.children);
							if(rs){
								rs.unshift(arr.value);
								return rs;
							}
						}else if(arr.value==v){
							return [v];
						}
					}
				};
				var vs=fn(this.selectDatas[f]);
				if(vs){
					this.cascaders[f]=vs;
				}
			},
			doClearField:function(f,other){
				if(other){//级联选择传参
					var arr=other[0];
					if(arr.length>0){
						this.param['params['+f+']']=arr[arr.length-1];
					}else{
						this.param['params['+f+']']="";
					}
				}
				var cf=this.clearField[f];
				if(cf){
					if(undefined!=this.param['params['+cf+']']){
						this.param['params['+cf+']']='';
					}else{
						this.param['params['+cf+'down]']='';
						this.param['params['+cf+'up]']='';
					}
					this.execSelectMethod(cf);
				}
			},
			inlineHandler:function(param,data){
				this.backEvent=param.success;
				this.openMode=param.event;
				var d={openKey:this.key,openMode:param.event};
				for(var n=0;n<param.params.length;n++){
					var arr=param.params[n];
					d[arr[0]]=arr[1]?data[arr[1].replace(/\./g,'_')]:arr[2];
				}
				for(var n=0;n<param.queryParams.length;n++){
					var arr=param.queryParams[n];
					d[arr[0]]=arr[1]?this.values[arr[1]]:arr[2];
				}
				var fn=(function(){
					if("AJAX"==param.event){
						$.execJSON(param.url,d,(function(json){
							if(json.code==0){
								this.$Message.success(json.msg);
								this.handlerResult(param.success,'',true);
							}else{
								this.$Message.error(json.msg);
								this.handlerResult(param.success,'',false);
							}
						}).bind(this));
					}else if("PAGE"==param.event){
						$.loadVuePage($("#_table_page_"+this.key),
							param.url,$.convertJSONData(param.url,d),
							(function(vueObj,vueId){
								this.showList=false;
								this.showPage=true;
							}).bind(this)
						);
					}else if("MODAL"==param.event){
						this.modalWidth=param.width;
						$.loadVuePage($("#_table_modal_"+this.key),
							param.url,$.convertJSONData(param.url,d),
							(function(vueObj,vueId){
								this.showModal=true;
							}).bind(this)
						);
					}else if("OPEN"==param.event){
						var data={};
						for(var i in d){
							if(d[i] instanceof Date){
								data[i]=d[i].format('yyyy-MM-dd HH:mm:ss');
							}else{
								data[i]=d[i];
							}
						}
						window.open(param.url+(param.url.indexOf("?")>=0?"&":"?")+$.param(data));
					}
				}).bind(this);
				if(param.confirm){
					var self=this;
					this.$Modal.confirm({
						title: '操作确认',
						content: '<p>'+param.confirm+'</p>',
						loading: true,
						onOk:function(){
							fn();
							self.$Modal.remove();
						}
					});
				}else fn();
			},
			toolsHandler:function(param){
				this.backEvent=param.success;
				this.openMode=param.event;
				var self=this;
				var ds=this.selected;
				var d={openKey:this.key,openMode:param.event};
				if(param.useOther){
					d=self[param+this.key];
					d['openMode']=param.event;
					d['backEvent']=param.success;
					d['openKey']=this.key;
				}
				for(var n=0;n<param.queryParams.length;n++){
					var arr=param.queryParams[n];
					d[arr[0]]=arr[1]?this.param["params["+arr[1]+"]"]:arr[2];
				}
				if("AJAX"!=param.event){
					for(var n=0;n<param.params.length;n++){
						var arr=param.params[n];
						var arrd=[];
						for(var i=0;i<ds.length;i++){
							arrd.push(arr[1]?ds[i][arr[1]]:arr[2]);//.replace(/\./g,'_')
						}
						d[arr[0]]=arrd+"";
					}
				}
				if("PAGE"==param.event){
					$.loadVuePage($("#_table_page_"+this.key),
						param.url,$.convertJSONData(param.url,d),
						(function(vueObj,vueId){
							this.showList=false;
							this.showPage=true;
						}).bind(this)
					);
					return;	
				}else if("MODAL"==param.event){
					self.modalWidth=param.width;
					$.loadVuePage($("#_table_modal_"+self.key),
						param.url,$.convertJSONData(param.url,d),
						(function(vueObj,vueId){
							self.showModal=true;
						}).bind(this)
					);
					return;
				}else if("OPEN"==param.event){
					var data={};
					for(var i in d){
						if(d[i] instanceof Date){
							data[i]=d[i].format('yyyy-MM-dd HH:mm:ss');
						}else{
							data[i]=d[i];
						}
					}
					window.open(param.url+(param.url.indexOf("?")>=0?"&":"?")+$.param(data));
					return;
				}
				if(!(ds.length!=0||param.params.length==0)){
					this.$Message.error("请选择要操作的记录");
					return;
				}
				var num=0,msg="",n1=0,n2=0;
				var fn=function(){
					self.loadModal.show=true;
					self.loadModal.content="执行中";
					if(num<ds.length||param.params.length==0){
						self.loadModal.percent=ds.length>0?num/ds.length*100:99;
						for(var n=0;n<param.params.length;n++){
							var arr=param.params[n];
							d[arr[0]]=arr[1]?ds[num][arr[1]]:arr[2];//.replace(/\./g,'_')
						}
						$.execJSON(param.url,d,function(json){
							if(param.params.length==0){
								self.loadModal.show=false;
								if(json.code==0){
									self.handlerResult(param.success,'',true);
									self.$Message.success(json.msg||'没有返回消息');
								}else{
									self.handlerResult(param.success,'',false);
									self.$Message.error(json.msg||'没有返回消息');
								}
							}else{
								if(json.code==0){n1++;}
								else{n2++; msg+="<br/>"+json.msg;}
								num++;setTimeout(fn,100);
							}
							self.loadModal.content="操作执行中，成功"+n1+"条"+(n2?("，失败"+n2+"条"+msg):"");
						});
					}else{
						if(n1==num) self.$Message.success("操作全部完成");
						else self.$Message.error("操作成功"+n1+"条，失败"+n2+"条"+msg);
						self.loadModal.show=false;
						self.handlerResult(param.success,'',true);
					}
				}
				if(param.confirm){
					var self=this;
					this.$Modal.confirm({
						title: '操作确认',
						content: '<p>'+param.confirm+'</p>',
						loading: true,
						onOk:function(){
							fn();
							self.$Modal.remove();
						}
					});
				}else fn();
			},
			colRender:function(h,params,key){
				var self=this;
				var buttons=params.column.buttons;
				var dropButtons=params.column.dropButtons;
				var link=params.column.link;
				var row=params.row;
				var isFun=false;
				var onFun={};
				if(!row._count_row&&link
						&&(!link.hiddenField||link.hiddenValues.indexOf(row[link.hiddenField])<0)
						&&(!link.showField||link.showValues.indexOf(row[link.showField])>=0)){
					isFun=true;
					onFun['click']=function(e){var ele=$(e.srcElement);self.inlineHandler(link.param,row);};
				}
				if(!row._count_row&&(buttons&&buttons.length||dropButtons&&dropButtons.length)){
					var arr=[];
					var btnObject={};
					for(var i=0;i<buttons.length;i++){
						var btn=buttons[i];btnObject[btn.title]=btn;
						if((!btn.hiddenField||btn.hiddenValues.indexOf(row[btn.hiddenField])<0)
								&&(!btn.showField||btn.showValues.indexOf(row[btn.showField])>=0)){
							arr.push(h('i-button', {props: {type: btn.style,size: 'small'},domProps:{innerHTML:'<i class="iconfont">'+btn.icon+'</i>&nbsp;<span>'+btn.title+'</span>&nbsp;'},
								on: {click:function(e){var ele=$(e.srcElement);ele=ele.attr("type")=="button"?ele:ele.parent();self.inlineHandler(btnObject[ele.find("span").text()].param,row);}}
							}, ''));
						}
					}
					for(var i=0;i<dropButtons.length;i++){
						var dbtn=dropButtons[i];
						var ds=[];
						for(var n=0;n<dbtn.buttons.length;n++){
							var db=dbtn.buttons[n];btnObject[dbtn.title+db.title]=db;
							if((!db.hiddenField||db.hiddenValues.indexOf(row[db.hiddenField])<0)
									&&(!db.showField||db.showValues.indexOf(row[db.showField])>=0)){
								ds.push(h('dropdown-item',{props:{name:dbtn.title+db.title},domProps:{innerHTML:'<i class="iconfont">'+db.icon+'</i>&nbsp;<span>'+db.title+'</span>&nbsp;'}},''));
							}
						}
						if((!dbtn.hiddenField||dbtn.hiddenValues.indexOf(row[dbtn.hiddenField])<0)
								&&(!dbtn.showField||dbtn.showValues.indexOf(row[dbtn.showField])>=0)){
							arr.push(h('i-button', {props: {type: dbtn.style,size: 'small'}},
								[h('dropdown',{props:{transfer:true},
										on:{'on-click':function(e){self.inlineHandler(btnObject[e].param,row);}}},[
									h('span',{domProps:{innerHTML:'<i class="iconfont">'+dbtn.icon+'</i>&nbsp;<span>'+dbtn.title+'</span>&nbsp;'}},''),
									h('icon',{props:{type:'ios-arrow-down'}},''),
									h('dropdown-menu',{slot:'list'},[ds])
								])]
							));
						}
					}
					return h('button-group', arr);
				}else{
					return h(isFun?'a':'span',{on:onFun}, row[key]);
				}
			},
			statusRender:function(h,params,key){
				if(params.row._count_row)return h('span', '');
				var self=this;
				var obj={value:params.row[key]=='0'?true:false};
				var sh=h('i-switch',{props:obj,
					on:{"on-change":function(flag){
						var method=flag?"doRecovery":"doDisable";
						$.execJSON(self.dataUrl.substring(0,self.dataUrl.lastIndexOf("/")+1)+method,{"model.oid":params.row['oid']},function(json){
							if(json.code==0){
								self.$Message.success(json.msg);
							}else{
								self.$Message.error(json.msg);
								self.query();
							}
						});
					}}
				});
				return sh;
			},
			colorRender:function(h,params,key){
				return h('span',{props:{},style:{color:'#fff',padding:'0 7px',backgroundColor:params.row[key]}},' ');
			},
			imageRender:function(h,params,key){
				var self=this;
				var link=params.column.link;
				var row=params.row;
				var onFun={};
				if(!row._count_row&&link
						&&(!link.hiddenField||link.hiddenValues.indexOf(row[link.hiddenField])<0)
						&&(!link.showField||link.showValues.indexOf(row[link.showField])>=0)){
					onFun['click']=function(e){var ele=$(e.srcElement);self.inlineHandler(link.param,row);};
				}
				if(row[key]){
					return h('img',{props:{},on:onFun,style:{height:'50px'},attrs:{src:row[key]}});
				}else{
					return h('span',{},'');
				}
			},
			summaryMethod:function(obj){
				var sums={};
				if(null!=this.countData){
					var cols=obj.columns;
					for(var i=0;i<cols.length;i++){
						var key=cols[i].key;
						sums[key]={key:key,value:this.countData[key]};
					}
				}
				return sums;
			},
			spanMethod:function(obj){
				var row=obj.row, column=obj.column, rowIndex=obj.rowIndex, columnIndex=obj.columnIndex;
				var span=row['_rowspan_num.'+column.key];
				if(!isNaN(span)){
					return [span,1];
				}else{
					return [1,1];
				}
			},
			query:function(){
				this.tableLoading=true;
				$.execJSON(this.dataUrl,this.param,(function(json){
					this.tableLoading=false;
					if(json.code==0){
						var arr=[];
						for(var i=0;i<json.data.length;i++){
							var dd=json.data[i];
							if(dd._count_row){
								this.countData=dd;
							}else{
								arr.push(dd);
							}
						}
						this.datas=arr;
						this.count=json.count;
					}else{
						this.$Message.error(json.msg);
					}
				}).bind(this),false,true);
				this.searchPanel=false;
			},
			queryList:function(){
				this.param.pageNo=1;
				this.query();
			},
			changePageNo:function(no){
				this.param.pageNo=no;
				this.query();
			},
			changePageNum:function(num){
				this.param.pageNum=num;
				this.query();
			},
			selectHandler:function(items){
				this.selected=items;
			},
			sortHandler:function(column){
				if(column.order=='asc'){
					this.param['order.name']=column.key;
					this.param['order.oper']=column.order;
				}else if(column.order=='desc'){
					this.param['order.name']=column.key;
					this.param['order.oper']=column.order;
				}else{
					this.param['order.name']='';
					this.param['order.oper']='';
				}
				this.query();
			},
			initSort:function(){
				for(var i=0;i<this.columns.length;i++){
					var col=this.columns[i];
					if(col.sortType){
						this.param['order.name']=col.key;
						this.param['order.oper']=col.sortType;
					}
				}
			},
			queryChart:function(){
				var option=this.chartOption;
				$.execJSON(this.dataUrl,this.param,(function(json){
					if(json.code==0){
						for(var k in json.data){
							this.chartOption[k]=json.data[k];
						} 
						if(!this.chartElement) this.chartElement=echarts.init(this.$refs['ichart']);
						console.log(this.chartOption);
						this.chartElement.setOption(this.chartOption,true);
					}else{
						self.$Message.error(json.msg);
					}
				}).bind(this));
				this.searchPanel=false;
			}
		}
	});
	$.fn.extend({
		
	});
	$(document).ready(function() {
		var pageVue=new Vue({
			el:document.createElement("div"),
			template:`<div>
				<modal width="60%" v-model="showImageModal" :footer-hide="true" :closable="false">
					<div id="show_image_page"></div>
					<div slot="footer">
					</div>
				</modal>
				<modal v-model="showModal" :width="modalWidth" :footer-hide="true" :mask-closable="false">
					<div id="show_page"></div>
					<div slot="footer">
					</div>
				</modal>
			</div>`,
			data:{
				showImageModal:false,
				showModal:false,
				modalWidth:800,
			},
			methods:{//主页面方法
				viewImage:function(imageOid){
					if(imageOid){
						var self=this;
						$.execHTML("action/manageImageInfo/viewImage",{"imageOid":imageOid},function(txt){
							$("#show_image_page").html(txt);
							self.showImageModal=true;
						});
					}
				},
				backHandler:function(success,msg){//打开窗体的回调\
					if(success){
						this.selectImageCallback(msg);
					}
					this.showModal=false;
				},
				selectImageCallback:function(msg){
					console.log(msg);
				},
				selectImage:function(oid,imageType,thumWidth,thumRatio,callback){
					this.selectImageCallback=callback;
					this.modalWidth=870;
					$.loadVuePage($("#show_page"),
						"action/manageImageInfo/selectImagePage",
						{"selected":'',"field":'selectImage',"imageType":imageType,"adminToken":"","thumWidth":thumWidth||"","thumRatio":thumRatio||"",openKey:"pageVue"},
						(function(vueObj,vueId){
							this.showModal=true;
						}).bind(this)
					);
				}
			}
		});
		document.body.appendChild(pageVue.$el);
		$.vue['pageVue']=pageVue;
		window.pageVue=pageVue;
	});
}).call(this);
