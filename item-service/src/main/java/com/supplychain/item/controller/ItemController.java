package com.supplychain.item.controller;

import com.supplychain.item.dto.CreateItemRequestDto;
import com.supplychain.item.entity.Item;
import com.supplychain.item.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item createItem(@Valid @RequestBody CreateItemRequestDto dto) {
        return itemService.createItem(dto);
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }
}