# Wandering Inn Scraper

This tool scrapes chapters from [The Wandering Inn](https://wanderinginn.com/table-of-contents/) and compiles them into an EPUB file.

## Prerequisites

- Java 17 or higher

## Building

```bash
./gradlew build
```

## Running

By default, the scraper processes only the first 5 chapters for testing purposes.

```bash
./gradlew run
```

To scrape **all** chapters (this will take a while due to rate limiting):

```bash
./gradlew run --args="all"
```

The output file `wandering_inn.epub` will be generated in the project root.

## Cmd to update test fixtures

curl -o src/test/resources/toc.html https://wanderinginn.com/table-of-contents/