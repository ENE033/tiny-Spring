package springframework.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyValues {
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    public PropertyValues(PropertyValue... pvs) {
        addPropertyValue(pvs);
    }

    public void addPropertyValue(PropertyValue pv) {
        propertyValueList.add(pv);
    }

    public void addPropertyValue(PropertyValue... pvs) {
        propertyValueList.addAll(Arrays.asList(pvs));
    }


    public PropertyValue[] getPropertyValues() {
        return propertyValueList.toArray(new PropertyValue[0]);
    }

    public PropertyValue getPropertyValue(String name) {
        for (PropertyValue propertyValue : propertyValueList) {
            if (propertyValue.getName().equals(name)) {
                return propertyValue;
            }
        }
        return null;
    }
}
