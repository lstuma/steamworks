package info.steamworks.steamworks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class SteamworksController {

    private final String[][] standard_formatting;

    public SteamworksController() throws IOException
    {
        this.standard_formatting = new String[][]{{"navbar", page("standard\\navbar.html")},
                {"fixed_footer", page("standard\\fixed_footer.html")},
                {"absolute_footer", page("standard\\absolute_footer.html")},
                {"head", page("standard\\head.html")}};
    }

    public String page(String path) throws IOException {
        return Files.readString(Paths.get("C:\\Users\\lukas\\OneDrive\\Desktop\\steamworks\\src\\main\\resources\\templates\\"+path));
    }

    // Format page using params and standard formatting
    public String format(String response, String[][] params)
    {
        // Formatting with params
        for(String[] param : params)
            response = response.replace(("$" + param[0]), param[1]);
        // Standard formatting
        for(String[] param : this.standard_formatting)
            response = response.replace(("$" + param[0]), param[1]);
        return response;
    }
    // Only use standard formatting
    public String format(String response)
    {
        String[][] params = {{"checkparam", "noparam"}};
        return format(response, params);
    }

    @GetMapping("/")
    public String index() throws IOException {
        return format(page("index.html"));
    }
}
