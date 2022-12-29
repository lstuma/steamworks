package info.steamworks.steamworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SteamworksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SteamworksApplication.class, args);

		System.out.println("Started server on http://localhost:8080");
	}

}
