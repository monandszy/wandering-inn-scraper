package code;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EpubGenerator {
  private static final Logger logger = LoggerFactory.getLogger(EpubGenerator.class);

  public void createEpub(List<Chapter> chapters, String outputPath) {
    try {
      Book book = new Book();
      book.getMetadata().addTitle("The Wandering Inn");
      book.getMetadata().addAuthor(new Author("pirateaba"));

      for (Chapter chapter : chapters) {
        if (chapter.content() != null) {
          // Create a valid HTML resource for the chapter
          String chapterHtml = "<html><head><title>" + chapter.title() + "</title></head><body>" +
              "<h1>" + chapter.title() + "</h1>" +
              chapter.content() +
              "</body></html>";

          book.addSection(chapter.title(), new Resource(chapterHtml.getBytes(), "chapter_" + chapters.indexOf(chapter) + ".html"));
        }
      }

      EpubWriter epubWriter = new EpubWriter();
      try (FileOutputStream out = new FileOutputStream(outputPath)) {
        epubWriter.write(book, out);
      }
      logger.info("EPUB created successfully at {}", outputPath);

    } catch (IOException e) {
      logger.error("Failed to create EPUB", e);
    }
  }

  public void createEpub(List<Chapter> chapters, String outputPath, String title) {
    try {
      Book book = new Book();
      book.getMetadata().addTitle(title);
      book.getMetadata().addAuthor(new Author("pirateaba"));

      // Generate Index Page
      StringBuilder indexHtml = new StringBuilder();
      indexHtml.append("<html><head><title>Table of Contents</title></head><body>");
      indexHtml.append("<h1>").append(title).append("</h1>");
      indexHtml.append("<ul>");

      for (int i = 0; i < chapters.size(); i++) {
        Chapter chapter = chapters.get(i);
        if (chapter.content() != null) {
          String filename = "chapter_" + i + ".html";
          indexHtml.append("<li><a href=\"").append(filename).append("\">")
              .append(chapter.title()).append("</a></li>");
        }
      }
      indexHtml.append("</ul></body></html>");

      // Add Index Page
      book.addSection("Table of Contents", new Resource(indexHtml.toString().getBytes(), "index.html"));

      for (int i = 0; i < chapters.size(); i++) {
        Chapter chapter = chapters.get(i);
        if (chapter.content() != null) {
          // Create a valid HTML resource for the chapter
          String chapterHtml = "<html><head><title>" + chapter.title() + "</title></head><body>" +
              "<h1>" + chapter.title() + "</h1>" +
              chapter.content() +
              "</body></html>";

          book.addSection(chapter.title(), new Resource(chapterHtml.getBytes(), "chapter_" + i + ".html"));
        }
      }

      EpubWriter epubWriter = new EpubWriter();
      try (FileOutputStream out = new FileOutputStream(outputPath)) {
        epubWriter.write(book, out);
      }
      logger.info("EPUB created successfully at {}", outputPath);

    } catch (IOException e) {
      logger.error("Failed to create EPUB", e);
    }
  }
}