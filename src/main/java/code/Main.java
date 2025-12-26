package code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Starting Wandering Inn Scraper...");

    VolumeScraper service = new VolumeScraper();

    int volumeToScrape = 1;
    boolean forceUpdate = false;

    if (args.length > 0) {
      try {
        volumeToScrape = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        logger.warn("Invalid volume number provided, defaulting to 1");
      }
    }

    if (args.length > 1) {
      if (args[1].equalsIgnoreCase("--force") || args[1].equalsIgnoreCase("-f")) {
        forceUpdate = true;
      }
    }

    service.scrapeVolume(volumeToScrape, "The_Wandering_Inn_Vol_" + volumeToScrape + ".epub", forceUpdate);
  }
}