package org.nuim.cyclone.model;

public abstract class Element {
    private String name;

    protected Element(String name){this.name=name;}
    public String name(){return this.name;}
    
}