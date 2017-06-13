package com.example.prith.groupjam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prith on 3/14/2017.
 */

public class GenerateAccessCode {
    private static final char[] numbers = {'0','1', '2','3','4','5','6','7','8','9'};
    private static final char[] symbols = {'!', '@', '#', '$', '%', '&', '*', '?', '/', '=', '+', '-'};
    private static final char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * generates the password to use
     * @param numLetter the number of letters
     * @param numSymbols the number of symbols
     * @param numNumbers the number of numbers
     * @return the password as a string
     */
    public static String generate(int numLetter, int numSymbols, int numNumbers){
        List<String> options = new ArrayList<>();
        //list that stores all the characters we want to use

        String rand;
        //add letters
        for (int i = 0; i < numLetter; i++) {
            rand = "" + letters[(int)(Math.random()*letters.length)];
            //randomly decide if we want an uppercase or lowercase
            if((int)(Math.random()*2) == 1){
                options.add(rand.toUpperCase());
            }
            else{
                options.add(rand);
            }
        }
        //now add numbers
        for (int i = 0; i < numNumbers; i++) {
            rand = "" + numbers[(int)(Math.random()*numbers.length)];
            options.add(rand);
        }
        for (int i = 0; i < numSymbols; i++) {
            rand = "" + symbols[(int)(Math.random()*symbols.length)];
            options.add(rand);
        }

        return scramble(options);
    }

    /**
     * scrambles the list of characters
     * @param options the list to scramble
     * @return a string of the scrambled characters
     */
    public static String scramble(List<String> options){
        String password = "";
        while(options.size() > 0){
            int randIndex = (int)(Math.random()*options.size());
            password += options.get(randIndex);
            options.remove(randIndex);
        }
        return password;
    }
}
