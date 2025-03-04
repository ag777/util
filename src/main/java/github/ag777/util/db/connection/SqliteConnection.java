package github.ag777.util.db.connection;

import github.ag777.util.db.model.DbDriverNames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Sqlite数据库连接辅助类
 * <p>
 * 需求sqlite-jdbc-xxx.jar(本工具包不自带)
 * </p>
 * 
 * @author ag777
 * @version create on 2018年04月24日,last modify at 2018年04月25日
 */
public class SqliteConnection extends BaseDbConnectionUtils{

	private SqliteConnection(){}

	/**
	 *
	 * @param filePath sqlite数据库路径
	 * @return 数据库连接
	 * @throws ClassNotFoundException 找不到驱动包
	 * @throws SQLException 连接异常
	 */
	public static Connection connect(String filePath) throws ClassNotFoundException, SQLException {
		StringBuilder url = new StringBuilder()
			.append("jdbc:sqlite:")
			.append(filePath);
		// 加载驱动程序
		Class.forName(DbDriverNames.SQLITE);
		return DriverManager.getConnection(url.toString());
	}

	@Override
	public int getDefaultPort() {	//不存在的，直接读取文件
		return -1;
	}
	
}
