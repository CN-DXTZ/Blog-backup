---
title: JAVA 虚拟机
author: DXTZ
tags: [JAVA, 虚拟机, JVM]
categories:
  - [后端, JAVA]
date: 2020-03-04 19:59:47
---
https://blog.csdn.net/zhangjg_blog/article/details/20380971
## JVM
JAVA虚拟机即Java Virtual Machine（JVM），是一个用于在操作系统之上模拟出一个虚拟计算机的程序。JAVA虚拟机通过模拟拥有自己虚构的软硬件，包括：处理器、寄存器、堆、栈以及相应的指令系统。
<!--more-->

通过JVM隔绝了应用程序与操作系统的直接交互，如下图，故此只需将Java程序编译为JVM的目标代码（字节码），即可实现跨平台运行。因为无论在任何操作系统，已经编译好的字节码只需要执行在JVM内即可，而具体不同的操作系统相关的细节，已经隐藏在JVM的抽象中，即所谓的“一次编译，处处运行”。

![JVM_02.png](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/JVM_02.png)

## 内存划分

![JVM_2.png](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/JVM_2.png)


### 程序计数器：


## 类加载机制

## 垃圾回收