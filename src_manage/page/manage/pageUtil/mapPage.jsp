<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<page>
	<h3>${map.edit?'选择':'查看' }坐标</h3>
	<div id="map${key }" style="width:100%;height:450px;"></div>
</page>
<script>
(function(){
	return { //vue对象属性
		data(){
			return {
				//key:'',
				//openKey:'',
				selected:'${map.selected}',
				map:{},
				mark:{},
				confirm:{}
			};
		},
		mounted:function(){
			this.initMap();
		},
		methods:{
			initMap:function(){
				this.confirm=new AMap.InfoWindow({
					content:"<div style=\"line-height:2;font-size:16px;\">点击<a href=\"javascript:;\" onclick=\"$.vue['"+this.key+"'].selectMap();\">选择</a>该地点.</div>",
					offset:new AMap.Pixel(0,-16)
				});
				this.map=new AMap.Map('map${key }',{
					zoom:this.selected?16:13,
					center:this.toLngLat(this.selected)
				});
				this.mark=new AMap.Marker({position:this.toLngLat(this.selected)});
				if(this.selected){
					this.map.add(this.mark);
				}
				${map.edit?'':'return;'}
				this.map.on("click",(function(e){
					this.mark.setPosition(e.lnglat);
					this.map.add(this.mark);
					this.confirm.open(this.map,e.lnglat);
				}).bind(this));
			},
			toLngLat:function(str){
				if(str){
					var arr=str.split(",");
					return new AMap.LngLat(parseFloat(arr[0]),parseFloat(arr[1]));
				}else{
					return str;
				}
			},
			selectMap:function(){
				this.back(true,'${map.field}|'+this.mark.getPosition().toString());
			}
			//backHandler:function(success,msg){//打开窗体的回调
			//}
		}
	};
})();
</script>