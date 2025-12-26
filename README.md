# Wandering Inn Scraper

A Java-based tool to scrape volumes of the web serial [The Wandering Inn](https://wanderinginn.com/) and compile them into EPUB files.

## Features

- **Volume-based Scraping**: Scrapes entire volumes at a time.
- **EPUB Generation**: Creates properly formatted EPUB files with a Table of Contents.
- **Smart Caching**: Uses a local SQLite database (`wandering_inn.db`) to cache chapter content, preventing redundant network requests and speeding up subsequent runs.
- **Content Cleanup**: Removes navigation links, comments, and other web clutter.
- **Rate Limiting**: Includes polite delays to respect the server.

## Prerequisites

- **Java 21** or higher

## Building

```bash
# Linux/macOS
./gradlew build

# Windows
.\gradlew.bat build
```
### Usage

**Scrape Volume x:**
```bash
./gradlew run --args="x"
```

**Scrape Volume x (Force Update):**
Use the `--force` or `-f` flag to ignore the local db cache and re-download the chapter.
```bash
./gradlew run --args="x --force"
```

### Using the JAR

After building, the JAR file will be located in `build/libs/`.

```bash
java -jar build/libs/wandering-inn-scraper-1.0-SNAPSHOT.jar 1
```

## Project Structure

- `src/main/java/code/`
  - `Main.java`: Entry point.
  - `VolumeScraperService.java`: Orchestrates the scraping and EPUB generation process.
  - `ChapterScraper.java`: Handles downloading and parsing individual chapters.
  - `IndexScraper.java`: Parses the Table of Contents.
  - `EpubGenerator.java`: Compiles chapters into an EPUB.
  - `db/`: Database management (SQLite) and repositories.

## Database

The application uses a `wandering_inn.db` SQLite file in the project root. This database caches:
- Chapter URLs
- Chapter Titles
- Scraped HTML Content

## Testing

```bash
./gradlew test
```
