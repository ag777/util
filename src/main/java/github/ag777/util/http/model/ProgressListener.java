package github.ag777.util.http.model;

/**
 * HTTP 上传/下载通用进度监听接口
 * 计数均使用 long，与 OkHttp/IO 语义保持一致。
 *
 * @author ag777
 * @version create on 2025年08月08日,last modify at 2025年08月08日
 */
@FunctionalInterface
public interface ProgressListener {
    /**
     * @param cur 已处理的字节数（已写入/已读取）
     * @param total 总字节数，未知时可能为 -1
     * @param step 本次处理的字节数
     * @param done 是否完成
     */
    void update(long cur, long total, long step, boolean done);
}


