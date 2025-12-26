package code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChapterScraperTest {

  private final ChapterScraper scraper = new ChapterScraper();

  @Test
  void testFetchChapterContentFromFixture() throws java.io.IOException {
    // Given
    java.io.File input = new java.io.File("src/test/resources/chapter_1_00.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/chapter_1_00.html not found. Run curl command first.");

    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/2017/03/03/rw1-00/");
    Chapter chapter = new Chapter("1.00", "https://wanderinginn.com/2017/03/03/rw1-00/", null);

    // When
    Chapter updatedChapter = scraper.parseChapterContent(doc, chapter);

    // Then
    assertNotNull(updatedChapter.content());
    assertNotEquals("<p>Content not found</p>", updatedChapter.content());
    assertTrue(updatedChapter.title().contains("1.00"));
  }

  @Test
  @Disabled
  void inspectChapterContentStructure() throws java.io.IOException {
    // Given
    java.io.File input = new java.io.File("src/test/resources/chapter_1_00.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/chapter_1_00.html not found.");

    // When
    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/2017/03/03/rw1-00/");

    Element content = doc.selectFirst(".entry-content");
    if (content == null) {
      content = doc.getElementById("main-content");
    }

    // Then
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
  @Disabled
  void locateClutter() throws java.io.IOException {
    // Given
    java.io.File input = new java.io.File("src/test/resources/chapter_1_00.html");
    Document doc = Jsoup.parse(input, "UTF-8", "https://wanderinginn.com/2017/03/03/rw1-00/");

    // When
    Elements sharedaddy = doc.select(".sharedaddy");

    // Then
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

  @Test
  void testParseChapterWithImageAtEnd() throws java.io.IOException {
    // Given
    java.io.File input = new java.io.File("src/test/resources/chapter_1_55_R.html");
    assertTrue(input.exists(), "Fixture file src/test/resources/chapter_1_55_R.html not found.");
    String url = "https://wanderinginn.com/2017/03/04/rw1-55-r/";

    // When
    Document doc = Jsoup.parse(input, "UTF-8", url);
    Chapter chapter = scraper.parseChapterContent(doc, new Chapter("Test Title", url, null));

    // Then
    assertNotNull(chapter);
    // Check if the image is present in the content
    // The image has alt="" so we might search for the src or the structure
    boolean hasImage = chapter.content().contains("Bloodfields_Archival.png");
    assertTrue(hasImage, "Chapter content should contain the Bloodfields image");

    // Ensure srcset is removed for EPUB compatibility
    assertFalse(chapter.content().contains("srcset="), "Image should not have srcset attribute");
  }

  @Test
  void testParseChapterWithImageOnlyParagraphAtEnd() {
    // Given
    String html = """
        <html>
        <head><title>Test Chapter - The Wandering Inn</title></head>
        <body>
          <div class="twi-article">
            <p>Some text.</p>
            <p><img src="map.png" /></p>
          </div>
        </body>
        </html>
        """;
    Document doc = Jsoup.parse(html);
    String url = "https://wanderinginn.com/test";

    // When
    Chapter chapter = scraper.parseChapterContent(doc, new Chapter("Test Chapter", url, null));

    // Then
    assertNotNull(chapter);
    assertTrue(chapter.content().contains("map.png"), "Image in last paragraph should be preserved");
  }
}