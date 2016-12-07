package com.equinox.qikexpress.Models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.equinox.qikexpress.Models.Constants.ADDRESS_ELEMENTS;
import static com.equinox.qikexpress.Models.Constants.NAME;
import static com.equinox.qikexpress.Models.Constants.TYPE;

/**
 * Created by mukht on 11/18/2016.
 */

public class GeoAddress {

    private List<GeoElement> addressElements = new ArrayList<>();
    private String[] typesOrder = new String[]{"country", "postal_code", "administrative_area_level_1",
            "administrative_area_level_2", "locality", "sublocality", "route"};

    public List<GeoElement> getAddressElements() {
        return addressElements;
    }
    public void setAddressElements(List<GeoElement> addressElements) {
        this.addressElements = addressElements;
    }

    public String getFullAddress() {
        reArrangeElements();
        StringBuilder builder = new StringBuilder();
        for (int i=addressElements.size()-1; i>=0; i--)
            builder.append(", " + addressElements.get(i).getName());
        return builder.toString().substring(2);
    }
    public String getBasePath() {
        reArrangeElements();
        StringBuilder DBpathBuilder = new StringBuilder();
        for (GeoAddress.GeoElement element : addressElements) {
            DBpathBuilder.append(element.getName() + "/");
            if (element.getTypes().contains("sublocality")) break;
        }
        return DBpathBuilder.toString();
    }
    private void reArrangeElements() {
        List<GeoElement> tempAddressElements = new ArrayList<>();
        for (String type : typesOrder) {
            for (GeoElement element : addressElements) {
                if (element.getTypes().contains(type))
                    tempAddressElements.add(element);
            }
        }
        addressElements = tempAddressElements;
    }

    public class GeoElement {
        private String name;
        private List<String> types = new ArrayList<>();

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<String> getTypes() {
            return types;
        }
        public void setTypes(List<String> types) {
            this.types = types;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put(NAME, name);
            result.put(TYPE, types);
            return result;
        }

        @Exclude
        public GeoElement fromMap(Map<String,Object> entry) {
            name = (String) entry.get(NAME);
            types = (List<String>) entry.get(TYPE);
            return this;
        }

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        List<Object> geoElementObjects = new ArrayList<>();
        for (GeoAddress.GeoElement element : addressElements)
            geoElementObjects.add(element.toMap());
        result.put(ADDRESS_ELEMENTS, geoElementObjects);
        return result;
    }

    @Exclude
    public GeoAddress fromMap(Map<String,Object> entry) {
        List<Object> geoElementObjects = (List<Object>) entry.get(ADDRESS_ELEMENTS);
        for (Object elementObject : geoElementObjects) {
            addressElements.add(new GeoElement().fromMap((Map<String,Object>) elementObject));
        }
        return this;
    }
}
