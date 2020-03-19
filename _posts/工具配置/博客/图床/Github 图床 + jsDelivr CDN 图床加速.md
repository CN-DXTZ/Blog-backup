---
title: Github 图床 + jsDelivr CDN 图床加速
author: DXTZ
tags: [博客, 图床, Github, jsDelivr, CDN]
categories:
  - [工具配置, 博客, 图床]
date: 2020-02-11 15:47:08
---

## 前言
用 MarkDown 写文章一定会涉及到一个问题 —— 插入图片，而图床是一个很好的解决方案。
图床，指储存图片的服务器，通过上传图片至服务器，之后即可通过所生成的链接随时访问。
本文选取 Github 作为图床的云端，并通过 jsDelivr CND 加速访问，以实现简单快速且免费稳定的图床配置。
而若想进一步一键上传图片并生成图片链接，参见下一篇：[《Github 图床 + PicGo 图床插件（VScode版）》](https://cn-dxtz.github.io/工具配置/博客/图床/Github%20图床%20+%20PicGo%20图床插件（VScode版）/)

<!--more-->

## GitHub 配置
图床的选择有很多，不一一列举，但是想要不担心小网站挂掉跑路，还得选大厂。
但大厂一般不但要收费，而且还操繁琐。故此既想免费，且操作简单，Github 必然是首选。
而且 Github 还基本不限流量。(*官方说明：推荐 1GB 以下，超过可能会收到邮件提醒；最大限制为 100GB，超过 75GB 每次更改都会警告。 -- [What is my disk quota? · GitHub Help](https://help.github.com/en/github/managing-large-files/what-is-my-disk-quota)*)

### 创建图床仓库
首先在 GitHub 上创建一个图床的云端仓库，具体步骤如下：
打开[GitHub](https://github.com/)官网，登录/注册后，
在主页选择【new】进入新建仓库页面，

![ImgBed_GitHub_1.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_GitHub_1.jpg)

并依次【填写 Repository name（仓库名）】 -> 【填写 Description（仓库描述）】 -> 【勾选 Initialize... （初始化 README）（可选）】 -> 【点击 Create Repository（创建仓库）】

![ImgBed_GitHub_2.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/ImgBed_GitHub_2.jpg)
### 简单使用
以上，图床的云端仓库就创建完了，可以通过下列链接访问：
```
https://raw.githubusercontent.com/用户名/图床仓库名/master/[文件路径...]/文件名
```
如上传一个测试文件test.png至仓库中，可直接通过：
https://raw.githubusercontent.com/CN-DXTZ/Blog-Img-Bed/master/test.png 进行访问

## jsDelivr 教程
由于国内的某些原因，直接访问 GitHub 太慢了，故通过 jsDelivr，一款快速免费的 CDN 解决方案进行优化。
### 原理介绍
> CDN的全称是Content Delivery Network，即内容分发网络。CDN是构建在网络之上的内容分发网络，依靠部署在各地的边缘服务器，通过中心平台的负载均衡、内容分发、调度等功能模块，使用户就近获取所需内容，降低网络拥塞，提高用户访问响应速度和命中率。 ——[百度百科](https://baike.baidu.com/item/CDN/420951?fr=aladdin)

简而言之，就是数据在总服务器中，通过分发至就近的服务器，提高用户访问的响应速度。
如存储在 GitHub 图床仓库的图片文件由于国内的某些原因访问太慢，而通过 jsDelivr 分发至就近的国内的服务器，就可以使得博文中插入的图片可以更快更高效地获取。
### 使用教程
使用方法也极为简单，只是将之前的域名 `raw.githubusercontent.com/` 用 `cdn.jsdelivr.net/gh/` 进行替换，并删除 `master/`，即
```
https://cdn.jsdelivr.net/gh/用户名/图床仓库名/[文件路径...]/文件名
```
则上述测试文件即可通过：
https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/test.png 进行访问
*更为详细地使用方法参见 [jsDelivr官方文档](https://www.jsdelivr.com/?docs=gh)*

## 结语
通过上述方法，即可免费稳定且高效快速地使用图床，然而此方法，图片的上传与网络链接的生成仍然过于繁琐，故此使用一种更为便捷地方法，可以一键上传图片并生成 MarkDown 语法的图片链接，参见：[《Github 图床 + PicGo 图床插件（VScode版）》](https://cn-dxtz.github.io/工具配置/博客/图床/Github%20图床%20+%20PicGo%20图床插件（VScode版）/)