package code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexScraper {
  private static final Logger logger = LoggerFactory.getLogger(IndexScraper.class);
  private static final String TOC_URL = "https://wanderinginn.com/table-of-contents/";

  private enum Selector {
    TABLE_OF_CONTENTS("table-of-contents"),
    CHAPTER_ENTRY(".chapter-entry a");

    public final String id;

    Selector(String id) {
      this.id = id;
    }
  }

  public List<Chapter> getChapterList() throws IOException {
    logger.info("Fetching Table of Contents from {}", TOC_URL);
    Document doc = Jsoup.connect(TOC_URL)
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .get();
    return parseChapterList(doc);
  }

  public List<Chapter> getChaptersForVolume(int volumeNumber) throws IOException {
    logger.info("Fetching Table of Contents for Volume {} from {}", volumeNumber, TOC_URL);
    Document doc = Jsoup.connect(TOC_URL)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .get();
    return parseChaptersForVolume(doc, volumeNumber);
  }

  public List<Chapter> parseChapterList(Document doc) {
    List<Chapter> chapters = new ArrayList<>();

    Element content = doc.getElementById(Selector.TABLE_OF_CONTENTS.id);

    if (content == null) {
      logger.error("Could not find #table-of-contents in TOC page");
      return chapters;
    }

    Elements links = content.select(Selector.CHAPTER_ENTRY.id);
    for (Element link : links) {
      String href = link.attr("abs:href");
      String title = link.text();

      chapters.add(new Chapter(title, href, null));

    }
    logger.info("Found {} chapters", chapters.size());
    return chapters;
  }

  public List<Chapter> parseChaptersForVolume(Document doc, int volumeNumber) {
    List<Chapter> chapters = new ArrayList<>();
    Element volumeContent = doc.getElementById("vol-" + volumeNumber);

    if (volumeContent == null) {
      logger.error("Could not find volume {} in TOC page", volumeNumber);
      return chapters;
    }

    Elements links = volumeContent.select(Selector.CHAPTER_ENTRY.id);
    for (Element link : links) {
      String href = link.attr("abs:href");
      String title = link.text();

      chapters.add(new Chapter(title, href, null));
    }
    logger.info("Found {} chapters in Volume {}", chapters.size(), volumeNumber);
    return chapters;
  }
}