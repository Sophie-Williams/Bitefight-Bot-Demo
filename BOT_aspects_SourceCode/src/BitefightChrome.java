import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.openqa.selenium.By;
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
	 * @currentAspects -- aspects that player currently has (HUMAN, KNOWL, ORDER, NATUR, BEAST, DESTR, CHAOS, CORRU)
	 * @desiredAspects -- aspects that player wants (HUMAN, KNOWL, ORDER, NATUR, BEAST, DESTR, CHAOS, CORRU)
	 * @mode -- bot mode commands: run, pause, stop
	 */
	
	public static String username = "";
	public static String password = "";
	public static String serverURL = "";
	public static int stopGold = 0;
	public static int stopHP = 0;
	public static Boolean purchaseAttributes = false;
	public static int[] responseTime = {180, 330};
	public static int[] currentAspects = new int[8];
	public static int[] desiredAspects = new int[8];
	public static String mode = "run";
	
	/** PROGRAM GLOBALS
	 * @storyActions -- array of numerized story actions
	 * @purchaseAttributeLimits -- array of attribute purchasing upper limits (STR, DEF, DEX, END, CHA)
	 * @status -- array of Hero's status info (gold, AP, HP)
	 * @attributes -- array of Hero's basic attribute values (STR, DEF, DEX, END, CHA)
	 * @isInStory -- can bot use "Hunt" shortcut to enter story mode?
	 */
	
	public static int[] storyActions = {46,45,34,55,44,25,37,28,7,33,38,12,24,27,29,6,4,5,3,2,30,31,23,22,
										11,10,20,21,42,1,8,43,32,9,26,53,39,49,40,41,47,52,35,36};
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
			
			/** CONTINUE ONLY IF:
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

	/** READ USER-DEFINED FILE CONFIGURATION */
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
					for (int i = 0; i < 8; i++) {
						try {
							desiredAspects[i] = Integer.parseInt(temp[i]);
						} catch (NumberFormatException e) {
							desiredAspects[i] = 0;
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
		driver.findElement(By.cssSelector("#loginForm > table > tbody > tr:nth-child(1) > td:nth-child(2) > input")).sendKeys(username);
		pause(300,500);
		driver.findElement(By.cssSelector("#loginForm > table > tbody > tr:nth-child(2) > td:nth-child(2) > input")).sendKeys(password);
		pause(300,500);
		driver.findElement(By.cssSelector("#loginForm > table > tbody > tr:nth-child(5) > td:nth-child(2) > input")).click();
	}

	/** RETRIEVE HERO INFO
	 * @param overview -- click "Overview" button if reloading page is required
	 * @param statusBool -- get Hero's status (gold, AP, HP) if required
	 * @param attributesBool -- get Hero's basic attribute values (STR, DEF, DEX, END, CHA) if required
	 * @param driver -- chromedriver.exe
	 */
	private static void getProfileInfo(Boolean overview, Boolean statusBool, Boolean attributesBool, WebDriver driver) {
		//  click "Overview" button if page reload is required
		if (overview == true) {
			driver.findElement(By.cssSelector("li.active > a:nth-child(1)")).click();
			// retrieve aspect values
			int a = 0;
			String text = driver.getPageSource();
			String[] items = text.split("[\r\n]+");
			for (int i = 0; i < items.length && a < 8; i++) {
				if (items[i].contains("<div class=\"tooltip\" style=\"text-align:left;\">")) {
					currentAspects[a++] = Integer.parseInt(items[i+2].replace(" ", "").replace("<br/>", "").replace("<br />", ""));
				}
			}
		
		}
		
		// parse Hero's status (gold, AP, HP) if required
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
			
		// // parse attributes from page source if required
		if (attributesBool == true) {
			String text = driver.getPageSource();
			String[] items = text.split("[\r\n]+");

			// check for attributes' basic values
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
		while(status[1] >= 3 && (stopGold == 0 || status[0] < stopGold) && status[2] >= stopHP && mode == "run") {
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
			
			// array of differences between current and desired aspects
			int[] sortedAspects = new int[8];
			for (int i = 0; i < 8; i++) {
				sortedAspects[i] = currentAspects[i] - desiredAspects[i];
			}
			int[] sortIndex = {0,1,2,3,4,5,6,7};
			int temp1 = 0, temp2 = 0;
			Boolean sorted = false;
			
			// sort differences between current and desired aspects from highest to lowest
			while(!sorted) {
				sorted = true;
				// if a current element is lower than the element next to it,
				for (int i = 0; i < 7; i++) {
					if (sortedAspects[i] < sortedAspects[i+1]) {
						// swap elements
						temp1 = sortedAspects[i];
						sortedAspects[i] = sortedAspects[i+1];
						sortedAspects[i+1] = temp1;
						// swap their indexes
						temp2 = sortIndex[i];
						sortIndex[i] = sortIndex[i+1];
						sortIndex[i+1] = temp2;
						sorted = false;
					}
				}
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

			// parse indexes of available actions from page source
			text = driver.getPageSource();
			items = text.split("[\r\n]+");
			// check if story actions are available
			Boolean click = false;
			// array to store indexes of available story actions
			int[] actions = {0, 0, 0, 0};
			
			int b = 0; // counter of fetched action indexes (max = 4)
			for (int i = 900; i < items.length && b < 4; i++) {
				if (items[i].contains("/city/adventure/decision/")) {
					int index = items[i].indexOf("/city/adventure/decision/")+24;
					items[i] = items[i].substring(index, index+4).replaceAll("[^0-9]", "");
					actions[b++] = Integer.parseInt(items[i]);
					click = true;
				}
			}
			
			// determine if action was selected
			Boolean selected = false;
			
			// check for actions to lower unwanted aspects (from highest to lowest)
			if (click == true) {
				actionLoop:
				for (int i = 0; i < 8 && !selected; i++) {
					
					// look for beast actions which will lower human aspect
					if (sortIndex[i] == 0 && (currentAspects[0] - desiredAspects[0]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 25 && (currentAspects[1] - desiredAspects[1]) >= 15) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[0] >= 5) {
									currentAspects[0] -= 5;
									currentAspects[3] += 1;
									currentAspects[4] += 3;
									currentAspects[5] += 1;
								} 
								if (currentAspects[1] >= 5) {
									currentAspects[1] -= 5;
									currentAspects[4] += 1;
									currentAspects[5] += 3;
									currentAspects[6] += 1;
								}
								// story action was selected
								selected = true;
								break actionLoop;
							}
						}
							
						if (!selected) {
							for (int j = 0; j < b; j++) {
								 if (actions[j] == 29 && (currentAspects[7] - desiredAspects[7]) >= 15) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									} 
									if (currentAspects[7] >= 5) {
										currentAspects[7] -= 5;
										currentAspects[2] += 1;
										currentAspects[3] += 3;
										currentAspects[4] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								 }
							}
						}
							
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 5 || actions[j] == 11) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									}
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 7 || actions[j] == 12) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
					}
				
					// look for destruction actions which will lower knowledge aspect
					else if (sortIndex[i] == 1 && (currentAspects[1] - desiredAspects[1]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 25 && (currentAspects[0] - desiredAspects[0]) >= 15) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[1] >= 5) {
									currentAspects[1] -= 5;
									currentAspects[4] += 1;
									currentAspects[5] += 3;
									currentAspects[6] += 1;
								}
								if (currentAspects[0] >= 5) {
									currentAspects[0] -= 5;
									currentAspects[3] += 1;
									currentAspects[4] += 3;
									currentAspects[5] += 1;
								} 
								// story action was selected
								selected = true;
								break actionLoop;
							}
						}
						
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 3) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[1] >= 5) {
										currentAspects[1] -= 5;
										currentAspects[4] += 1;
										currentAspects[5] += 3;
										currentAspects[6] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
					}
					
					// look for chaos actions which will lower order aspect
					else if (sortIndex[i] == 2 && (currentAspects[2] - desiredAspects[2]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 26) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[2] >= 5) {
									currentAspects[2] -= 5;
									currentAspects[5] += 1;
									currentAspects[6] += 3;
									currentAspects[7] += 1;
								}
								if (currentAspects[2] >= 5) {
									currentAspects[2] -= 5;
									currentAspects[5] += 1;
									currentAspects[6] += 3;
									currentAspects[7] += 1;
								}
								// story action was selected
								selected = true;
								break actionLoop;
							}
						}
						
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 9) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[2] >= 5) {
										currentAspects[2] -= 5;
										currentAspects[5] += 1;
										currentAspects[6] += 3;
										currentAspects[7] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								} 
							}
						}
						
					}
					
					// look for corruption actions which will lower nature aspect
					else if (sortIndex[i] == 3  && (currentAspects[3] - desiredAspects[3]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 28 && (currentAspects[4] - desiredAspects[4]) >= 15) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[3] >= 5) {
									currentAspects[3] -= 5;
									currentAspects[6] += 1;
									currentAspects[7] += 3;
									currentAspects[0] += 1;
								}
								if (currentAspects[4] >= 5) {
									currentAspects[4] -= 5;
									currentAspects[7] += 1;
									currentAspects[0] += 3;
									currentAspects[1] += 1;
								}
								// story action was selected
								selected = true;
								break actionLoop;
							}
						}
						
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 31) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[3] >= 5) {
										currentAspects[3] -= 5;
										currentAspects[6] += 1;
										currentAspects[7] += 3;
										currentAspects[0] += 1;
									}
									if (currentAspects[3] >= 5) {
										currentAspects[3] -= 5;
										currentAspects[6] += 1;
										currentAspects[7] += 3;
										currentAspects[0] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 38 || actions[j] == 23 || actions[j] == 6) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[3] >= 5) {
										currentAspects[3] -= 5;
										currentAspects[6] += 1;
										currentAspects[7] += 3;
										currentAspects[0] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
					}
					
					// look for human actions which will lower beast aspect
					else if (sortIndex[i] == 4  && (currentAspects[4] - desiredAspects[4]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 28 && (currentAspects[3] - desiredAspects[3]) >= 15) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[4] >= 5) {
									currentAspects[4] -= 5;
									currentAspects[7] += 1;
									currentAspects[0] += 3;
									currentAspects[1] += 1;
								}
								if (currentAspects[3] >= 5) {
									currentAspects[3] -= 5;
									currentAspects[6] += 1;
									currentAspects[7] += 3;
									currentAspects[0] += 1;
								}
								// story action was selected
								selected = true;
								break actionLoop;
							}
						}
						
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 43 || actions[j] == 32) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[4] >= 5) {
										currentAspects[4] -= 5;
										currentAspects[7] += 1;
										currentAspects[0] += 3;
										currentAspects[1] += 1;
									}
									if (currentAspects[4] >= 5) {
										currentAspects[4] -= 5;
										currentAspects[7] += 1;
										currentAspects[0] += 3;
										currentAspects[1] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 4 || actions[j] == 10) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[4] >= 5) {
										currentAspects[4] -= 5;
										currentAspects[7] += 1;
										currentAspects[0] += 3;
										currentAspects[1] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
					}
					
					// look for knowledge actions which will lower destruction aspect
					else if (sortIndex[i] == 5  && (currentAspects[5] - desiredAspects[5]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 53 || actions[j] == 39) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[5] >= 5) {
									currentAspects[5] -= 5;
									currentAspects[0] += 1;
									currentAspects[1] += 3;
									currentAspects[2] += 1;
								}
								if (currentAspects[5] >= 5) {
									currentAspects[5] -= 5;
									currentAspects[0] += 1;
									currentAspects[1] += 3;
									currentAspects[2] += 1;
								}
								// story action was selected
								selected = true;
								break actionLoop;
							}
						}

						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 30 || actions[j] == 22 || actions[j] == 1 || actions[j] == 8) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[5] >= 5) {
										currentAspects[5] -= 5;
										currentAspects[0] += 1;
										currentAspects[1] += 3;
										currentAspects[2] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
						
					}
					
					// look for order actions which will lower chaos aspect
					else if (sortIndex[i] == 6  && (currentAspects[6] - desiredAspects[6]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 37 || actions[j] == 27 || actions[j] == 20) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[6] >= 5) {
									currentAspects[6] -= 5;
									currentAspects[1] += 1;
									currentAspects[2] += 3;
									currentAspects[3] += 1;
								}
								if (currentAspects[6] >= 5) {
									currentAspects[6] -= 5;
									currentAspects[1] += 1;
									currentAspects[2] += 3;
									currentAspects[3] += 1;
								}
								// story action was selected
								selected = true;
								break actionLoop;
							} 
						}
						
					}
					
					// look for nature actions which will lower corruption aspect
					else if (sortIndex[i] == 7  && (currentAspects[7] - desiredAspects[7]) >= 15) {
						
						for (int j = 0; j < b; j++) {
							if (actions[j] == 29 && (currentAspects[0] - desiredAspects[0]) >= 15) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								// recalculate aspect values
								if (currentAspects[0] >= 5) {
									currentAspects[0] -= 5;
									currentAspects[3] += 1;
									currentAspects[4] += 3;
									currentAspects[5] += 1;
								} 
								if (currentAspects[7] >= 5) {
									currentAspects[7] -= 5;
									currentAspects[2] += 1;
									currentAspects[3] += 3;
									currentAspects[4] += 1;
								}
								// story action was selected
								selected = true;
								break actionLoop;
							}
						}

						if (!selected) {
							for (int j = 0; j < b; j++) {
								if (actions[j] == 24) {
									// pause
									pause(responseTime[0], responseTime[1]);
									// click
									driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
									// recalculate aspect values
									if (currentAspects[7] >= 5) {
										currentAspects[7] -= 5;
										currentAspects[2] += 1;
										currentAspects[3] += 3;
										currentAspects[4] += 1;
									}
									// story action was selected
									selected = true;
									break actionLoop;
								}
							}
						}
					
					}
					
				}
				
				// if desired actions were not found, use neutral actions
				if(!selected) {
					for (int j = 0; j < b; j++) {
						if (actions[j] == 35 || actions[j] == 33 || actions[j] == 34 || actions[j] == 2) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							// story action was selected
							selected = true;
							break;
						}
					}
				}
				
				if(!selected) {
					for (int j = 0; j < b; j++) {
						if (actions[j] == 46 || actions[j] == 45) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							// story action was selected
							selected = true;
							break;
						}
					}
				}
				
				if(!selected) {
					for (int j = 0; j < b; j++) {
						if (actions[j] == 55) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							// story action was selected
							selected = true;
							break;
						}
					}
				}
				
				if(!selected) {
					for (int j = 0; j < b; j++) {
						if (actions[j] == 44) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							// story action was selected
							selected = true;
							break;
						}
					}
				}
				
				// if none of the above actions were found, use the least preferred actions
				if(!selected) {
					for (int j = 0; j < b; j++) {
						if (actions[j] == 21) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							// story action was selected
							selected = true;
							// recalculate aspects
							if (currentAspects[7] >= 5) {
								currentAspects[7] -= 5;
								currentAspects[2] += 1;
								currentAspects[3] += 3;
								currentAspects[4] += 1;
							}
							break;
						}
					}
				}
				
				if (!selected) {
					for (int j = 0; j < b; j++) {
						if (actions[j] == 40 || actions[j] == 41 || actions[j] == 42 || actions[j] == 47 || actions[j] == 49 || actions[j] == 52) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							// story action was selected
							selected = true;
							break;
						}
					}
				}
				
				if (!selected) {
					for (int j = 0; j < b; j++) {
						if (actions[j] == 36) {
							// pause
							pause(responseTime[0], responseTime[1]);
							// click
							driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
							// story action was selected
							selected = true;
							break;
						}
					}
				}
				
				// if no appropriate actions were selected, choose one from hard-coded actions order
				if (!selected) {
					loopAction:
					for (int i = 0; i < storyActions.length; i++) {
						for (int j = 0; j < b; j++) {
							if (storyActions[i] == actions[j]) {
								// pause
								pause(responseTime[0], responseTime[1]);
								// click
								driver.findElement(By.cssSelector("#content > div.wrap-left.clearfix > div > div > div:nth-child(" + (4+j) + ") > div > a")).click();
								
								// recalculate aspects
								if (storyActions[i] == 10 || storyActions[i] == 4) {
									if (currentAspects[4] >= 5) {
										currentAspects[4] -= 5;
										currentAspects[7] += 1;
										currentAspects[0] += 3;
										currentAspects[1] += 1;
									}
								} else if (storyActions[i] == 43 || storyActions[i] == 32) {
									if (currentAspects[4] >= 5) {
										currentAspects[4] -= 5;
										currentAspects[7] += 1;
										currentAspects[0] += 3;
										currentAspects[1] += 1;
									} 
									if (currentAspects[4] >= 5) {
										currentAspects[4] -= 5;
										currentAspects[7] += 1;
										currentAspects[0] += 3;
										currentAspects[1] += 1;
									}
								} else if (storyActions[i] == 28) {
									if (currentAspects[3] >= 5) {
										currentAspects[3] -= 5;
										currentAspects[6] += 1;
										currentAspects[7] += 3;
										currentAspects[0] += 1;
									}
									if (currentAspects[4] >= 5) {
										currentAspects[4] -= 5;
										currentAspects[7] += 1;
										currentAspects[0] += 3;
										currentAspects[1] += 1;
									}
								} else if (storyActions[i] == 1 || storyActions[i] == 22 || storyActions[i] == 30 || storyActions[i] == 8) {
									if (currentAspects[5] >= 5) {
										currentAspects[5] -= 5;
										currentAspects[0] += 1;
										currentAspects[1] += 3;
										currentAspects[2] += 1;
									}
								} else if (storyActions[i] == 39 || storyActions[i] == 53) {
									if (currentAspects[5] >= 5) {
										currentAspects[5] -= 5;
										currentAspects[0] += 1;
										currentAspects[1] += 3;
										currentAspects[2] += 1;
									} 
									if (currentAspects[5] >= 5) {
										currentAspects[5] -= 5;
										currentAspects[0] += 1;
										currentAspects[1] += 3;
										currentAspects[2] += 1;
									}
								} else if (storyActions[i] == 20 || storyActions[i] == 37 || storyActions[i] == 27) {
									if (currentAspects[6] >= 5) {
										currentAspects[6] -= 5;
										currentAspects[1] += 1;
										currentAspects[2] += 3;
										currentAspects[3] += 1;
									} 
									if (currentAspects[6] >= 5) {
										currentAspects[6] -= 5;
										currentAspects[1] += 1;
										currentAspects[2] += 3;
										currentAspects[3] += 1;
									}
								} else if (storyActions[i] == 21 || storyActions[i] == 24) {
									if (currentAspects[7] >= 5) {
										currentAspects[7] -= 5;
										currentAspects[2] += 1;
										currentAspects[3] += 3;
										currentAspects[4] += 1;
									}
								} else if (storyActions[i] == 29) {
									if (currentAspects[7] >= 5) {
										currentAspects[7] -= 5;
										currentAspects[2] += 1;
										currentAspects[3] += 3;
										currentAspects[4] += 1;
									}
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									}
								} else if (storyActions[i] == 12 || storyActions[i] == 7) {
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									}
								} else if (storyActions[i] == 11 || storyActions[i] == 5) {
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									} 
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									}
								} else if (storyActions[i] == 25) {
									if (currentAspects[0] >= 5) {
										currentAspects[0] -= 5;
										currentAspects[3] += 1;
										currentAspects[4] += 3;
										currentAspects[5] += 1;
									}
									if (currentAspects[1] >= 5) {
										currentAspects[1] -= 5;
										currentAspects[4] += 1;
										currentAspects[5] += 3;
										currentAspects[6] += 1;
									}
								} else if (storyActions[i] == 3) {
									if (currentAspects[1] >= 5) {
										currentAspects[1] -= 5;
										currentAspects[4] += 1;
										currentAspects[5] += 3;
										currentAspects[6] += 1;
									}
								} else if (storyActions[i] == 9) {
									if (currentAspects[2] >= 5) {
										currentAspects[2] -= 5;
										currentAspects[5] += 1;
										currentAspects[6] += 3;
										currentAspects[7] += 1;
									}
								} else if (storyActions[i] == 26) {
									if (currentAspects[2] >= 5) {
										currentAspects[2] -= 5;
										currentAspects[5] += 1;
										currentAspects[6] += 3;
										currentAspects[7] += 1;
									} 
									if (currentAspects[2] >= 5) {
										currentAspects[2] -= 5;
										currentAspects[5] += 1;
										currentAspects[6] += 3;
										currentAspects[7] += 1;
									}
								} else if (storyActions[i] == 23 || storyActions[i] == 38 || storyActions[i] == 6) {
									if (currentAspects[3] >= 5) {
										currentAspects[3] -= 5;
										currentAspects[6] += 1;
										currentAspects[7] += 3;
										currentAspects[0] += 1;
									}
								} else if (storyActions[i] == 31) {
									if (currentAspects[3] >= 5) {
										currentAspects[3] -= 5;
										currentAspects[6] += 1;
										currentAspects[7] += 3;
										currentAspects[0] += 1;
									}
									if (currentAspects[3] >= 5) {
										currentAspects[3] -= 5;
										currentAspects[6] += 1;
										currentAspects[7] += 3;
										currentAspects[0] += 1;
									}
								}
								
								break loopAction;
							}
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
								
			// refresh Hero's status
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
		// retrieve aspect values while we're at "Overview" refresh
		int a = 0;
		String text = driver.getPageSource();
		String[] items = text.split("[\r\n]+");
		for (int i = 0; i < items.length && a < 8; i++) {
			if (items[i].contains("<div class=\"tooltip\" style=\"text-align:left;\">")) {
				currentAspects[a++] = Integer.parseInt(items[i+2].replace(" ", "").replace("<br/>", "").replace("<br />", ""));
			}
		}
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
