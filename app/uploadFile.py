import urllib3
from urllib.parse import quote
import requests
import json
import sys
from urllib import parse


# 上传文件
def uploadFile():
    fileName = sys.argv[1]
    path = sys.argv[2]
    filePath = sys.argv[3]

    print(fileName)

    token = "85c0db1a1fe4342075f86b2b5dee23ce448d73e8"
    http = urllib3.PoolManager()
    header = {'Authorization': "Token " + token}
    result = http.request('GET', "http://disk.xiaoniu.com/api2/repos/", None, header)

    resJson = json.loads(result.data)

    repoId = "";

    for x in resJson:
        if x['name'] == "安卓公开资料":
            repoId = x['id']
            break

    result = http.request('GET', "http://disk.xiaoniu.com/api2/repos/" + repoId + "/dir/",
                          {"p": path}, header)

    resJson = json.loads(result.data)

    if isinstance(resJson,dict) and len(resJson['error_msg']) != 0:
            # 没有文件夹 http://disk.xiaoniu.com/api2/repos/%repoId/dir/
            result = http.request('POST',
                                  "http://disk.xiaoniu.com/api2/repos/" + repoId + "/dir/?reloaddir=true&p=" + parse.quote(path) ,
                                  {"operation": "mkdir"}, header)
            resJson = json.loads(result.data)

    isUpload = True

    for item in resJson:
        if item['name'] == fileName:
            isUpload = False
            break

    requetUrl = ""
    targetKey = ""
    if isUpload:
        requetUrl = "http://disk.xiaoniu.com/api2/repos/" + repoId + "/upload-link/"
        targetKey = "parent_dir"
        targetValue = path
    else:
        requetUrl = "http://disk.xiaoniu.com/api2/repos/" + repoId + "/update-link/"
        targetKey = "target_file"
        targetValue = path + "/" + fileName

    result = http.request('GET', requetUrl, None, header)

    uploadUrl = json.loads(result.data)

    fileUpload = open(filePath,errors='ignore').read()

    result = http.request('POST', uploadUrl, fields={
        targetKey: targetValue,
        'file': (fileName, fileUpload)
    }, headers=header)

    if result.status == 200:
        print('上传成功')


if __name__ == '__main__':
    uploadFile()
