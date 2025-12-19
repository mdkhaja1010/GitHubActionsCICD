package programs;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Login {
	
    @Test
	public void searchingTrains() throws InterruptedException {
			WebDriverManager.chromedriver().setup();
			// for disabling the browser popups
			ChromeOptions options = new ChromeOptions();
			//EdgeOptions options=new EdgeOptions();
			

		        // Disable password manager completely
			Map<String, Object> prefs = new HashMap<String, Object>();
			 prefs.put("credentials_enable_service", false);
		        prefs.put("profile.password_manager_enabled", false);
		        prefs.put("profile.password_manager_leak_detection", false);
		        prefs.put("password_manager_leak_detection", false);
		        options.setExperimentalOption("prefs", prefs);
		        options.addArguments("--disable-features=PasswordLeakDetection,PasswordCheck");
		        options.addArguments("--disable-save-password-bubble");
		        options.addArguments("--no-first-run", "--no-default-browser-check");
			 
		WebDriver driver = new ChromeDriver(options);
		//	WebDriver driver=new EdgeDriver(options);
			driver.get("https://www.saucedemo.com/");
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			driver.findElement(By.cssSelector("#user-name")).sendKeys("standard_user");
			driver.findElement(By.cssSelector("#password")).sendKeys("secret_sauce",Keys.ENTER);
			driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
			
	}

}
