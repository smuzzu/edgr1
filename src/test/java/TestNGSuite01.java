import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;


public class TestNGSuite01 {

    private static final boolean headless=false;
    private static final boolean wait=false;


    @Test
    public void Test001_Qubitsuite_signup_no_data() {
        WebDriver driver = openSignupPage();

        WebElement buttonElement = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//button[@type='submit']"),30L);
        buttonElement.click();

        checkRequiredElement(driver,"Nombre",null);
        checkRequiredElement(driver,"Apellido",null);
        checkRequiredElement(driver,"Email",null);
        checkRequiredElement(driver,"Teléfono","Por favor ingresa un teléfono válido");
        checkRequiredElement(driver,"Contraseña",null);
        checkRequiredElement(driver,"Repetir Contraseña",null);

        if (wait) {
            waitSeconds(30L);
        }
        driver.close();
    }

    @Test
    public void Test002_Qubitsuite_signup_default_country_code() {
        WebDriver driver = openSignupPage();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String browserLanguage = (String) js.executeScript("return navigator.language || navigator.userLanguage;");

        Assert.assertNotNull(browserLanguage,"no pudo detectar el lenguaje");

        Locale locale = Locale.forLanguageTag(browserLanguage);
        String country = locale.getCountry();
        String displayCountry = locale.getDisplayCountry();

        String countryXpath="//div[contains(text(),'Teléfono')]/..//div[contains(text(), '"+displayCountry+"')]";

        try {
            driver.findElement(By.xpath(countryXpath));
        }catch (Exception e){
            Assert.fail("no pudo localizar el pais "+displayCountry);
        }

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String phonePrefix = "+"+phoneUtil.getCountryCodeForRegion(country);

        String phonePrefixXpath="//div[contains(text(),'Teléfono')]/..//div[contains(text(), '"+phonePrefix+"')]";

        try {
            driver.findElement(By.xpath(phonePrefixXpath));
        }catch (Exception e){
            Assert.fail("no pudo localizar el prefijo telefonico "+phonePrefix);
        }


    }

    @Test
    public void Test003_Qubitsuite_signup_wrong_password() {
        WebDriver driver = openSignupPage();

        WebElement firstName = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='firstName']"),30L);
        firstName.sendKeys("Diego");
        WebElement lastName = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='lastName']"),30L);
        lastName.sendKeys("Maradona");
        WebElement email = FluentWaitUtil.getWebElementFluentWait(driver,By.xpath("//input[@name='email']"),30L);
        email.sendKeys("diego@gmail.com");

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

}
