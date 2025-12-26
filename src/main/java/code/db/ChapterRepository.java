package code.db;

import code.Chapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ChapterRepository {
  private static final Logger logger = LoggerFactory.getLogger(ChapterRepository.class);
  private final DatabaseManager dbManager;

  public ChapterRepository(DatabaseManager dbManager) {
    this.dbManager = dbManager;
  }

  public void saveChapter(Chapter chapter, int volumeId, int index) {
    String sql = "INSERT OR REPLACE INTO chapters(url, title, content, volume_id, chapter_index) VALUES(?, ?, ?, ?, ?)";

    try (Connection conn = dbManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, chapter.url());
      pstmt.setString(2, chapter.title());
      pstmt.setString(3, chapter.content());
      pstmt.setInt(4, volumeId);
      pstmt.setInt(5, index);

      pstmt.executeUpdate();

    } catch (SQLException e) {
      logger.error("Failed to save chapter: {}", chapter.title(), e);
    }
  }

  public Optional<Chapter> getChapterByUrl(String url) {
    String sql = "SELECT title, url, content FROM chapters WHERE url = ?";

    try (Connection conn = dbManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, url);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new Chapter(
              rs.getString("title"),
              rs.getString("url"),
              rs.getString("content")
          ));
        }
      }

    } catch (SQLException e) {
      logger.error("Failed to get chapter by url: {}", url, e);
    }
    return Optional.empty();
  }
}