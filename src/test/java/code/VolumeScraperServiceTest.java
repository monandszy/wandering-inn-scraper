package code;

import code.db.ChapterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolumeScraperServiceTest {

  @Mock
  private IndexScraper indexScraper;

  @Mock
  private ChapterScraper chapterScraper;

  @Mock
  private EpubGenerator epubGenerator;

  @Mock
  private ChapterRepository chapterRepository;

  @InjectMocks
  private VolumeScraperService volumeScraperService;

  @Test
  void testScrapeVolume() throws IOException {
    // Given
    int volumeNumber = 1;
    String outputPath = "volume1.epub";
    Chapter chapter1 = new Chapter("1.00", "url1", null);
    Chapter chapter2 = new Chapter("1.01", "url2", null);
    List<Chapter> chapters = List.of(chapter1, chapter2);

    Chapter cachedChapter1 = new Chapter("1.00", "url1", "<p>Cached Content 1</p>");
    Chapter fetchedChapter2 = new Chapter("1.01", "url2", "<p>Fetched Content 2</p>");

    when(indexScraper.getChaptersForVolume(volumeNumber)).thenReturn(chapters);

    // Chapter 1 is cached
    when(chapterRepository.getChapterByUrl("url1")).thenReturn(Optional.of(cachedChapter1));

    // Chapter 2 is not cached
    when(chapterRepository.getChapterByUrl("url2")).thenReturn(Optional.empty());
    when(chapterScraper.fetchChapterContent(chapter2)).thenReturn(fetchedChapter2);

    // When
    volumeScraperService.scrapeVolume(volumeNumber, outputPath, false);

    // Then
    verify(indexScraper).getChaptersForVolume(volumeNumber);

    // Verify chapter 1 was NOT fetched from web
    verify(chapterScraper, times(0)).fetchChapterContent(chapter1);

    // Verify chapter 2 WAS fetched from web and saved
    verify(chapterScraper).fetchChapterContent(chapter2);
    verify(chapterRepository).saveChapter(fetchedChapter2, volumeNumber, 2);

    verify(epubGenerator).createEpub(any(), eq(outputPath), eq("The Wandering Inn - Volume 1"));
  }

  @Test
  void testScrapeVolumeWithForceUpdate() throws IOException {
    // Given
    int volumeNumber = 1;
    String outputPath = "test.epub";
    Chapter chapter1 = new Chapter("Chapter 1", "url1", null);
    List<Chapter> chapters = List.of(chapter1);

    Chapter cachedChapter1 = new Chapter("Chapter 1", "url1", "Cached Content");
    Chapter fetchedChapter1 = new Chapter("Chapter 1", "url1", "Fetched Content");

    when(indexScraper.getChaptersForVolume(volumeNumber)).thenReturn(chapters);

    // We don't stub getChapterByUrl because we expect it NOT to be called
    when(chapterScraper.fetchChapterContent(chapter1)).thenReturn(fetchedChapter1);

    // When
    volumeScraperService.scrapeVolume(volumeNumber, outputPath, true);

    // Then
    // Verify we did NOT check the cache (or at least ignored it)
    // Actually, my implementation doesn't call getChapterByUrl if forceUpdate is true.
    verify(chapterRepository, times(0)).getChapterByUrl("url1");

    // Verify chapter 1 WAS fetched from web
    verify(chapterScraper).fetchChapterContent(chapter1);
    verify(chapterRepository).saveChapter(fetchedChapter1, volumeNumber, 1);

    verify(epubGenerator).createEpub(any(), eq(outputPath), eq("The Wandering Inn - Volume 1"));
  }
}