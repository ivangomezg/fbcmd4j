package org.fbcmd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fbcmd4j.utils.Utils;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;

public class Main {
	
	static final Logger logger = LogManager.getLogger(Main.class);
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";

	public static void main(String[] args) {
		logger.info("initializing");
		Facebook fb = null;
		Properties props = null;

		try {
			props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {
			logger.error(ex);
		}

		int option = 1;
		try {
			Scanner scan = new Scanner(System.in);
			while (true) {
				fb = Utils.configFacebook(props);
				System.out.println("Options: \n" +
						"(0) Configure client \n" + 
						"(1) Check NewsFeed \n" + 
						"(2) Check Wall \n" + 
						"(3) Publish Status \n" +
						"(4) Publish Link \n" + 
						"(5) Exit \n" + 
						"\nSelect an option:");
				try {
					option = scan.nextInt();
					scan.nextLine();
					switch (option) {
					case 0:
						Utils.configTokens(CONFIG_DIR, CONFIG_FILE, props, scan);
						props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
						break;
					case 1:
						System.out.println("NewsFeed:");
						ResponseList<Post> newsFeed = fb.getFeed();
						for (Post p : newsFeed) {
							Utils.desplegarPost(p);
						}
						preguntarGuardar("NewsFeed", newsFeed, scan);
						break;
					case 2:
						System.out.println("Wall:");
						ResponseList<Post> wall = fb.getPosts();
						for (Post p : wall) {
							Utils.desplegarPost(p);
						}
						preguntarGuardar("Wall", wall, scan);
						break;
					case 3:
						System.out.println("Write a status: ");
						String estado = scan.nextLine();
						Utils.publicarStatus(estado, fb);
						break;
					case 4:
						System.out.println("Insert link: ");
						String link = scan.nextLine();
						Utils.publicarLink(link, fb);
						break;
					case 5:
						System.out.println("Terminating...");
						System.exit(0);
						break;
					default:
						break;
					}
				} catch (InputMismatchException ex) {
					System.out.println("check log to see the error");
					logger.error("invalid option. %s. \n", ex.getClass());
				} catch (FacebookException ex) {
					System.out.println("check log to see the error");
					logger.error(ex.getErrorMessage());
				} catch (Exception ex) {
					System.out.println("check log to see the error");
					logger.error(ex);
				}
				System.out.println();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static void preguntarGuardar(String fileName, ResponseList<Post> posts, Scanner scan) {
		System.out.println("Save results? Y/N");
		String option = scan.nextLine();

		if (option.contains("y") || option.contains("Y")) {
			List<Post> ps = new ArrayList<>();
			int n = 0;

			while (n <= 0) {
				try {
					System.out.println("How many posts will you save?");
					n = Integer.parseInt(scan.nextLine());

					if (n <= 0) {
						System.out.println("Enter a valid number");
					} else {
						for (int i = 0; i < n; i++) {
							if (i > posts.size() - 1)
								break;
							ps.add(posts.get(i));
						}
					}
				} catch (NumberFormatException e) {
					logger.error(e);
				}
			}

			Utils.guardatPostArchivo(fileName, ps);
		}
	}

}
