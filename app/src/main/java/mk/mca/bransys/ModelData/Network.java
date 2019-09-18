package mk.mca.bransys.ModelData;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;


public class Network {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("company")
    @Expose
    private Object company;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @SerializedName("location")
    @Expose
    private Location location;

    public List<String> getCompanies() {
        if (company instanceof List<?>) {
            return (List<String>) company;
        } else if (company instanceof String) {
            return Arrays.asList((String) company);
        } else {
            return null;
        }
    }

    public void setCompany(Arrays company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

