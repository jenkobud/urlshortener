package com.roadmapsh.urlshortener.dtos.requests;

public class UrlShortenerRequest {
    private String url;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {
        return "UrlShortenerRequest{" +
                "url='" + url + '\'' +
                '}';
    }
}
