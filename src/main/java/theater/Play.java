package theater;

/**
 * Represents a play that can be performed in a theater.
 * A play has a name and a type (e.g., tragedy or comedy).
 */
public class Play {

    private final String name;
    private final String type;

    /**
     * Creates a new Play with the given name and type.
     *
     * @param name the title of the play
     * @param type the category or genre of the play
     */
    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of this play.
     *
     * @return the play's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this play.
     *
     * @return the play's type
     */
    public String getType() {
        return type;
    }
} // Done
