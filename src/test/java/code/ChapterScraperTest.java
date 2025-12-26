package code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChapterScraperTest {

  private final ChapterScraper scraper = new ChapterScraper();

  @Test
  void testFetchChapterContentFromFixture() throws java.io.IOException {
    java.io.File input = new java.io.File("src/test/resources/chapter_1_00.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/chapter_1_00.html not found. Run curl command first.");

    // Mocking the network call by parsing the file directly is not exactly what ChapterScraper does (it calls Jsoup.connect),
    // but we can refactor ChapterScraper to accept a Document or use a mock server.
    // For now, let's just test the parsing logic if we extract it, or we can't easily test Jsoup.connect without a mock.
    // However, the user asked to "examine a part of the page for the content structure", so let's assume we want to test the parsing logic.

    // To test properly without network, I should refactor ChapterScraper to separate fetching and parsing.
    // But for this task, I will just create a test that parses the fixture manually and asserts on the logic I would put in a parse method.

    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/2017/03/03/rw1-00/");
    Chapter chapter = new Chapter("1.00", "https://wanderinginn.com/2017/03/03/rw1-00/", null);

    // I'll add a parse method to ChapterScraper to make it testable
    Chapter updatedChapter = scraper.parseChapterContent(doc, chapter);

    assertNotNull(updatedChapter.content());
    assertNotEquals("<p>Content not found</p>", updatedChapter.content());
    assertTrue(updatedChapter.title().contains("1.00"));
  }

  @Test
  void inspectChapterContentStructure() throws java.io.IOException {
    java.io.File input = new java.io.File("src/test/resources/chapter_1_00.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/chapter_1_00.html not found.");

    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/2017/03/03/rw1-00/");

    // Based on previous exploration, we know #main-content or .entry-content might be relevant.
    // Let's check .entry-content specifically as it's a standard WordPress class for content.
    Element content = doc.selectFirst(".entry-content");
    if (content == null) {
      content = doc.getElementById("main-content");
    }

    if (content != null) {
      System.out.println("Found content container (" + content.tagName() + " class=" + content.className() + " id=" + content.id() + "):");
      // Print the first 1000 chars to see the start of the content and any potential clutter
      System.out.println(content.outerHtml().substring(0, Math.min(content.outerHtml().length(), 1000)));

      System.out.println("\n--- Potential Clutter Elements ---");
      // Check for common clutter classes mentioned in previous code or found in WP sites
      content.select(".sharedaddy, .wpcnt, .post-navigation, script, style, .jp-relatedposts").forEach(e ->
          System.out.println("Found clutter: " + e.tagName() + "." + e.className())
      );
    } else {
      System.out.println("Content container not found");
    }
  }

  @Test
  void locateClutter() throws java.io.IOException {
    java.io.File input = new java.io.File("src/test/resources/chapter_1_00.html");
    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/2017/03/03/rw1-00/");

    Elements sharedaddy = doc.select(".sharedaddy");
    System.out.println("Found " + sharedaddy.size() + " .sharedaddy elements");
    for (Element e : sharedaddy) {
      System.out.println("Parent of .sharedaddy: " + e.parent().tagName() + " class=" + e.parent().className() + " id=" + e.parent().id());
      // Walk up to find if it's in main-content
      Element p = e.parent();
      while (p != null) {
        if ("main-content".equals(p.id())) {
          System.out.println("  -> Is inside #main-content");
          break;
        }
        p = p.parent();
      }
    }
  }
}