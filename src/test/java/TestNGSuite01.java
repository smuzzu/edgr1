import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;


public class TestNGSuite01 {

    private static final boolean headless=true;
    private static final boolean wait=false;


    @Test
    public void Test001_Qubitsuite_signup_no_data() {
        WebDriver driver = openSignupPage();

        WebElement buttonElement = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//button[@type='submit']"),30L);
        buttonElement.click();

        checkRequiredElement(driver,"Nombre");
        checkRequiredElement(driver,"Apellido");
        checkRequiredElement(driver,"Email");
        checkRequiredElement(driver,"Teléfono");
        checkRequiredElement(driver,"Contraseña");
        checkRequiredElement(driver,"Repetir Contraseña");

        if (wait) {
            waitSeconds(30L);
        }
        driver.close();
    }

    @Test
    public void Test002_Qubitsuite_signup_wrong_email() {
        WebDriver driver = openSignupPage();

        WebElement firstName = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='firstName']"),30L);
        firstName.sendKeys("Diego");
        WebElement lastName = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='lastName']"),30L);
        lastName.sendKeys("Maradona");
        WebElement email = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='email']"),30L);
        email.sendKeys("diego@gmail.com");
        //WebElement countryCode = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//button/*[@data-testid='ArrowDropDownIcon']/.."),30L);
        WebElement countryCode = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//div[contains(text(),'Teléfono')]/..//input"),30L);
        countryCode.sendKeys("Arg");

        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        actions.sendKeys(Keys.RETURN).perform();
        WebElement phone = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='phone']"),30L);
        phone.sendKeys("1144445555");
        WebElement password = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='password']"),30L);
        password.sendKeys("1234567");
        WebElement password2 = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='password2']"),30L);
        password2.sendKeys("wrongPass");

        //muestra los passwords
        List<WebElement> eyeIcons = driver.findElements(By.xpath("//button/*[@class='remixicon-icon ']/.."));
        for (WebElement eyeIcon : eyeIcons) {
            eyeIcon.click();
        }

        WebElement buttonElement = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//button[@type='submit']"),30L);
        buttonElement.click();


        WebElement passwordError1 = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='password']/../..//p"),30L);
        String text = passwordError1.getText();
        Assert.assertEquals(text,"La contraseña debe tener al menos 8 caracteres"," password1 error no coincide");

        WebElement passwordError2 = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='password']/../..//p"),30L);
        text = passwordError2.getText();
        Assert.assertEquals(text,"La contraseña debe tener al menos 8 caracteres"," password2 error no coincide");




        if (wait) {
            waitSeconds(30L);
        }
        driver.close();
    }


    private static WebDriver openSignupPage() {
        String url = "https://app.qubitsuite.com/signup";
        String osName = System.getProperty("os.name");


        if (osName.equals("Linux")) {
            System.out.println("This is a Linux system. Setting geckodriver for Firefox");
            System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");

        }

        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        WebDriver driver = new FirefoxDriver(options);

        driver.get(url);


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

        return driver;
    }

    private static void checkRequiredElement(WebDriver driver, String label) {
        WebElement errMsgElement = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//*[contains(text(),'"+label+"')]/..//p"),30L);
        String text = errMsgElement.getText();
        Assert.assertEquals(text,"Requerido",label+" not found");
    }


    private static void waitSeconds(long seconds){
        try {
            Thread.sleep(1000*seconds);
        } catch (InterruptedException e) {
        }
    }

}
