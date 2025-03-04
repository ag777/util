package github.ag777.util.db.interf;

import github.ag777.util.db.DbHelper;

/**
 * 数据库事务接口
 * 
 * @author ag777
 * @version create on 2017年10月16日,last modify at 2017年10月16日
 */
public interface DBTransactionInterf {
	boolean doTransaction(DbHelper helper) throws Exception;
}
