package com.vecoo.extralib.world;

public class UtilBiome {
    public static String getBiomeName(String biomeId) {
        String[] split = biomeId.split(":");
        StringBuilder name = new StringBuilder();

        if (split[1].contains("_")) {
            String[] fullName = split[1].split("_");
            for (String s : fullName) {
                String word = s.substring(0, 1).toUpperCase() + s.substring(1);
                name.append(word).append(" ");
            }

            name = new StringBuilder(name.substring(0, name.length() - 1));
        } else {
            name = new StringBuilder(split[1].substring(0, 1).toUpperCase() + split[1].substring(1));
        }
        return name.toString();
    }
}
