package info.steamworks.steamworks;

import info.steamworks.steamworks.submission.ImageSubmission;
import info.steamworks.steamworks.submission.Submission;
import info.steamworks.steamworks.submission.SubmissionRepository;
import info.steamworks.steamworks.submission.SubmissionService;
import info.steamworks.steamworks.user.AppUser;
import info.steamworks.steamworks.user.AppUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class SteamworksController {

    private final String[][] standard_formatting;
    public final AppUserService appUserService;
    public final SubmissionService submissionService;
    private ArrayList<String> loginSessions;
    private int submissionCount;

    Random rand;

    @Autowired
    public SteamworksController(AppUserService appUserService, SubmissionService submissionService) throws IOException
    {
        this.standard_formatting = new String[][]{{"navbar_login_state", page("standard\\navbar_logged_out.html")},
                {"navbar", page("standard\\navbar.html")},
                {"fixed_footer", page("standard\\fixed_footer.html")},
                {"absolute_footer", page("standard\\absolute_footer.html")},
                {"head", page("standard\\head.html")},
                {"alert", page("standard\\alert.html")},
                {"submission", page("standard\\submission.html")}};
        this.loginSessions = new ArrayList<String>();
        this.rand = new Random();
        this.submissionCount = 1;
        this.appUserService = appUserService;
        this.submissionService = submissionService;

        // Create first submission so site doesn't crash lol
        Submission submission = new Submission();
        submissionService.saveSubmission(submission);
    }

    public String page(String path) throws IOException {
        return Files.readString(Paths.get("PATH\\templates\\"+path));
    }

    // Format page using params and standard formatting
    public ResponseEntity format(String response, String[][] params, int steps){
        while(--steps>=0) {
            // Formatting with params
            for (String[] param : params)
                response = response.replace(("$" + param[0]), param[1]);
            // Standard formatting
            for (String[] param : this.standard_formatting)
                response = response.replace(("$" + param[0]), param[1]);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // Only use standard formatting
    public ResponseEntity format(String response, Optional<String> loginId) throws IOException {
        String[][] params = {{"alert", ""}};
        return format(response, params, 2, loginId);
    }
    public ResponseEntity format(String response, String[][] params, int steps, Optional<String> loginId) throws IOException {
        // If there is no loginId provided, use standard formatting
        if(loginId.isEmpty() || !verifyLoginSession(loginId.get())) return format(response, params, steps);
        // Add extra params
        String[][] newParams = new String[params.length+2][2];
        System.arraycopy(params, 0, newParams, 0, params.length);
        newParams[params.length] = new String[]{"navbar_login_state", page("standard/navbar_logged_in.html")};
        newParams[params.length+1] = new String[]{"username", getUserFromLoginSession(loginId.get()).get().getUsername()};
        // Formatting
        return format(response, newParams, steps);
    }
    // Only use standard formatting
    public ResponseEntity format(String response){
        String[][] params = {{"alert", ""}};
        return format(response, params, 2);
    }

    public String generateLoginSession(String username)
    {
        // Generate session id
        String sessionId = "";
        for(int i = 0; i < 40; i++)
            sessionId += "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()[rand.nextInt(62)];
        String session = sessionId + "=" + username;
        loginSessions.add(session);
        return session;
    }
    public Cookie generateLoginSessionCookie(String username)
    {
        String session = generateLoginSession(username);
        Cookie cookie = new Cookie("loginId", session);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
    public boolean verifyLoginSession(String session)
    {
        return loginSessions.contains(session);
    }
    public Optional<String> getLoginSession(Cookie[] cookies)
    {
        if (cookies != null) {
            for(Cookie cookie: cookies)
                if(cookie.getName().equals("loginId")) return Optional.of(cookie.getValue());
        }
        return Optional.empty();
    }
    public Optional<AppUser> getUserFromLoginSession(String session)
    {
        Optional<AppUser> user = appUserService.getUser(session.substring(session.indexOf('=')+1));
        return user;
    }
    public void delSession(String session)
    {
        while(loginSessions.remove(session));
    }

    @GetMapping("/")
    public ResponseEntity index(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // Check if any params are provided otherwise provide them
        if(request.getParameterMap().isEmpty())
            return format(page("redirect.html"), new String[][]{{"link", "/?page="+this.submissionCount}}, 1);

        // Get page
        int page = Integer.parseInt(request.getParameter("page"));

        // Check for cookies and return page
        Optional<String> loginId = getLoginSession(request.getCookies());

        if(page < 1 || page > this.submissionCount) return format(page("error.html"), new String[][]{{"reason", "This page does not exist."}}, 2, loginId);

        // Get submission body, title, and username of poster
        Optional<Submission> submission = submissionService.getSubmission(page);
        System.out.println("INDEX.HTML>" + submission);
        String title = submission.get().getTitle();
        String body = submission.get().getBody();
        String postername = submission.get().getUsername();
        return format(page("index.html"), new String[][]{{"title", title}, {"body", body}, {"postername", "u/"+postername}, {"prev", "page="+(page<this.submissionCount?page+1:this.submissionCount)}, {"next", "page="+(page>1?String.valueOf(page-1):1)}, {"page", String.valueOf(page)}}, 2, loginId);
    }

    @GetMapping("/write")
    public ResponseEntity write(HttpServletRequest request) throws IOException
    {
        Optional<String> loginId = getLoginSession(request.getCookies());
        if(loginId.isPresent() && verifyLoginSession(loginId.get()))
            return format(page("write.html"), loginId);
        return format(page("login.html"));
    }
    @PostMapping("/write")
    public ResponseEntity write(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get submission title and body
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String img_src = request.getParameter("img_src");
        // Make sure submission title and body aren't empty
        if(title.isEmpty() || title.isBlank() || body.isEmpty() || body.isBlank())
            return format(page("write.html"), new String[][]{{"reason", "Post title or body cannot be left empty!"}}, 2);
        // Find loginId in cookies
        Optional<String> loginId = getLoginSession(request.getCookies());
        // Check validity otherwise redirect to login page
        if(loginId.isPresent() && verifyLoginSession(loginId.get()))
        {
            // Create submission
            Submission submission = img_src.isEmpty() || img_src.isBlank()?
                    new Submission(getUserFromLoginSession(loginId.get()).get().getUsername(), title, body):
                    new ImageSubmission(getUserFromLoginSession(loginId.get()).get().getUsername(), title, body, img_src);

            // Save submission to database
            boolean post_saved = submissionService.addSubmission(submission);
            // Return your post is live page if saving to database worked
            if (post_saved) {
                this.submissionCount++;
                System.out.println("Successfully saved post " + submission);
                return format(page("your_post_is_live.html"), loginId);
            } else
                format(page("write.html"), new String[][]{{"reason", "An unknown exception occurred while trying to publish your post!"}}, 2);
        }
        // Return redirect to homepage
        return format(page("redirect.html"), new String[][]{{"link", "/login"}}, 1);
    }

    @GetMapping("/login")
    public ResponseEntity login() throws IOException {
        return format(page("login.html"));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody String body, HttpServletResponse response) throws IOException {
        // Get params
        String[] params = body.split("&");
        for(int i = 0; i < 2; i++) params[i] = params[i].substring(params[i].indexOf("=")+1);

        // Check if user credentials are correct
        AppUser user = new AppUser(params[0], params[1]);
        if(!appUserService.loginValid(user)) return illegal_login("Incorrect username or password!");

        // Add and generate loginId cookie
        response.addCookie(generateLoginSessionCookie(params[0]));
        // Return redirect to homepage
        return format(page("redirect.html"), new String[][]{{"link", "/"}}, 1);
    }
    private ResponseEntity illegal_login(String reason) throws IOException {
        return format(page("login.html"), new String[][]{{"reason", reason}}, 2);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Find loginId in cookies
        Optional<String> loginId = getLoginSession(request.getCookies());
        // Remove loginId cookie if present
        if(loginId.isPresent())
        {
            Cookie cookie = new Cookie("loginId", loginId.get());
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            // Set MaxAge to 0 in order to remove cookie
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        // Return redirect to homepage
        return format(page("redirect.html"), new String[][]{{"link", "/"}}, 1);
    }

    @GetMapping("/signup")
    public ResponseEntity signup() throws IOException {
        return format(page("signup.html"));
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody String body) throws IOException {
        // Get paramas
        String[] params = body.split("&");
        for(int i = 0; i < 2; i++) params[i] = (params[i].substring(params[i].indexOf("=")+1));

        // Input validation
        for(String param: params)
        {
            if(param.isBlank() || param.isEmpty()) return illegal_signup("Empty username or password!");
            for(String c: new String[]{"<",">","!","_","$","%","&","/","\\","\"", "'","?","|"," ", "+"})
                if(param.contains(c)) return illegal_signup("Illegal characters in username or password!");
        }
        // Check if there even is input
        // Create user
        if(appUserService.addUser(new AppUser(params[0], params[1]))) return format(page("redirect.html"), new String[][]{{"link", "/login"}}, 1);
        else return illegal_signup("This user already exists!");
    }
    private ResponseEntity illegal_signup(String reason) throws IOException {
        return format(page("signup.html"), new String[][]{{"reason", reason}}, 2);
    }

    @GetMapping("/api/v1/user")
    public List<AppUser> getUsers()
    {
        return appUserService.getUsers();
    }

    @GetMapping("/error")
    public ResponseEntity error() throws IOException {
        return format(page("error.html"));
    }

}
