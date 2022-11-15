package com.murongyehua.tpplus.common;

import org.bukkit.Material;

public enum ENBaseType {
    IRON_BLOCK("1", Material.IRON_BLOCK),
    DIAMOND_BLOCK("2", Material.DIAMOND_BLOCK)
    ;

    private String key;

    private Material material;

    ENBaseType(String key, Material material) {
        this.key = key;
        this.material = material;
    }

    public static Material getMaterialByKey(String key) {
        for (ENBaseType baseType : ENBaseType.values()) {
            if (baseType.key.equals(key)) {
                return baseType.material;
            }
        }
        // 配置匹配不上时，默认是铁
        return IRON_BLOCK.material;
    }

}
