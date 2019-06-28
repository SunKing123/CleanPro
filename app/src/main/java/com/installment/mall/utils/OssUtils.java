package com.installment.mall.utils;

import com.alibaba.sdk.android.oss.OSSClient;
import com.installment.mall.callback.OssCallBack;

/**
 * Created by fengpeihao on 2018/8/21.
 */

public class OssUtils {

    /**
     * 上传到OSS服务器
     *
     * @param imgPath
     * @param callBack
     */
    public static void uploadPicToOss(String imgPath, OssCallBack callBack) {

//        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(ApiModule.OSS_ACCESS_KEY_ID, ApiModule.OSS_ACCESS_KEY_SECRET);
//        ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
//        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
//        conf.setMaxConcurrentRequest(8); // 最大并发请求数，默认5个
//        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//
//        // oss为全局变量，OSS_ENDPOINT是一个OSS区域地址
//        OSSClient oss = new OSSClient(AppApplication.getInstance(), ApiModule.OSS_END_POINT, credentialProvider, conf);
//        ossUpload(oss, imgPath, callBack);
    }

    /**
     * 上传到OSS服务器
     *
     * @param uploadData
     * @param callBack
     */
    public static void uploadPicToOss(byte[] uploadData, OssCallBack callBack) {

//        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(ApiModule.OSS_ACCESS_KEY_ID, ApiModule.OSS_ACCESS_KEY_SECRET);
//        ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
//        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
//        conf.setMaxConcurrentRequest(8); // 最大并发请求数，默认5个
//        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//
//        // oss为全局变量，OSS_ENDPOINT是一个OSS区域地址
//        OSSClient oss = new OSSClient(AppApplication.getInstance(), ApiModule.OSS_END_POINT, credentialProvider, conf);
//        ossUpload(oss, uploadData, callBack);
    }

    private static void ossUpload(OSSClient oss, byte[] uploadData, final OssCallBack callBack) {

//        // 指定数据类型，没有指定会自动根据后缀名判断
//        ObjectMetadata objectMeta = new ObjectMetadata();
//        objectMeta.setContentType("image/jpeg");
//
//        // 构造上传请求
//        // 这里的objectKey其实就是服务器上的路径，即目录+文件名
//        //因为目录命名逻辑涉及公司信息，被我删去，造成不知道这个objectKey不知为何物，如下是我们公司的大致命名逻辑
//        //String objectKey = keyPath + "/" + carArr[times] + ".jpg";
//        final String imageKey = "android/image/pic_" + getSystemTime() + "_" + AndroidUtil.getCustomerId() + ".jpg";
//        PutObjectRequest put = new PutObjectRequest(ApiModule.OSS_BUCKET_NAME, imageKey, uploadData);
//        put.setMetadata(objectMeta);
////        try {//同步上传
////            PutObjectResult putObjectResult = oss.putObject(put);
////        } catch (ClientException e) {
////            e.printStackTrace();
////        } catch (ServiceException e) {
////            e.printStackTrace();
////        }
//
//        // 异步上传时可以设置进度回调
////        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
////            @Override
////            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
////                // 在这里可以实现进度条展现功能
////                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
////            }
////        });
//        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                Log.d("PutObject", "UploadSuccess");
//                callBack.upLoadSuccess(imageKey);
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                }
//                callBack.upLoadFail();
//            }
//        });
    }

    private static void ossUpload(OSSClient oss, String imgPath, final OssCallBack callBack) {

//        // 指定数据类型，没有指定会自动根据后缀名判断
//        ObjectMetadata objectMeta = new ObjectMetadata();
//        objectMeta.setContentType("image/jpeg");
//
//        // 构造上传请求
//        // 这里的objectKey其实就是服务器上的路径，即目录+文件名
//        //因为目录命名逻辑涉及公司信息，被我删去，造成不知道这个objectKey不知为何物，如下是我们公司的大致命名逻辑
//        //String objectKey = keyPath + "/" + carArr[times] + ".jpg";
//        final String imageKey = "android/image/headpic_" + getSystemTime() + "_" + AndroidUtil.getCustomerId();
//        PutObjectRequest put = new PutObjectRequest(ApiModule.OSS_BUCKET_NAME, imageKey, imgPath);
//        put.setMetadata(objectMeta);
////        try {//同步上传
////            PutObjectResult putObjectResult = oss.putObject(put);
////        } catch (ClientException e) {
////            e.printStackTrace();
////        } catch (ServiceException e) {
////            e.printStackTrace();
////        }
//
//        // 异步上传时可以设置进度回调
////        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
////            @Override
////            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
////                // 在这里可以实现进度条展现功能
////                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
////            }
////        });
//        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                Log.d("PutObject", "UploadSuccess");
//                callBack.upLoadSuccess(imageKey);
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                }
//                callBack.upLoadFail();
//            }
//        });
    }

    private static String getSystemTime() {
        long time = System.currentTimeMillis() / 1000;
        return time + "";
    }
}
