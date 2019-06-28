package com.installment.mall.callback;

public interface UploadProgressListener {
	void onProgress(long progress);

	void onSucceed(byte[] response);

	void onError();
}
