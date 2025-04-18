package github.ag777.util.db.model;

/**
 * 数据库字段信息存放pojo
 * 
 * @author ag777
 * @version last modify at 2025年02月07日
 */
public class DbInfo {

	public final static String TYPE_MYSQL ="MySQL";
	public final static String TYPE_ORACLE ="Oracle";
	public final static String TYPE_SQLITE = "SQLite";
	
	public String name; 		//用以获得当前数据库是什么数据库。比如oracle，access等。
	public String version; 	//获得数据库的版本。
	public String driverVersion; 	//获得驱动程序的版本。
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDriverVersion() {
		return driverVersion;
	}
	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}
	
}
