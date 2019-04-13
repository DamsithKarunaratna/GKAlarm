package com.example.gkalarm.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample alarmTime for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AlarmData {

    /**
     * Array of items.
     */
    public static final List<AlarmItem> ITEMS = new ArrayList<AlarmItem>();

    /**
     * Map of items, by ID.
     */
    public static final Map<String, AlarmItem> ITEM_MAP = new HashMap<String, AlarmItem>();

    private static final int COUNT = 3;

    static {
        // 1555154100000L 04/13/2019 16:45:00
        // 1555154220000L 04/13/2019 16:47:00
        // 1555154400000L 04/13/2019 16:50:00

        // Add some sample items.
        addItem(new AlarmItem(1, "16:45",
                "alarm 1", 1555154100000L));
        addItem(new AlarmItem(2, "16:47",
                "alarm 2", 1555154220000L));
        addItem(new AlarmItem(3, "16:50",
                "alarm 3", 1555154400000L));
    }

    public static void addItem(AlarmItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.id), item);
    }

    /**
     * Class representing a single list item
     */
    public static class AlarmItem {
        public int id;
        public String alarmTime;
        public String alarmName;
        public long alarmTimeInMilis;

        public AlarmItem(int id, String alarmTime, String alarmName, long alarmTimeInMilis) {
            this.id = id;
            this.alarmTime = alarmTime;
            this.alarmName = alarmName;
            this.alarmTimeInMilis = alarmTimeInMilis;
        }

        @Override
        public String toString() {
            return alarmTime + " : " + alarmName;
        }
    }
}
