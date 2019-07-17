
$(function(){
	Date.prototype.format=function(fmt){ 
		if(!fmt) return "";
		var d = this;
		var zeroize = function (value, length){
			if (!length) length = 2;
			value = String(value);
			for (var i = 0, zeros = ''; i < (length - value.length); i++){
				zeros += '0';
			}
			return zeros + value;
		};
		return fmt.replace(/"[^"]*"|'[^']*'|\b(?:d{1,4}|m{1,4}|yy(?:yy)?|([hHMstT])\1?|[lLZ])\b/g, function ($0){
			switch ($0){
				case 'd': return d.getDate();
				case 'dd': return zeroize(d.getDate());
				case 'ddd': return ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][d.getDay()];
				case 'dddd': return ['星期天', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][d.getDay()];
				case 'M': return d.getMonth() + 1;
				case 'MM': return zeroize(d.getMonth() + 1);
				case 'MMM': return ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'][d.getMonth()];
				case 'MMMM': return ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'][d.getMonth()];
				case 'yy': return String(d.getFullYear()).substr(2);
				case 'yyyy': return d.getFullYear();
				case 'h': return d.getHours() % 12 || 12;
				case 'hh': return zeroize(d.getHours() % 12 || 12);
				case 'H': return d.getHours();
				case 'HH': return zeroize(d.getHours());
				case 'm': return d.getMinutes();
				case 'mm': return zeroize(d.getMinutes());
				case 's': return d.getSeconds();
				case 'ss': return zeroize(d.getSeconds());
				case 'l': return zeroize(d.getMilliseconds(), 3);
				case 'L': var m = d.getMilliseconds();
				if (m > 99) m = Math.round(m / 10);
				return zeroize(m);
				case 'tt': return d.getHours() < 12 ? '上' : '下';
				case 'TT': return d.getHours() < 12 ? '上午' : '下午';
				case 'Z': return d.toUTCString().match(/[A-Z]+$/);
				// Return quoted strings with the surrounding quotes removed
				default: return $0.substr(1, $0.length - 2);
			}
		});
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
			return index;
		}else{
			return -1;
		}
	};
	Array.prototype.addAll = function(arr){
		for(var i=0;i<arr.length;i++){
			this.push(arr[i]);
		}
	}
	$(window).on('hashchange',function(){
		$.closeMessage();
		$.closeMenu();
		$.loading(false);
		if($.pageManager._flag){}else{
			$.pageManager._flag="finally";
			$.pageManager._message=this._flag;
		}
		if($.getLocationHash()){
			$.pageManager.gotoHash();
		}else{
			$.pageManager.closeAllPage();
		}
	});
	window.pm=$.pageManager;
	$.ajaxSetup({
		error:function(res){
			$.remind(res.responseText);
			$.loading(false);
		}
	});
});	
//----------------------
$.fn.extend({
	loadInnerHtml:function(src,data,fn){
		$.loading(true);
		return this.each(function(){
			var self=$(this);
			$.execHTML(src,data,function(ele){
				self.html($(ele).html());
				$.loading(false);
				if(fn) fn();
			});
		});
	},
});

