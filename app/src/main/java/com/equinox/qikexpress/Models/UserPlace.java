package com.equinox.qikexpress.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mukht on 1/1/2017.
 */

public class UserPlace {

    private GeoAddress address;
    private LatLng location;
    private String houseNumber, houseName, apartmentName, apartmentNumber,  buildingNumber, buildingName, community, society, zone,
            organization, addresseeName, neighbourhood, municipality, fullAddressText;
    private Integer postBox;

    @Exclude
    public List<String> getList() {
        List<String> userPlaceList = new ArrayList<>();
        userPlaceList.add("House Number");
        userPlaceList.add("House Name");
        userPlaceList.add("Apartment Name");
        userPlaceList.add("Apartment Number");
        userPlaceList.add("Building Number");
        userPlaceList.add("Building Name");
        userPlaceList.add("Community");
        userPlaceList.add("Society");
        userPlaceList.add("Zone");
        userPlaceList.add("Organization");
        userPlaceList.add("Addressee Name");
        userPlaceList.add("Neighbourhood");
        userPlaceList.add("Municipality");
        userPlaceList.add("Post Box");
        return userPlaceList;
    }

    @Exclude
    public void putValue(String type, String value) {
        switch (type) {
            case "House Number":
                houseNumber = value;
                break;
            case "House Name":
                houseName = value;
                break;
            case "Apartment Name":
                apartmentName = value;
                break;
            case "Apartment Number":
                apartmentNumber = value;
                break;
            case "Building Number":
                buildingNumber = value;
                break;
            case "Building Name":
                buildingName = value;
                break;
            case "Community":
                community = value;
                break;
            case "Society":
                society = value;
                break;
            case "Organization":
                organization = value;
                break;
            case "Addressee Name":
                addresseeName = value;
                break;
            case "Neighbourhood":
                neighbourhood = value;
                break;
            case "Municipality":
                municipality = value;
                break;
            case "Post Box":
                postBox = Integer.valueOf(value);
                break;
        }
    }

    public void removeValue(String type) {
        switch (type) {
            case "House Number":
                houseNumber = null;
                break;
            case "House Name":
                houseName = null;
                break;
            case "Apartment Name":
                apartmentName = null;
                break;
            case "Apartment Number":
                apartmentNumber = null;
                break;
            case "Building Number":
                buildingNumber = null;
                break;
            case "Building Name":
                buildingName = null;
                break;
            case "Community":
                community = null;
                break;
            case "Society":
                society = null;
                break;
            case "Organization":
                organization = null;
                break;
            case "Addressee Name":
                addresseeName = null;
                break;
            case "Neighbourhood":
                neighbourhood = null;
                break;
            case "Municipality":
                municipality = null;
                break;
            case "Post Box":
                postBox = null;
                break;
        }
    }

    @Exclude
    public HashMap<String,Object> toMapEdit() {
        HashMap<String,Object> userPlaceMap = new HashMap<>();
        userPlaceMap.put("House Number",houseNumber);
        userPlaceMap.put("House Name",houseName);
        userPlaceMap.put("Apartment Name", apartmentName);
        userPlaceMap.put("Apartment Number", apartmentNumber);
        userPlaceMap.put("Building Number",buildingNumber);
        userPlaceMap.put("Building Name",buildingName);
        userPlaceMap.put("Community",community);
        userPlaceMap.put("Society",society);
        userPlaceMap.put("Zone",zone);
        userPlaceMap.put("Organization",organization);
        userPlaceMap.put("Addressee Name",addresseeName);
        userPlaceMap.put("eighbourhood",neighbourhood);
        userPlaceMap.put("Nmunicipality",municipality);
        userPlaceMap.put("Post Box",postBox);
        return userPlaceMap;
    }

