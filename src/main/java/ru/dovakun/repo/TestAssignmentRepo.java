package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dovakun.data.entity.TestAssignment;

import java.util.List;

public interface TestAssignmentRepo extends JpaRepository<TestAssignment, Long> {

    @Query("select t from TestAssignment t where t.user.id = :userId")
    List<TestAssignment> findAllByUser(Long userId);

    TestAssignment findByUniqueLink(String uniqueLink);
}
