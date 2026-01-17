import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TestNGSuite01 {

    private static final boolean headless=false;
    private static final boolean wait=false;

    private static WebDriver webDriver;
    private static String outputFileTimestamp;


    @Test
    public void Test001_Qubitsuite_signup_no_data() {
        openSignupPage();

        WebElement buttonElement = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//button[@type='submit']"),30L);
        buttonElement.click();

        checkRequiredElement(webDriver,"Nombre",null);
        checkRequiredElement(webDriver,"Apellido",null);
        checkRequiredElement(webDriver,"Email",null);
        checkRequiredElement(webDriver,"Teléfono","Por favor ingresa un teléfono válido");
        checkRequiredElement(webDriver,"Contraseña",null);
        checkRequiredElement(webDriver,"Repetir Contraseña",null);

        if (wait) {
            waitSeconds(30L);
        }
    }


    @Test
    public void Test002_Qubitsuite_signup_wrong_password() {
        openSignupPage();

        WebElement firstName = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='firstName']"),30L);
        firstName.sendKeys("Diego");
        WebElement lastName = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='lastName']"),30L);
        lastName.sendKeys("Maradona");
        WebElement email = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='email']"),30L);
        email.sendKeys("diego@gmail.com");

        Actions actions = new Actions(webDriver);
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        actions.sendKeys(Keys.RETURN).perform();
        WebElement phone = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='phone']"),30L);
        phone.sendKeys("1144445555");
        WebElement password = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='password']"),30L);
        password.sendKeys("1234567");
        WebElement password2 = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='password2']"),30L);
        password2.sendKeys("wrongPass");

        //muestra los passwords
        List<WebElement> eyeIcons = webDriver.findElements(By.xpath("//button/*[@class='remixicon-icon ']/.."));
        for (WebElement eyeIcon : eyeIcons) {
            eyeIcon.click();
        }

        WebElement buttonElement = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//button[@type='submit']"),30L);
        buttonElement.click();


        WebElement passwordError1 = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='password']/../..//p"),30L);
        String text = passwordError1.getText();
        Assert.assertEquals(text,"La contraseña debe tener al menos 8 caracteres"," password1 error no coincide");

        WebElement passwordError2 = FluentWaitUtil.getWebElementFluentWait(webDriver,By.xpath("//input[@name='password']/../..//p"),30L);
        text = passwordError2.getText();
        Assert.assertEquals(text,"La contraseña debe tener al menos 8 caracteres"," password2 error no coincide");




        if (wait) {
            waitSeconds(30L);
        }
    }


    private static void openSignupPage() {
        String url = "https://app.qubitsuite.com/signup";
        String osName = System.getProperty("os.name");


        if (osName.equals("Linux")) {
            System.out.println("This is a Linux system. Setting Chromedriver location");
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");

        }

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        webDriver = new ChromeDriver(options);
        webDriver.manage().window().maximize();
        webDriver.manage().window().fullscreen();

        webDriver.get(url);


        if (!headless) {
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            waitSeconds(3L);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }

    }

    private static void checkRequiredElement(WebDriver driver, String label, String errorMessage) {
        WebElement errMsgElement = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//*[contains(text(),'"+label+"')]/..//p"),30L);
        String text = errMsgElement.getText();
        if (errorMessage==null || errorMessage.isEmpty()){
            errorMessage="Requerido";
        }
        Assert.assertEquals(text,errorMessage,label+" not found");
    }


    private static void waitSeconds(long seconds){
        try {
            Thread.sleep(1000*seconds);
        } catch (InterruptedException e) {
        }
    }


    @AfterMethod
    public void takeScreenShotOnFailure(ITestResult testResult)  {
        if (testResult.getStatus() != ITestResult.SUCCESS) {
            try {
                File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                String methodName=testResult.getMethod().getConstructorOrMethod().getMethod().getName();
                String fileName="target/surefire-reports/failure_"+ outputFileTimestamp +"_"+methodName;
                String screenshotFileName=fileName+".png";
                FileHandler.copy(scrFile, new File(screenshotFileName));

                String htmlContent=webDriver.getPageSource();
                if (htmlContent!=null) {
                    String htmlFileName = fileName + ".html";
                    Path file = Paths.get(htmlFileName);
                    Files.writeString(file, htmlContent, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                System.out.println("Exception while taking screenshot " + e.getMessage());
            }
            webDriver.quit();
        }
    }


    @BeforeClass
    private void setUp() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        outputFileTimestamp = dateFormat.format(date);
    }





}
