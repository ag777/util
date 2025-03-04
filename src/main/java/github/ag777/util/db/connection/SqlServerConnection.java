package github.ag777.util.db.connection;

import github.ag777.util.db.DbConnectionUtil;
import github.ag777.util.db.model.DbDriverNames;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Mysql数据库连接辅助类
 * <p>
 * 需求sqljdbc-xxx.jar(本工具包不自带)
 * </p>
 * 
 * @author ag777
 * @version create on 2018年04月24日,last modify at 2019年08月20日
 */
public class SqlServerConnection extends BaseDbConnectionUtils{

	private SqlServerConnection() {}
	
	/**
	 * 
	 * @param ip ip
	 * @param port port
	 * @param user user
	 * @param password password
	 * @param dbName dbName
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connect(String ip, int port, String user, String password, String dbName) throws ClassNotFoundException, SQLException {
		return connect(ip, port, user, password, dbName, null);
	}
	
	/**
	 * 连接sqlserver数据库
	 * <p>连接数据库可以使用jtds这个驱动包，也可以使用sqljdbc4这个驱动包,这个方法使用后者
	 * 
	 * ipv4 Driver URL: 
	 *		jdbc:sqlserver://127.0.0.1:1433/master
	 *	ipv6 Driver URL:
	 *		jdbc:sqlserver://
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
	public static Connection connect(String ip, int port, String user, String password, String dbName, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
		StringBuilder url = new StringBuilder()
				.append("jdbc:sqlserver://");
		if(!isIpV6(ip)) {	//ipV4
			url.append(ip)
					.append(':')
					.append(port)
					.append(";");
			if(dbName != null) {
				url.append("databaseName=")
					.append(dbName);
			}
		} else {	//ipV6
			if(propMap == null) {
				propMap = new HashMap<>(3);
			}
			propMap.put("portNumber", port);  
			propMap.put("instanceName ", dbName);  
			propMap.put("serverName", ip);
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
		return DbConnectionUtil.connect(url, user, password, DbDriverNames.SQLSERVER, props);
	}

	@Override
	public int getDefaultPort() {
		return 1433;
	}
	
}