$.extend({
	vueId:1,
	vue:{},
	vuePage:{},
	vueParams:{},
	mconfig:{
		business:"",
		setting:{},
		busi:{},
		api:{}
	},
	_mask_timer:null,
	loading:function(flag,load_txt){//加载中窗体  flag:true显示,false隐藏
		if(!load_txt)load_txt="加载中...";
		var mask=$("#page_mask");
		if(mask.length==0){
			$(document.body).prepend('<div id="page_mask" style="display:none;">'
				+'<div class="weui-mask_transparent"></div>'
				+'<div class="weui-toast">'
				+'<i class="weui-loading weui-icon_toast"></i>'
				+'<p class="weui-toast__content" id="page_mask_txt">数据处理中</p>'
				+'</div>'
				+'</div>');
			mask=$("#page_mask");
		}
		$("#page_mask_txt").html(load_txt);
		if(this._mask_timer) clearTimeout(this._mask_timer);
		if(flag){
			this._mask_timer=setTimeout((function(){
				this.show();
			}).bind(mask),100);
		}else{
			this._mask_timer=setTimeout((function(){
				this.hide();
			}).bind(mask),100);
		}
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
	parseParams: function(url) {
		var obj = {};
		var keyvalue = [];
		var key = "",value = "";
		var arr = url.substring(url.indexOf("?") + 1, url.length).split("&");
		for (var i=0;i<arr.length;i++) {
			var ind=arr[i].indexOf("=");
			key = arr[i].substring(0,ind);
			value = arr[i].substring(ind+1);
			$.fillJSONData(obj,key,value);
		}
		return obj;
	},
	execJSON:function(url,data,fn,efn,isbody,async,noload){
		if(typeof(efn)=='boolean') isbody=efn;
		if(async) async=false; else async=true;
		if(!noload) $.loading(true);
		if(!data) data={};
		if(data instanceof Array){
			data.push({'name':'business','value':$.mconfig.business});
		}else{
			data['business']=$.mconfig.business;
		}
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
				d[i]=tmp
			}
		}
		$.ajax({
			type:"POST",
			url:url,
			async:async,
			data:isbody?JSON.stringify(d):d,
			dataType: "JSON",
			headers: {'Content-Type': isbody?'application/json':'application/x-www-form-urlencoded'},
			success:function(ele){
				$.loading(false);
				if(fn){
					fn(ele);
				}else{
					$.remind(ele);
				}
			},
			error:efn||function(res){
				$.remind(res.responseText);
				$.loading(false);
			}
		});
	},
	execHTML:function(url,data,fn,efn,isbody,async,noload){
		if(typeof(efn)=='boolean') isbody=efn;
		if(async) async=false; else async=true;
		if(!noload) $.loading(true);
		if(!data) data={};
		if(data instanceof Array){
			data.push({'name':'business','value':$.mconfig.business});
		}else{
			data['business']=$.mconfig.business;
		}
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
				d[i]=tmp
			}
		}
		$.ajax({
			type:"POST",
			url:url,
			async:async,
			data:isbody?JSON.stringify(d):d,
			dataType:"html",
			headers: {'Content-Type': isbody?'application/json':'application/x-www-form-urlencoded'},
			success:function(ele){
				$.loading(false);
				if(fn){
					fn(ele);
				}else{
					$.remind(ele,true);
				}
			},
			error:efn||function(res){
				$.remind(res.responseText,false);
				$.loading(false);
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
		data:{
		},
		methods:{
		}
	};
})();
</script>
	
	*/
	loadVuePage:function(page,url,params,fn,efn){
		console.log(url,params);
		var self=$(page);
		var pt=$.parseParams(url);
		$.extend(params,pt);
		url=url.indexOf("?")>-1?url.substring(0,url.indexOf("?")):url;
		var tpage=$.vuePage[url];
		var exec=function(html){
			var pageId=self.attr("id")||'vue_page_'+$.vueId;
			var $html=$('<div>'+html+'</div>');
			if($html.children('style').length){
				self.parent().append("<style>"+$html.children('style').html()+"</style>");
			}
			var $page=self;
			self.attr('id',pageId);
			$page.append($html.children('page').html());
			self.append($page);
			$.vueParams[$.vueId]=params;
			var js='<script>var vue_js_'+$.vueId+'='+$html.children('script').html()+'\r\n'
				+'vue_js_'+$.vueId+'["el"]="#'+pageId+'";\r\n'
				+'vue_js_'+$.vueId+'["mixins"]=[{data(){return $.vueParams["'+$.vueId+'"];},methods:vui.util}];\r\n'
				+'$.vue["'+$.vueId+'"]=new Vue(vue_js_'+$.vueId+');</script>';
			self.append(js);
			if(fn){
				fn($.vue[$.vueId],$.vueId);
			}
			$.vueId++;
		};
		if(tpage){
			$.loading(false);
			exec(tpage);
		}else{
			$.execHTML(url,params,function(txt){
				if(url.substring(url.indexOf("."))=='.html'){
					$.vuePage[url]=txt;
				}
				exec(txt);
			},efn);
		}
	},
	isMessageClick:false,
	messageFunctionArray:[],
	closeMessage:function(i){
		if(!this.isMessageClick)return;
		this.isMessageClick=false;
		$("#messageDialog").addClass('weui-animate-fade-out').on('animationend webkitAnimationEnd',function(){
			$("#messageDialog").remove();
			if(typeof(i)=='number'){
				var method=$.messageFunctionArray[i];
				if(method){
					method();
				}
			}
		});
	},
	message:function(message,title,operArray){
		var self=this;
		if(null==operArray||operArray.length==0){
			operArray=[{"label":"知道了","type":"primary"}];
		}
		var msghtml='<div id="messageDialog" class="weui-mask_transparent">'
			+'<div class="weui-mask"></div>'
			+'<div class="weui-dialog">'
			+(title?'<div class="weui-dialog__hd"><strong class="weui-dialog__title">'+title+'</strong></div>':'')
			+'<div class="weui-dialog__bd">'+message+'</div>'
			+'<div class="weui-dialog__ft">';
		this.messageFunctionArray.length=0;
		for(var i=0;i<operArray.length;i++){
			var oper=operArray[i];
			this.messageFunctionArray.push(oper.method);
			msghtml+='<a href="javascript:;" onclick="$.closeMessage('+i+');" class="weui-dialog__btn weui-dialog__btn_'+oper.type+'">'+oper.label+'</a>';
		}
		msghtml+='</div></div></div>';
		var $msg=$(msghtml);
		$(document.body).prepend($msg);
		$msg.addClass('weui-animate-fade-in').on('animationend webkitAnimationEnd',function(){
			$("#messageDialog").removeClass('weui-animate-fade-in').unbind("animationend webkitAnimationEnd");
			self.isMessageClick=true;
		});
	},
	alert:function(message,title,method){
		this.message(message,title,
			[{"label":"知道了","type":"primary","method":method}]
		);
	},
	confirm:function(message,title,method1,method2){
		this.message(message,title,
			[{"label":"取消","type":"default","method":method2},
			{"label":"确定","type":"primary","method":method1}]
		);
	},
	remindTimer:null,
	remind:function(message,ico){
		if($("#remindDialog").length){
			$("#remindMessage").html("");
		}else{
			var msghtml='<div id="remindDialog" style="display:none;">'
				//+'<div class="weui-mask_transparent"></div>'
				+'<div class="remind_message" id="remindMessage">'
				+'</div>'
				+'</div>';
			var $msg=$(msghtml);
			$(document.body).prepend($msg);
		}
		if(ico){
		}else{
			ico="weui-icon-info-circle";
		}
		var remindDialog=$("#remindDialog");
		var remindMessage=$("#remindMessage");
		remindMessage.html('<i class="'+ico+'"></i>'+message);
		if($.remindTimer) 
			clearTimeout($.remindTimer);
		else 
			remindDialog.css("display","block");
		remindMessage.css("margin-left",-remindMessage.width());
		remindMessage.css("margin-left",-10-remindMessage.width()/2);
		$.remindTimer=setTimeout(function(){
			remindDialog.css("display","none");
			$.remindTimer=null;
		},3000);
	},
	isMenuClick:false,
	menuFunctionArray:[],
	closeMenu:function(i){
		if(!this.isMenuClick)return;
		this.isMenuClick=false;
		if(typeof(i)=='number'){
			var method=$.menuFunctionArray[i];
			if(method){
				method(i-1);
			}
		}
		$("#menuDialog").addClass('weui-animate-slide-bottom').on('animationend webkitAnimationEnd',function(){
			$("#menuDialog").remove();
		});
		$("#menuMask").addClass('weui-animate-fade-out').on('animationend webkitAnimationEnd',function(){
			$("#menuMask").remove();
		});
	},
	menu:function(operArray){
	//////[{"title":"","ico":"","method":function(){}},{"title":"","method":function(){}}]
		if(null==operArray||operArray.length==0){
			return;
		}
		var self=this;
		var msgMask='<div class="weui-mask" onclick="$.closeMenu(0)" id="menuMask"></div>';
		var msghtml='<div class="weui-actionsheet " style="background-color:transparent;" id="menuDialog">'
			+'<div class="weui-actionsheet__menu panel_card">';
		this.menuFunctionArray.length=0;
		this.menuFunctionArray.push(null);
		for(var i=0;i<operArray.length;i++){
			var oper=operArray[i];
			this.menuFunctionArray.push(oper.method);
			msghtml+='<div class="weui-actionsheet__cell" onclick="$.closeMenu('+(i+1)+')">'+(oper.ico?'<img class="actionsheet_ico" src="'+oper.ico+'" />':'')+oper.title+'</div>';
		}
		msghtml+='</div><div class="weui-actionsheet__action panel_card"><div class="weui-actionsheet__cell" onclick="$.closeMenu(0)" >取消</div></div></div>';
		var $msg=$(msghtml);
		var $mask=$(msgMask);
		$(document.body).append($mask).append($msg);
		$msg.addClass('weui-animate-slide-top').on('animationend webkitAnimationEnd',function(){
			$("#menuDialog").unbind("animationend webkitAnimationEnd");
			self.isMenuClick=true;
		});
	},
	_ajaxUpload:function(url,file,callBack,progress){ 
		var fd = new FormData();
		fd.append(file.name,file);
		var xhr = new XMLHttpRequest(); 
		xhr.open('POST',url,true); // 异步传输 
		// xhr.upload 这是html5新增的api,储存了上传过程中的信息 
		xhr.upload.onprogress = function (ev) {
			if(ev.lengthComputable) {
				progress(file, ev.loaded, ev.total);
			}
		}// 文件上传成功或是失败
		xhr.onreadystatechange = function(e) {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
					callBack(file,xhr.responseText);
				}else if(xhr.status == 400){
					$.remind("文件上传失败!"+xhr.responseText);
				}
			}
		};
		xhr.send(fd); 
	},
	uploadImage:function(fileObj,imageType,adminToken,fn,width,ratio){
		width=width?width:500;
		ratio=ratio?ratio:1;
		var files=fileObj.files;
		for(var i=files.length-1;i>=0;i--){
			if(files[i].type.indexOf("image/")<0){
				$.remind("请选择图片");
			}else if(files[i].size>10399000){
				$.remind("图片太大");
			}else{
				$._ajaxUpload("action/manageImageInfo/uploadImage?imageType="+imageType+"&adminToken="+adminToken+"&thumWidth="+width+"&thumRatio="+ratio,
					files[i],
					function(file,text){
						$.loading(false);
						if(fn) fn(text);
					},
					function(file,loaded,total){
						var percent = 0; 
						percent = Math.round(100 * loaded/total); 
						$.loading(true,percent+"%");
					}
				);
			}
		}
	},
	uploadView:function(oid,oids,bus_oid,adminToken,fn){
		var del=fn?'Y':'';
		pm.go("static/manage/viewImages.html?del="+del+"&oids="+oids+"&busoid="+bus_oid+"&token="+adminToken+"&model.oid="+oid,function(msg){
			if(fn){fn(msg);}
		});
	},
	viewImages:function(oid,bus_oid,image_type){
		pm.go("static/manage/viewImages.html?del=&imagetype="+image_type+"&busoid="+bus_oid+"&model.oid="+oid);
	},
	url2id:function(url){
		return url.replace(new RegExp("/","g"),"-1_")
			.replace(new RegExp("=","g"),"-2_")
			.replace(new RegExp("\\?","g"),"-3_")
			.replace(new RegExp("\\.","g"),"-4_")
			.replace(new RegExp("&","g"),"-5_")
			.replace(new RegExp("%","g"),"-6_");
	},
	id2url:function(url){
		return url.replace(new RegExp("-6_","g"),"%")
			.replace(new RegExp("-5_","g"),"&")
			.replace(new RegExp("-4_","g"),".")
			.replace(new RegExp("-3_","g"),"?")
			.replace(new RegExp("-2_","g"),"=")
			.replace(new RegExp("-1_","g"),"/");
	},
	getLocationHash:function(){
		if(location.hash&&"#"!=location.hash){
			return decodeURIComponent(location.hash);
		}else{
			return "";
		}
	},
	pageManager:{
		_current:"",
		_flag:"",
		_goFlag:false,
		_message:"",
		_pageArray:[],
		_callArray:{},
		_timerArray:{},
		//存在第一个页面和没有页面的时候调用，第一个页面true，没有页面false, 需要使用的时候覆盖方法
		hasPageHandler:function(flag){},
		$:function(str){
			return $("div.page[name='"+$.url2id(this._current)+"']").find(str);
		},
		clearTimeout:function(){
			if(this._timerArray[this._current]){
				clearTimeout(this._timerArray[this._current]);
				this._timerArray[this._current]=null;
			}
		},
		setTimeout:function(fn,n){
			this.clearTimeout();
			this._timerArray[this._current]=setTimeout(fn,n);
		},
		_closePage:function(){
			if(this._pageArray.length){
				var pageIndex=this._pageArray.length-1;
				var delUrl=this._pageArray[pageIndex];
				var page=$("div[name='"+$.url2id(delUrl)+"']");
				page=page.eq(page.length-1);
				page.addClass('weui-animate-slide-right')
				.on('animationend webkitAnimationEnd', function(){
					$(this).remove();
				});
				this.clearTimeout();
				var obj=this._callArray[this._current];
				var curl=this._current;
				var fl=this._flag;
				var ms=this._message;
				this._pageArray.splice(pageIndex,1);
				if(this._pageArray.length==0){
				this.hasPageHandler(false);
					this._current="";
				}else{
					this._current=this._pageArray[pageIndex-1];
				}
				this._flag="";
				this._message="";
				if(obj){
					if("success"==fl&&obj["success"]){
						obj["success"](ms);
					}
					if(obj["finally"]){
						obj["finally"](ms);
					}
					this._callArray[curl]=null;
				}
			}
		},
		_openPage:function(){
			var lh=$.getLocationHash();
			if(lh){
				$.loading(true);
				var url=lh.substring(1);
				this._pageArray.push(url);
				this._current=url;
				var dataPage=$('<div class="page" ></div>');
				$(document.body).append($('<div class="page weui-animate-slide-left" name="'+$.url2id($.pageManager._current)+'"></div>').append(dataPage));
				//dataPage.addClass('weui-animate-slide-left')
				//.on('animationend webkitAnimationEnd', function(){
				//	$(this).removeClass('weui-animate-slide-left');
				//});
				$.loadVuePage(dataPage,url,$.parseParams(url),function(vue,index){
					if($.pageManager._pageArray.length==1){
						$.pageManager.hasPageHandler(true);
					}
				},function(){
					$.remind(data);
					$.pageManager.back();
				});
			}
		},
		closeAllPage:function(){
			while(this._pageArray.length){
				this._closePage();
			}
			if(this._homeHandler){
				this._homeHandler();
				this._homeHandler=null;
			}
		},
		_homeHandler:null,
		gotoHash:function(homeFunction){
			var lh=$.getLocationHash();
			if(homeFunction){
				if(lh){
					var url=lh.substring(1);
					if(url){
						this._openPage(url);
						this._homeHandler=homeFunction;
					}
				}else{
					homeFunction();
				}
			}else{
				var b=false;
				if(this._current==""&&!this._goFlag){
					if(wx) wx.closeWindow();
					location.hash="#";
				}
				if(this._current!=""&&("#"+this._current)!=lh){//backPage or nextPage
					var pageIndex=this._pageArray.length-1;
					if(""==lh&&this._pageArray.length==1
						||this._pageArray.length>=2&&("#"+this._pageArray[pageIndex-1])==lh&&!this._goFlag){
					//backPage
						this._closePage();
					}else if(lh!=""){ 
					//nextPage
						b=true;
					}
				}else{
				//openPage
					b=true;
				}
				if(b){
					this._goFlag=false;
					this._openPage();
				}
			}
		},
		home:function(){
			this._flag="finally";
			this._message=this._flag;
			location.hash="#";
		},
		setBack:function(flag,message){
			if(flag){
				this._flag=flag;
			}else{
				this._flag="finally";
			}
			if(message){
				this._message=message;
			}else{
				this._message=this._flag;
			}
		},
		//默认为normal
		back:function(flag,message){
			if(flag) this.setBack(flag,message);
			if(this._homeHandler&&this._pageArray.length==1){
				location.hash="#";
			}else{
				history.back();
			}
		},
		//{"success":function(){},"finally":function(){}}
		//success:页面返回成功后执行, finally:页面返回一定执行,  成功方法先执行
		go:function(url,obj){
			if(typeof(obj)=="function"){
				this._callArray[url]={"success":obj};
			}else if(typeof(obj)=="object"){
				this._callArray[url]=obj;
			}
			this._goFlag=true;
			location.hash="#"+encodeURIComponent(url);
		},
		refresh:function(src){
			if(this._current){
				var page=$("div[name='"+$.url2id(this._current)+"']");
				page=page.eq(page.length-1);
				page.html("");
				if(src){
					$.loadVuePage(page,src,$.parseParams(src),function(vue,index){},function(){});
				}else{
					$.loadVuePage(page,this._current,$.parseParams(this._current),function(vue,index){},function(){});
				}
			}
		}
	},
});
iscroller={};
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function vueTouch(el,binding,type){
	var _this=this;
	this.obj=el;
	this.binding=binding;
	this.touchType=type;
	this.vueTouches={x:0,y:0};
	this.vueMoves=true;
	this.vueLeave=true;
	this.longTouch=true;
	this.vueCallBack=typeof(binding.value)=="object"?binding.value.fn:binding.value;
	this.obj.addEventListener("touchstart",function(e){
		_this.start(e);
	},false);
	this.obj.addEventListener("touchend",function(e){
		_this.end(e);
	},false);
	this.obj.addEventListener("touchcancel",function(e){
		_this.end(e);
	},false);
	this.obj.addEventListener("touchmove",function(e){
		_this.move(e);
	},false);
};
vueTouch.prototype={
	start:function(e){
		this.vueMoves=true;
		this.vueLeave=true;
		this.longTouch=true;
		this.vueTouches={x:e.changedTouches[0].pageX,y:e.changedTouches[0].pageY};
		this.time=setTimeout(function(){
			if(this.vueLeave&&this.vueMoves){
				this.touchType=="longtap"&&this.vueCallBack(this.binding.value,e);
				this.longTouch=false;
			};
		}.bind(this),1000);
	},
	end:function(e){
		var disX=e.changedTouches[0].pageX-this.vueTouches.x;
		var disY=e.changedTouches[0].pageY-this.vueTouches.y;
		e.changedTouches[0].moveX=disX;
		e.changedTouches[0].moveY=disY;
		clearTimeout(this.time);
		if(Math.abs(disX)>10||Math.abs(disY)>100){
			this.touchType=="swipe"&&this.vueCallBack(this.binding.value,e);
			if(Math.abs(disX)>Math.abs(disY)){
				if(disX>10){
					this.touchType=="swiperight"&&this.vueCallBack(this.binding.value,e);
				};
				if(disX<-10){
					this.touchType=="swipeleft"&&this.vueCallBack(this.binding.value,e);
				};
			}else{
				if(disY>10){
					this.touchType=="swipedown"&&this.vueCallBack(this.binding.value,e);
				};
				if(disY<-10){
					this.touchType=="swipeup"&&this.vueCallBack(this.binding.value,e);
				}; 
			};
		}else{
			if(this.longTouch&&this.vueMoves){
				this.touchType=="tap"&&this.vueCallBack(this.binding.value,e);
				this.vueLeave=false
			}else{
				this.touchType=="swipeend"&&this.vueCallBack(this.binding.value,e);
			}
		};
	},
	move:function(e){
		this.vueMoves=false;
		e.changedTouches[0].moveX=e.changedTouches[0].pageX-this.vueTouches.x;
		e.changedTouches[0].moveY=e.changedTouches[0].pageY-this.vueTouches.y;
		this.touchType=="swipemove"&&this.vueCallBack(this.binding.value,e);
	}
};
Vue.directive("tap",{
	bind:function(el,binding){
		new vueTouch(el,binding,"tap");
	}
});
Vue.directive("swipe",{
	bind:function(el,binding){
		new vueTouch(el,binding,"swipe");
	}
});
Vue.directive("swipemove",{
	bind:function(el,binding){
		new vueTouch(el,binding,"swipemove");
	}
});
Vue.directive("swipeend",{
	bind:function(el,binding){
		new vueTouch(el,binding,"swipeend");
	}
});
Vue.directive("swipeleft",{
	bind:function(el,binding){
		new vueTouch(el,binding,"swipeleft");
	}
});
Vue.directive("swiperight",{
	bind:function(el,binding){
		new vueTouch(el,binding,"swiperight");
	}
});
Vue.directive("swipedown",{
	bind:function(el,binding){
		new vueTouch(el,binding,"swipedown");
	}
});
Vue.directive("swipeup",{
	bind:function(el,binding){
		new vueTouch(el,binding,"swipeup");
	}
});
Vue.directive("longtap",{
	bind:function(el,binding){
		new vueTouch(el,binding,"longtap");
	}
});
var vui={
	id:1,
	dict:{},
	util:{
		fn_formatDate:function(date,style){
			if(!date){
				return "";
			}else if(date instanceof Date){
				return date.format(style);
			}else{
				return new Date(date.replace(/-/g, "/")).format(style);
			}
		},
		fn_formatShow:function(dateString){
			if(!dateString) return "";
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
		},
		fn_dict:function(type,value){
			if(vui.dict[type]){
				return vui.dict[type][value];
			}else{
				$.execJSON("action/manageDictionaryType/getDict",{"model.type":type},
					(function(json){
						if(json.code==0){
							vui.dict[type]={};
							for(var i=0;i<json.list.length;i++){
								vui.dict[type][json.list[i].value]=json.list[i].name;
							}
						}
					}).bind(this),function(){},
					false,true
				);
				if(vui.dict[type]){
					return vui.dict[type][value];
				}else{
					return '';
				}
			}
		},
		fn_level:function(level){
			if(level==1) return "&#xe61c;";
			else if(level==2) return "&#xe619;";
			else if(level==3) return "&#xe61d;";
			else if(level==4) return "&#xe61a;";
			else if(level==5) return "&#xe61b;";
			else if(level==6) return "&#xe61e;";
			else if(level==7) return "&#xe61f;";
			else if(level==8) return "&#xe620;";
			else if(level==9) return "&#xe621;";
			else if(level==10) return "&#xe623;";
			else if(level==11) return "&#xe622;";
			else if(level==12) return "&#xe627;";
			else if(level==13) return "&#xe624;";
			else if(level==14) return "&#xe626;";
			else if(level==15) return "&#xe625;";
			else return '';
		},
		//url , params 包含page.index和page.num , callback json 包含list属性
		fn_queryData:function(url,params,callback,scroller,isRefresh){
			if(isRefresh) params['page.index']=0;
			else params['page.index']=params['page.index']+params['page.num'];
			$.execJSON(url,params,
				(function(json){
					callback(json);
					if(json.list.length<params['page.num']){
						scroller.mescroll.endSuccess(0,false);
						scroller.mescroll.showNoMore();
					}else{
						scroller.mescroll.endSuccess(json.list.length,true);
					}
				}).bind(this)
			);
		}
	}
};
//vui-icon图标组件   style样式
Vue.component('vui-icon',{
	template:`<i class="iconfont" :style="style"><slot></slot></i>`,
	props:{
		style:{type:String}
	}
});
Vue.component('vui-level',{
	template:`<i class="iconfont text_size_maxx text_shadow color_red" :style="style" v-html="lv"></i>`,
	props:{
		level:{type:Number},
		style:{type:String}
	},
	data(){
		return {
			lv:''
		};
	},
	watch:{
		level:function(val){
			this.setLv(val);
		}
	},
	mounted:function(){
		this.setLv(this.level);
	},
	methods:{
		setLv:function(val){
			this.lv=vui.util.fn_level(val);
		}
	}
});
Vue.component('vui-row',{
	template:`<div class="weui-flex" :style="style"><slot></slot></div>`,
	props:{
		style:{type:String},
	},
});
Vue.component('vui-col',{
	template:`<div :class="['weui-flex__item']" :style="[{flex:flex},style]"><slot></slot></div>`,
	props:{
		flex:{type:Number,default:1},
		style:{type:String},
	},
});
//vui-button按钮组件   title标题   type样式（primary,default,warn,plain-primary,plain-default） loading加载样式
Vue.component('vui-button',{
	template:`<a href="javascript:;" :class="['weui-btn','weui-btn_'+type,{'weui-btn_loading':loading,'weui-btn_mini':mini}]" v-tap="{fn:btnClick}"><i v-if="loading" class="weui-loading"></i>{{title}}<slot></slot></a>`,
	props:{
		title:{type:String},
		type:{type:String,default:"default"},
		loading:{type:Boolean,default:false},
		mini:{type:Boolean,default:false}
	},
	data(){
		return {
			typeStyle:''
		};
	},
	methods:{
		btnClick:function(){
			if(!this.loading){
				this.$emit('on-click',this);
			}
		}
	}
});
//底部tabbar 首页使用
Vue.component('vui-tabbar',{
	template:`<div class="weui-tabbar" :style="{zIndex:1}">
		<a v-for="item in items" href="javascript:;" v-tap="{fn:tapTab,item:item}" :class="{'weui-tabbar__item':true,'weui-bar__item_on':selected.oid==item.oid}">
			<span style="display: inline-block;position: relative;">
			<i class="iconfont weui-tabbar__icon" style="font-size:27px;" v-html="selected.oid==item.oid?item.icoHover:item.icoFont"></i>
			</span>
			<p class="weui-tabbar__label">{{item.name}}</p>
		</a>
	</div>`,
	props:{
			//[{oid:'',name:'',icoFont:'',icoHover:''},{}]
		items: Array,
		selected: Object
	},
	methods:{//on-change
		tapTab:function(s,e){
			this.changeTab(s.item);
		},
		changeTab:function(item){
			this.selected=item;
			this.$emit('on-change', item);
		}
	}
});
//滚动内容 top顶部空白距离  bottom底部空白距离   tool是否有工具条  refresh是否下拉刷新   load是否上拉加载  
//  方法  @on-refresh下拉刷新   @on-load下拉加载  @on-init初始化完成
Vue.component('vui-scroller',{
	template:`<div ref="mescroll" :style="[style,{'top':top+'px','bottom':bottom+'px','height':'auto',position:'fixed'}]" class="mescroll">
		<div><slot></slot></div>
	</div>`,
	props:{
		style:{type:String},
		//id:{type:String,required:true},
		top:{type:Number,default:0},
		bottom:{type:Number,default:0},
		refresh:{type:Boolean,default:false},
		load:{type:Boolean,default:false},
		param:{type:String,default:''},
	},
	data(){
		return {
			key:++vui.id,
			mescroll:{},
			msup:{},
			msdown:{}
		};
	},
	mounted:function(){
		this.msup={use:this.load,auto:false,
			callback:(function(page, mescroll){this.$emit('on-load',this,this.param);}).bind(this)
		};
		this.msdown={use:this.refresh,auto:false,
			callback:(function(page, mescroll){this.$emit('on-refresh',this,this.param);}).bind(this)
		};

		this.mescroll = new MeScroll(this.$refs.mescroll, {
			up: this.msup,
			down: this.msdown
		})
		this.$emit('on-init', this.mescroll) // init回调mescroll对象
	},
	methods:{
		toTop:function(){
			this.mescroll.scrollTo(0,300);
			//this.scroller.scrollTo(0,0,300);
		}
	}
});
//nav面板
Vue.component('vui-nav-scroller',{
	template:`<div class="weui-tab">
		<div class="weui-navbar" :id="'ind'+key" :style="{backgroundColor:'transparent'}">
		<template v-for="(item,n) in titles">
			<div :class="'weui-navbar__item '+(n==index?'weui-bar__item_on':'')" :style="{padding:'10px 0'}" v-tap="{fn:tapItem,index:n}">{{item}}</div>
		</template>
		</div>
		<div class="swiper-container" :style="{height:'100%'}" :id="'nav_scroller_'+key">
			<div class="swiper-wrapper"><slot></slot></div>
			<div class="swiper-scrollbar" :id="'nav_scrollbar_'+key" :style="{top:'41px',height:'3px',left:'0',width:'100%',borderRadius:'0px'}"></div>
		</div>
	</div>`,
	props:{
		index:{type:Number,default:0},
		titles:{type:Array,default:['']}
	},
	data(){
		return {
			key:++vui.id,
		};
	},
	mounted:function(){
		this.swiper = new Swiper("#nav_scroller_"+this.key,{
			on:{
				slideChange:(function(){
					this.gotoIndex(this.swiper.activeIndex);
				}).bind(this),
			},scrollbar: {
				el: "#nav_scrollbar_"+this.key,
			},
		});
		this.swiper.scrollbar.$dragEl.css('background','#1aad19');
		this.gotoIndex(this.index,true);
	},
	methods:{
		tapItem:function(s,e){
			this.gotoIndex(s.index, true);
		},
		gotoIndex:function(i,gotoPage){
			this.index=i;
			if(gotoPage){
				this.swiper.slideTo(this.index);
			}
			this.$emit('on-change', i);
		}
	}
});
//nav面板项
Vue.component('vui-nav-scroller-item',{
	template:`<div class="swiper-slide"><slot></slot></div>`,
	props:{
		index:{type:Number},
		count:{type:Number}
	}
});
Vue.component('vui-page-title',{//,backgroundColor:'#e0ffe9',boxShadow:'0 -20px 20px #f8f9ff inset'
	template:`<div class="weui-cells__title" :style="[style,{width:'100%',margin:0,padding:'6px 0 0 0',position:'relative',overflow:'hidden',display:'flex'}]">
		<div :style="{flex:0,color:'inherit'}">
			<vui-button v-if="back" title="" :style="{flex:0,padding:'0 10px',color:'inherit'}" type="transparent" :mini="true" @on-click="doBack" ><vui-icon style="font-size:22px;line-height:35px;">&#xe679;</vui-icon></vui-button>
		</div>
		<div :style="{paddingRight:(back?'42px':'0px'),display:'block',flex:1,lineHeight:'35px',color:'inherit',height:'35px',paddingLeft:(!back?'11px':'')}">
			{{title}}<slot></slot>
		</div>
	</div>`,
	props:{
		style:{type:Object},
		back:{type:Boolean},
		title:{type:String}
	},
	methods:{
		doBack:function(){
			this.$emit('on-back');
		}
	}
});
//搜索条
Vue.component('vui-search-bar',{
	template:`<div class="weui-search-bar" :id="'searchBar'+key" :style="style">
		<form class="weui-search-bar__form" @submit.prevent="search();">
			<div class="weui-search-bar__box">
				<i class="weui-icon-search"></i>
				<input type="search" :id="'searchText'+key" class="weui-search-bar__input" :placeholder="hint" required="">
				<a href="javascript:" class="weui-icon-clear"></a>
			</div>
		</form>
	</div>`,
	props:{
		hint:{type:String},
		style:{type:String}
	},
	data(){
		return {
			key:++vui.id
		};
	},
	methods:{
		search:function(){
			this.$emit('on-search',$('#searchText'+this.key).val(),$('#searchText'+this.key));
			return false;
		}
	}
});
//页面工具条  
Vue.component('vui-tool',{
	template:`<div :class="clazz" :style="{top:top,bottom:bottom,height:'45px'}"><slot></slot></div>`,
	data(){
		return {
			top:'auto',
			bottom:'0px',
			clazz:'weui-tabbar'
		};
	},
	mounted:function(){
	},
});
//工具条项目  flex比例
Vue.component('vui-tool-item',{
	template:`<div class="weui-navbar__item" :style="{flex:flex}" ><slot></slot></div>`,
	props:{
		flex:{type:Number,default:1},
	},
});
//panel 标题行
Vue.component('vui-title',{
	template:`<div class="weui-cells__title">{{title}}<slot></slot></div>`,
	props:{
		title:{type:String}
	}
});
//滚动海报 
Vue.component('vui-carousel',{
	template:`<div class="swiper-container" :id="'homePoster'+key" v-tap="{fn:tapItem}">
		<div class="swiper-wrapper">
			<div class="swiper-slide" v-for="(item,index) in items">
				<img :style="{width:'100%'}" :src="item.image.thumPath" />
			</div>
		</div>
		<div class="swiper-pagination" :id="'homeTag'+key" style="top:0;"></div>
	</div>`,
	props:{//[{url:'',image:{thumPath:''}}]
		items:{type:Array},
	},
	data(){
		return {
			key:++vui.id,
			minHeight:0,
			swiper:{}
		};
	},
	mounted:function(){
		this.swiper = new Swiper('#homePoster'+this.key, {
			//spaceBetween: 30,
			centeredSlides: true,
			autoplay: {
				delay: 5000,
				disableOnInteraction: false,
			},
			pagination: {
				el: '#homeTag'+this.key,
			},
		});
		this.$watch('items', (function(nval, oval) {
			this.swiper.update();
		}).bind(this));
	},
	methods:{
		tapItem:function(){
			this.toPosterUrl(this.items[this.swiper.activeIndex].url);
		},
		toPosterUrl:function(urlPath){
			if(urlPath){
				pm.go(urlPath);
			}
		}
	}
});
Vue.component('vui-swiper',{
	template:`<div class="swiper-container" :id="'vui_swiper'+key">
		<div class="swiper-wrapper">
			<slot></slot>
		</div>
	</div>`,
	data(){
		return {
			key:++vui.id,
			swiper:{}
		};
	},
	mounted:function(){
		this.swiper = new Swiper('#vui_swiper'+this.key, {
			slidesPerView: 'auto'
		});
	},
	methods:{
		refresh:function(){
			setTimeout((function(){
				this.swiper.update();
			}).bind(this),100);
		}
	}
});
Vue.component('vui-swiper-item',{
	template:`<div class="swiper-slide" :style="{width:ratio+'%'}"><slot></slot></div>`,
	props:{
		ratio:{type:Number,default:100},
	}
});
//预览面板
Vue.component('vui-preview',{
	template:`<div :class="'weui-form-preview '+type" :style="style">
		<div class="weui-form-preview__hd">
			<div class="weui-form-preview__item">
				<label class="weui-form-preview__label">{{title}}</label>
				<em class="weui-form-preview__value">{{label}}</em>
			</div>
		</div>
		<div class="weui-form-preview__bd">
			<slot></slot>
		</div>
	</div>`,
	props:{
		title:{type:String},
		label:{type:String},
		type:{type:String,default:''},
		style:{type:String}
	}
});
//预览面板项
Vue.component('vui-preview-item',{
	template:`<div class="weui-form-preview__item" :style="style">
		<label class="weui-form-preview__label">{{title}}<slot name="title"></slot></label>
		<span class="weui-form-preview__value">{{label}}<slot name="label"></slot></span>
	</div>`,
	props:{
		title:{type:String},
		label:{type:String},
		style:{type:String}
	}
});

