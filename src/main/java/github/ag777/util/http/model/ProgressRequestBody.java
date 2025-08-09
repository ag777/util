package github.ag777.util.http.model;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.*;

import java.io.IOException;

/**
 * okhttp上传进度监听辅助类
 *
 * 通过包装原始的 RequestBody，在写出过程中统计已写入字节数并回调进度。
 *
 * @author ag777
 * @version last modify at 2025年08月03日
 */
public class ProgressRequestBody extends RequestBody {

    private final RequestBody delegate;
    private final ProgressListener progressListener;

    public ProgressRequestBody(RequestBody delegate, ProgressListener progressListener) {
        this.delegate = delegate;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (progressListener == null) {
            delegate.writeTo(sink);
            return;
        }

        long contentLength = contentLength();

        CountingSink countingSink = new CountingSink(sink, contentLength, progressListener);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        try {
            delegate.writeTo(bufferedSink);
        } finally {
            bufferedSink.flush();
        }
    }

    private static final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0L;
        private final long contentLength;
        private final ProgressListener progressListener;

        CountingSink(Sink delegate, long contentLength,
                     ProgressListener progressListener) {
            super(delegate);
            this.contentLength = contentLength;
            this.progressListener = progressListener;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            boolean done = contentLength != -1 && bytesWritten >= contentLength;
            progressListener.update(bytesWritten, contentLength, byteCount, done);
        }
    }
}

