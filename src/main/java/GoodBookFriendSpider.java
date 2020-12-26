
import WeiboSpider.KillHandler;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.OSInfo;

import java.io.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.bytebuddy.implementation.MethodDelegation.to;

/**
 * 好书友和阅次元每日签到和在线任务 ChromeDriver 83.0.4103.39
 */
public class GoodBookFriendSpider {
    private static final String BOOK_URL="https://www.93hsy.com"; // https://www.93book.com
    private static final String ABOOKY_URL="https://www.abooky.com";
    private static WebDriver driver = null;
    private static Logger logger = LoggerFactory.getLogger(GoodBookFriendSpider.class);
    // 初始化配置环境
    static {
        //程序被kill杀死时执行    关闭钩子本质上是一个线程（也称为Hook线程），对于一个JVM中注册的多个关闭钩子它们将会并发执行，所以JVM并不保证它们的执行顺序；由于是并发执行的，那么很可能因为代码不当导致出现竞态条件或死锁等问题，为了避免该问题，可使用SignalHandler
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                finish();
            }
        });
        switch (OSInfo.getOSType()) {
            case LINUX:
                System.setProperty("webdriver.chrome.driver", "/root/goodbookfriend/chromedriver/chromedriver_linux64");
                break;
            case MACOSX:
                System.setProperty("webdriver.chrome.driver", getPath() + "/chromedriver/chromedriver_mac64");
                break;
            case WINDOWS:
                System.setProperty("webdriver.chrome.driver", getPath() + "/chromedriver/chromedriver_win32.exe");
                break;
            default:
                throw new RuntimeException("不支持当前操作系统类型");
        }
    }
    public static void main(String[] args) throws Throwable {
        while (true){
            if(isTime(false,
                    new MyTime(1,40),
                    new MyTime(14,10))){
                task();
            }
            Thread.sleep(30*1000L);
        }
//        while (true) {
//            task();
//            Thread.sleep(30*1000L);
//        }
    }
    public static void initChromeDriver() throws Throwable {
        //        System.out.println(GoodBookFriendSpider.class.getProtectionDomain().getCodeSource().getLocation().toString());
//        System.out.println(System.getProperty("java.class.path"));
        logger.info("\n\n\n\n");
        try {
            // ChromeOptions
            ChromeOptions chromeOptions = new ChromeOptions();
            // 设置后台静默模式启动浏览器
            //chromeOptions.addArguments("--headless");
            //隐藏滚动条, 应对一些特殊页面
            chromeOptions.addArguments("--hide-scrollbars");
            //谷歌文档提到需要加上这个属性来规避bug
            chromeOptions.addArguments("--disable-gpu");
            //在root权限下运行 linux  解决DevToolsActivePort文件不存在的报错
            chromeOptions.addArguments("--no-sandbox");
            // 不加载图片
            chromeOptions.addArguments("blink-settings=imagesEnabled=false");
            //设置分辨率
            chromeOptions.addArguments("--window-size=1920,1080");
            //最大化窗口运行
            //chromeOptions.addArguments("--start-maximized");
            //浏览器最大化 该方式有可能报错：Unable to receive message from renderer
            //driver.manage().window().maximize();
            //设置header
            //chromeOptions.addArguments("User-Agent='Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36'");
            // 谷歌驱动生成
            driver = new ChromeDriver(chromeOptions);
        }catch (Throwable e){
            logger.info("创建ChromeDriver失败");
            throw e;
        }
    }
    public static void finish(){
        logger.info("进入优雅关闭");
        //如果chromedriver没有及时关闭会一直在进程中，可能会占用端口
        if (driver != null) {
            screenshot();
        }
        if (driver != null) {
            driver.quit();
            logger.info("关闭driver : " + driver);
        }
        logger.info("优雅关闭结束");
    }
    static class MyTime{
        private int hour;
        private int minute;
        public MyTime(int hour,int minute){
            this.hour=hour;
            this.minute=minute;
        }
        public MyTime(int minute){
            this.minute=minute;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }
    }

    private static boolean isTime(boolean onlyMinute,MyTime... myTimeList) throws InterruptedException {
        Date date = new Date();
        for (MyTime myTime:myTimeList){
            if(onlyMinute){
                if(myTime.getMinute()==date.getMinutes()) {
                    return true;
                }
            }else {
                if(myTime.getHour()==date.getHours() && myTime.getMinute()==date.getMinutes()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void task(){
        try {
            if(driver==null){
                initChromeDriver();
            }
            abooky(driver);
            goodBookFriend(driver);
        } catch (Throwable e) {
            if (driver != null) {
                screenshot();
            }
            logger.info("程序异常", e);
        } finally {
            finish();
            logger.info("程序执行完毕");
        }
    }

    private static void abooky(WebDriver driver) {

        try {
            logger.info("阅次元开始");
            // 进入首页
            driver.get(ABOOKY_URL);
            logger.info("进入网站成功");
            //  模拟登录
            //点击登录按钮
            driver.findElement(By.xpath("//a[@class='aclogin']")).click();
            logger.info("点击登录成功");
            Thread.sleep(5000);
            //页面上用户名输入框id会变，所以不能直接取
            driver.findElement(By.xpath("//input[contains(@id,'username_')]")).sendKeys(getProperties("abooky.username"));
            logger.info("输入账号成功");
            driver.findElement(By.xpath("//input[contains(@id,'password3_')]")).sendKeys(getProperties("abooky.password"));
            logger.info("输入密码成功");
            //By.className("pn vm")不能有空格，可以使用By.xpath("//button[@class='pn vm']"
            driver.findElement(By.xpath("//button[@class='pn pnc']")).click();
            logger.info("登录成功");
            //登录完成后的页面
            Thread.sleep(10000);
            driver.findElement(By.xpath("//li[@id='mn_Nac60']")).click();
            logger.info("点击进入签到页面");
            Thread.sleep(10000);
            //点击每日领取任务
            if (isExist(driver, By.xpath("//a[@id='JD_sign']"))) {
                click(driver, By.xpath("//a[@id='JD_sign']"));
                logger.info("领取任务成功");
            } else {
                logger.info("已经签到了！无需再次签到！");
            }
            Thread.sleep(2000);
            logger.info("【阅次元完美结束】");
        } catch (Exception e) {
            logger.info("阅次元异常结束", e);
        }
        screenshot();
    }

    private static void goodBookFriend(WebDriver driver) {
        try {
            logger.info("好书友开始");
            // 进入首页
            driver.get(BOOK_URL);
            logger.info("进入网站成功");
            //  模拟登录
            driver.findElement(By.id("ls_username")).sendKeys(getProperties("GoodBookFriendSpider.username"));
            screenshot();
            logger.info("输入账号成功");
            driver.findElement(By.id("ls_password")).sendKeys(getProperties("GoodBookFriendSpider.password"));
            logger.info("输入密码成功");
            //By.className("pn vm")不能有空格，可以使用By.xpath("//button[@class='pn vm']"
            driver.findElement(By.xpath("//button[@class='pn vm']")).click();
            logger.info("点击登录成功");
            //登录完成后的页面
            Thread.sleep(10000);
            //关闭阻挡的浮动框
            click(driver, By.xpath("//em[@class='ignore_notice']"));
            logger.info("关闭浮动框成功");
            Thread.sleep(2000);
            //点击每日领取任务
            driver.findElement(By.id("k_misign_topb")).click();
            logger.info("点击每日领取任务成功");

            //点击每日4次任务
            int k = 0;
            while (!(isExist(driver, By.id("online_time")) && driver.findElement(By.id("online_time")).getText().equals("今日奖励已领完"))) {
                if (isExist(driver, By.id("online_time"))) {
                    logger.info(driver.findElement(By.id("online_time")).getText());
                }
                Thread.sleep(2000);
                click(driver, By.id("fwin_dialog_submit"));
                Thread.sleep(2000);
                //click(driver,By.id("online_link"));

                if (isExist(driver, By.id("online_time"))) {
                    driver.findElement(By.id("online_time")).click();
                    logger.info("点击了online_time");
                    k = 0;
                } else {
                    k++;
                    if (k > 5) {
                        driver.findElement(By.id("online_time")).click();
                    }
                }
                Thread.sleep(2000);
                //刷新 //2分钟刷新一次
                //driver.navigate().refresh();
                driver.get(BOOK_URL);
                Thread.sleep(1000 * 100);
            }
            logger.info(driver.findElement(By.id("online_time")).getText());
            Thread.sleep(2000);
            logger.info("【好书友完美结束】");
        } catch (Exception e) {
            logger.info("好书友异常结束", e);
        }
        screenshot();
    }

    public static void screenshot() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-(HH：mm)"); //转换时间格式
        String time = dateFormat.format(Calendar.getInstance().getTime()); //获取当前时间
        try {
            FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File("log/" + time + "截图.jpg"));
            logger.info("截图成功");
        } catch (Exception e) {
            logger.info("截图异常", e);
        }

    }

    private static void click(WebDriver driver, By by) throws Exception {
        if (driver != null && by != null) {
            try {
                if (isExist(driver, by)) driver.findElement(by).click();
            } catch (Exception e) {
                logger.info("元素存在但无法点击！", e);
            }

        }
    }

    private static boolean isExist(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            logger.info("未找到元素 " + by);
            return false;
        }
    }

    public static String getPath() {
        String path = GoodBookFriendSpider.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(0, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }


    public static String getProperties(String keyWord) throws IOException {
        /*  /src/main/resources/login.properties内容
        #阅次元
        abooky.username=123
        abooky.password=456
        #好书友
        GoodBookFriendSpider.username=123
        GoodBookFriendSpider.password=456
        */
        //通过该配置文件读取登录的账号和密码
        String filePath=null;
        switch (OSInfo.getOSType()) {
            case LINUX:
                filePath="/root/goodbookfriend/chromedriver/login.properties";
                break;
            case WINDOWS:
                filePath="src/main/resources/login.properties";
                break;
            default:
                throw new RuntimeException("不支持当前操作系统类型");
        }
        Properties prop = new Properties();
        InputStream inputStream=null;
        String value = null;
        try {
            // 通过输入缓冲流进行读取配置文件
            inputStream = new BufferedInputStream(new FileInputStream(new File(filePath)));
            // 加载输入流
            prop.load(inputStream);
            // 根据关键字获取value值
            value = prop.getProperty(keyWord);
        } finally {
            if(inputStream!=null) {
                inputStream.close();
            }
        }
        return value;
    }
}
