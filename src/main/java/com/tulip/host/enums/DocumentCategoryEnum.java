package com.tulip.host.enums;

public enum DocumentCategoryEnum {
    ACADEMIC("Academic"),
    UPLOAD_TEMPLATE("Template"),
    EXAMINATION("Examination Cell");

    private final String category;

    DocumentCategoryEnum(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
