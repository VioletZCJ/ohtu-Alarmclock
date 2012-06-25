package ohtu.beddit.api.jsonclassimpl;

/**
 * Encapsulates beddit user data as object.
 */
public class UserData {

    /**
     * List of users on this beddit account.
     */
    private User[] users;

    public String getUsername() throws InvalidJsonException {
        if(users[0].getUsername() == null)
            throw new InvalidJsonException("beddit says null");
        return users[0].getUsername();
    }

    public String getFirstName() throws InvalidJsonException {
        if(users[0].getUsername() == null)
            throw new InvalidJsonException("beddit says null");
        return users[0].getFirst_name();
    }

    public String getLastName() throws InvalidJsonException {
        if(users[0].getUsername() == null)
            throw new InvalidJsonException("beddit says null");
        return users[0].getLast_name();
    }


}
