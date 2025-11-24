import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Selenium FluentWait Utility class
 */

public class FluentWaitUtil {

    protected static WebElement getWebElementFluentWait(WebDriver webdriver, By by, long timeout) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(webdriver);
        fluentWait.withTimeout(Duration.ofSeconds(timeout));
        fluentWait.pollingEvery(Duration.ofSeconds(1));
        fluentWait.ignoring(NoSuchElementException.class);

        return  fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement we = driver.findElement(by);
                if (we.isEnabled() && we.isDisplayed()){
                    return we;
                }else {
                    return null;
                }
            }
        });
    }
}