//面板  title标题  label底部标签 type样式（panel_card）  @on-click底部标签点击事件
Vue.component('vui-panel',{
	template:`<div :class="'weui-panel weui-panel_access '+type" :style="[style,{backgroundImage:(image?('url('+image+')'):'')}]">
		<div v-if="title" class="weui-panel__hd">{{title}}<slot name="title"></slot></div>
		<div class="weui-panel__bd"><slot></slot></div>
		<div v-if="label" class="weui-panel__ft">
			<a href="javascript:void(0);" v-tap="{fn:footClick}" class="weui-cell weui-cell_access weui-cell_link">
				<div class="weui-cell__bd">{{label}}</div>
				<span class="weui-cell__ft"></span>
			</a>
		</div>
	</div>`,
	props:{
		title:{type:String},
		image:{type:String},
		label:{type:String},
		type:{type:String,default:''},
		style:{type:String}
	},
	methods:{//on-click
		footClick:function(){
			this.$emit('on-click');
		}
	}
});
//面板单元格 
Vue.component('vui-cell',{
	template:`<a href="javascript:void(0);" class="weui-media-box weui-media-box_appmsg" v-tap="{fn:onClick}">
		<slot></slot>
	</a>`,
	methods:{
		onClick:function(){
			this.$emit('on-click');
		}
	}
});
//面板单元格图片  width宽  height高  icon图标  image图片
Vue.component('vui-cell-image',{
	template:`<div v-if="icon||image" class="weui-media-box__hd" :style="{width:width+'px',height:height+'px'}">
		<i v-if="undefined!=icon" class="iconfont" :style="{fontSize:width+'px'}">{{icon}}</i>
		<img v-if="undefined!=image" class="weui-media-box__thumb" :style="{borderRadius:radius+'px',width:width+'px',height:height+'px'}" :src="image" alt="">
		<slot></slot>
	</div>`,
	props:{
		width:{type:Number,default:60},
		height:{type:Number,default:60},
		icon:{type:String},
		image:{type:String},
		radius:{type:Number,default:3}
	}
});
//面板单元格中间文字  title标题，支持slot  desc描述，支持slot  
Vue.component('vui-cell-text',{
	template:`<div class="weui-media-box__bd">
		<h4 class="weui-media-box__title">{{title}}<slot name="title"></slot></h4>
		<p v-if="desc" class="weui-media-box__desc">{{desc}}<slot name="desc"></slot></p>
		<slot></slot>
	</div>`,
	props:{
		title:{type:String},
		desc:{type:String}
	},
});
//面板单元格右侧内容
Vue.component('vui-cell-foot',{
	template:`<div class="weui-media-box__fd">
		<slot></slot>
	</div>`,
});
//面板链接 
Vue.component('vui-link',{
	template:`<a href="javascript:void(0);" class="weui-cell weui-cell_access" v-tap="{fn:onClick}" :style="style">
		<slot></slot>
	</a>`,
	props:{
		style:{type:String}
	},
	methods:{
		onClick:function(){
			this.$emit('on-click');
		}
	}
});
//面板链接图片  width宽  height高  icon图标  image图片
Vue.component('vui-link-image',{
	template:`<div v-if="icon||image" class="weui-cell__hd" :style="{height:height+'px'}">
		<i v-if="undefined!=icon" class="iconfont" :style="{fontSize:width+'px',marginRight:'5px'}">{{icon}}</i>
		<img v-if="undefined!=image" :style="[{borderRadius:radius+'px',width:width+'px',height:height+'px',marginRight:'5px',display:'block'}]" :src="image" alt="">
	</div>`,
	props:{
		width:{type:Number,default:22},
		height:{type:Number,default:22},
		icon:{type:String},
		image:{type:String},
		radius:{type:Number,default:3}
	}
});
//面板链接中间文字  title标题，支持slot
Vue.component('vui-link-text',{
	template:`<div class="weui-cell__bd weui-cell_primary" :style="style">
		<p>{{title}}<slot name="title"></slot><slot></slot></p>
	</div>`,
	props:{
		title:{type:String},
		style:{type:String}
	},
});
//面板链接右侧内容
Vue.component('vui-link-foot',{
	template:`<div class="weui-cell__ft" :style="style">
		<slot></slot>
	</div>`,
	props:{
		style:{type:String}
	},
});
//面板格子项  ratio格子比例   width宽  height高  icon图标  image图片  title标题  flag 标记
Vue.component('vui-grid-item',{
	template:`<div class="weui-grid" :style="{width:ratio+'%',padding:padding+'px 0 '+(parseInt(padding)-4)+'px 0'}" v-tap="{fn:onClick}">
		<div v-if="icon||image" class="weui-grid__icon" :style="{width:size+'px',height:size+'px'}">
			<i v-if="undefined!=icon" class="iconfont" :style="{fontSize:size+'px',color:color}">{{icon}}</i>
			<img v-if="undefined!=image" :style="{width:size+'px',height:size+'px',display:'block'}" :src="image" alt="">
			<span v-if="flag" class="weui-badge" :style="'position: absolute;top:10%;left:55%;white-space:nowrap;background-color:'+flagcolor">{{flag}}</span>
		</div>
		<p class="weui-grid__label">{{title}}<slot></slot></p>
	</div>`,
	props:{
		size:{type:Number,default:28},
		padding:{type:Number,default:18},
		ratio:{type:Number,default:33.33},
		flag:{type:Object},
		flagcolor:{type:String,default:'#e64340'},
		color:{type:String,default:'#000'},
		icon:{type:String},
		image:{type:String},
		title:{type:String}
	},
	methods:{
		onClick:function(){
			this.$emit('on-click');
		}
	}
});
Vue.component('vui-grid-cell',{
	template:`<div class="weui-grid" :style="{width:ratio+'%',padding:padding+'px 0 '+parseInt(padding)+'px 0'}" v-tap="{fn:onClick}">
		<div v-if="icon||image" class="weui-grid__icon" :style="{height:size+'px',margin:'0',width:'100%',textAlign:'center'}">
			<i v-if="undefined!=icon" class="iconfont" :style="{fontSize:size+'px',color:color,verticalAlign:'bottom'}">{{icon}}</i>
			<img v-if="undefined!=image" :style="{width:size+'px',height:size+'px',display:'block',verticalAlign:'bottom'}" :src="image" alt="">
			<span :style="{color:(acolor?color:'#000')}">{{title}}<slot></slot></span>
			<span v-if="flag" class="weui-badge" :style="'position:absolute;top:10%;right:15%;white-space:nowrap;background-color:'+flagcolor">{{flag}}</span>
		</div>
	</div>`,
	props:{
		size:{type:Number,default:28},
		padding:{type:Number,default:18},
		ratio:{type:Number,default:33.33},
		flag:{type:Object},
		flagcolor:{type:String,default:'#e64340'},
		color:{type:String,default:'#000'},
		acolor:{type:Boolean,default:false},
		icon:{type:String},
		image:{type:String},
		title:{type:String}
	},
	methods:{
		onClick:function(){
			this.$emit('on-click');
		}
	}
});
Vue.component('vui-image-panel',{
	template:`<div class="weui-image-panel">
		<div><slot></slot></div>
	</div>`
});
Vue.component('vui-image-item',{
	template:`<div v-tap="{fn:onClick}" :style="{width:(ratio+'%'),display:'block',float:'left',position:'relative'}">
			<div class="weui-image-grid">
				<div style="border-radius:4px;box-shadow: 0px 0px 4px rgba(0,0,0,0.1);">
					<img :src="image" alt="" style="width:100%;border-radius:4px;" />
					<div class="weui-image__label">{{title}}<slot name="title"></slot></div>
					<div style="background-color: rgba(255,255,255,0.8);border-radius:0 0 4px 4px;">
						<slot></slot>
					</div>
				</div>
			</div>
		</div>`,
	props:{
		image:{type:String},
		ratio:{type:Number,default:100},
		title:{type:String}
	},
	methods:{
		onClick:function(){
			this.$emit('on-click');
		}
	}
});

