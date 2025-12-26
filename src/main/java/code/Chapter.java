package code;

public record Chapter(String title, String url, String content) {
  public Chapter withContent(String content) {
    return new Chapter(title, url, content);
  }

  public Chapter withTitle(String title) {
    return new Chapter(title, url, content);
  }
}