package code;

import code.db.ChapterRepository;
import code.db.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VolumeScraperService {
  private static final Logger logger = LoggerFactory.getLogger(VolumeScraperService.class);
  private final IndexScraper indexScraper;
  private final ChapterScraper chapterScraper;
  private final EpubGenerator epubGenerator;
  private final ChapterRepository chapterRepository;

  public VolumeScraperService() {
    DatabaseManager dbManager = new DatabaseManager();
    this.indexScraper = new IndexScraper();
    this.chapterScraper = new ChapterScraper();
    this.epubGenerator = new EpubGenerator();
    this.chapterRepository = new ChapterRepository(dbManager);
  }

  public VolumeScraperService(IndexScraper indexScraper, ChapterScraper chapterScraper, EpubGenerator epubGenerator, ChapterRepository chapterRepository) {
    this.indexScraper = indexScraper;
    this.chapterScraper = chapterScraper;
    this.epubGenerator = epubGenerator;
    this.chapterRepository = chapterRepository;
  }

  public void scrapeVolume(int volumeNumber, String outputPath, boolean forceUpdate) {
    logger.info("Starting scrape for Volume {} (Force Update: {})", volumeNumber, forceUpdate);
    try {
      List<Chapter> chapters = indexScraper.getChaptersForVolume(volumeNumber);
      if (chapters.isEmpty()) {
        logger.warn("No chapters found for Volume {}", volumeNumber);
        return;
      }

      List<Chapter> chaptersWithContent = new ArrayList<>();
      int index = 0;
      for (Chapter chapter : chapters) {
        index++;
        Optional<Chapter> cachedChapter = Optional.empty();

        if (!forceUpdate) {
          cachedChapter = chapterRepository.getChapterByUrl(chapter.url());
        }

        if (cachedChapter.isPresent() && cachedChapter.get().content() != null) {
          logger.info("Found cached content for {}", chapter.title());
          chaptersWithContent.add(cachedChapter.get());
        } else {
          Chapter fetchedChapter = chapterScraper.fetchChapterContent(chapter);
          if (fetchedChapter.content() != null) {
            chapterRepository.saveChapter(fetchedChapter, volumeNumber, index);
          }
          chaptersWithContent.add(fetchedChapter);
          // Be nice to the server
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Scraping interrupted");
            break;
          }
        }
      }

      epubGenerator.createEpub(chaptersWithContent, outputPath, "The Wandering Inn - Volume " + volumeNumber);
      logger.info("Finished scraping Volume {}. EPUB saved to {}", volumeNumber, outputPath);

    } catch (IOException e) {
      logger.error("Error scraping volume {}", volumeNumber, e);
    }
  }
}