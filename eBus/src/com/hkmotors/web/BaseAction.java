package com.hkmotors.web;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.hkmotors.service.IInitService;
import com.opensymphony.xwork2.ActionSupport;

public abstract class BaseAction extends ActionSupport implements SessionAware,
		ServletRequestAware, ServletResponseAware {

	private static final long serialVersionUID = 822679222894881527L;
	@Resource
	protected IInitService initService;

	/**
	 * 当前Locale在Session中的Attribute name
	 */
	public static final String CURRENT_LOCALE = "WW_TRANS_I18N_LOCALE";

	protected Map<String, Object> session;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected static String contextPath;
	// 对错误进行前台显示-错误标题
	protected String errorTitle;
	// 对错误进行前台显示-错误内容
	protected String errorContent;
	protected static String CLOSE = "close";
	protected static String BACK = "back";
	protected String errorAction = BACK;

	public void setSession(Map<String, Object> map) {
		session = map;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getContextPath() {
		if (contextPath == null) {
			if (("http".equalsIgnoreCase(request.getScheme()) && request
					.getServerPort() == 80)
					|| ("https".equalsIgnoreCase(request.getScheme()) && request
							.getServerPort() == 443))
				contextPath = request.getScheme() + "://"
						+ request.getServerName() + request.getContextPath();
			else
				contextPath = request.getScheme() + "://"
						+ request.getServerName() + ":"
						+ request.getServerPort() + request.getContextPath();
		}
		return contextPath;
	}

	/**
	 * 得到BasePath
	 * 
	 * @return
	 */
	public String getBasePath() {
		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName();
		int port = request.getServerPort();
		if (port != 80 && port != 443) {
			basePath = basePath + ":" + request.getServerPort() + path + "/";
		} else {
			basePath = basePath + path + "/";
		}
		return basePath;
	}

	@Override
	public Locale getLocale() {
		Locale lc = (Locale) session.get(CURRENT_LOCALE);
		if (lc == null) {
			if (super.getLocale() == null) {
				session.put(CURRENT_LOCALE, new Locale("en", "US"));
			} else {
				session.put(CURRENT_LOCALE, super.getLocale());
			}
			lc = (Locale) session.get(CURRENT_LOCALE);
		}
		if (!(lc.equals(Locale.US) || lc.equals(new Locale("zh", "CN")))) {
			lc = Locale.US;
			session.put(CURRENT_LOCALE, lc);
		}
		return lc;
	}

	/**
	 * 得到当前登录用户的用户名
	 * 
	 * @return
	 */
	// public String getCurrentUserName() {
	// return UserContext.getContext().getSignInUser().getNickname();
	// }

	/**
	 * 当前用户是否已经登录
	 * 
	 * @return
	 */
	// public boolean isLogin() {
	// return getCurrentUserName() == null ? false : true;
	// }

	public String getErrorContent() {
		return errorContent;
	}

	/**
	 * @param errorContent
	 *            the errorContent to set
	 */
	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}

	/**
	 * @return the errorTitle
	 */
	public String getErrorTitle() {
		return errorTitle;
	}

	/**
	 * @param errorTitle
	 *            the errorTitle to set
	 */
	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}

	/**
	 * @return the errorAction
	 */
	public String getErrorAction() {
		return errorAction;
	}

	/**
	 * @param errorAction
	 *            the errorAction to set
	 */
	public void setErrorAction(String errorAction) {
		this.errorAction = errorAction;
	}

}