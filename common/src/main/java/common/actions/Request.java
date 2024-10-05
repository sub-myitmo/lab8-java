package common.actions;

import java.io.Serializable;

public class Request implements Serializable {
    private String name;
    private String stringArgument;
    private Serializable otherArguments;

    private User user;

    public Request(String name, String stringArgument, Serializable otherArguments, User user) {
        this.name = name;
        this.stringArgument = stringArgument;
        this.otherArguments = otherArguments;
        this.user = user;
    }
    public Request(String name, String stringArgument, User user) {
        this(name, stringArgument, null, user);
    }

    public Request(User user) {
        this("","", user);
    }
    public Request() {
        this("","", null);
    }


    /**
     * @return Command name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Command string argument.
     */
    public String getStringArgument() {
        return stringArgument;
    }

    /**
     *
     */
    public Object getOtherArguments() {
        return otherArguments;
    }

    /**
     *
     */
    public User getUser() {
        return user;
    }

    /**
     * @
     */
    public boolean isEmpty() {
        return name.isEmpty() && stringArgument.isEmpty() && otherArguments == null;
    }

    @Override
    public String toString() {
        return "Request[" + name + ", " + stringArgument + ", " + otherArguments + "]";
    }
}
