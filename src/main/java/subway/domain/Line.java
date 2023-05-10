package subway.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;

    private final LinkedList<Station> stations;
    private final Map<Map.Entry<Station, Station>, Integer> distances = new HashMap<>();

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = new LinkedList<>();
    }

    public void insertBoth(Station top, Station bottom, int distance) {
        validateEmptyStations();

        stations.add(top);
        stations.add(bottom);

        insertDistanceBetween(top, bottom, distance);
    }

    private void validateEmptyStations() {
        if (!stations.isEmpty()) {
            throw new IllegalStateException("빈 노선에만 한 번에 두 역을 추가할 수 있습니다");
        }
    }

    public void insertUpper(Station station, Station base, int distance) {

        stations.add(stations.indexOf(base), station);
        insertDistanceBetween(station, base, distance);

        if (isTop(station)) {
            return;
        }

        int previousDistance = getDistanceBetween(getUpperOf(station), base);
        int upperDistance = Math.abs(previousDistance - distance);
        insertDistanceBetween(station, getUpperOf(station), upperDistance);

        deleteDistanceBetween(getUpperOf(station), base);
    }

    public void insertLower(Station station, Station base, int distance) {
        stations.add(stations.indexOf(base) + 1, station);
        insertDistanceBetween(station, base, distance);

        if (isBottom(station)) {
            return;
        }

        int previousDistance = getDistanceBetween(getLowerOf(station), base);
        int lowerDistance = Math.abs(previousDistance - distance);
        insertDistanceBetween(station, getLowerOf(station), lowerDistance);
        deleteDistanceBetween(getLowerOf(station), base);
    }

    public int getDistanceBetween(Station from, Station to) {
        if (distances.get(Map.entry(from, to)) != null) {
            return distances.get(Map.entry(from, to));
        }

        int distance = 0;
        int startInclusive = Math.min(stations.indexOf(from), stations.indexOf(to));
        int endInclusive = Math.max(stations.indexOf(from), stations.indexOf(to));

        for (int i = startInclusive; i < endInclusive; i++) {
            Station station = stations.get(i);
            Station other = stations.get(i + 1);
            distance += distances.get(Map.entry(station, other));
        }

        return distance;
    }

    private void insertDistanceBetween(Station station, Station other, int distance) {
        distances.put(Map.entry(station, other), distance);
        distances.put(Map.entry(other, station), distance);
    }

    private void deleteDistanceBetween(Station station, Station other) {
        distances.remove(Map.entry(station, other));
        distances.remove(Map.entry(other, station));
    }

    private boolean isTop(Station station) {
        return stations.getFirst().equals(station);
    }

    private boolean isBottom(Station station) {
        return stations.getLast().equals(station);
    }

    private Station getUpperOf(Station station) {
        int index = stations.indexOf(station);
        return stations.get(index - 1);
    }

    private Station getLowerOf(Station station) {
        int index = stations.indexOf(station);
        return stations.get(index + 1);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Line line = (Line)o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
