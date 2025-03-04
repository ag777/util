package github.ag777.util.lang.exception.model;

import java.io.Serial;

/**
 * json语法异常，一般在json转换中使用
 * @author ag777
 *
 */
public class GsonSyntaxException extends Exception{

	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 46516454273625416L;

	public GsonSyntaxException(Throwable throwable) {
		super(throwable);
	}
}