//面板输入项  title标题 type(text,date,time,number,password,datetime-local)
Vue.component('vui-form-item',{
	template:`<div class="weui-cell">
		<div class="weui-cell__hd" v-if="title"><label class="weui-label">{{title}}<slot name="title"></slot></label></div>
		<div class="weui-cell__bd">
			<input class="weui-input" :type="type" :readonly="readonly" :value="value" v-on:input="$emit('input',$event.target.value)" :placeholder="hint">
		</div>
		<slot name="foot"></slot>
	</div>`,
	props:{
		value:{},
		title:{type:String},
		hint:{type:String},
		type:{type:String,default:'text'},
		readonly:{type:Boolean,default:false}
	},
	watch:{
		value:function(val){
			if(this.type=='date'){
				this.value=vui.util.fn_formatDate(val,'yyyy-MM-dd');
			}
		}
	}
});
//文本框 slot foot使用
Vue.component('vui-form-foot',{
	template:`<div class="weui-cell__ft"><slot></slot></div>`,
});
//面板textarea
Vue.component('vui-form-text',{
	template:`<div class="weui-cell">
		<div class="weui-cell__hd" v-if="title">
			<label class="weui-label">{{title}}<slot name="title"></slot></label>
		</div>
		<div class="weui-cell__bd">
			<textarea class="weui-textarea" :readonly="readonly" :placeholder="hint" v-on:input="$emit('input',$event.target.value)" :rows="rows">{{value}}</textarea>
		</div>
		<slot name="foot"></slot>
	</div>`,
	props:{
		value:{},
		title:{type:String},
		hint:{type:String},
		rows:{type:Number,default:3},
		readonly:{type:Boolean,default:false}
	}
});
//选择框组件 items{oid0:name0,oid1:name1}
Vue.component('vui-form-select',{
	template:`<div class="weui-cell weui-cell_select weui-cell_select-after">
		<div class="weui-cell__hd" v-if="title">
			<label class="weui-label">{{title}}<slot name="title"></slot></label>
		</div>
		<div class="weui-cell__bd">
			<select v-if="!readonly" class="weui-select" :value="value" v-on:change="$emit('input',$event.target.value)">
				<option value="">{{hint}}</option>
				<option v-for="(item,key,index) in items" :value="key">{{item}}</option>
			</select>
			<input v-if="readonly" class="weui-input" style="padding:11px 0;" type="text" :readonly="readonly" :value="items[value]">
		</div>
		<slot name="foot"></slot>
	</div>`,
	props:{
		value:{},
		items:{type:Object},
		title:{type:String},
		hint:{type:String,default:''},
		readonly:{type:Boolean,default:false}
	}
});
//面板switch  title标题
Vue.component('vui-form-switch',{
	template:`<div class="weui-cell weui-cell_switch">
		<div class="weui-cell__bd">{{title}}<slot name="title"></slot></div>
		<div class="weui-cell__ft">
			<label class="weui-switch-cp">
				<input class="weui-switch-cp__input" type="checkbox" :value="value" :checked="value==checked" v-on:click="$emit('input',$event.target.checked?checked:nocheck);$emit('on-change');">
				<div class="weui-switch-cp__box"></div>
			</label>
		</div>
	</div>`,
	props:{
		value:{type:String},
		title:{type:String},
		checked:{type:String,default:'Y'},
		nocheck:{type:String,default:'N'},
	}
});
//多选组件
Vue.component('vui-form-checkbox',{
	template:`<label class="weui-cell weui-check__label weui-cells_checkbox">
		<div class="weui-cell__hd">
			<input type="checkbox" class="weui-check" :value="value" :checked="value==checked" v-on:click="$emit('input',$event.target.checked?checked:nocheck);$emit('on-change');" >
			<i class="weui-icon-checked"></i>
		</div>
		<div class="weui-cell__bd">
			<p>{{title}}<slot name="title"></slot></p>
		</div>
	</label>`,
	props:{
		value:{type:String},
		title:{type:String},
		checked:{type:String,default:'Y'},
		nocheck:{type:String,default:'N'},
	}
});
//单选组件
Vue.component('vui-form-radio',{
	template:`<label class="weui-cell weui-check__label weui-cells_checkbox">
		<div class="weui-cell__bd">
			<p>{{title}}<slot name="title"></slot></p>
		</div>
		<div class="weui-cell__fd">
			<slot name="label"></slot>
			<input type="radio" class="weui-check" :name="name" :value="value" v-on:change="$emit('on-change')" :checked="value==checked" v-on:click="$emit('input',$event.target.checked?checked:''); $emit('on-change');" >
			<i class="weui-icon-checked"></i>
		</div>
	</label>`,
	props:{
		value:{type:String},
		name:{type:String},
		title:{type:String},
		checked:{type:String,default:'Y'}
	}
});
//评星组件
Vue.component('vui-form-star',{
	template:`<div class="weui-cell">
		<div class="weui-cell__hd" v-if="title">
			<label class="weui-label">{{title}}<slot name="title"></slot></label>
		</div>
		<div class="weui-cell__bd">
			<div class="m-star-box">
				<div class="m-star">
					<div ref="star" class="m-star__inner" @touchstart="clickStar">
						<div :style="{width:width+'%'}" class="m-star__track"></div>
					</div>
				</div>
			</div>
		</div>
		<slot name="foot"></slot>
	</div>`,
	props:{
		value:{type:String},
		title:{type:String},
		readonly:{type:Boolean,default:false}
	},
	data(){
		return {
			width:0,
			totalLen:0,
		};
	},
	mounted:function(){
		this.totalLen=$(this.$refs['star']).width();
		this.setStar();
	},
	methods:{
		clickStar:function(e){
			if(this.readonly) return;
			this.value=parseInt((e.targetTouches[0].clientX-e.currentTarget.offsetLeft+20)/this.totalLen*5);
			if(this.value>5) this.value=5;
			this.setStar();
			this.$emit('input',this.value);
			this.$emit('on-change');
		},
		setStar:function(){
			var percent=this.value*20;
			this.width=percent;
		}
	}
});

