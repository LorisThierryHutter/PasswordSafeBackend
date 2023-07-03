package ch.bbw.m183passwordmanagerbackend.service;


import ch.bbw.m183passwordmanagerbackend.model.Entry;
import ch.bbw.m183passwordmanagerbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    boolean existsByUrlAndUserEmail(String url, String email);

    @Query("SELECT COUNT(e) FROM Entry e WHERE e.user = :user")
    int getEntryCountByUser(@Param("user") User user);

}
