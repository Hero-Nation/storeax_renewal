package net.heronattion.solowin.data;

/**
 * Created by heronation on 2017-06-14.
 */

public class BrandProductItem {

    private String title;
    private String price;
    private String photo;
    private String url;
    private int productID;
    private boolean favorite;

    public BrandProductItem(String title, String price, String photo, String url, int productid, boolean favorite) {
        this.title = title;
        this.price = price;
        this.photo = photo;
        this.productID = productid;
        this.favorite = favorite;
        this.url = url;

    }
    //get
    public String getTitle() {
        return title;
    }
    public String getPrice() {
        return price;
    }
    public String getPhoto() {
        return photo;
    }
    public int getProductID() {
        return productID;
    }
    public boolean getFavorite() {
        return favorite;
    }
    public String getUrl(){
        return url;
    }

    //set
    public void setTitle(String title) {
        this.title = title;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setPhoto(String photo) {
        this.photo= photo;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
