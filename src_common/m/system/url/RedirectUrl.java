package m.system.url;

public interface RedirectUrl {
	/**
	 * 根据域名返回重定向地址
	 * @param domain
	 * @return
	 */
	public String getRedirectUrl(String domain);
	/**
	 * 判断来路是否合理
	 * @param referer
	 * @return
	 */
	public boolean isReferer(String referer);
}
