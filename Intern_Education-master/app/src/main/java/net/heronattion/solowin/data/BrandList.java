package net.heronattion.solowin.data;

/**
 * Created by heronation on 2017-06-14.
 */

public class BrandList {

    private String brandKey;
    private String name;
    private String logo;
    private boolean favorite;

    public BrandList(String brandKey, String name, String logo, boolean favorite) {
        this.brandKey = brandKey;
        this.name = name;
        this.logo = logo;
        this.favorite = favorite;
    }

    public String getBrandKey() {
        return brandKey;
    }

    public void setBrandKey(String brandKey) {
        this.brandKey = brandKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

}
