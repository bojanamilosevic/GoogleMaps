package seven.bridges;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestGoogleMaps {

	WebDriver driver;
	WebElement element;
		
	public void findElementAndInput(String xpath,String input) {
		
		driver.findElement(By.xpath(xpath)).sendKeys(input);
			
		}
			

    public void clickElement(String id_element) {
				
		driver.findElement(By.id(id_element)).click();
		}
			
	public void clickElementXPath(String xpath) {
				
		driver.findElement(By.xpath(xpath)).click();
				
			}
			
	public void waitForElement(int i,String xpath) {
				
		WebDriverWait wait = new WebDriverWait(driver,i);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
						
		}
			
	public void addStartingPoint(String place_name) {
				
		String starting_point_path = "//*[@id=\"sb_ifc51\"]/input";
		findElementAndInput(starting_point_path,place_name);
				
		}
			
	public void addDestination(String place_name) {
				
		 String destination_path = "//*[@id=\"sb_ifc52\"]/input";
		 findElementAndInput(destination_path,place_name);
				
	}
		    
	public void chooseTravelMode(String mode_path) {
		    	
		clickElementXPath(mode_path);
		    	
	 }
		    
	public void chooseRouteOptions(String route_options_path) {
		    	
		clickElementXPath("//*[@id=\"pane\"]/div/div[1]/div/div/div[2]/button[2]");  //options
		clickElementXPath(route_options_path);
		    }
	
	public void inputRouteOfChoice() {
		
		waitForElement(10,"//*[@id=\"searchbox-directions\"]");
		clickElement("searchbox-directions");
		addStartingPoint("Budapest");
		addDestination("Belgrade");
		clickElementXPath("//*[@id=\"directions-searchbox-1\"]"); // enter
		chooseTravelMode("//*[@id=\"omnibox-directions\"]/div/div[2]/div/div/div[1]/div[2]/button"); // driving mode
		chooseRouteOptions("//*[@id=\"pane\"]/div/div[1]/div/div/div[2]/div[2]/div/div[1]/div[1]/label[1]");
		waitForElement(10, "//*[@id=\"pane\"]/div/div[1]/div/div/div[5]"); // waiting for routes list
		clickElementXPath("//*[@id=\"pane\"]/div/div[1]/div/div/div[2]/button[2]"); // close options
	}
	
	public void countRoutes() {
		
		int i = 0;
		int max_i = 0;
		List<WebElement> elements;
		while (true) {
			String distance_path = "//*[@id=\"section-directions-trip-" + String.valueOf(i)
					+ "\"]";
			elements = driver.findElements(By.xpath(distance_path));

			if (elements.isEmpty()) {
				break;
			}
			else 
				max_i++;

		}
		System.out.println(max_i);
	}
	
	public void chooseLongestRoute(int routes_size) {

		int max_i = 0;
		int distance;
		int max_distance = 0;
		
		for (int i=0;i<routes_size;i++)
		{
			String distance_path = "//*[@id=\"section-directions-trip-" + String.valueOf(i)
			+ "\"]/div[2]/div[1]/div[1]/div[2]/div";
			String distance_str = driver.findElement(By.xpath(distance_path)).getText();
			distance = Integer.parseInt(distance_str.replace(" km", ""));
			if (distance > max_distance) {
				max_distance = distance;
				max_i = i;
			}
		}
		
		System.out.println(max_distance);
		System.out.println(max_i);

		Boolean element_selected = driver
				.findElement(By.xpath("//*[@id=\"section-directions-trip-" + String.valueOf(max_i) + "\"]"))
				.isSelected();

		if (element_selected)

			clickElementXPath("//*[@id=\"section-directions-trip-" + String.valueOf(max_i) + "\"]/div[2]/div[1]/div[4]/button");

		else {
			clickElementXPath("//*[@id=\"section-directions-trip-" + String.valueOf(max_i) + "\"]");
			clickElementXPath("//*[@id=\"section-directions-trip-" + String.valueOf(max_i) + "\"]/div[2]/div[1]/div[4]/button");
		}

	}
			
	@BeforeMethod
	public void launchBrowser() {
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\bmx\\Documents\\Instalacije\\BrowserDrivers\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver(); 
		driver.manage().deleteAllCookies(); 
		driver.manage().window().maximize(); 
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.get("https://www.google.com/maps");		
	}

	@Test(priority=1)
	public void checkGoogleMaps() {
	
		String expected_url = "https://www.google.com/maps";
		String actual_url = driver.getCurrentUrl();
		Assert.assertEquals(actual_url, expected_url);	
		
		Boolean maps = driver.findElement(By.xpath("//*[@id=\"searchbox\"]")).isDisplayed();
		Assert.assertTrue(maps);
		

		}
	
	@Test(priority=2)
	public void testDirection() {
		
		boolean validation = true;	
		boolean val = true;
		int i = 0;
		int routes_list_size = 0;
		List<WebElement> elements;
		
		inputRouteOfChoice();
		Boolean routes = driver.findElement(By.xpath("//*[@id=\"pane\"]/div/div[1]/div/div/div[5]")).isDisplayed();
		Assert.assertTrue(routes);
		
		Boolean driving_mode = driver.findElement(By.xpath("//*[@id=\"section-directions-trip-0\"]/div[1]")).isDisplayed();
		Assert.assertTrue(driving_mode);
		
	
		while (true) {
			
			String path = "//*[@id=\"section-directions-trip-" + String.valueOf(i)
					+ "\"]";
			elements = driver.findElements(By.xpath(path));
			
            if (elements.isEmpty()) 
				break;
            routes_list_size = routes_list_size+1;
			i++;

		}
		System.out.println(routes_list_size);
		
		for (int j=0;j<routes_list_size;j++) {
			String route_number = driver.findElement(By.xpath("//*[@id=\"section-directions-trip-"+String.valueOf(j)+"\"]/div[2]/div[1]/div[2]/h1[1]/span")).getText();
			if (route_number.contains("M5")|route_number.contains("M1")|route_number.contains("A1")) {
				val = false;
				validation=val;
			}
			
		}
		
		Assert.assertTrue(validation);
		
		chooseLongestRoute(routes_list_size);
		
	}
	
	
	@Test(priority=3)
	public void testDetailsPage() {
		
		inputRouteOfChoice();	
		chooseLongestRoute(3);
		waitForElement(10,"//*[@id=\"pane\"]/div/div[1]/div/div/div[3]"); //check if Details page is opened
				
		Boolean ete = driver.findElement(By.xpath("//*[@id=\"pane\"]/div/div[1]/div/div/div[3]/div[1]/h1/span[1]/span[1]")).isDisplayed();
		
		Boolean distance = driver.findElement(By.xpath("//*[@id=\"pane\"]/div/div[1]/div/div/div[3]/div[1]/h1/span[1]/span[2]")).isDisplayed();
		
		Assert.assertTrue(ete);
		Assert.assertTrue(distance);

		}
	
	@AfterMethod
	public void closeDriver() {
		driver.quit();
	}
	
}
