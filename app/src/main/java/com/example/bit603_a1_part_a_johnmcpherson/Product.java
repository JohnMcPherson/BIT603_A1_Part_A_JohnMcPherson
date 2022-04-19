package com.example.bit603_a1_part_a_johnmcpherson;

public enum Product {
    KIWI ("Kiwi"),
    TIKI ("Tiki"),
    BUZZY_BEE ("Buzzy Bee"),
    GUMBOOTS ("Gumboots");

    private String name;

    private Product(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
