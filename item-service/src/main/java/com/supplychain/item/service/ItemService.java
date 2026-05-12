package com.supplychain.item.service;

import com.supplychain.item.dto.CreateItemRequestDto;
import com.supplychain.item.entity.Item;
import com.supplychain.item.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(CreateItemRequestDto dto) {

        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setCategory(dto.getCategory());
        item.setSku(dto.getSku());
        item.setUnit(dto.getUnit());
        item.setSupplierId(1L);
        item.setCreatedAt(LocalDateTime.now());

        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}