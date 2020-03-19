---
title: instant.page：预加载提升博客访问速度
author: DXTZ
tags: [博客, Hexo, instant.page]
categories:
  - [工具配置, 博客, Hexo]
date: 2020-02-28 19:41:17
---

## 介绍
[instant.page](https://instant.page/) 是一款开源的JS脚本，核心是即时预加载技术，通过检测鼠标在一个连接上的悬停时间，超过一定阈值即**可能**进一步访问，故在点击前对此页面进行预加载，而当用户**实际**点击该链接后，就从预加载的缓存中直接读取页面内容，在感观上提升了访问速度，但其实在打开页面前已经加载到本地了，故可以加快渲染。

<!--more-->
其效果如下图：

![instant_page_1.gif](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/instant_page_1.gif)
从红框中可以看到，在未点击目标链接前，只要悬停在目标链接上，对应链接的网页就已经预加载到本地了。

## 使用方法

### 官方外链
使用起来十分简单，将官网的插入脚本的代码添加至 `themes\主题名\layout\layout.ejs` 文件的 `</body>` 标签之前即可：
```html
<script src="//instant.page/3.0.0" type="module" defer integrity="sha384-OeDn4XE77tdHo8pGtE1apMPmAipjoxUQ++eeJa6EtJCfHlvijigWiJpD7VDPWXV1"></script>
```

### 本地链
为避免由于国内的某些原因，直接访问该存储在国外服务器上的官方脚本过于缓慢，亦可通过保存至本地进行加载，下载地址见【官网[Technical details](https://instant.page/tech)】的【[Download the latest version](https://instant.page/3.0.0)（本文版本下载链接，右键另存为即可）】进行下载

本文保存至： `themes\主题名\source\js\instantpage-3.0.0.js`，然后通过类似官方外链的方法插入该脚本（将下列代码添加至 `themes\主题名\layout\layout.ejs` 文件的 `</body>` 标签之前）:
```html
<script src="/js/instantpage-3.0.0.js" type="module"></script>
```
如下图：

![instant_page_2.jpg](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/instant_page_2.jpg)