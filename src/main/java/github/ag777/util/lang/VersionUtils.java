package github.ag777.util.lang;

/**
 * @author ag777 <837915770@vip.qq.com>
 * @version 2025/2/5 下午3:50
 */
public class VersionUtils {
    /**
     * 两版本号比较判断旧版本号是否小于新版本号
     * <p>
     * 	支持任意多节点的版本号比较
     * </p>
     *
     * @param versionCodeOld versionCodeOld
     * @param versionCodeNew versionCodeNew
     * @return
     */
    public static boolean isVersionBefore(String versionCodeOld, String versionCodeNew) {
        if(versionCodeOld == null || versionCodeOld.trim().isEmpty()) {	//不存在旧版本号肯定是要更新的
            return true;
        }
        if(versionCodeNew == null) {	//不存在新版本肯定也升级不了
            return false;
        }
        if(versionCodeOld.equals(versionCodeNew)) {	//两个版本字符串一致,则说明完全不用升级
            return false;
        }

        //逐级比较
        String[] codesOld = versionCodeOld.split("\\.");
        String[] codesNew = versionCodeNew.split("\\.");
        int length_old = codesOld.length;
        int length_new = codesNew.length;
        for(int i=0;;i++) {
            if(i>=length_old) {
                if(i>=length_new) {	//新旧版本都到底了还没分出胜负则平局
                    return false;
                } else {	//旧版本到底了，新版本还有下文，则需要升级
                    return true;
                }
            } else if(i>=length_new) {	//旧版本还有下文，新版本到底了，说明旧版本更新(存在这种情况一版说明版本控制有问题)
                return false;
            } else {	//每级版本进行不同的pk，胜者说明对应的是新版本
                long shouldUpdate = Long.parseLong(codesNew[i]) - Long.parseLong(codesOld[i]);
                if(shouldUpdate > 0) {
                    return true;
                } else if(shouldUpdate < 0) {	//旧版本号比新版本号还大，出现这种情况说明版本控制没操作好
                    return false;
                }
            }

        }
    }
}
