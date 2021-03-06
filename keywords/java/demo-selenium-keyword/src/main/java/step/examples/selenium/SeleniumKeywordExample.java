package step.examples.selenium;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class SeleniumKeywordExample extends AbstractKeyword {

	@Keyword(name = "Open Chrome")
	public void openChrome() throws Exception {
		
		ChromeOptions options = new ChromeOptions();
		boolean headless = input.getBoolean("headless", false);
		if (headless) {
			options.addArguments(Arrays.asList("headless", "disable-infobars", "disable-gpu", "no-sandbox"));
		}
		
		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		session.put(new DriverWrapper(driver));
	}

	@Keyword(name = "Search in google")
	public void searchInGoogle() throws Exception {
		if (input.containsKey("search")) {
			if (session.get(DriverWrapper.class) == null) {
				throw new Exception(
						"Please first execute keyword \"Open Chome\" in order to have a driver available for this keyword");
			}
			WebDriver driver = session.get(DriverWrapper.class).driver;

			driver.get("http://www.google.com");

			WebElement searchInput = driver.findElement(By.id("lst-ib"));

			String searchString = input.getString("search");
			searchInput.sendKeys(searchString + Keys.ENTER);

			WebElement resultCountDiv = driver.findElement(By.xpath("//div/nobr"));

			List<WebElement> resultHeaders = driver.findElements(By.xpath("//h3[@class='r']"));
			for (WebElement result : resultHeaders) {
				output.add(result.getText(), result.findElement(By.xpath("..//cite")).getText());
			}
		} else {
			throw new Exception("Input parameter 'search' not defined");
		}
	}
	
	// Wrapping the WebDriver instance as it is not implementing the Closeable interface
	public class DriverWrapper implements Closeable {

		final WebDriver driver;

		public DriverWrapper(WebDriver driver) {
			super();
			this.driver = driver;
		}

		@Override
		public void close() throws IOException {
			driver.quit();
		}
	}
}