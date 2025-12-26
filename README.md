# Wandering Inn Scraper

A Java-based tool to scrape volumes of the web serial [The Wandering Inn](https://wanderinginn.com/) and compile them into EPUB files.
Written on 26.12.2025 to have something to read on my e-reader during my Christmas break.

I do not affiliate with 'The Wandering in', All rights attributed to pirateabe (Thank you!). 
If you have the option, I highly encourage you to read on the official site, and to support the author by buying the official releases on [Amazon](https://www.amazon.com/stores/pirate-aba/author/B07XCYVYMW)
Only use this tool if you absolutely cannot read the novel online.

## Prerequisites

- **Java 21** or higher

## Building

```bash
./gradlew.bat build
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
java -jar build/libs/wandering-inn-scraper-1.0-SNAPSHOT.jar x --force
```

## Project Structure

- `src/main/java/code/`
  - `Main.java`: Entry point.
  - `VolumeScraper.java`: Orchestrates the scraping and EPUB generation process.
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