package github.ag777.util.db.connection;

import github.ag777.util.db.DbConnectionUtil;
import github.ag777.util.db.model.DbDriverNames;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Oracle数据库连接辅助类
 * <p>
 * 需求ojdbc6.jar(本工具包不自带)
 * 该jar包为最新版本,以下为关联:
 * ojdbc14.jar - for Java 1.4 and 1.5
	ojdbc5.jar - for Java 1.5
	ojdbc6.jar - for Java 1.6
 * </p>
 * 
 * @author ag777
 * @version create on 2018年04月24日,last modify at 2019年08月20日
 */
public class OracleConnection extends BaseDbConnectionUtils{

	public static final String ROLE_NORMAL = "normal";
	public static final String ROLE_SYSDBA = "sysdba";
	public static final String ROLE_SYSOPER = "sysoper";
	
	private OracleConnection(){}
	
	/**
	 * 
	 * @param ip ip
	 * @param port port
	 * @param user user
	 * @param password password
	 * @param dbName dbName
	 * @param role role
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connect(String ip, int port, String user, String password, String dbName, OracleRole role) throws ClassNotFoundException, SQLException {
		return connect(ip, port, user, password, dbName, role, null);
	}
	
	/**
	 * 
	 * @param ip ip
	 * @param port port
	 * @param user user
	 * @param password password
	 * @param dbName dbName
	 * @param role role
	 * @param propMap propMap
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connect(String ip, int port, String user, String password, String dbName, OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
		return connect(ip, port, user, password, null, dbName, role, propMap);
	}
	
	/**
	 * 
	 * @param ip ip
	 * @param port port
	 * @param user user
	 * @param password password
	 * @param sid sid
	 * @param role role
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connectBySid(String ip, int port, String user, String password, String sid, OracleRole role) throws ClassNotFoundException, SQLException {
		return connectBySid(ip, port, user, password, sid, role, null);
	}
	
	/**
	 * 
	 * @param ip ip
	 * @param port port
	 * @param user user
	 * @param password password
	 * @param sid sid
	 * @param role role
	 * @param propMap propMap
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connectBySid(String ip, int port, String user, String password, String sid, OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
		return connect(ip, port, user, password, sid, null, role, propMap);
	}
	
	/**
     *
     * ipv4 Driver URL:
     * 		jdbc:oracle:thin:@//127.0.0.1:3306/orcl
     *
     * ipv6 Driver URL:
     * 		jdbc:oracle:thin:@(DESCRIPTION=
     (ADDRESS=(PROTOCOL=tcp)(HOST=[fe80::5cf:72])(PORT=1521))
     (CONNECT_DATA=(SERVICE_NAME=fnstdb1)))
     *
     * <a href="http://stackoverflow.com/questions/10647845/does-oracle-11gr2-actually-support-ipv6">Does Oracle 11gR2 actually support IPV6?</a>
     *
     *  Deiver package version
     *  <dependency>
     *   <groupId>com.oracle</groupId>
     *   <artifactId>ojdbc14</artifactId>
     *   <version>10.2.0.3.0</version>
     *  </dependency>
     *
     *  {@code
     *		①username@[//]host[:port][/service_name][:server][/instance_name]
     *		②(DESCRIPTION=
    (ADDRESS=(PROTOCOL=tcp)(HOST=host)(PORT=port))
    (CONNECT_DATA=
    (SERVICE_NAME=service_name)
    (SERVER=server)
    (INSTANCE_NAME=instance_name)))
     *    }
     *
     * <a href="https://docs.oracle.com/cd/E18283_01/network.112/e10836/naming.htm">官方文档</a>
     *
     * {@code
     * 	[示例]
     * 通过SID: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=[2001:183:1:162::131])(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))
     * 通过服务名称: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=[2001:183:1:162::131])(PORT=1521))(CONNECT_DATA=(SID=orcl)))
     * }
     *
     * @param ip ip
     * @param port 端口号
     * @param user 用户名
     * @param password 密码
     * @param sid sid
     * @param dbName 数据库名称/这个字段更可能是服务名称
     * @param role 角色normal/sysdba/sysoper
     * @param propMap 其它参数
     * @return 数据库连接
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException SQLException
     */
	public static Connection connect(String ip, int port, String user, String password, String sid, String dbName,
			OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
		StringBuilder url = new StringBuilder().append("jdbc:oracle:thin:@");
		if(!isIpV6(ip)) {	//ipV4
			url.append(ip).append(':').append(port);
			if (sid != null) {
				url.append(":").append(sid);
			} else if (dbName != null) {
				url.append('/').append(dbName);
			}
		} else {
			ip = "["+ip+"]";
			url.append("DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=")
					.append(ip).append(")(PORT=").append(port).append("))");
				url.append("(CONNECT_DATA=");
				if(sid!=null) {
					url.append("(SID=").append(sid).append(")");
				} else if(dbName!=null){
					url.append("(SERVICE_NAME=").append(dbName).append(")");
				}
				url.append(')');
			url.append(')');
		}
		return connect(url.toString(), user, password, role, propMap);
	}
	
	/**
	 * 
	 * @param url url
	 * @param user user
	 * @param password password
	 * @param role role
	 * @param propMap propMap
	 * @return 数据库连接
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 */
	public static Connection connect(String url, String user, String password, OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
		Properties props = getProperties(propMap);
		if(role != null) {
			props.put("internal_logon", role.getName());
		}
		
		return DbConnectionUtil.connect(url, user, password, DbDriverNames.ORACLE, props);
	}

	@Override
	public int getDefaultPort() {
		return 1521;
	}

	public enum OracleRole {

		NORMAL {
			@Override
			public String getName() {
				return "normal";
			}
		},
		SYSDBA {
			@Override
			public String getName() {
				return "sysdba";
			}
		},
		SYSOPER {
			@Override
			public String getName() {
				return "sysoper";
			}
		};

		public String getName() {
			return null;
		}
	}
}