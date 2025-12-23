package programs;

import java.awt.RenderingHints.Key;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class IRCTCFromTO {
	String name;
	String name1;
	String monthtext1;
	
	@Test
	public void searchingTrains() throws InterruptedException {
	//	WebDriverManager.chromedriver().setup();
		// for disabling the browser popups
		ChromeOptions options = new ChromeOptions();
		//EdgeOptions options=new EdgeOptions();
		options.addArguments("--disable-notifications");

		// OR using preferences
		options.addArguments("--disable-popup-blocking");
		options.setExperimentalOption("prefs", new java.util.HashMap<String, Object>() {
			{
				put("profile.default_content_setting_values.notifications", 2);
			}
		});
	WebDriver driver = new ChromeDriver(options);
	//	WebDriver driver=new EdgeDriver(options);
		driver.get("https://www.irctc.co.in/nget/train-search");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.findElement(By.xpath("//*[text()='OK']")).click();
		driver.findElement(By.cssSelector("[aria-label*='Enter From station']")).sendKeys("Machilipatnam");
		List<WebElement> fromcities = driver.findElements(By.cssSelector(
				"[class*='ui-autocomplete-panel ui-widget ui-widget-content ui-corner-all ui-shadow ng-star-inserted'] [class='ng-star-inserted']"));
		for (int i = 0; i < fromcities.size(); i++) {

			System.out.println(fromcities.get(i).getText());
			name = fromcities.get(i).getText();
			if (fromcities.get(i).getText().equals(name)) {
				fromcities.get(i).click();
				break;

			}
		}

		driver.findElement(By.cssSelector("[aria-label*='Enter To station']")).sendKeys("Secunderabad");
		Thread.sleep(2000);
		List<WebElement> tocities = driver.findElements(By.cssSelector(
				"[class*='ui-autocomplete-panel ui-widget ui-widget-content ui-corner-all ui-shadow ng-star-inserted'] [class='ng-star-inserted']"));

		for (int i = 0; i < tocities.size(); i++) {

			System.out.println(tocities.get(i).getText());
			name1 = tocities.get(i).getText();
			System.out.println(name1);
			if (tocities.get(i).getText().equals(name1)) {
				tocities.get(i).click();
				break;

			}
		}
		driver.findElement(By.cssSelector("[class*='ng-tns-c76-11 pi pi-chevron-down']")).click();
		List<WebElement> category=driver.findElements(By.cssSelector("[class*='ui-dropdown-item ui-corner-all']"));
		for(int i=0;i<category.size();i++) {
			if(category.get(i).getText().equalsIgnoreCase("general")) {
				category.get(i).click();
			break;
			}
		}
		Thread.sleep(3000);
		driver.findElement(By.cssSelector("[class*='ui-inputtext ui-widget ui-state-default ui-corner-all ng-star-inserted']")).click();
		while(true) {
			WebElement monthtext=driver.findElement(By.cssSelector("[class*='ui-datepicker-month']"));
			monthtext1=monthtext.getText();
			if(monthtext1.contains("January")) {
				break;
			}
			driver.findElement(By.cssSelector("[class*='ui-datepicker-next-icon']")).click();
				
		}
		Thread.sleep(2000);
		List<WebElement> days=driver.findElements(By.cssSelector("[class*='ui-state-default ng-tns-c69-9 ng-star-inserted']"));
		for(WebElement day: days) {
			if(day.getText().equals("11")) {
				day.click();
				break;
			}
		}
		driver.findElement(By.xpath("//*[contains  (text(), 'Search')]")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//strong[contains(text(), 'Sleeper')]/parent::div/following-sibling::div//span[@class='fa fa-repeat']")).click();
		driver.quit();
		

	}

}
