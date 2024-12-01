package ru.practicum.shareit.itemrequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getByAuthor(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemRequestClient.getByAuthor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        return itemRequestClient.getAll();
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Object> getById(@PathVariable("id") @Positive Long itemRequestId) {
        return itemRequestClient.getById(itemRequestId);
    }

}
