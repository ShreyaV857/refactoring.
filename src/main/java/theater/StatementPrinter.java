package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Creates a new StatementPrinter for the given invoice and play mapping.
     *
     * @param invoice the invoice containing performances
     * @param plays   a map of play IDs to Play objects
     */
    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Generates and returns a formatted statement for the invoice associated
     * with this printer. The statement includes the cost for each performance,
     * the total amount owed, and the volume credits earned.
     *
     * @return a formatted multi-line string representing the statement
     * @throws RuntimeException if a performance references a play type that is unknown
     */
    public String statement() {

        final StringBuilder result = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator()
        );

        // Loop 1 — build all lines
        for (Performance performance : invoice.getPerformances()) {
            result.append(String.format("  %s: %s (%s seats)%n",
                    getPlay(performance).getName(),
                    usd(getAmount(performance)),
                    performance.getAudience()));
        }

        // Loop 2 — use helper for total amount
        result.append(String.format("Amount owed is %s%n", usd(getTotalAmount())));

        // Loop 3 — use helper for total volume credits
        result.append(String.format("You earned %s credits%n", getTotalVolumeCredits()));

        return result.toString();
    }

    /**
     * Returns the play associated with the given performance.
     *
     * @param performance the performance
     * @return the corresponding Play object
     */
    private Play getPlay(Performance performance) {
        return plays.get(performance.getPlayID());
    }

    /**
     * Computes the amount owed for a given performance.
     *
     * @param performance the performance to evaluate
     * @return the calculated amount
     * @throws RuntimeException if the play type is unknown
     */
    private int getAmount(Performance performance) {
        int result;
        final Play play = getPlay(performance);

        switch (play.getType()) {
            case "tragedy":
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;

            default:
                throw new RuntimeException(
                        String.format("unknown type: %s", play.getType()));
        }
        return result;
    }

    /**
     * Computes the total amount owed for the invoice.
     *
     * @return the total amount in cents
     */
    private int getTotalAmount() {
        int result = 0;
        for (Performance p : invoice.getPerformances()) {
            result += getAmount(p);
        }
        return result;
    }

    /**
     * Computes the volume credits earned for a given performance.
     *
     * @param performance the performance
     * @return the number of volume credits earned
     */
    private int getVolumeCredits(Performance performance) {
        int result = 0;

        // base volume credit
        result += Math.max(performance.getAudience()
                - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);

        // bonus for comedy
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience()
                    / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }

        return result;
    }

    /**
     * Computes the total volume credits for the entire invoice.
     *
     * @return total volume credits
     */
    private int getTotalVolumeCredits() {
        int result = 0;
        for (Performance p : invoice.getPerformances()) {
            result += getVolumeCredits(p);
        }
        return result;
    }

    /**
     * Converts an integer amount of cents into a US dollar string.
     *
     * @param amount the raw amount in cents
     * @return the formatted amount in US dollars
     */
    private String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US)
                .format(amount / Constants.PERCENT_FACTOR);
    }
}

