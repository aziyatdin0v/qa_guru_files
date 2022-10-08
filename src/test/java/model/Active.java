package model;

import java.util.List;

public class Active {
    public int id;
    public String name;
    public String dnsName;
    public boolean isActive;
    public List<String> applicationSoftware;
    public Active.networkConfigurations networkConfigurations;
    public static class networkConfigurations {
        public String socket;
        public String address;
    }
}

