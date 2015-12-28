package com.beehivesnetwork.justword.object;

import java.io.Serializable;

/**
 * Created by davidtang on 2015-12-22.
 */
public class Novel implements Serializable{
    private String nid;
    private String author_id;
    private String author_name;
    private String author_pic;
    private String category;
    private String category_id;
    private String content;
    private String pic;
    private int read;
    private int like;
    private String date;
    private String share;
    private String liked;

    public Novel(String nid, String author_id, String author_name, String author_pic, String category, String category_id, String content, String pic, int read, int like, String date, String share, String liked) {
        this.nid = nid;
        this.author_id = author_id;
        this.author_name = author_name;
        this.author_pic = author_pic;
        this.category = category;
        this.category_id = category_id;
        this.content = content;
        this.pic = pic;
        this.read = read;
        this.like = like;
        this.date = date;
        this.share = share;
        this.liked = liked;
    }

    public String getNid() {
        return nid;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getAuthor_pic() {
        return author_pic;
    }

    public String getCategory() {
        return category;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getContent() {
        return content;
    }

    public String getPic() {
        return pic;
    }

    public int getRead() {
        return read;
    }

    public int getLike() {
        return like;
    }

    public String getDate() {
        return date;
    }

    public String getShare() {
        return share;
    }

    public String getLiked() {
        return liked;
    }

    public void addLike() {
        like = like+1;
    }

}
