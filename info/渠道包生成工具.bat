@echo off
REM 后续命令使用的是：UTF-8编码
chcp 65001
echo "请输入渠道前缀"
set /p channel=
echo "请输入渠道开始值"
set /p start=
echo "请输入渠道结束值"
set /p end=
for /l %%i in (%start%,1,%end%) do (
	echo "正在写入"+jwnl_%channel%%%i%
	echo jwnl_%channel%%%i% >> clean.txt
	)
pause