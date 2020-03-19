---
title: Github 图床 + PicGo 图床插件（VScode版）
author: DXTZ
tags: [博客, 图床, Github, PicGo, VScode]
categories:
  - [工具配置, 博客, 图床]
date: 2020-02-11 18:48:08
---

## 前言
若不会 Github 图床配置，参见上一篇[《Github 图床 + jsDelivr CDN 图床加速》](https://cn-dxtz.github.io/工具配置/博客/图床/Github%20图床%20+%20jsDelivr%20CDN%20图床加速/)
本文选取插件 PicGo 实现一键上传图片至图床，并生成 MarkDown 语法的图片链接。
PicGo 有很多平台的版本，大同小异，由于本人使用 VScode 进行博客撰写，故选取的是 VScode 插件版。其他版本如 Windows 版亦可作参考。

<!--more-->

## GitHub
[前文](https://cn-dxtz.github.io/工具配置/博客/图床/Github%20图床%20+%20jsDelivr%20CDN%20图床加速/)已经新建了 GitHub 的图床仓库，但该创建的仓库暂时只能本人直接通过网页上传图片，使用不便，于是通过新建一个私人认证，允许其他合规的程序通过认证直接访问仓库，更为便捷，具体步骤如下：
### 新建私人认证
在主页依次【点击个人头像】 -> 【选择 Settings】进入设置页面，

![ImgBed_GitHub_3.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_GitHub_3.jpg)

【点击 Developer settings】进入开发设置页面，

![ImgBed_GitHub_4.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_GitHub_4.jpg)

【点击 Personal access tokens】 -> 【点击 Generate new token】，进入新建私人认证页面，

![ImgBed_GitHub_5.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_GitHub_5.jpg)

【填写私人认证描述】，【全选repo】，【最后点击 Generate token】生成一个Token

![ImgBed_GitHub_6.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_GitHub_6.jpg)

![ImgBed_GitHub_7.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_GitHub_7.jpg)
**注：此Token只会显示一次，请保存，用于后续配置PicGo**

## PigGo
PigGo，一款一键上传图片至图床，并生成 MarkDown 语法的图片链接的软件，同类型的软件还有很多。
本文选取该软件主要是因为在 VScode 平台使用该插件较为方便，该软件的其他版本以可对照参考进行配置，大同小异。
### PigGo 配置
在插件库中搜索 PicGo 并安装，

![ImgBed_PicGo_1.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_PicGo_1.jpg)

完毕后打开设置页面进行配置（`Ctrl+,`）；
【搜索 PicGo】 -> 【下滑至 GitHub 相关处】进行配置：

![ImgBed_PicGo_2.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_PicGo_2.jpg)

- 3：选取当前图床：github
- 4：仓库分支：master（图床默认分支，若有修改则自拟）
- 5：自定义生成的 MarkDown 语法的图片链接：  
    `https://cdn.jsdelivr.net/gh/用户名/图床仓库名`（采取 jsDelivr CDN 加速）
    （*`https://raw.githubusercontent.com/用户名/图床仓库名`（标准 github 图床链接，不填写时默认使用，二者区别见[《Github 图床 + jsDelivr CDN 图床加速》](https://cn-dxtz.github.io/工具配置/博客/图床/Github%20图床%20+%20jsDelivr%20CDN%20图床加速/)）*）
- 6：在仓库内的存储路径：自定义
- 7：仓库：`用户名/图床仓库名`
- 8：token：之前保存的私人认证
注：若出现错误，请核对是否多或少 **/**
### PicGo 使用
打开键盘快捷键设置目录（`Ctrl+K, Ctrl+S`），
搜索 PicGo 相关命令：

![ImgBed_PicGo_3.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_PicGo_3.jpg)

可以看到一共有三个命令：
1. 从打开的文件浏览器手动插入图片：
```
Ctrl + Alt + E
```
2. 从剪贴板插入图片：
```
Ctrl + Alt + U
```
3. 从输入目录插入图片（相对目录和绝对目录都行）
```
Ctrl + Alt + O
```
使用后可知，十分简单，一键即可上传图片至图床，并生成 MarkDown 语法的图片链接。