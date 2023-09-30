package com.example.test_01;

public class XML {
    public static String createXML(String username, String university, String major){
        String returnXML="";

        returnXML = "<?xml version \"1.0\"?>\n"+
                "<StudentCertificate>\n"+
                "\t<NAME> "+username+" </Name>\n"+
                "\t<UNIVERSITY> "+university+" </UNIVERSITY>\n"+
                "\t<MAJOR> "+major+" </MAJOR>\n"+
                "</StudentCertificate>";
        return returnXML;
    }
}