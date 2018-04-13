package com.bagirapp.environmentalnews;

public class News {
    private String title;
    private String publicationDate;
    private String url;
    private String sectionName;
    private String author;

    public News(String title, String publicationDate, String url, String section, String author) {
        this.title = title;
        this.publicationDate = publicationDate;
        this.url = url;
        this.sectionName = section;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }


}
