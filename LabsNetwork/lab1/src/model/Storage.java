package model;

import java.util.*;

public class Storage {

    private Set<String> oldStorage;
    private Set<String> newStorage;

    Storage() {
        newStorage = new TreeSet<>();
        oldStorage = new TreeSet<>();
    }

    public void add(String adress) {
        newStorage.add(adress);
    }

    public void checkStorage() {
        if (!oldStorage.equals(newStorage)) {
            System.out.println("Group:");
            for (String adr : newStorage)
                System.out.println(adr);
        }
    }

    public void clean() {
        oldStorage = newStorage;
        newStorage = new TreeSet<>();
    }
}

