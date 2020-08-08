# 基于Java+selenium的阅次元、好书友每日签到和在线任务领取  

`src/main/java/GoodBookFriendSpider.java`  
# 基于Java+HttpClient微博图片爬虫（单/多线程）  

`src/main/java/WeiboSpider/Weibo.java`  
`src/main/java/WeiboSpider/WeiboThreads.java`
# 阅次元、好书友每日签到和在线任务领取脚本如何部署在linux上 
1. 下载selenium驱动，自己放在项目同级目录驱动文件夹下，版本要和Chrome一致  
    地址：http://npm.taobao.org/mirrors/chromedriver  
2. 安装chrome  
    1. 下载安装脚本  
    在下载目录中，执行以下命令，将安装脚本下载到本地  
    `wget https://intoli.com/install-google-chrome.sh`  
    2. 然后授予可执行权限  
    `chmod 755 ./install-google-chrome.sh`  
    3. 执行脚本  
    `./install-google-chrome.sh`  
    安装脚本会自动下载、安装chrome（合适的版本），并且目前两个系统中，所缺少的依赖，都会被安装。  
    4. 测试安装结果  
    执行命令，`google-chrome-stable --no-sandbox --headless --disable-gpu --screenshot https://www.baidu.com/`  
    如果在当前文件夹中出现screenshot.png  则安装成功
    5. 查看Chrome版本  
    `google-chrome --version`  
3. 运行maven打包的项目jar文件
# CentOS 7 cron开启定时任务
> `systemctl start crond`启动cron服务   


- `systemctl stop crond`停止cron服务
- `systemctl restart crond`重启cron服务
- `systemctl enable crond.service`设置cron开机启动
> `crontab -e`添加定时任务  

> `0 1 * * *  nohup java -jar /root/goodbookfriend/lagou-spider-1.0-SNAPSHOT.jar &`每天1点执行  

> `crontab -l`查看定时任务

> `cat /var/log/cron` 查看crontab任务执行情况

### crontab任务执行情况中显示MAIL (mailed 3595 bytes of output but got status 0x0 04b#012  
`cat  /var/log/maillog`发现错误fatal: parameter inet_interfaces: no local interface found for ::1  
解决：  
1. 编辑/etc/postfix/main.cf文件,把inet_interfaces = all这一行前面的注释符号取消。
2. 注释掉inet_interfaces = localhost
3. `service postfix start`  

原因：  
此时可以正常发送mail了。如果脚本执行错误可以通过mail命令查看邮件。在mail程序中，输入序号查看邮件，输入d删除邮件，输入q退出。

根本原因：
脚本执行->需要将执行信息（nohup.out）发送到mail(/var/spool/mail/root)->设置错误导致无法发送mail->cat /var/log/cron中提示发送mail错误  
查看`cat /var/spool/mail/root` 可以得到脚本执行后台信息  
脚本/程序在哪个目录下执行，相对路径就是这个目录，而不是其存放的目录







# linux上chrome中文显示方块解决方法
将windows中的字体拷贝到centos中，然后执行几个命令即可。
windows xp中字体位于C:/WINDOWS/Fonts目录中，每中字体一个文件，比如simsun.ttc
centos中的字体文件位于/usr/share/fonts/,每种字体一个目录，比如wqy-zenhei
安装过程是，首先在centos的/usr/share/fonts/目录下新建simsun目录
然后将windows中的simsun.ttc拷贝到/usr/share/fonts/simsun目录
输入以下3个命令  
`mkfontscale`如果找不到命令mkfontscale则要安装，`yum install mkfontscale`  
`mkfontdir`  
`fc-cache -fv`fc命令也是一样的执行： `yum install fontconfig`   
然后再次打开的Chrome就显示中文了。 
```
如果安装时出现Could not resolve host: mirrors.cloud.aliyuncs.com; Name or service not known"
Trying other mirror.,出现类似的错误，则很有可能是dns解析的问题：可以在vi /etc/resolv.conf配置两个dns：
nameserver 8.8.8.8 ##google域名 服务器
nameserver 8.8.4.4 ##谷歌域名服务器  
然后service network restart 重启
```
如果还不行，重启机器，如果不生效最好也重启下应用reboot;
如果还不行再执行以下内容：
```$xslt
#yum groupinstall chinese-support
 
console终端也必须设置为utf8编码
1、console终端乱码
 
在/etc/profile文件的最后一行添加如下内容：
 
export LC_ALL="zh_CN.utf8"
```



如果找不到命令mkfontscale则要安装，yum install mkfontscale （fc命令也是一样的执行： yum install fontconfig ）
# selenium出现`Timed out receiving message from renderer`或`chrome not reachable`错误的解决办法
- ps -aux  | grep webdriver 看有没有残留Chrome进程，kill掉
- ps -aux  | grep chromedriver 看有没有残留chromedriver进程，kill掉
- Chrome不要最大化运行？
# chrome启动参数
|启动参数 |	作用|
|  ----  | ----  |
|--user-agent="" |	设置请求头的User-Agent|
|--window-size=1366,768 	|设置浏览器分辨率（窗口大小）|
|--headless |	无界面运行（无窗口）|
|--start-maximized |	最大化运行（全屏窗口）|
|--incognito |	隐身模式（无痕模式）|
|--disable-javascript 	|禁用javascript|
|--disable-infobars 	|禁用浏览器正在被自动化程序控制的提示|

# xpath语法查找元素
一.绝对路径（不要使用，除非已经使用了所有方式仍然无法定位）
方法：根据实际目录，逐层输写。
例子： find_element_by_xpath("/html/body/div[2]/form/span/input") #div[2]指第2个元素
二.相对路径（建议使用）

方法:首先找目录元素是否有”精准元素“即唯一能标识的属性，找到，则用此属性定位；

1. 通过元素本身的唯一属性定位

   方法：找到目标元素所在的”精准元素“即唯一标识属性，使用此属性定位

    1.1 通过id属性定位

      例：find_element_by_xpath("//input[@id='input']")        #@后跟属性，可以是任何属性

    1.2 通过name属性定位

      例：find_element_by_xpath("//div[@name='q']")
      
  

### 在使用selenium-Java对元素进行定位时经常遇见以下两种问题：

问题一：在当前页面进行跳转时，提示 Unable to locate element

原因：driver在打开网页时，网页加载速度过慢，导致接下来的语句找不到元素；

解决办法：在跳转之前Thread.sleep(3000);

问题二：在页面跳转时产生窗口切换，提示Unable to locate element

原因：窗口句柄还停留在上一个页面，需要对窗口进行切换，获得当前句柄之后，再进行跳转，就不会出现问题了。

java实现如下：
```
/*
* 窗口切换，获取窗口句柄
*/
String winHandleBefore = driver.getWindowHandle(); 
for(String winHandle : driver.getWindowHandles()) {
if (winHandle.equals(winHandleBefore)) {
continue;
}
driver.switchTo().window(winHandle); 
break;  
} 

driver.findElement(By.linkText("基本信息")).click();
try {
Thread.sleep(1000);
} catch (InterruptedException e) {
// TODO Auto-generated catch block
e.printStackTrace();

}
```

### 报错ElementClickInterceptedException的解决方案
可能是有浮动框挡住了按钮，需要先关闭浮动框

### 找不到元素
可能是该元素id会变化
