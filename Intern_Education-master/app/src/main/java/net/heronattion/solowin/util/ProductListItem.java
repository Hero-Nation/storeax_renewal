package net.heronattion.solowin.util;

/**
 * Created by Brant on 2017-02-21.
 */

public class ProductListItem {
    private String imageURL;
    private String productURL;
    private String description;
    private String title;
    private String amount;
    private String hashTag;

    //------------- set -----------------

    public void setImageURL(String URL) {
        this.imageURL = URL;
    }

    public void setProductURL(String URL) {
        this.productURL = URL;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setTitle(String title) {
        this.title = title ;
    }

    public void setHashTag(String hash) {
        this.hashTag = hash;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    //-------------- get -------------

    public String getImageURL() {
        return this.imageURL;
    }

    public String getProductURL() {
        return this.productURL;
    }

    public String getDescription() {
        return this.description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getHashTag() {
        return this.hashTag;
    }

    public String getAmount() {
        return this.amount;
    }
}
