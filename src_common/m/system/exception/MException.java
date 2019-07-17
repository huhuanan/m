package m.system.exception;

/**
 * 系统通用异常类
 * @author Administrator
 *
 */
public class MException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 创建异常
	 * @param clazz 抛出异常的类
	 * @param errorMessage 异常消息
	 */
	public MException(Class<?> clazz,String errorMessage){
		super(errorMessage);
	}
	/**
	 * 捕获异常后,记录
	 */
	public void record(){
		//System.err.println("捕获异常: "+this.getMessage());
		this.printStackTrace();
	}
}
