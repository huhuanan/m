var wxUtil={
	isWechat:false,//是否微信客户端
	jsApiList:["scanQRCode","chooseWXPay"],
	scanQRCode:function(fn){
		if(wxUtil.isWechat){
			wx.scanQRCode({
				needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
				scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
				success: (function (res) {
					var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
					if(fn){
						fn(result);
					}
				}).bind(this)
			});
		}else{
			$.alert("扫码功能需要在微信客户端运行","提示");
		}
	}
};
$(function(){
	wx.ready(function(){
		wxUtil.isWechat=true;
	});
	wx.error(function(res){
	});
	$.execJSON("action/appApi/getWxConfig",
		{"url":location.href.split('#')[0]},
		function(ele){
			if(ele.code==0){
				wx.config({
					debug: false, // 
					appId: ele.appId, // 
					timestamp: ele.timestamp, // 
					nonceStr: ele.nonceStr, //
					signature: ele.signature,//
					jsApiList: wxUtil.jsApiList
				});
			}else{
				console.log(ele.msg);
			}
		}
	);
});