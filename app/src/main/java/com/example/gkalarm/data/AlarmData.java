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
     * Array of items to be stored in memory.
     */
    public static final List<AlarmItem> ITEMS = new ArrayList<AlarmItem>();

    public static void addItem(AlarmItem item) {
        ITEMS.add(item);
    }

    public static void addAll(ArrayList<AlarmItem> sourceList) {
        if(sourceList != null) {
            ITEMS.addAll(sourceList);
        }
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
    }
}
