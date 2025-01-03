package com.MegaKnytes.DecisionTable.utils;

public class ConfigurationException extends RuntimeException{

    public ConfigurationException(){
        throw new RuntimeException();
    }

    public ConfigurationException(String string){
        throw new RuntimeException(string);
    }
}
