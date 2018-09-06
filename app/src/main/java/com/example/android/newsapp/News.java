package com.example.android.newsapp;


public class News {
    private String NewsTitle;
    private String NewsSection;
    private String NewsAuthor;
    private String NewsDate;
    private String NewsUrl;

    /// Constructor of news object
    /**
     * Constructs a new {@link News} object.
     *
     * @param title is the title of news
     * @param section is the news category
     * @param author is the author's name
     * @param date is when the news was published
     * @param url is the website URL to find more details about the news
     */
    public News(String title, String section, String author, String date, String url){
        NewsTitle = title;
        NewsSection = section;
        NewsAuthor = author;
        NewsDate = date;
        NewsUrl = url;
    }

    // Public getter methods so that each data type is returned

    public String getNewsTitle(){
        return NewsTitle;
    }

    public String getNewsSection(){
        return NewsSection;
    }

    public String getAuthorsName(){
        return NewsAuthor;
    }

    public String getNewsDate(){
        return NewsDate;
    }

    public String getNewsUrl(){
        return NewsUrl;
    }
}
