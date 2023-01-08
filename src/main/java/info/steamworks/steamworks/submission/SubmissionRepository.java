package info.steamworks.steamworks.submission;

import info.steamworks.steamworks.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {

    @Query("SELECT s FROM Submission s WHERE s.id = ?1")
    Optional<Submission> findSubmissionById(int id);

}
