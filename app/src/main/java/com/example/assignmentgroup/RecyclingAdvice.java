package com.example.assignmentgroup;

import java.util.Locale;

// TODO: replace mockCentresFor() with a real Places/Maps API lookup once location integration is ready.
public class RecyclingAdvice {

    public static String tipsFor(String category) {
        String key = category == null ? "" : category.toLowerCase(Locale.ROOT);

        if (key.contains("plastic")) {
            return "Refuse single-use plastics where possible. Rinse and dry before recycling. " +
                    "Most plastics can be reused as storage containers before being recycled.";
        }
        if (key.contains("paper")) {
            return "Reduce paper use by going digital. Flatten and keep dry for recycling. " +
                    "Clean paper/cardboard can often be reused for packing or crafts first.";
        }
        if (key.contains("glass")) {
            return "Glass can be reused indefinitely if unbroken - consider it for storage. " +
                    "Otherwise recycle at a glass collection point; keep separate from other waste.";
        }
        if (key.contains("metal")) {
            return "Metal is highly recyclable and retains value - recycle rather than bin it. " +
                    "Rinse food cans before dropping them off.";
        }
        if (key.contains("e-waste") || key.contains("electronic")) {
            return "Never bin e-waste - it contains recoverable materials and hazardous parts. " +
                    "Take it to a certified e-waste collection/recovery point.";
        }
        if (key.contains("organic") || key.contains("food")) {
            return "Reduce food waste by planning portions. Compost organic scraps where possible " +
                    "to recover nutrients instead of sending them to landfill.";
        }
        if (key.contains("textile")) {
            return "Reuse or donate wearable textiles first. Damaged fabric can often be repurposed " +
                    "as cleaning rags before being recycled.";
        }
        return "Refuse what you don't need, reduce consumption, and reuse before you recycle. " +
                "Check with your local council for the correct disposal channel.";
    }

    public static String[] mockCentresFor(String category) {
        return new String[]{
                "Greenline Recycling Hub - 1.2 km away",
                "EcoPoint Collection Centre - 2.4 km away",
                "City Council Recycling Depot - 3.7 km away"
        };
    }
}
