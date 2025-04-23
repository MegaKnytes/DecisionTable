package com.decisiontable.ftc.core.utils;

public class ConfigurationException extends RuntimeException{

    public ConfigurationException(){
        throw new RuntimeException();
    }

    public ConfigurationException(String string){
        throw new RuntimeException(string);
    }
}
