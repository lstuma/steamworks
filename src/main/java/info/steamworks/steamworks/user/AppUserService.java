package info.steamworks.steamworks.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AppUserService {
    public final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository)
    {
        this.appUserRepository = appUserRepository;
    }

    public List<AppUser> getUsers()
    {
        return appUserRepository.findAll();
    }

    public void saveUser(AppUser user)
    {
        appUserRepository.save(user);
    }

    public boolean addUser(AppUser user)
    {
        // If user already exists, stop signup process
        Optional<AppUser> userOptional = appUserRepository.findAppUserByUsername(user.getUsername());
        if(userOptional.isPresent()) return false;
        // Otherwise just save user
        appUserRepository.save(user);
        return true;
    }

    public boolean loginValid(AppUser user)
    {
        // Check if user exists
        Optional<AppUser> userOptional = appUserRepository.findAppUserByUsername(user.getUsername());
        if(userOptional.isEmpty()) return false;
        // Check if password is wrong
        return Objects.equals(userOptional.get().getPassword(), user.getPassword());
    }

    public Optional<AppUser> getUser(String username)
    {
        return appUserRepository.findAppUserByUsername(username);
    }
}
