package test;

import java.sql.SQLException;

import m.system.db.DataSet;
import m.system.db.SqlBuffer;
import m.system.db.TransactionManager;
import m.system.exception.MException;
import m.system.listener.InitListener;

public class Test {
	public static void main(String[] a) throws SQLException, MException {
		InitListener.initDBConfig();
		Double d=null;
		SqlBuffer sql=new SqlBuffer()
			.append("select * from t_goods_info gi")
			.append("where price>?",100)
			.append(null!=d?"and stock_num>?":"", d);
			
		DataSet ds=sql.executeQuery();
		System.out.println(ds.size());
		System.out.println(ds.get(Integer.class, 0, "price"));
//		Map<String,String> eMap=new HashMap<String,String>();
//		eMap.put("loginCount", "sum(#{loginCount})");
//		List<AdminLogin> list=ModelQueryList.getModelList(AdminLogin.class, new String[]{"realname"}, new QueryPage(0,10),null,eMap,true, null);
//		
//		System.out.println(list.size());
//		for(int i=0;i<15;i++) {
//			new TestThread(i).start();
//		}
//		for(int n=0;n<100;n++) {
//			System.out.println("db num : "+DBConnection.getUseLinkNum());
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
}
class TestThread extends Thread{
	private int i;
	public TestThread(int n) {
		i=n;
	}
	@Override
	public void run() {
		try {
			TransactionManager.initConnection();
		} catch (MException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TransactionManager tm=new TransactionManager();
		//String key=null;
		try {
			System.out.println("----"+i);
			tm.begin("1");
			//key=CacheUtil.executeSynch("1");
			System.out.println("-->>"+i);
			Thread.sleep(150);
			System.out.println("<<--"+i);
			tm.commit();
		} catch (Exception e) {
			tm.rollback();
			System.out.println(e.getMessage());
		} finally {
			//CacheUtil.releaseSynch(key);
		}

		TransactionManager.closeConnection();
		
		super.run();
	}
	
}
