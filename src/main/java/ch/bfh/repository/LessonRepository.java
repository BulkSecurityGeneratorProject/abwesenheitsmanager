package ch.bfh.repository;

import ch.bfh.domain.Lesson;
import ch.bfh.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Data JPA repository for the Lesson entity.
 */
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByStudent(Student student);
}
