package bg.uni.sofia.fmi.mjt.sentiment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseInSensitiveMap {

    private Map<String, Map.Entry<String, Map.Entry<Integer, Double>>> caseInSensitiveMap;

    public CaseInSensitiveMap() {
        this.caseInSensitiveMap = new HashMap<>();
    }

    public boolean containsKey(String key) {
        return key == null ? false : caseInSensitiveMap.containsKey(key.toLowerCase());
    }

    public void put(String key, Map.Entry<Integer, Double> value) {
        caseInSensitiveMap.put(key.toLowerCase(), Map.entry(key, value));
    }

    public Map.Entry<Integer, Double> get(String key) {
        String toLower = key == null ? null : key.toLowerCase();
        return !caseInSensitiveMap.containsKey(toLower) ? null : caseInSensitiveMap.get(toLower).getValue();
    }

    public Set<Map.Entry<String, Map.Entry<Integer, Double>>> entrySet() {
        return caseInSensitiveMap.entrySet()
                                 .stream()
                                 .map(Map.Entry::getValue)
                                 .collect(Collectors.toSet());
    }

    public int size() {
        return caseInSensitiveMap.size();
    }

    @Override
    public String toString() {
        return caseInSensitiveMap.values().toString();
    }
}