    @Exclude
    public HashMap<String,Object> toMap() {
        HashMap<String,Object> userPlaceMap = new HashMap<>();
        userPlaceMap.put("houseNumber",houseNumber);
        userPlaceMap.put("houseName",houseName);
        userPlaceMap.put("apartmentName", apartmentName);
        userPlaceMap.put("apartmentNumber", apartmentNumber);
        userPlaceMap.put("buildingNumber",buildingNumber);
        userPlaceMap.put("buildingName",buildingName);
        userPlaceMap.put("community",community);
        userPlaceMap.put("society",society);
        userPlaceMap.put("zone",zone);
        userPlaceMap.put("organization",organization);
        userPlaceMap.put("addresseeName",addresseeName);
        userPlaceMap.put("neighbourhood",neighbourhood);
        userPlaceMap.put("municipality",municipality);
        userPlaceMap.put("postBox",postBox);
        userPlaceMap.put("address",address.toMap());
        userPlaceMap.put("location",location);
        userPlaceMap.put("fullAddressText",fullAddressText);
        return userPlaceMap;
    }

    @Exclude
    public UserPlace fromMap(Map<String,Object> userPlaceMap) {
        address = new GeoAddress().fromMap((Map<String,Object>) userPlaceMap.get("address"));
        Map<String,Double> locationInfo = (Map<String,Double>) userPlaceMap.get("location");
        location = new LatLng(locationInfo.get("latitude"), locationInfo.get("longitude"));
        if (userPlaceMap.containsKey("houseNumber"))
            houseNumber = (String) userPlaceMap.get("houseNumber");
        if (userPlaceMap.containsKey("houseName"))
            houseName = (String) userPlaceMap.get("houseName");
        if (userPlaceMap.containsKey("apartmentNumber"))
            apartmentNumber = (String) userPlaceMap.get("apartmentNumber");
        if (userPlaceMap.containsKey("apartmentName"))
            apartmentName = (String) userPlaceMap.get("apartmentName");
        if (userPlaceMap.containsKey("buildingNumber"))
            buildingNumber = (String) userPlaceMap.get("buildingNumber");
        if (userPlaceMap.containsKey("buildingName"))
            buildingName = (String) userPlaceMap.get("buildingName");
        if (userPlaceMap.containsKey("community"))
            community = (String) userPlaceMap.get("community");
        if (userPlaceMap.containsKey("society"))
            society = (String) userPlaceMap.get("society");
        if (userPlaceMap.containsKey("zone"))
            zone = (String) userPlaceMap.get("zone");
        if (userPlaceMap.containsKey("organization"))
            organization = (String) userPlaceMap.get("organization");
        if (userPlaceMap.containsKey("addresseeName"))
            addresseeName = (String) userPlaceMap.get("addresseeName");
        if (userPlaceMap.containsKey("neighbourhood"))
            neighbourhood = (String) userPlaceMap.get("neighbourhood");
        if (userPlaceMap.containsKey("municipality"))
            municipality = (String) userPlaceMap.get("municipality");
        if (userPlaceMap.containsKey("fullAddressText"))
            fullAddressText = (String) userPlaceMap.get("fullAddressText");
        if (userPlaceMap.containsKey("postBox"))
            postBox = (int) (long) userPlaceMap.get("postBox");
        return this;
    }

    public GeoAddress getAddress() {
        return address;
    }
    public void setAddress(GeoAddress address) {
        this.address = address;
    }
    public String getHouseNumber() {
        return houseNumber;
    }
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
    public String getHouseName() {
        return houseName;
    }
    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }
    public String getApartmentName() {
        return apartmentName;
    }
    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }
    public String getApartmentNumber() {
        return apartmentNumber;
    }
    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }
    public String getBuildingNumber() {
        return buildingNumber;
    }
    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }
    public String getBuildingName() {
        return buildingName;
    }
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
    public String getCommunity() {
        return community;
    }
    public void setCommunity(String community) {
        this.community = community;
    }
    public String getSociety() {
        return society;
    }
    public void setSociety(String society) {
        this.society = society;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }
    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public String getAddresseeName() {
        return addresseeName;
    }
    public void setAddresseeName(String addresseeName) {
        this.addresseeName = addresseeName;
    }
    public String getNeighbourhood() {
        return neighbourhood;
    }
    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
    public String getMunicipality() {
        return municipality;
    }
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
    public Integer getPostBox() {
        return postBox;
    }
    public void setPostBox(Integer postBox) {
        this.postBox = postBox;
    }
    public LatLng getLocation() {
        return location;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }
    public String getFullAddressText() {
        return fullAddressText;
    }
    public void setFullAddressText(String fullAddressText) {
        this.fullAddressText = fullAddressText;
    }

}
