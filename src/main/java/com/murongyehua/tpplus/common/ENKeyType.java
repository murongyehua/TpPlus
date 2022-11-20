package com.murongyehua.tpplus.common;

import org.bukkit.Material;

public enum ENKeyType {
    LAPIS_LAZULI("1", Material.LAPIS_LAZULI),
    REDSTONE("2", Material.REDSTONE)
    ;

    private String key;

    private Material material;

    ENKeyType(String key, Material material) {
        this.key = key;
        this.material = material;
    }

    public static Material getMaterialByKey(String key) {
        for (ENKeyType baseType : ENKeyType.values()) {
            if (baseType.key.equals(key)) {
                return baseType.material;
            }
        }
        // 配置匹配不上时，默认是铁
        return LAPIS_LAZULI.material;
    }

}
