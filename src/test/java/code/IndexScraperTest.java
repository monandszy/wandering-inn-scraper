package code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexScraperTest {

  private final IndexScraper scraper = new IndexScraper();

  @Test
  void testParseChapterListFromFixture() throws java.io.IOException {
    // Given
    java.io.File input = new java.io.File("src/test/resources/toc.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/toc.html not found. Run curl command first.");
    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/table-of-contents/");

    // When
    List<Chapter> chapters = scraper.parseChapterList(doc);

    // Then
    assertFalse(chapters.isEmpty(), "Should find chapters in the real TOC");
    assertTrue(chapters.size() > 100, "Should find many chapters (found " + chapters.size() + ")");

    boolean foundFirstChapter = chapters.stream()
        .anyMatch(c -> c.title().contains("1.00") && c.url().contains("1-00"));
    assertTrue(foundFirstChapter, "Should find chapter 1.00");

    System.out.println("Found " + chapters.size() + " chapters. First 10:");
    chapters.stream().limit(10).forEach(System.out::println);
  }

  @Test
  @Disabled
  void inspectTableOfContents() throws java.io.IOException {
    // Given
    java.io.File input = new java.io.File("src/test/resources/toc.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/toc.html not found.");

    // When
    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/table-of-contents/");
    Element toc = doc.getElementById("table-of-contents");

    // Then
    if (toc != null) {
      System.out.println("Found #table-of-contents:");
      // Print the structure (children tags) to understand what to parse
      System.out.println(toc.outerHtml().substring(0, Math.min(toc.outerHtml().length(), 2000)));
    } else {
      System.out.println("#table-of-contents not found");
    }
  }
}