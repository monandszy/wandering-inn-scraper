package code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Starting Wandering Inn Scraper...");
    IndexScraper indexScraper = new IndexScraper();
    ChapterScraper chapterScraper = new ChapterScraper();
    EpubGenerator epubGenerator = new EpubGenerator();

    try {
      List<Chapter> allChapters = indexScraper.getChapterList();

      // For testing/safety, let's limit to first 5 chapters by default unless args say otherwise
      // If you want all, pass "all" as argument
      int limit = 5;
      if (args.length > 0 && "all".equalsIgnoreCase(args[0])) {
        limit = allChapters.size();
      }

      logger.info("Processing {} chapters...", Math.min(limit, allChapters.size()));

      List<Chapter> chaptersToProcess = new java.util.ArrayList<>();
      for (int i = 0; i < Math.min(limit, allChapters.size()); i++) {
        Chapter chapter = allChapters.get(i);
        chaptersToProcess.add(chapterScraper.fetchChapterContent(chapter));
        // Be polite to the server
        Thread.sleep(1000);
      }

      epubGenerator.createEpub(chaptersToProcess, "wandering_inn.epub");

    } catch (Exception e) {
      logger.error("An error occurred", e);
    }
  }
}