package eimock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
    public static List<Integer> deriveChildren(String childrenVarChar) {
        if (("null").equalsIgnoreCase(childrenVarChar)) {
            return Collections.emptyList();
        } else {
            String splitElements = childrenVarChar.substring(1, childrenVarChar.length() - 1);
            String[] children = splitElements.split(",");
            List<Integer> childrenIntegers = new ArrayList<>();
            for (String child : children) {
                childrenIntegers.add(Integer.parseInt(child.trim()));
            }
            return childrenIntegers;
        }
    }
}
