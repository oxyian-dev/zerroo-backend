package com.hionstudios;

public class NumberToWords {
    private final int number;

    public NumberToWords(int number) {
        this.number = number;
    }

    public static String toWords(int number) {
        return new NumberToWords(number).convert();
    }

    private static String[] units = {
            "",
            " One",
            " Two",
            " Three",
            " Four",
            " Five",
            " Six",
            " Seven",
            " Eight",
            " Nine"
    };
    private static String[] teen = {
            " Ten",
            " Eleven",
            " Twelve",
            " Thirteen",
            " Fourteen",
            " Fifteen",
            " Sixteen",
            " Seventeen",
            " Eighteen",
            " Nineteen"
    };
    private static String[] tens = {
            " Twenty",
            " Thirty",
            " Forty",
            " Fifty",
            " Sixty",
            " Seventy",
            " Eighty",
            " Ninety"
    };
    private static String[] maxs = {
            "",
            "",
            " Hundred",
            " Thousand",
            " Lakh",
            " Crore"
    };

    public String convert() {
        String input = numToString(number);
        String converted = "";
        int pos = 1;
        boolean hun = false;
        while (input.length() > 0) {
            if (pos == 1) {
                if (input.length() >= 2) {
                    String temp = input.substring(input.length() - 2, input.length());
                    input = input.substring(0, input.length() - 2);
                    converted += digits(temp);
                } else if (input.length() == 1) {
                    converted += digits(input);
                    input = "";
                }
                pos++;
            } else if (pos == 2) {
                String temp = input.substring(input.length() - 1, input.length());
                input = input.substring(0, input.length() - 1);
                if (converted.length() > 0 && digits(temp) != "") {
                    converted = (digits(temp) + maxs[pos] + " and") + converted;
                    hun = true;
                } else {
                    if (digits(temp) == "")
                        ;
                    else
                        converted = (digits(temp) + maxs[pos]) + converted;
                    hun = true;
                }
                pos++;
            } else if (pos > 2) {
                if (input.length() >= 2) {
                    String temp = input.substring(input.length() - 2, input.length());
                    input = input.substring(0, input.length() - 2);
                    if (!hun && converted.length() > 0)
                        converted = digits(temp) + maxs[pos] + " and" + converted;
                    else {
                        if (digits(temp) == "")
                            ;
                        else
                            converted = digits(temp) + maxs[pos] + converted;
                    }
                } else if (input.length() == 1) {
                    if (!hun && converted.length() > 0)
                        converted = digits(input) + maxs[pos] + " and" + converted;
                    else {
                        if (digits(input) == "")
                            ;
                        else
                            converted = digits(input) + maxs[pos] + converted;
                        input = "";
                    }
                }
                pos++;
            }
        }
        return converted.trim();
    }

    private String digits(String temp) {
        String converted = "";
        for (int i = temp.length() - 1; i >= 0; i--) {
            int ch = temp.charAt(i) - 48;
            if (i == 0 && ch > 1 && temp.length() > 1)
                converted = tens[ch - 2] + converted;
            else if (i == 0 && ch == 1 && temp.length() == 2) {
                int sum = 0;
                for (int j = 0; j < 2; j++)
                    sum = (sum * 10) + (temp.charAt(j) - 48);
                return teen[sum - 10];
            } else {
                if (ch > 0)
                    converted = units[ch] + converted;
            }
        }
        return converted;
    }

    private String numToString(int x) {
        String num = "";
        while (x != 0) {
            num = ((char) ((x % 10) + 48)) + num;
            x /= 10;
        }
        return num;
    }
}