//图片组件
Vue.component('vui-form-image',{
	template:`<div class="weui-cell" :style="{paddingBottom:'3px'}">
		<div class="weui-cell__hd" v-if="title">
			<label class="weui-label">{{title}}<slot name="title"></slot></label>
		</div>
		<div class="weui-cell__bd">
			<div v-for="item in images" class="weui-uploader__file" v-tap="{fn:tapImage,oid:item.oid}" :style="{backgroundImage:'url('+item.thumPath+')'}"></div>
			<label v-if="images.length<max&&!readonly" class="weui-uploader__input-box">
				<input ref="file" class="weui-uploader__input" @change="uploadImage" type="file">
			</label>
		</div>
		<slot name="foot"></slot>
	</div>`,
	props:{
		value:{type:String},
		title:{type:String},
		max:{type:Number,default:1},
		readonly:{type:Boolean,default:false},
		width:{type:Number,default:500},
		ratio:{type:Number,default:1},
		busoid:{type:String,default:''},
		type:{type:String,default:''},
		token:{type:String,default:''}
	},
	data(){
		return {
			oids:[],
			images:[],
		};
	},
	mounted:function(){
		if(this.value){
			this.oids=this.value.split(",");
		}
		if(this.oids.length>0){
			$.execJSON('action/appImageManage/getImages',
				{"model.oid":this.oids[0],"oids":this.oids},
				(function(json){
					if(json.code==0){
						this.oids.length=0;
						this.images=json.list;
						for(var i=0;i<this.images.length;i++){
							this.oids.push(this.images[i].oid);
						}
					}else{
						$.remind(json.msg);
					}
					this.$emit('on-init');
				}).bind(this)
			);
		}
	},
	methods:{
		tapImage:function(s,e){
			this.viewImage(s.oid);
		},
		viewImage:function(oid){
			var fn;
			if(!this.readonly){
				fn=(function(del_oid){
					var arr=del_oid.split(",");
					var isDel=false;
					for(var i=0;i<arr.length;i++){
						var n=this.oids.remove(arr[i]);
						if(n>=0){
							this.images.splice(n, 1);
							isDel=true;
						}
					}
					if(isDel){
						this.$emit('input',this.oids+'');
						this.$emit('on-change');
					}
				}).bind(this);
			}
			$.uploadView(oid,this.oids+'',this.busoid,this.token,fn);
		},
		uploadImage:function(){
			var file=$(this.$refs['file']).get(0);
			$.uploadImage(file,this.type,this.token,(function(txt){
				eval('var info='+txt);
				if(info.code==0){
					this.oids.push(info.model.oid);
					this.images.push(info.model);
					this.$emit('input',this.oids+'');
					this.$emit('on-change');
				}else{
					$.alert(info.msg);
				}
			}).bind(this),this.width,this.ratio);
		}
	}
});

