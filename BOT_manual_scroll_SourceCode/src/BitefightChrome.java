import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BitefightChrome {

	/** USER INPUTS are set in #config.txt
	 * @username -- account login name
	 * @password -- account login password
	 * @serverURL -- Bitefight server URL
	 * @stopGold -- bot will pause if Hero collects gold above @stopGold threshold (0 = ignore it)
	 * @stopHP -- bot will pause if Hero's HP falls below @stopHP threshold
	 * @purchaseAttributes -- enable or disable purchasing attributes
	 * @responseTime -- imitate human reaction time of clicking buttons in milliseconds (min, max)
	 * @mode -- current bot mode (run, pause, stop)
	 */
	
	public static String username = "";
	public static String password = "";
	public static String serverURL = "";
	public static int stopGold = 0;
	public static int stopHP = 0;
	public static Boolean purchaseAttributes = false;
	public static int[] responseTime = {180, 330};
	public static String mode = "run";
	
	/** PROGRAM GLOBALS
	 * @storyActions -- array of numerized story actions
	 * @purchaseAttributeLimits -- array of attribute purchasing upper limits (STR, DEF, DEX, END, CHA)
	 * @status -- array of Hero's status info (gold, AP, HP)
	 * @attributes -- array of Hero's basic attribute values (STR, DEF, DEX, END, CHA)
	 * @isInStory -- can bot use "Hunt" shortcut to enter story mode?
	 */
	
	public static int[] storyActions = new int[50];
	public static int[] purchaseAttributeLimits = new int[5];
	public static int[] status = new int[3];
	public static int[] attributes = new int[5];
	public static Boolean isInStory = false;
	
	
	/** MAIN FUNCTION, where magic happens */
	public static void main(String[] args) throws InterruptedException {
		// read configuration file and set user configuration
		readConfig();
		// run Google Chrome WebDriver
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		// login into game
		login(driver);
		// wait for page to fully load
		pause(4000,6000);
		
		// run bot until bot mode is set to "stop"
		while(mode != "stop") {
			// read configuration file and set user configuration
			readConfig();
			
			// do not proceed until bot mode is set to "run"
			while (mode == "pause") {
				// read configuration file and set user configuration
				readConfig();
				// pause
				pause(3000,5000);
				// disable "Hunt" shortcut
				isInStory = false;
			}
			
			// get Hero's status and attributes
			getProfileInfo(true, true, true, driver);
			
			// if purchasing attributes is allowed, check if player has enough gold to purchase attributes
			// and do so before entering story
			if (purchaseAttributes == true) {
				for (int i = 0; i < 5; i++) {
					// read configuration file and set user configuration
					readConfig();
					// check if purchasing attributes is still allowed
					while (purchaseAttributes == true && (status[0] > (Math.pow(attributes[i] - 4, 2.4) + 1)) && (attributes[i] < purchaseAttributeLimits[i])) {
						// purchase attribute
						purchaseAttribute((i+1), driver);
					}
				}
			}
			
			/** continue only if:
			/*    there is enough AP and 
			/*    gold amount is below @stopGold threshold and
			/*    Hero's HP is above @stopHP threshold and
			/*    bot mode is set to "run"
			 */
			if(status[1] >= 3 && (stopGold == 0 || status[0] < stopGold) && status[2] >= stopHP && mode == "run") {
				// start story
				story(driver);
			}
			
			// otherwise pause bot
			else {
				// pause bot for 3-5 seconds
				if (mode == "pause") {
					// pause
					pause(3000, 5000);
					// read configuration file and set user configuration
					readConfig();
					// disable "Hunt" shortcut
					isInStory = false;
				// or pause bot and check for Hero's conditions (at least 15 AP, gold below threshold, HP above threshold)
				} else if (status[1] <= 15 || (stopGold != 0 && status[0] > stopGold) || status[2] <= stopHP) {
					// pause for 1-4 minutes
					pause(60000, 240000);
					// read configuration file and set user configuration
					readConfig();
				}
			}
		}
		
	}

	/** READ USER CONFIGURATION IN #config.txt */
	private static void readConfig() {
		BufferedReader reader = null;
		int lineCount = 0;
		String[] temp;
		
		// parse user configuration file
		try {
			reader = new BufferedReader(new FileReader("config.txt"));
			String line;
		
			while ((line = reader.readLine()) != null) {
				lineCount++;
				if (lineCount == 2) { username = line; }
				if (lineCount == 4) { password = line; }
				if (lineCount == 6) { serverURL = line; }
				if (lineCount == 8) { stopGold = Integer.parseInt(line); }
				if (lineCount == 10) { stopHP = Integer.parseInt(line); }
				if (lineCount == 12) {
					if (line.equalsIgnoreCase("true")) {
						purchaseAttributes = true;
					} else if (line.equalsIgnoreCase("false")) {
						purchaseAttributes = false;
					}
				}
				if (lineCount == 14) {
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++) {
						try {
							purchaseAttributeLimits[i] = Integer.parseInt(temp[i]);
						} catch (NumberFormatException e){
							purchaseAttributeLimits[i] = 0;
						}
						
					}
				}
				if (lineCount == 16) {
					temp = line.split(",");
					for (int i = 0; i < temp.length; i++) {
						try {
							storyActions[i] = Integer.parseInt(temp[i]);
						} catch (NumberFormatException e) {
							storyActions[i] = 0;
						}
					}
				}
				if (lineCount == 18) {
					temp = line.split(",");
					for (int i = 0; i < 2; i++) {
						try {
							responseTime[i] = Integer.parseInt(temp[i]);
						} catch (NumberFormatException e){
							responseTime[i] = 180 * (i+1);
						}
					}
					if (responseTime[1] < responseTime[0]) {
						int tempo = responseTime[0];
						responseTime[0] = responseTime[1];
						responseTime[1] = tempo;
					}
				}
				if (lineCount == 20) {
					if (line.equalsIgnoreCase("pause")) {
						mode = "pause";
					} else if (line.equalsIgnoreCase("run")) {
						mode = "run";
					} else if (line.equalsIgnoreCase("stop")) {
						mode = "stop";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}

	/** LOGIN INTO GAME */
	private static void login(WebDriver driver) {
		driver.get(serverURL + "/user/login");
		pause(600,1000);
		if (driver.findElements(By.cssSelector(".cc-compliance")).size() > 0) {
			driver.findElement(By.cssSelector(".cc-compliance")).click();
		}
		pause(500,800);
		driver.findElement(By.xpath("/html/body/div[5]/div[2]/div/div[2]/div/div/table/tbody/tr/td[2]/form/table/tbody/tr[1]/td[2]/input")).sendKeys(username);
		pause(300,500);
		driver.findElement(By.xpath("/html/body/div[5]/div[2]/div/div[2]/div/div/table/tbody/tr/td[2]/form/table/tbody/tr[2]/td[2]/input")).sendKeys(password);
		pause(300,500);
		driver.findElement(By.xpath("/html/body/div[5]/div[2]/div/div[2]/div/div/table/tbody/tr/td[2]/form/table/tbody/tr[5]/td[2]/input")).click();
	}

	/** RETRIEVE HERO INFO
	 * @param overview -- click "Overview" button if reloading page is required
	 * @param statusBool -- get Hero's status (gold, AP, HP) if required
	 * @param attributesBool -- get Hero's basic attribute values (STR, DEF, DEX, END, CHA) if required
	 * @param driver -- chromedriver.exe
	 */
	private static void getProfileInfo(Boolean overview, Boolean statusBool, Boolean attributesBool, WebDriver driver) {
		//  click "Overview" button if reloading page is required
		if (overview == true) {
			driver.findElement(By.cssSelector("li.active > a:nth-child(1)")).click();
		}
		
		// parse Hero's status info (gold, AP, HP) if required
		if (statusBool == true) {
			String text = driver.findElement(By.cssSelector(".gold")).getText();
			String[] items = text.replaceAll("	 ", ",").replaceAll("    ", ",").replaceAll(" / ", ",").split(",");
			for (int i = 0; i < items.length; i++) {
				items[i] = items[i].replaceAll("\\.", "");
			}
			status[0] = Integer.parseInt(items[0]); // gold
			status[1] = Integer.parseInt(items[3]); // AP
			status[2] = Integer.parseInt(items[5]); // HP
		}
			
		// parse hero's basic attribute values from page source if required
		if (attributesBool == true) {
			String text = driver.getPageSource();
			String[] items = text.split("[\r\n]+");

			// check for basic attribute values
			int a = 0; // counter of fetched attribute basic values (max = 5)
			for (int i = 0; i < items.length && a < 5; i++) {
				if (items[i].contains("<hr /></td></tr><tr><td>")) {
					// get string position (index) of attribute basic value
					int index = items[i].indexOf("</td><td align=\"right\">")+22;
					// convert substring into integer
					attributes[a++] = Integer.parseInt(items[i].substring(index, index+5).replaceAll("[^0-9]", ""));
				}
			}
		}
		
	}
	
	/** PAUSE FOR A RANDOM TIME INTERVAL
	 * @param min -- minimum waiting time in milliseconds
	 * @param max -- maximum waiting time in milliseconds
	 */
	private static void pause(int min, int max) {
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		try {
			Thread.sleep(randomNum);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** PLAY STORY MODE */
	private static void story(WebDriver driver) {
		// javascript executor for manual scrolling
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		
		// if bot has already entered story mode before, proceed to story mode via "Hunt" shortcut 
		if (isInStory == true) {
			driver.findElement(By.xpath("/html/body/div[4]/div[1]/ul/li[6]/a")).click();
		} 
		// or else proceed to story mode in a long standard way
		else {
			// click "City"
			driver.findElement(By.xpath("/html/body/div[4]/div[1]/ul/li[5]/a")).click();
			// pause
			pause(responseTime[0], responseTime[1]);
			// scroll down and pause a little bit
			jse.executeScript("scroll(0, 350);");
			pause(responseTime[0]/2, responseTime[1]/2);
			// click "Tavern"
			driver.findElement(By.cssSelector("#addBuddy > div.wrap-left.clearfix > div > div.table-wrap > table > tbody > tr:nth-child(3) > td:nth-child(2) > a")).click();
			// pause
			pause(responseTime[0], responseTime[1]);
			// click "Start story" in tavern
			driver.findElement(By.cssSelector("#newQuest > div:nth-child(2) > div > div.buttonOverlay")).click();
			isInStory = true;
		}
		
		/** RUN IN LOOP WHILE HERO HAS:
		 *   enough AP and
		 *   gold amount below @stopGold threshold and
		 *   HP above @stopHP threshold and
		 *   bot mode is set to "run"
		 */
		while (status[1] >= 3 && (stopGold == 0 || status[0] < stopGold) && status[2] >= stopHP && mode == "run") {
			// read configuration file and set user configuration
			readConfig();
			// get Hero status
			getProfileInfo(false, true, false, driver);
			
			// read configuration file and set user configuration;
			// if bot mode is set to "pause", break out of loop and pause
			readConfig();
			if (mode == "pause") {
				isInStory = false;
				break;
			}
			
			// check for exceptional action "Start story"
			String text = driver.getPageSource();
			String[] items = text.split("[\r\n]+");
			for (int i = 900; i < items.length; i++) {
				if (items[i].contains("/city/adventure/startquest")) {
					// pause
					pause(responseTime[0], responseTime[1]);
					// click
					driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div.wrap-content.wrap-right.clearfix > div > div > div > a")).click();
				}
			}
			
			// read configuration file and set user configuration
			readConfig();
			if (mode == "pause") {
				isInStory = false;
				break;
			}
			
			// parse available numerized actions from page source
			text = driver.getPageSource();
			items = text.split("[\r\n]+");
			// check if story actions are available
			Boolean click = false;
			// initialize array to store indexes of available story actions (max = 4)
			int[] actions = {0, 0, 0, 0};
			
			int b = 0; // counter of fetched numerized actions (max = 4)
			for (int i = 900; i < items.length && b < 4; i++) {
				if (items[i].contains("/city/adventure/decision/")) {
					int index = items[i].indexOf("/city/adventure/decision/")+24;
					items[i] = items[i].substring(index, index+4).replaceAll("[^0-9]", "");
					actions[b++] = Integer.parseInt(items[i]);
					click = true;
				}
			}
			
			if (click == true) {
				// choose action
				loopAction:
				for (int i = 0; i < storyActions.length; i++) {
					for (int j = 0; j < b; j++) {
						if (storyActions[i] == actions[j]) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// scroll down and pause a little bit
							jse.executeScript("scroll(0, 250);");
							pause(responseTime[0]/2, responseTime[1]/2);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							break loopAction;
						}
					}
				}
			}
			
			// read configuration file and set user configuration
			readConfig();
			if (mode == "pause") {
				isInStory = false;
				break;
			}
			
			// check for exceptional pop-up "Mission accomplishment"
			text = driver.getPageSource();
			items = text.split("[\r\n]+");
			for (int k = 800; k < items.length; k++) {
				if (items[k].contains("div id=\"infoPopup\" class=\"message_screen blackoutdialog\"")) {
					// pause
					pause(responseTime[0], responseTime[1]);
					// click "Cancel"
					try {
						driver.findElement(By.cssSelector("#infoPopup > div.wrap-left.clearfix > div > div > div:nth-child(3) > div.button.right > div.buttonOverlay")).click();
					} catch (NoSuchElementException e) {
						// or use URL
						driver.get(serverURL + "/city/adventure/");
					}
					break;
				}
			}
			
			// read configuration file and set user configuration
			readConfig();
			if (mode == "pause") {
				isInStory = false;
				break;
			}
			
			// check for exceptional action "Continue"
			text = driver.getPageSource();
			items = text.split("[\r\n]+");
			for (int k = 800; k < items.length; k++) {
				if (items[k].contains("/city/adventure/\"")) {
					// pause
					pause(responseTime[0], responseTime[1]);
					// scroll down and pause a little bit
					jse.executeScript("scroll(0, 250);");
					pause(responseTime[0]/2, responseTime[1]/2);
					// click "Continue"
					driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div.btn-left.center > div > a")).click();
					break;
				}
			}
						
			// read configuration file and set user configuration
			readConfig();
			if (mode == "pause") {
				isInStory = false;
				break;
			}
			
			// refresh Hero's status info
			Boolean attributePurchased = false;
			getProfileInfo(false, true, false, driver);
			// if purchasing attributes is allowed, check if user has enough gold to purchase attributes
			if (purchaseAttributes == true) {
				for (int i = 0; i < 5; i++) {
					// read configuration file and set user configuration
					readConfig();
					// check if purchasing attributes is still allowed
					while (purchaseAttributes == true && (status[0] > (Math.pow(attributes[i] - 4, 2.4) + 1)) && (attributes[i] < purchaseAttributeLimits[i])) {
						// purchase attribute
						purchaseAttribute((i+1), driver);
						attributePurchased = true;
					}
				}
				// enter story mode via "Hunt" shortcut
				if (attributePurchased == true) {
					driver.findElement(By.xpath("/html/body/div[4]/div[1]/ul/li[6]/a")).click();
				}
			}
			
		}
		// reset variable when "while" loop conditions are not fulfilled
		isInStory = false;
	}

	private static void purchaseAttribute(int index, WebDriver driver) {
		// click "Overview"
		driver.findElement(By.xpath("/html/body/div[4]/div[1]/ul/li[2]/a")).click();
		// pause
		pause(responseTime[0], responseTime[1]);
		// click "Attributes" tab
		driver.findElement(By.cssSelector("li.ui-state-default:nth-child(2) > a:nth-child(1)")).click();
		// pause
		pause(responseTime[0], responseTime[1]);
		// click attribute plus icon
		driver.findElement(By.cssSelector("#skills_tab > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(" + index + ") > td:nth-child(3) > div:nth-child(1)")).click();
		// refresh gold info
		getProfileInfo(false, true, true, driver);
	}
	
}