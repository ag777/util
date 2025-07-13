package github.ag777.util.lang.exception.model;

import java.io.Serial;

/**
 * 验证异常(验证参数过程，判断出的异常)
 * @author ag777
 *
 * @version create on 2018年05月17日,last modify at 2025年07月13日
 */
public class ValidateException extends Exception {

	@Serial
	private static final long serialVersionUID = -1358796764058245553L;

	private String extraMsg;	//拓展信息
	private Boolean isException;	// 可以用于确定是系统异常还是输入异常
	private String customMessage;	// 自定义消息字段，用于支持消息修改

	public String getExtraMsg() {
		return extraMsg;
	}

	public ValidateException(String message){
		super(message);
		this.customMessage = message;
		this.isException = false;
	}

	public ValidateException(String message, String extraMsg){
		super(message);
		this.customMessage = message;
		this.extraMsg = extraMsg;
		this.isException = false;
	}

	public ValidateException(String message, Throwable cause) {
		super(message, cause);
		this.customMessage = message;
		this.isException = cause != null;
	}

	public ValidateException(String message, String extraMsg, Throwable cause) {
		super(message, cause);
		this.customMessage = message;
		this.extraMsg = extraMsg;
		this.isException = cause != null;
	}

	public Boolean getException() {
		return isException;
	}

	public ValidateException setException(Boolean exception) {
		isException = exception;
		return this;
	}

	/**
	 * 重写getMessage方法，返回自定义消息
	 * @return 当前的错误消息
	 */
	@Override
	public String getMessage() {
		return customMessage != null ? customMessage : super.getMessage();
	}

	/**
	 * 直接修改当前异常对象的message，不创建新对象
	 * @param newMessage 新的错误信息
	 * @return 当前异常对象
	 */
	public ValidateException setMessage(String newMessage) {
		this.customMessage = newMessage;
		return this;
	}

	/**
	 * 获取原始的Exception消息（来自父类）
	 * @return 原始消息
	 */
	public String getOriginalMessage() {
		return super.getMessage();
	}

}
