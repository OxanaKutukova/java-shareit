package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_Id(Long requestorId, Sort sort);

    @Query("SELECT ir FROM ItemRequest ir " +
            "JOIN ir.requestor user " +
            "WHERE user.id <> :id ")
    List<ItemRequest> findAllByNotRequestorId(@Param("id") Long userId, Pageable pageable);
}
