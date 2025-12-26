package code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexScraperTest {

  private final IndexScraper scraper = new IndexScraper();

  @Test
  void testParseChapterListFromFixture() throws java.io.IOException {
    java.io.File input = new java.io.File("src/test/resources/toc.html");
    // Ensure the file exists before trying to parse it, to give a clear error if curl failed
    assertTrue(input.exists(), "Fixture file src/test/resources/toc.html not found. Run curl command first.");

    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/table-of-contents/");

    List<Chapter> chapters = scraper.parseChapterList(doc);

    assertFalse(chapters.isEmpty(), "Should find chapters in the real TOC");
    // We expect a large number of chapters, definitely more than 100
    assertTrue(chapters.size() > 100, "Should find many chapters (found " + chapters.size() + ")");

    // Check for a known chapter
    boolean foundFirstChapter = chapters.stream()
        .anyMatch(c -> c.title().contains("1.00") && c.url().contains("1-00"));
    assertTrue(foundFirstChapter, "Should find chapter 1.00");

    System.out.println("Found " + chapters.size() + " chapters. First 10:");
    chapters.stream().limit(10).forEach(System.out::println);
  }

  @Test
  void inspectTableOfContents() throws java.io.IOException {
    java.io.File input = new java.io.File("src/test/resources/toc.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/toc.html not found.");

    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/table-of-contents/");
    Element toc = doc.getElementById("table-of-contents");

    if (toc != null) {
      System.out.println("Found #table-of-contents:");
      // Print the structure (children tags) to understand what to parse
      System.out.println(toc.outerHtml().substring(0, Math.min(toc.outerHtml().length(), 2000)));
    } else {
      System.out.println("#table-of-contents not found");
    }
  }
}