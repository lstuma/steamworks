package info.steamworks.steamworks.user;

import jakarta.persistence.*;

@Entity
@Table
public class AppUser {
    @Id
    private String username;
    private String password;

    public AppUser()
    {
        this("check", "check");
    }

    public AppUser(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return this.password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
