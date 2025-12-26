package code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChapterScraper {
  private static final Logger logger = LoggerFactory.getLogger(ChapterScraper.class);

  private enum Selector {
    ARTICLE(".twi-article"),
    NEXT_CHAPTER("a:contains(Next Chapter)"),
    PREVIOUS_CHAPTER("a:contains(Previous Chapter)"),
    PARAGRAPH("p"),
    HORIZONTAL_RULE("hr");

    public final String id;

    Selector(String id) {
      this.id = id;
    }
  }

  public Chapter fetchChapterContent(Chapter chapter) {
    logger.info("Fetching chapter: {}", chapter.title());
    try {
      Document doc = Jsoup.connect(chapter.url())
          .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
          .get();
      return parseChapterContent(doc, chapter);
    } catch (IOException e) {
      logger.error("Failed to fetch chapter: {}", chapter.url(), e);
      return chapter.withContent("<p>Failed to load content</p>");
    }
  }

  public Chapter parseChapterContent(Document doc, Chapter chapter) {
    String title = extractTitle(doc, chapter);
    Element contentElement = doc.selectFirst(Selector.ARTICLE.id);

    if (contentElement != null) {
      cleanContent(contentElement);
      return chapter.withTitle(title).withContent(contentElement.html());
    } else {
      logger.warn("No content found for {}", chapter.url());
      return chapter.withContent("<p>Content not found</p>");
    }
  }

  private String extractTitle(Document doc, Chapter chapter) {
    String pageTitle = doc.title();
    if (pageTitle != null && !pageTitle.isEmpty()) {
      return pageTitle.replace(" - The Wandering Inn", "").trim();
    }
    return chapter.title();
  }

  private void cleanContent(Element contentElement) {
    removeNavigationLinks(contentElement);
    removeTrailingEmptyElements(contentElement);
  }

  private void removeNavigationLinks(Element contentElement) {
    contentElement.select(Selector.NEXT_CHAPTER.id).remove();
    contentElement.select(Selector.PREVIOUS_CHAPTER.id).remove();
  }

  private void removeTrailingEmptyElements(Element contentElement) {
    Element last = contentElement.lastElementChild();
    while (last != null) {
      if (isEmptyParagraph(last) || isHorizontalRule(last)) {
        last.remove();
        last = contentElement.lastElementChild();
      } else {
        break;
      }
    }
  }

  private boolean isEmptyParagraph(Element element) {
    return element.tagName().equals(Selector.PARAGRAPH.id) &&
        (element.text().trim().isEmpty() || element.html().equals("&nbsp;"));
  }

  private boolean isHorizontalRule(Element element) {
    return element.tagName().equals(Selector.HORIZONTAL_RULE.id);
  }
}