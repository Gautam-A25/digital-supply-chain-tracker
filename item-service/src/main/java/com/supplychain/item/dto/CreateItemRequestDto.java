package com.supplychain.item.dto;

import com.supplychain.item.enums.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateItemRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private ItemCategory category;

    @NotBlank
    private String sku;

    @NotBlank
    private String unit;

    public CreateItemRequestDto() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public String getSku() {
        return sku;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}