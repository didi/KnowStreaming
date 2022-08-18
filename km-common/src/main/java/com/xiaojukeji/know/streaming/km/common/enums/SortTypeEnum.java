package com.xiaojukeji.know.streaming.km.common.enums;

public enum SortTypeEnum {
    ASC("asc"),

    DESC("desc"),

        ;

    private final String sortType;

    SortTypeEnum(String sortType) {
        this.sortType = sortType;
    }

    public String getSortType() {
        return sortType;
    }

    public static SortTypeEnum getByTypeName(String typeName) {
        for (SortTypeEnum typeEnum: SortTypeEnum.values()) {
            if (typeName.equals(typeEnum.getSortType())) {
                return typeEnum;
            }
        }

        return null;
    }
}
