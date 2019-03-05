package com.mirceadan.stacker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Sha1Class {



    public Sha1Class(){}

    public String sha1Algo(String message){

        String[] hArr = initRands();

        String chunkString = chunkMessage(message);
        //TODO this should be more general
        ArrayList<String> chunks = new ArrayList<>();

        chunks.add(chunkString);

        return processChunks(chunks, hArr);


    }

    private String processWord(String w1, String w2, String w3, String w4){
        return shiftString(xorBitString(xorBitString(xorBitString(w1,w2), w3), w4), 1);
    }

    private String processChunks(ArrayList<String> chunks, String[] hArr) {

        for(String chunk:chunks){
            ArrayList<String> words = splitString(chunk, 32);

            for(int i = 16; i < 79; i++){
                words.add(processWord(words.get(i-3), words.get(i-8), words.get(i-14), words.get(i-16)));
            }

            String A = hArr[0];
            String B = hArr[1];
            String C = hArr[2];
            String D = hArr[3];
            String E = hArr[4];

            int contor = 0;

            while(contor < 79){

                String temp = getTemp(words, A, B, C, D, E, contor);
                E = D;
                D = C;
                C = shiftString(B, 30);
                B = A;
                A = temp;

                contor ++;
            }

            hArr[0] = binaryAddition(hArr[0], A);
            hArr[1] = binaryAddition(hArr[1], B);
            hArr[2] = binaryAddition(hArr[2], C);
            hArr[3] = binaryAddition(hArr[3], D);
            hArr[4] = binaryAddition(hArr[4], E);
        }

        return binToHex(hArr[0] + hArr[1] + hArr[2] + hArr[3] + hArr[4]);
    }

    private String binToHex(String str){

        ArrayList<String> hexString = splitString(str, 4);
        String hexMessage = "";

        for (String s: hexString){
            int decimal = Integer.parseInt(s,2);
            hexMessage += Integer.toString(decimal, 16);
        }

        return hexMessage;
    }

    private String getTemp(ArrayList<String> words, String a, String b, String c, String d, String e, int contor) {
        return binaryAddition(binaryAddition(binaryAddition(shiftString(a, 5), f(contor, b, c, d)), e), binaryAddition(words.get(contor), K(contor)));
    }

    private String K(int contor) {

        if(contor <= 19) return hexToBits("5A827999");
        if(contor <= 39) return hexToBits("6ED9EBA1");
        if(contor <= 59) return hexToBits("8F1BBCDC");
        if(contor <= 79) return hexToBits("CA62C1D6");

        return "";
    }

    private String f(int contor, String B, String C, String D) {

        if(contor <= 19) return binaryOr(binaryAnd(B, C), binaryAnd(binaryNot(B), D));
        if(contor <= 39) return xorBitString(xorBitString(B, C), D);
        if(contor <= 59) return binaryOr(binaryOr(binaryAnd(B, C), binaryAnd(B, D)), binaryAnd(C, D));
        if(contor <= 79) return xorBitString(xorBitString(B, C), D);

        return "";
    }

    private String binaryAnd(String str1, String str2){

        String tempStr = "";

        for(int i = 0; i < str1.length(); i++){
            if(str1.charAt(i) == '1' && str2.charAt(i) == '1')
                tempStr += '1';
            else
                tempStr += '0';
        }

        return tempStr;
    }

    private String binaryNot(String str1){

        String tempStr = "";

        for(char c:str1.toCharArray()){
            if(c == '1')
                tempStr += '0';
            if(c == '0')
                tempStr += '1';
        }

        return tempStr;
    }

    private String binaryOr(String str1, String str2){


        String tempStr = "";

        for(int i = 0; i < str1.length(); i++){
            if(str1.charAt(i) == '1' || str2.charAt(i) == '1')
                tempStr += '1';
            else
                tempStr += '0';
        }
        return tempStr;
    }

    public String binaryAddition(String a, String b) {

        String result = "";

        int s = 0;

        int i = a.length() - 1, j = b.length() - 1;
        while (i >= 0 || j >= 0 || s == 1)
        {

            s += ((i >= 0)? a.charAt(i) - '0': 0);
            s += ((j >= 0)? b.charAt(j) - '0': 0);

            result = (char)(s % 2 + '0') + result;

            s /= 2;

            i--; j--;
        }

        if(result.length() > 32)
            result = result.substring(1);

        return result;
    }


    private String shiftString(String str, int nShifts){
        return str.substring(nShifts, str.length()) + str.substring(0, nShifts);
    }

    private String xorBitString (String firstStr, String secondStr){
        /**
         * XOR two bit strings
         */
        String newStr = "";

        for(int i=0; i < firstStr.length(); i++){
            if(firstStr.charAt(i) == secondStr.charAt(i))
                newStr += "0";
            else
                newStr += "1";
        }

        return newStr;

    }

    private ArrayList<String> splitString(String str, int len){

        return new ArrayList<String>(Arrays.asList(str.split("(?<=\\G.{" + len + "})")));

    }

    private String chunkMessage(String message) {

        String bitString = "";

        for(char c:message.toCharArray()){
            String ascii = Integer.toBinaryString(c - '0');

            while(ascii.length() < 8){
                ascii = "0" + ascii;
            }

            bitString += ascii;
        }

        boolean isOne = false;

        while (bitString.length() < 448){

            if(!isOne){
                bitString += "1";
                isOne = true;

            }else{
                bitString += "0";

            }
        }

        String messLength = Integer.toBinaryString(message.length() * 8);
        String messLenPad = "";
        for(int i = 0; i < 64 - messLength.length(); i++){

            messLenPad += "0";
        }
        messLength = messLenPad + messLength;

        return bitString + messLength;
    }

    private String[] initRands() {

        String[] tempHarr = new String[5];

        tempHarr[0] = hexToBits(getRandomHexString(8));
        tempHarr[1] = hexToBits(getRandomHexString(8));
        tempHarr[2] = hexToBits(getRandomHexString(8));
        tempHarr[3] = hexToBits(getRandomHexString(8));
        tempHarr[4] = hexToBits(getRandomHexString(8));

        return tempHarr;
    }

    private String getRandomHexString(int numchars){

        Random r = new Random();
        StringBuffer sb = new StringBuffer();

        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

    private String hexToBits(String str) {
        /**
         * converts every character into groups of 4 bits
         */
        ArrayList<String> messList = new ArrayList<>();
        for(char c: str.toCharArray()){
            messList.add(Integer.toBinaryString(Integer.parseInt(String.valueOf(c), 16)));
        }


        StringBuilder bytes = new StringBuilder();
        for(String s: messList){
            while(s.length() < 4){
                s = "0" + s;
            }

            bytes.append(s);
        }


        return bytes.toString();

    }
}
