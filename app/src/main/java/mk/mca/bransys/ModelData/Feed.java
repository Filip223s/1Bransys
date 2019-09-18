package mk.mca.bransys.ModelData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import mk.mca.bransys.ModelData.Network;

public class Feed {
    @SerializedName("networks")
    @Expose
    private ArrayList<Network> networks;

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<Network> networks) {
        this.networks = networks;
    }
}


