package github.ag777.util.db.connection;


import github.ag777.util.db.DbConnectionUtil;
import github.ag777.util.db.model.DbDriverNames;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Mysql数据库连接辅助类
 * <p>
 * 需求db2jcc4-xxx.jar(本工具包不自带)
 * </p>
 * 
 * @author ag777
 * @version create on 2019年08月20日,last modify at 2019年08月20日
 *
 */
public class Db2Connection extends BaseDbConnectionUtils {

	private Db2Connection() {}
	
	/**
	 * 
	 * jdbc:db2://192.168.10.10:50000/sample
	 * jdbc:db2://[fec0:ffff:ffff:8000:20e:cff:fe50:39c8]:50000/sample
	 * 
	 * 改用db2jcc4.jar(原来使用db2jcc.jar,两个包的区别大概是协议/标准不同),v11.5 FP0 (GA)	4.26.14
	 * <a href="https://www-01.ibm.com/support/docview.wss?uid=swg21363866">唯一指定下载地址(需梯子)</a>
	 * 
	 * @param ip ip
	 * @param port port
	 * @param user user
	 * @param password password
	 * @param dbName dbName
	 * @param propMap propMap
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connect(String ip, String port, String user, String password, Object dbName,
			Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
		StringBuilder url = new StringBuilder("jdbc:db2://");
		if(isIpV6(ip)) {
			ip = "[" + ip + "]";
		}
		url.append(ip).append(':').append(port).append('/');
		if (dbName != null) {
			url.append(dbName);
		}

		return connect(url.toString(), user, password, propMap);
	}
	
	/**
	 * 
	 * @param url url
	 * @param user user
	 * @param password password
	 * @param propMap propMap
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connect(String url, String user, String password, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
		Properties props = getProperties(propMap);
		return DbConnectionUtil.connect(url, user, password, DbDriverNames.DB2, props);
	}
	
	@Override
	public int getDefaultPort() {
		return 50000;
	}

}