package com.accounted4.assetmanager.finance.loan;

/*
 * If we want to create a batch of cheques, try to automatically increment the number
 * within the reference field as well, if possible.
 *
 * About the regex:
 *     (?<=\D) is a look-behind assertion of a group of non-digits
 *     (?=\d) is a look-ahead ahead assertion of a group of digits
 *     So if we have a group of non digits behind us and a group of digits ahead, this is a break point.
 *     OR
 *     Symmetrically, if we hava group of digits behind us and a group of non-digits ahead: break point.
 * See: https://autohotkey.com/docs/misc/RegEx-QuickRef.htm about assertions.
 */

/**
 * A Utility for "incrementing" the last numeric portion of a reference string.
 * For example, the string: "TD 123 ABC 00050 X"
 * could generate the sequence: "TD 123 ABC 00050 X", "TD 123 ABC 00051 X", "TD 123 ABC 00052 X"
 * @author gheinze
 */
class ReferenceIncrementer {

    private final String reference;
    private boolean foundNumericInReference = false;
    private long numericBase;
    private String formatString;
    private String prefix = "";
    private String suffix = "";
    private long incrementCounter = 0;

    public ReferenceIncrementer(String reference) {
        this.reference = null == reference ? "" : reference;
        String[] split = this.reference.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        for (int splitIndex = split.length - 1; splitIndex >= 0; splitIndex--) {
            try {

                numericBase = Long.parseLong(split[splitIndex]);

                foundNumericInReference = true;
                int minNumericFieldLength = split[splitIndex].length();
                formatString = "%s%0" + minNumericFieldLength + "d%s";

                for (int j = 0; j < splitIndex; j++) {
                    prefix += split[j];
                }

                for (int k = splitIndex + 1; k < split.length; k++) {
                    suffix += split[k];
                }

                break;
            } catch (NumberFormatException nfe) {
            }
        }
    }

    public String getNext() {
        return foundNumericInReference ? getNextAndIncrement() : reference;
    }

    private String getNextAndIncrement() {
        long seq = numericBase + incrementCounter++;
        return String.format(formatString, prefix, seq, suffix);
    }

}
