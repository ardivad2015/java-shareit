package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select ir from ItemRequest ir inner join fetch ir.author where ir.author.id = :userId " +
            "order by ir.created desc")
    List<ItemRequest> findAllByAuthorIdWithAuthorEagerly(@Param("userId") Long userId);
}
