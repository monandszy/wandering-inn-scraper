package code;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EpubGeneratorTest {

  @Test
  void testCreateEpub(@TempDir Path tempDir) {
    EpubGenerator generator = new EpubGenerator();
    Path outputPath = tempDir.resolve("test.epub");

    List<Chapter> chapters = List.of(
        new Chapter("Chapter 1", "url1", "<p>Content 1</p>"),
        new Chapter("Chapter 2", "url2", "<p>Content 2</p>")
    );

    generator.createEpub(chapters, outputPath.toString(), "Test Book");

    assertTrue(outputPath.toFile().exists());
  }
}