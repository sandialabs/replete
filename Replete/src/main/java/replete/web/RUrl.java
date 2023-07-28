package replete.web;

public class RUrl {


    ////////////
    // FIELDS //
    ////////////

    private String protocol;
    private String user;
    private String password;
    private String host;
    private String port;
    private String path;
    private String query;
    private String fragment;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getProtocol() {
        return protocol;
    }
    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }
    public String getHost() {
        return host;
    }
    public String getPort() {
        return port;
    }
    public String getPath() {
        return path;
    }
    public String getQuery() {
        return query;
    }
    public String getFragment() {
        return fragment;
    }

    // Accessors (Computed)

    public String getUserPassword() {             // Returns "" instead of null because it's a composite method
        return
            (user != null || password != null ?
                ((user != null ? user : "") + ":" + (password != null ? password : "")) + "@" : "")
        ;
    }

    public String getHostPort() {                 // Returns "" instead of null because it's a composite method
        return
            host +
            (port != null ? ":" + port : "")
        ;
    }

    public String getAfterHostPort() {            // Returns "" instead of null because it's a composite method
        return
            (path != null ? "/" + path : "") +
            (query != null ? "?" + query : "") +
            (fragment != null ? "#" +fragment : "")
        ;
    }

    // Mutators

    public RUrl setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }
    public RUrl setUser(String user) {
        this.user = user;
        return this;
    }
    public RUrl setPassword(String password) {
        this.password = password;
        return this;
    }
    public RUrl setHost(String host) {
        this.host = host;
        return this;
    }
    public RUrl setPort(String port) {
        this.port = port;
        return this;
    }
    public RUrl setPath(String path) {
        this.path = path;
        return this;
    }
    public RUrl setQuery(String query) {
        this.query = query;
        return this;
    }
    public RUrl setFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }


    //////////
    // MISC //
    //////////

    public String toString2() {
        return
            part("protocol", protocol) + "\n" +
            part("user",     user)     + "\n" +
            part("password", password) + "\n" +
            part("host",     host)     + "\n" +
            part("port",     port)     + "\n" +
            part("path",     path)     + "\n" +
            part("query",    query)    + "\n" +
            part("fragment", fragment)
        ;
    }

    private String part(String name, String value) {
        return name + "=" + (value != null ? "[" + value + "]" : "");
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return
            protocol + "://" +
            getUserPassword() +
            getHostPort() +
            getAfterHostPort();
    }
}
