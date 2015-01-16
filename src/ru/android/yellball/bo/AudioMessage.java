package ru.android.yellball.bo;

import java.util.Date;

/**
 * Created by user on 10.12.2014.
 */
public class AudioMessage {
    private String id;
    private String title;
    private String description;
    private byte[] data;
    private long duration;
    private String author;
    private AudioMessage replyTo;
    private Date created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public AudioMessage getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(AudioMessage replyTo) {
        this.replyTo = replyTo;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
