package info.steamworks.steamworks.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {

    @Query("SELECT s FROM AppUser s WHERE s.username = ?1")
    Optional<AppUser> findAppUserByUsername(String username);

}
