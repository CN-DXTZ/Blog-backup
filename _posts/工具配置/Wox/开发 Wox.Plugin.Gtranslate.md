---
title: 开发 Wox.Plugin.Gtranslate
author: DXTZ
tags: [Wox, 谷歌翻译]
categories:
  - [工具配置]
date: 2020-03-17 10:09:28
---

wox 十分好用，不过安装的好几个翻译插件都不好使，要不干脆没法用了，能用的用起来也不顺手，因此直接自己写了一个。通过破解爬取的[谷歌翻译](https://translate.google.cn/)的请求，通过python调用该接口实现快速的英汉互译，当然其他语言也可以自动识别并翻译。

<!--more-->
之前开发 Minimalist (hexo theme) 的时候没有记录开发过程是一个遗憾，所以这次开发 Wox.Plugin.Gtranslate 特意记录一下。

## 谷歌翻译API
谷歌翻译没有免费的公共API，不过可以通过爬取[谷歌翻译](https://translate.google.cn/)的请求，实现类似谷歌翻译API的效果。

### Request: 
先在页面中输入test，爬一下请求
- Request Method: GET
- Request URL: 

``` None
https://translate.google.cn/translate_a/single?client=webapp&sl=en&tl=zh-CN&hl=en&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&otf=1&pc=1&ssel=3&tsel=0&kc=2&tk=651411.1001436&q=test
```

响应格式很简单，就JSON，具体内容很多就不附上了：
- Response content-type: application/json; charset=utf-8

### 解析Request
请求的URL是：`https://translate.google.cn/translate_a/single`
参数含义如下：
- client = webapp 访问服务器的客户端
- sl = en 输入源文本的源语言代码，可以为auto，表示谷歌翻译自动识别语言类型
- tl = zh-CN 输出翻译的目标语言代码
- hl = en 界面语言，如词性等，返回noun（"en"）还是名词（"zh-CN"）
- dt = 需要服务器返回的数据种类：
  - t 源文本的直接翻译结果
  - at 备选翻译
  - rm 源文本音译
  - bd 字典（翻译语言解释），当源文本为单个词（组）时生效
  - md 定义（源语言解释），当源文本为单个词（组）时生效
  - ss 同义词，当源文本为单个词（组）时生效
  - ex 例子
  - rw 参考
- *otf, pc, ssel, tsel, kc... 没太搞懂，随便删了或者修改也没影响response结果，忽略*
- **tk** 根据源文本通过JS生成的加密token
- q = test 源文本

### tk计算
#### JS 计算 tk
根据上述对Request的分析可以知道，其他参数直接按需要设定就可以了，唯一的问题就是根据请求参数q（源文本）计算生成的加密参数tk（token）。

tk的计算，其实是通过谷歌翻译的内置的JS脚本实现的，通过一个计算出的TKK值和请求参数q（源文本）共同计算得出的。

我肯定不会算，但我会Google啊，国外已经有大佬实现了简化版的tk计算函数，[tk.js](https://github.com/CN-DXTZ/Wox.Plugin.Gtranslate/blob/master/tk.js)

``` javascript
var TKK = ((function() {
  var a = 561666268;
  var b = 1526272306;
  return 406398 + '.' + (a + b);
})());
 
function b(a, b) {
  for (var d = 0; d < b.length - 2; d += 3) {
      var c = b.charAt(d + 2),
          c = "a" <= c ? c.charCodeAt(0) - 87 : Number(c),
          c = "+" == b.charAt(d + 1) ? a >>> c : a << c;
      a = "+" == b.charAt(d) ? a + c & 4294967295 : a ^ c
  }
  return a
}
 
function tk(a) {
    for (var e = TKK.split("."), h = Number(e[0]) || 0, g = [], d = 0, f = 0; f < a.length; f++) {
        var c = a.charCodeAt(f);
        128 > c ? g[d++] = c : (2048 > c ? g[d++] = c >> 6 | 192 : (55296 == (c & 64512) && f + 1 < a.length && 56320 == (a.charCodeAt(f + 1) & 64512) ? (c = 65536 + ((c & 1023) << 10) + (a.charCodeAt(++f) & 1023), g[d++] = c >> 18 | 240, g[d++] = c >> 12 & 63 | 128) : g[d++] = c >> 12 | 224, g[d++] = c >> 6 & 63 | 128), g[d++] = c & 63 | 128)
    }
    a = h;
    for (d = 0; d < g.length; d++) a += g[d], a = b(a, "+-a^+6");
    a = b(a, "+-3^+b+-f");
    a ^= Number(e[1]) || 0;
    0 > a && (a = (a & 2147483647) + 2147483648);
    a %= 1E6;
    return a.toString() + "." + (a ^ h)
}
```

#### python 计算 tk
由于wox的插件只支持用C#或者python编写，所以这个JS脚本不能直接使用。
C#基本没用过用，当然用python编写。
python有可以运行JS的库，不过有的太慢，有的还需要额外的引擎，所以通过js2py直接一劳永逸的把JS转化为python
```python
import js2py
js2py.translate_file('tk.js', 'tk.py')
```
这杨就把tk.js转换成了[tk.py](https://github.com/CN-DXTZ/Wox.Plugin.Gtranslate/blob/master/tk.py)，然后以后直接调用tk.py就可以了.

## Gtranslate

### Google Translate
以上已经有了完整的谷歌翻译API的方法了。
根据输入文本q而变化的值包括：
- tk 根据输入文本计算得出
- sl 输入文本语言代码
- tl 输出文本语言代码
第一项通过谷歌翻译API已经可以自动计算得出了，现在需要进行的就是自动识别输入语言，最后就可以通过谷歌翻译转换为所需的翻译结果了。

#### 自动识别输入语言
前文介绍谷歌翻译API时，已经提及了语言代码可以用auto代替，谷歌翻译将自动识别语言。然而，虽然sl和tl都可以用auto，但是当tl为auto是，将无法保证最终的翻译语言是真正需要的，但可以通过该方法自动识别输入语言。
即通过请求 https://translate.google.cn/translate_a/single?client=t&sl=auto&tl=auto&tk=TK&q=QUERY 解析回应获得输入文本的语言代码。
其中不需要的参数已经省略了，避免回应数据过大，关键在于：
- **&sl=auto&tl=auto**
- &tk=TK&q=QUERY 源文本q及对应计算值tk随输入文本QUERY变化

测试的结果如下：

| 语言 | request | response |
| :---: | :---: | :---: |
| 英语 | https://translate.google.cn/translate_a/single?client=t&sl=auto&tl=auto&tk=833972.690890&q=test | [None, None, **'en'**, None, None, None, 0.7647059, [], [['en'], None, [0.7647059], ['en']]] |
| 汉语 | https://translate.google.cn/translate_a/single?client=t&sl=auto&tl=auto&tk=334219.207605&q=测试 | [None, None, **'zh-CN'**, None, None, None, 1.0, [], [['zh-CN'], None, [1.0], ['zh-CN']]] |
| 德语 | https://translate.google.cn/translate_a/single?client=t&sl=auto&tl=auto&tk=158329.284935&q=Prüfung | [None, None, **'de'**, None, None, None, 1.0, [], [['de'], None, [1.0], ['de']]] |
| 俄语 | https://translate.google.cn/translate_a/single?client=t&sl=auto&tl=auto&tk=52680.458422&q=тестовое%20задание | [None, None, **'ru'**, None, None, None, 1.0, [], [['ru'], None, [1.0], ['ru']]] |

根据上述response的结果，即可发现加粗部分——`json[2]`为输入文本的语言代码，因此则可以得出输入和输出文本的语言代码：
1. 输入其他语言比如`en`（英语）, 翻译为`zh-CN`（汉语）
2. 输入`zh-CN` （汉语）, 翻译为`en`（英语）

所以通过谷歌翻译实现自动识别输入语言的代码如下：
``` python
from tk import tk
import requests
def RecgLang(QUERY):
    TK = tk.tk(QUERY)
    response = requests.get(BASE_URL + "?client=t&sl=auto&tl=auto&tk={}&q={}".format(TK, QUERY))
    re_lang = response.json()[2]
    my_lang = "zh-CN"
    if re_lang != my_lang:
        [SL, TL] = [re_lang, my_lang]
    else:
        obj_lang = "en"
        [SL, TL] = [my_lang, obj_lang]
    return [SL, TL]
```

#### 谷歌翻译
通过上述RecgLang(QUERY)方法，就可以得出输入和输出的语言代码[SL, TL]，则可以正式调用谷歌翻译进行翻译了。
此外，相比较之前的请求链接，还需要添加如下参数：
- **hl = en** 界面语言，返回noun，verb等词性
- **dt =** 服务器返回的数据种类：
  - **t** 源文本的直接翻译结果
  - **bd** 字典，当源文本为单个词（组）时生效

以下以分别以英汉为例：

英语：
request: 
https://translate.google.cn/translate_a/single?client=t&hl=en&dt=t&dt=bd&sl=en&tl=zh-CN&tk=749122.875836&q=waste
response:
> [[[**'浪费'**, 'waste', None, None, 1]], [[**'verb'**, **['浪费', '耗费', '耗', '糜费', '白费', '耗损', '虚度', '枉费', '靡', '糟', '作践', '暴', '摅']**, [['浪费', ['waste', 'squander'], None, 0.51879317], ['耗费', ['spend', 'consume', 'waste', 'squander'], None, 0.00031025222], ['耗', ['consume', 'waste', 'spend', 'squander'], None, 0.00022698537], ['糜费', ['waste'], None, 0.00019415087], ['白费', ['waste'], None, 0.00017677639], ['耗损', ['consume', 'waste', 'lose'], None, 9.762519e-05], ['虚度', ['waste', 'fritter away', 'squander', 'desecrate', 'spend time in vain', 'dissipate'], None, 8.350325e-05], ['枉费', ['waste', 'try in wane', 'spend in vane'], None, 7.142412e-05], ['靡', ['waste', 'go with fashion'], None, 4.9089027e-05], ['糟', ['waste', 'spoil'], None, 7.183312e-06], ['作践', ['spoil', 'humiliate', 'insult', 'disparage', 'waste'], None, 2.0580533e-06], ['暴', ['expose', 'bulge', 'ruin', 'waste', 'spoil', 'stand out'], None, 2.0580533e-06], ['摅', ['dart', 'jump up', 'express', 'state', 'unwind', 'waste'], None, 2.0580533e-06]], 'waste', 2], [**'noun'**, **['废物', '垃圾', '糜费', '旷', '废墟', '靡', '废话']**, [['废物', ['waste', 'refuse', 'trash', 'garbage', 'rubbish', 'junk'], None, 0.2528396], ['垃圾', ['garbage', 'waste', 'refuse', 'trash', 'rubbish', 'dump'], None, 0.06293675], ['糜费', ['waste'], None, 0.00019415087], ['旷', ['waste', 'wilderness'], None, 0.00015846132], ['废墟', ['ruins', 'ruin', 'debris', 'wreckage', 'remains', 'waste'], None, 9.171038e-05], ['靡', ['waste'], None, 4.9089027e-05], ['废话', ['nonsense', 'bullshit', 'rubbish', 'blah', 'guff', 'waste'], None, 2.0580533e-06]], 'waste', 1], [**'adjective'**, **['荒', '燥', '废弃的']**, [['荒', ['waste', 'barren', 'desolate', 'uncultivated'], None, 0.0008838263], ['燥', ['dry', 'arid', 'parched', 'droughty', 'rainless', 'waste'], None, 
2.0580533e-06], ['废弃的', ['deserted', 'out-of-date', 'waste', 'obsolete']]], 'waste', 3]], 'en', None, None, None, None, []]

汉语：
request: 
https://translate.google.cn/translate_a/single?client=t&hl=en&dt=t&dt=bd&sl=zh-CN&tl=en&tk=387423.252449&q=计划
response:
> [[[**'plan'**, '计划', None, None, 2]], [[**'verb'**, **['plan', 'project', 'design', 'map out']**, [['plan', ['计划', '打算', '安排', '准备', '设计', '谋'], [57285], 0.13117145], ['project', ['投射', '计划', '预测', '放映', '发射', '预报'], [57285], 0.009502028], ['design', ['设计', '计划', '酝酿', '酝'], [57285], 0.00045852305], ['map out', ['制订', '计划'], None, 4.3818486e-07]], '计划', 2], [**'noun'**, **['plan', 'program', 'project', 'projet', 'programme']**, [['plan', ['计划', '规划', '打算', '方案', '策划', '图'], [72059], 0.13117145], ['program', ['程序', '计划', '方案', '节目', '规划', '日程'], [72059], 0.06493458], ['project', ['项目', '工程', '计划'], None, 0.009502028], ['projet', ['谟', '计划'], None, 1.2098672e-06], ['programme', ['程序', '计划', '方案', '节目', '规划', '日程'], [72059]]], '计划', 1], ['adjective', ['planned'], [['planned', ['计划'], None, 0.028367816]], '计划', 3]], 'zh-CN', None, None, None, None, []]

通过上述示例可以验证通过该request确实可以获得所需的response，所以谷歌翻译实现如下：
``` python
from tk import tk
import requests
def GoogleTranslate(SL, TL, QUERY):
    TK = tk.tk(QUERY)
    tt = BASE_URL + "?client=t&hl=en&dt=t&dt=bd&sl={}&tl={}&tk={}&q={}".format(SL, TL, TK, QUERY)
    response = requests.get(tt)
    return response.json()
```

### GTranslate
根据上述示例可知，response的所需元素——加粗部分对应意义如下：
1. `json[0]`为源文本的直接翻译结果，而真正的**内容**即为`json[0][0][0]`
2. `json[1]`为源文本的字典（句子则该项为None），下一级为各类词性，再下一级第一项即`json[1][][0]`为**词性名**，第二项`json[1][][1]`为该词性对应的**释义列表**。

#### wox 插件编写规范
接下来根据这些json内容开始编写wox插件。[Wox文档·Python插件](http://doc.wox.one/zh/plugin/python_plugin.html)已经给出了示例教程作为参考。

#### 基础内容显示
首先需要[创建plugin.json文件](http://doc.wox.one/zh/plugin/plugin_json.html)：

```json
{
    "ID": "c342af6e4e1c4a5a9e0ce4efb9d1f05b",
    "ActionKeyword": "tran",
    "Name": "Gtranslate/谷歌翻译",
    "Description": "Google Translate/谷歌英汉互译",
    "Author": "CN-DXTZ",
    "Version": "1.0.0",
    "Language": "python",
    "Website": "https://github.com/CN-DXTZ/Wox.Plugin.Gtranslate",
    "IcoPath": "Images\\Gtranslate.png",
    "ExecuteFileName": "Gtranslate.py"
}
```

然后根据教程可知，wox的Python插件整体编写规范如下:

```python
from wox import Wox, WoxAPI
import webbrowser
# 必须：基础Wox基类
class Main(Wox):
    # 必须：用户执行查询时自动调用
    def query(self, key): # key: 输入
        results = [] # 返回的JSON内容
        # 按字典格式添加一项（行）标准的内容
        results.append({
            "IcoPath": "Images/app.ico", # 每项（行）最左端显示的图标
            "Title": title, # 每项（行）标题显示的内容
            "SubTitle": subTitle, # 每项（行）副标题显示的内容
            # 可选项：该行点击或回车后调用的方法
            "JsonRPCAction": {
                    "method": "openUrl", # 方法名
                    "parameters": ["www.baidu.com"], # 方法参数，必须以数组形式传递
                    "dontHideAfterAction": False, # 是否显示窗口
            },
        })
        return results
    # 自定义方法，可以调用WoxAPI。调用格式：Wox.方法名(参数)。
    # 方法列表见"%userprofile%\AppData\Local\Wox\app-1.3.524\JsonRPC\wox.py"
    def openUrl(self,url):
        webbrowser.open(url)
        # WoxAPI.change_query(url)
# 必须
if __name__ == "__main__":
  Main()
```

根据上述规范，将之前已经准备好的JSON内容填充进去，首先是`json[0][0][0]`，源文本的直接翻译内容：

```python
from wox import Wox, WoxAPI
from GoogleTranslate import GoogleTranslate, RecgLang

ICO_PATH = "Images/Gtranslate.png"

class Gtranslate(Wox):
    def query(self, query):
        results = []
        [SL, TL] = RecgLang(query) # 识别语言
        Jresponse = GoogleTranslate(SL, TL, query) # 谷歌翻译
        # 加入简单翻译结果项
        results.append({
            "IcoPath": ICO_PATH,
            "Title": Jresponse[0][0][0],
        })
        return results
if __name__ == "__main__":
    Gtranslate()
```

则显示结果如下：

![Gtranslate_2.png](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/Gtranslate_2.png)

接着加入显示字典，先判断`json[1]`是否为`None`确定有无字典项，然后遍历`json[1][]`，其中`json[1][][0]`为词性名，`json[1][][1]`为该词性对应的释义**列表**

```diff-python
from wox import Wox, WoxAPI
from GoogleTranslate import GoogleTranslate, RecgLang

ICO_PATH = "Images/Gtranslate.png"
+ DICT_PREFIX = {"noun": "n", "verb": "v", "pronoun": "pron", "adjective": "adj", "adverb": "adv", "numeral": "num", "article": "art", "preposition": "prep", "conjunction": "conj", "interjection": "interj", "abbreviation": "abbr"}

class Gtranslate(Wox):
    def query(self, query):
        results = []
        [SL, TL] = RecgLang(query)
        Jresponse = GoogleTranslate(SL, TL, query)
        results.append({
            "IcoPath": ICO_PATH,
            "Title": Jresponse[0][0][0],
        })
+       # 加入词(组)字典详情项
+       if Jresponse[1] != None: # 如果存在字典项
+           for item in Jresponse[1]:
+               prefix = item[0] # 词性前缀
+               if prefix in DICT_PREFIX: # 词性前缀转换为缩写
+                   prefix = DICT_PREFIX[prefix]
+               # 拼接词性和对应的释义列表
+               str = "{}.  {}".format(prefix, ", ".join(item[1]))
+               results.append({
+                   "IcoPath": ICO_PATH,
+                   "Title": str,
+               })
        return results
if __name__ == "__main__":
    Gtranslate()
```

则显示结果如下：

![Gtranslate_3.png](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/Gtranslate_3.png)

此时Wox.Plugin.Gtranslate的基础已经构建完成了

#### 一键复制结果到剪贴板

再添加功能性方法，首先对于翻译结果，经常需要复制，故添加一键复制结果到剪贴板的功能。
通过导入pyperclip包，该包中有一个方法——pyperclip.copy(string)可以将字符串复制到剪贴板：

```diff-python
from wox import Wox, WoxAPI
from GoogleTranslate import GoogleTranslate, RecgLang
+ # 考虑到用户不一定安装了pyperclip
+ try:
+   import pyperclip
+   flag_package = True
+ except ImportError:
+   flag_package = False

ICO_PATH = "Images/Gtranslate.png"
DICT_PREFIX = {"noun": "n", "verb": "v", "pronoun": "pron", "adjective": "adj", "adverb": "adv", "numeral": "num",
               "article": "art", "preposition": "prep", "conjunction": "conj", "interjection": "interj", "abbreviation": "abbr"}

class Gtranslate(Wox):
    def query(self, query):
        results = []
        [SL, TL] = RecgLang(query)
        Jresponse = GoogleTranslate(SL, TL, query)
        results.append({
            "IcoPath": ICO_PATH,
            "Title": Jresponse[0][0][0],
+           "SubTitle": "复制到剪贴板",
+           # 调用copy方法，传入该项的内容，并隐藏wox
+           "JsonRPCAction": {
+               "method": "copy",
+               "parameters": [Jresponse[0][0][0]],
+               "dontHideAfterAction": False,
            },
        })
        if Jresponse[1] != None:
            for item in Jresponse[1]:
                prefix = item[0]
                if prefix in DICT_PREFIX:
                    prefix = DICT_PREFIX[prefix]
                str = "{}.  {}".format(prefix, ", ".join(item[1]))
                results.append({
                    "IcoPath": ICO_PATH,
                    "Title": str,
+                   "SubTitle": "复制到剪贴板",
+                   # 调用copy方法，传入该项的内容，并隐藏wox
+                   "JsonRPCAction": {
+                       "method": "copy",
+                       "parameters": [str],
+                       "dontHideAfterAction": False,
                    },
                })
        return results

+   def copy(self, ans):
+       if flag_package: # 如果成功导入了pyperclip，则复制内容
+           pyperclip.copy(ans)
+       else: # 如果没成功导入pyperclip，则提示错误
+           WoxAPI.change_query("tran ERROR: pyperclip is not installed", True)
if __name__ == "__main__":
    Gtranslate()
```

则显示结果如下：

![Gtranslate_4.gif](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/Gtranslate_4.gif)

#### 打开浏览器查看详情

有时候通过插件显示的翻译结果不够全面，需要打开网页在浏览器中查看详情，故添加该功能。
通过导入webbrowser包，该包中有一个方法——webbrowser.open(url)可以用默认浏览器打开链接：

```diff-python
from wox import Wox, WoxAPI
from GoogleTranslate import GoogleTranslate, RecgLang
try:
    import pyperclip
    flag_package = True
except ImportError:
    flag_package = False
+ import webbrowser

ICO_PATH = "Images/Gtranslate.png"
DICT_PREFIX = {"noun": "n", "verb": "v", "pronoun": "pron", "adjective": "adj", "adverb": "adv", "numeral": "num",
               "article": "art", "preposition": "prep", "conjunction": "conj", "interjection": "interj", "abbreviation": "abbr"}

class Gtranslate(Wox):
    def query(self, query):
        results = []
        [SL, TL] = RecgLang(query)
        Jresponse = GoogleTranslate(SL, TL, query)
        results.append({
            "IcoPath": ICO_PATH,
            "Title": Jresponse[0][0][0],
            "SubTitle": "复制到剪贴板",
            "JsonRPCAction": {
                "method": "copy",
                "parameters": [Jresponse[0][0][0]],
                "dontHideAfterAction": False,
            },
        })

        # 打开浏览器查看详情
+       results.append({
+           "IcoPath": ICO_PATH,
+           "Title": "【Open Browser to View Details】",
+           "SubTitle": "打开浏览器查看谷歌翻译详情",
+           "JsonRPCAction": {
+               "method": "openUrl",
+               # 网页版搜索链接格式
+               "parameters": ["https://translate.google.cn/#view=home&op=translate&sl={}&tl={}&text={}".format(SL, TL, query)],
+               "dontHideAfterAction": False,
+           },
+       })

        if Jresponse[1] != None:
            for item in Jresponse[1]:
                prefix = item[0]
                if prefix in DICT_PREFIX:
                    prefix = DICT_PREFIX[prefix]
                str = "{}.  {}".format(prefix, ", ".join(item[1]))
                results.append({
                    "IcoPath": ICO_PATH,
                    "Title": str,
                    "SubTitle": "复制到剪贴板",
                    "JsonRPCAction": {
                        "method": "copy",
                        "parameters": [str],
                        "dontHideAfterAction": False,
                    },
                })
        return results

    def copy(self, ans):
        if flag_package:
            pyperclip.copy(ans)
        else:
            WoxAPI.change_query("tran ERROR: pyperclip is not installed", True)
            
+   def openUrl(self, url):
+       webbrowser.open(url)
if __name__ == "__main__":
    Gtranslate()
```

则显示结果如下：

![Gtranslate_5.gif](https://cdn.jsdelivr.net/gh/CN-DXTZ/Blog-Img-Bed/PicGo/Gtranslate_5.gif)

到此Wox.Plugin.Gtranslate的主体部分已经全部完成了