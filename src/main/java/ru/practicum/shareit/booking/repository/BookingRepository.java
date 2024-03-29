package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingState status, Pageable pageable);

    List<Booking> findAllByItem_Owner_Id(Long ownerId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long bookerId, BookingState status, Pageable pageable);

    List<Booking> findAllByBooker_Id_AndItem_Id_AndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end, Sort sort);

    Booking findFirstByItem_Owner_IdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end, Sort sort);

    Booking findFirstByItem_Owner_IdAndItemIdAndStartIsAfter(Long bookerId, Long itemId, LocalDateTime start, Sort sort);

}
