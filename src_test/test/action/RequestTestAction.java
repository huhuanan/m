package test.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import m.common.action.Action;
import m.common.action.ActionMeta;
import m.common.model.type.FieldType;
import m.system.document.DocumentMeta;
import m.system.document.DocumentMethodMeta;
import m.system.document.DocumentParamMeta;
import m.system.util.HttpRequestUtil;
import m.system.util.JSONMessage;

//Action注释, 该类所在的包需要配置到config/mconfig.properties文件中
@ActionMeta(name="testRequestTest",title="请求测试",description="最基础的Action测试, 继承m.common.action.Action")
public class RequestTestAction extends Action {
	//定义Action接收参数, 并生成get,set方法.
	private String name;
	private Date date;
	private Map<String,String> params;
	
	@DocumentMeta(//接口注释, 有接口注释的方法会在后台的开发指南的接口中显示, 方便测试
		method=@DocumentMethodMeta(title="测试API",description="一个简单的接口测试",permission=false,
			result="返回json串"),//result返回结构示例,自行编写
		params={
			@DocumentParamMeta(name="name",description="名字",type=FieldType.STRING,length=20,notnull=true)
		}
	)
	public JSONMessage testMethod(){//Action可访问的方法没有参数, 返回类型最常用的是JSONMessage 
		JSONMessage result=new JSONMessage();//框架自写的一个用于返回前台数据的json
		try {
			result.push("name", "接收到的name:"+name);
			result.push("code", 0);
			result.push("msg", "测试成功");
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
		}
		return result;
	}
	
	public static void main(String[] a) throws Exception {
		HttpRequestUtil request=new HttpRequestUtil();
		Map<String,String> header=new HashMap<String, String>();
		header.put("Authorization", "test 12345678");
		String result=request.doPost("http://10.100.42.53/action/testRequestTest/testMethod", "name=1234", header);
		System.out.println(result);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public String getSessionLogin() {
		return "_login";
	}
	
	
}
