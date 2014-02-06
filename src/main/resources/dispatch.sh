#!/bin/sh
#-*- utf-8 -*-

echo ">create project root dir at:/usr/local/rtms"
mkdir /usr/local/rtms/

echo ">the base dir path is : /usr/local/rtms/workspace/"
RTMS_WORKSPACE=/usr/local/rtms/workspace
RTMS_WORKSPACE_TEMPLETE=$RTMS_WORKSPACE/templete
RTMS_WORKSPACE_DATA=$RTMS_WORKSPACE/data
RTMS_WORKSPACE_TOOL=$RTMS_WORKSPACE/tool
RTMS_WORKSPACE_CONFIG=$RTMS_WORKSPACE/config

echo ">create workspace dirs"
mkdir $RTMS_WORKSPACE
mkdir $RTMS_WORKSPACE_TEMPLETE
mkdir $RTMS_WORKSPACE_DATA
mkdir $RTMS_WORKSPACE_TOOL
mkdir $RTMS_WORKSPACE_CONFIG

echo ">give all dirs r,w,x permission"
chmod -R 7777 /usr/local/rtms/

echo ">copy kindlegen to workspace"
cp kindlegen $RTMS_WORKSPACE_TOOL/

echo ">create softlink for kindlegen"
ln -s $RTMS_WORKSPACE_TOOL/kindlegen /usr/bin/kindlegen

echo ">copy template file to workspace"
cp templete_* $RTMS_WORKSPACE_TEMPLETE/

echo ">copy fullTxt to workspace"
cp fullTxt $RTMS_WORKSPACE_TOOL/

echo ">give fullTxt execute permission"
chmod +x $RTMS_WORKSPACE_TOOL/fullTxt

echo ">copy kindlestrip to workspace"
cp kindlestrip $RTMS_WORKSPACE_TOOL/

echo ">give kindlestrip execute permission"
chmod +x $RTMS_WORKSPACE_TOOL/kindlestrip

echo ">copy redisMaintain script to workspace"
cp redisMaintain.sh $RTMS_WORKSPACE_TOOL/

echo ">give redisMaintain script to workspace"
chmod +x $RTMS_WORKSPACE_TOOL/redisMaintain.sh

echo ">copy properties file to workspace"
cp *.properties $RTMS_WORKSPACE_CONFIG/

echo ">copy feedlinks.txt to workspace"
cp feedlinks.txt $RTMS_WORKSPACE_CONFIG/
