package m.common.model;

public interface UserModel {
	/**
	 * 获取真实姓名
	 * @return
	 */
	public String getRealname();
	/**
	 * 获取登陆账号
	 * @return
	 */
	public String getUsername();
	/**
	 * 获取用户类型
	 * @return
	 */
	public String getUserType();
}
